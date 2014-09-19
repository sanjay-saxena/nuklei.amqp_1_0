/*
 * Copyright 2014 Kaazing Corporation, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.nuklei.amqp_1_0.connection;

import static java.util.EnumSet.allOf;

import org.kaazing.nuklei.amqp_1_0.codec.transport.Close;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Frame;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Header;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Open;

/*
 * See AMQP 1.0 specification, section 2.4.7 "Connection State Diagram"
 */
public class ConnectionStateMachine {

    private final ConnectionHooks connectionHooks;
    
    public ConnectionStateMachine(ConnectionHooks connectionHooks) {
        this.connectionHooks = connectionHooks;
    }

    public void start(Connection connection) {
        connection.state = ConnectionState.START;
        connectionHooks.whenInitialized.accept(connection);
    }
    
    public void received(Connection connection, Header header) {
        connection.headerReceived = header.buffer().getLong(header.offset());

        switch (connection.state) {
        case START:
            transition(connection, ConnectionTransition.RECEIVED_HEADER);
            connectionHooks.whenHeaderReceived.accept(connection, header);
            break;
        default:
            if (connection.headerReceived == connection.headerSent) {
                transition(connection, ConnectionTransition.RECEIVED_HEADER);
                connectionHooks.whenHeaderReceived.accept(connection, header);
            }
            else {
                transition(connection, ConnectionTransition.RECEIVED_HEADER_NOT_EQUAL_SENT);
                connectionHooks.whenHeaderReceivedNotEqualSent.accept(connection, header);
            }
            break;
        }

    }
    
    public void sent(Connection connection, Header header) {
        connection.headerSent = header.buffer().getLong(header.offset());

        switch (connection.state) {
        case START:
            transition(connection, ConnectionTransition.SENT_HEADER);
            connectionHooks.whenHeaderSent.accept(connection, header);
            break;
        default:
            if (connection.headerReceived == connection.headerSent) {
                transition(connection, ConnectionTransition.SENT_HEADER);
                connectionHooks.whenHeaderSent.accept(connection, header);
            }
            else {
                transition(connection, ConnectionTransition.SENT_HEADER_NOT_EQUAL_RECEIVED);
                connectionHooks.whenHeaderSentNotEqualReceived.accept(connection, header);
            }
            break;
        }
    }
    
    public void received(Connection connection, Frame frame, Open open) {
        transition(connection, ConnectionTransition.RECEIVED_OPEN);
        connectionHooks.whenOpenReceived.accept(connection, frame, open);
    }
    
    public void sent(Connection connection, Frame frame, Open open) {
        transition(connection, ConnectionTransition.SENT_OPEN);
        connectionHooks.whenOpenSent.accept(connection, frame, open);
    }
    
    public void received(Connection connection, Frame frame, Close close) {
        transition(connection, ConnectionTransition.RECEIVED_CLOSE);
        connectionHooks.whenCloseReceived.accept(connection, frame, close);
    }
    
    public void sent(Connection connection, Frame frame, Close close) {
        transition(connection, ConnectionTransition.SENT_CLOSE);
        connectionHooks.whenCloseSent.accept(connection, frame, close);
    }
    
    public void error(Connection connection) {
        transition(connection, ConnectionTransition.ERROR);
        connectionHooks.whenError.accept(connection);
    }

    public void end(Connection connection) {
        // TODO: confirm if this is the correct transition from transport close
        connection.state = ConnectionState.END;
    }

    private static void transition(Connection connection, ConnectionTransition transition) {
        connection.state = STATE_MACHINE[connection.state.ordinal()][transition.ordinal()];
    }
   
    private static final ConnectionState[][] STATE_MACHINE;
    
    static {
        int stateCount = ConnectionState.values().length;
        int transitionCount = ConnectionTransition.values().length;

        ConnectionState[][] stateMachine = new ConnectionState[stateCount][transitionCount];
        for (ConnectionState state : allOf(ConnectionState.class)) {
            for (ConnectionTransition transition : allOf(ConnectionTransition.class)) {
                // default transition to "end" state
                stateMachine[state.ordinal()][transition.ordinal()] = ConnectionState.END;
            }

            // default "error" transition to "discarding" state
            stateMachine[state.ordinal()][ConnectionTransition.ERROR.ordinal()] = ConnectionState.DISCARDING;
        }
        
        stateMachine[ConnectionState.START.ordinal()][ConnectionTransition.RECEIVED_HEADER.ordinal()] = ConnectionState.HEADER_RECEIVED;
        stateMachine[ConnectionState.START.ordinal()][ConnectionTransition.SENT_HEADER.ordinal()] = ConnectionState.HEADER_SENT;
        stateMachine[ConnectionState.HEADER_RECEIVED.ordinal()][ConnectionTransition.SENT_HEADER.ordinal()] = ConnectionState.HEADER_EXCHANGED;
        stateMachine[ConnectionState.HEADER_SENT.ordinal()][ConnectionTransition.RECEIVED_HEADER.ordinal()] = ConnectionState.HEADER_EXCHANGED;
        stateMachine[ConnectionState.HEADER_SENT.ordinal()][ConnectionTransition.SENT_OPEN.ordinal()] = ConnectionState.OPEN_PIPE;
        stateMachine[ConnectionState.HEADER_EXCHANGED.ordinal()][ConnectionTransition.RECEIVED_OPEN.ordinal()] = ConnectionState.OPEN_RECEIVED;
        stateMachine[ConnectionState.HEADER_EXCHANGED.ordinal()][ConnectionTransition.SENT_OPEN.ordinal()] = ConnectionState.OPEN_SENT;
        stateMachine[ConnectionState.OPEN_PIPE.ordinal()][ConnectionTransition.RECEIVED_HEADER.ordinal()] = ConnectionState.OPEN_SENT;
        stateMachine[ConnectionState.OPEN_PIPE.ordinal()][ConnectionTransition.SENT_CLOSE.ordinal()] = ConnectionState.OPEN_CLOSE_PIPE;
        stateMachine[ConnectionState.OPEN_CLOSE_PIPE.ordinal()][ConnectionTransition.RECEIVED_HEADER.ordinal()] = ConnectionState.CLOSE_PIPE;
        stateMachine[ConnectionState.OPEN_RECEIVED.ordinal()][ConnectionTransition.SENT_OPEN.ordinal()] = ConnectionState.OPENED;
        stateMachine[ConnectionState.OPEN_SENT.ordinal()][ConnectionTransition.RECEIVED_OPEN.ordinal()] = ConnectionState.OPENED;
        stateMachine[ConnectionState.CLOSE_PIPE.ordinal()][ConnectionTransition.RECEIVED_OPEN.ordinal()] = ConnectionState.CLOSE_SENT;
        stateMachine[ConnectionState.OPENED.ordinal()][ConnectionTransition.RECEIVED_CLOSE.ordinal()] = ConnectionState.CLOSE_RECEIVED;
        stateMachine[ConnectionState.OPENED.ordinal()][ConnectionTransition.SENT_CLOSE.ordinal()] = ConnectionState.CLOSE_SENT;
        stateMachine[ConnectionState.CLOSE_RECEIVED.ordinal()][ConnectionTransition.SENT_CLOSE.ordinal()] = ConnectionState.END;
        stateMachine[ConnectionState.CLOSE_SENT.ordinal()][ConnectionTransition.RECEIVED_CLOSE.ordinal()] = ConnectionState.END;
        stateMachine[ConnectionState.DISCARDING.ordinal()][ConnectionTransition.RECEIVED_CLOSE.ordinal()] = ConnectionState.END;
        
        STATE_MACHINE = stateMachine;
    }
}