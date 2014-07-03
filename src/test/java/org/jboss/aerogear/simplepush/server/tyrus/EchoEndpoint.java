/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.simplepush.server.tyrus;

import org.jboss.aerogear.simplepush.protocol.MessageType;
import org.jboss.aerogear.simplepush.protocol.impl.*;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.util.UUIDUtil;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

@ServerEndpoint("/echo")
public class EchoEndpoint {

    private static final Logger LOGGER = Logger.getLogger(EchoEndpoint.class.getName());

    @OnMessage
    public void receiveTextMessage(String message, Session session) throws IOException {
        String request = message;
        final MessageType messageType = JsonUtil.parseFrame(request);
        switch (messageType.getMessageType()) {
            case HELLO:
                session.getBasicRemote().sendText(JsonUtil.toJson(new HelloResponseImpl(UUIDUtil.newUAID())));
                break;
            case REGISTER:
                final RegisterMessageImpl registerMessage = JsonUtil.fromJson(request, RegisterMessageImpl.class);
                final StatusImpl status = new StatusImpl(200, "N/A");
                final String channelId = registerMessage.getChannelId();
                final RegisterResponseImpl response = new RegisterResponseImpl(channelId, status, "ws://localhost:" + 9999);
                session.getBasicRemote().sendText(JsonUtil.toJson(response));
                break;
            case UNREGISTER:
                break;
            /**
             * Convenient manner of sending Notification to the client, that will not happen in a real SPS server
             */
            case NOTIFICATION:
                session.getBasicRemote().sendText(JsonUtil.toJson(new NotificationMessageImpl(new AckImpl(UUIDUtil.newUAID(), 1))));
        }
    }

    @OnMessage
    public void receiveBinaryMessage(ByteBuffer message, Session session) throws IOException {
        LOGGER.info("Received Binary Message");
        session.getBasicRemote().sendBinary(message);
    }
}
