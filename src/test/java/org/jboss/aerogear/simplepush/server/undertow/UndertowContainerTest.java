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
package org.jboss.aerogear.simplepush.server.undertow;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.undertow.Undertow;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.jboss.aerogear.simplepush.AbstractSimplePushClientTest;
import org.jboss.aerogear.simplepush.protocol.MessageType;
import org.jboss.aerogear.simplepush.protocol.impl.*;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.util.UUIDUtil;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.logging.Logger;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

public class UndertowContainerTest extends AbstractSimplePushClientTest {

    private static final Logger LOGGER = Logger.getLogger(UndertowContainerTest.class.getName());

    private Undertow server;

    @Before
    public void bootUndertow() {
        server = Undertow.builder()
                .addHttpListener(9999, "localhost")
                .setHandler(path()
                        .addPrefixPath("/echo", websocket(new WebSocketConnectionCallback() {

                            @Override
                            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                                channel.getReceiveSetter().set(new AbstractReceiveListener() {

                                    @Override
                                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                                        LOGGER.info("Received Text Message");
                                        String request = message.getData();
                                        final MessageType messageType = JsonUtil.parseFrame(request);
                                        switch (messageType.getMessageType()) {
                                            case HELLO:
                                                WebSockets.sendText(JsonUtil.toJson(new HelloResponseImpl(UUIDUtil.newUAID())), channel, null);
                                                break;
                                            case REGISTER:
                                                final RegisterMessageImpl registerMessage = JsonUtil.fromJson(request, RegisterMessageImpl.class);
                                                final StatusImpl status = new StatusImpl(200, "N/A");
                                                final String channelId = registerMessage.getChannelId();
                                                final RegisterResponseImpl response = new RegisterResponseImpl(channelId, status, "ws://localhost:" + 9999);
                                                WebSockets.sendText(JsonUtil.toJson(response),channel,null);
                                                break;
                                            case UNREGISTER:
                                                break;
                                            /**
                                             * Convenient manner of sending Notification to the client, that will not happen in a real SPS server
                                             */
                                            case NOTIFICATION:
                                                WebSockets.sendText(JsonUtil.toJson(new NotificationMessageImpl(new AckImpl(UUIDUtil.newUAID(), 1))), channel, null);
                                        }

                                    }

                                    @Override
                                    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
                                        LOGGER.info("Received Binary Message");
                                        WebSockets.sendBinary(message.getData().getResource(), channel, null);
                                    }
                                });
                                channel.resumeReceives();
                            }
                        }))).build();
        server.start();
    }

    @After
    public void shutdownUndertow() {
        server.stop();
    }
}
