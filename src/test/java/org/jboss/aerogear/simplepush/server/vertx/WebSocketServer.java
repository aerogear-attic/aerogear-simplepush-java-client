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
package org.jboss.aerogear.simplepush.server.vertx;

import org.jboss.aerogear.simplepush.protocol.MessageType;
import org.jboss.aerogear.simplepush.protocol.impl.*;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.util.UUIDUtil;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.http.WebSocketFrame;
import org.vertx.java.core.http.impl.ws.WebSocketFrameInternal;

public class WebSocketServer {

    private HttpServer httpServer;
    private Vertx vertx;

    public void start(int port) {

        vertx = VertxFactory.newVertx();

        httpServer = vertx.createHttpServer();
        httpServer.websocketHandler(new Handler<ServerWebSocket>() {
            public void handle(final ServerWebSocket ws) {

                if (ws.path().equals("/echo")) {

                    ws.frameHandler(new Handler<WebSocketFrame>() {
                        @Override
                        public void handle(WebSocketFrame webSocketFrame) {
                            if (webSocketFrame.isBinary()) {
                                Buffer buff = new Buffer(((WebSocketFrameInternal) webSocketFrame).getBinaryData());
                                ws.writeBinaryFrame(buff);
                            } else if (webSocketFrame.isText()) {
                                String request = webSocketFrame.textData();
                                final MessageType messageType = JsonUtil.parseFrame(request);
                                switch (messageType.getMessageType()) {
                                    case HELLO:
                                        ws.writeTextFrame(JsonUtil.toJson(new HelloResponseImpl(UUIDUtil.newUAID())));
                                        break;
                                    case REGISTER:
                                        final RegisterMessageImpl registerMessage = JsonUtil.fromJson(request, RegisterMessageImpl.class);
                                        final StatusImpl status = new StatusImpl(200, "N/A");
                                        final String channelId = registerMessage.getChannelId();
                                        final RegisterResponseImpl response = new RegisterResponseImpl(channelId, status, "ws://localhost:" + 9999);
                                        ws.writeTextFrame(JsonUtil.toJson(response));
                                        break;
                                    case UNREGISTER:
                                        break;
                                    /**
                                     * Convenient manner of sending Notification to the client, that will not happen in a real SPS server
                                     */
                                    case NOTIFICATION:
                                        ws.writeTextFrame(JsonUtil.toJson(new NotificationMessageImpl(new AckImpl(UUIDUtil.newUAID(), 1))));
                                }
                            }
                        }
                    });

                } else {
                    ws.reject();
                }
            }
        }).listen(port);
    }

    public void stop() {
        httpServer.close();

    }
}
