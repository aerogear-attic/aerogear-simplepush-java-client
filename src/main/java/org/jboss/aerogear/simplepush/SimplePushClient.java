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
package org.jboss.aerogear.simplepush;


import net.wessendorf.websocket.SimpleWebSocketClient;
import net.wessendorf.websocket.WebSocketHandlerAdapter;
import org.jboss.aerogear.simplepush.protocol.Ack;
import org.jboss.aerogear.simplepush.protocol.MessageType;
import org.jboss.aerogear.simplepush.protocol.impl.*;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.util.UUIDUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Push Client
 */
public class SimplePushClient {

    private final SimpleWebSocketClient websocketClient;
    private RegistrationListener registrationListener;
    private MessageListener listener;
    private List<String> registeredChannels = new ArrayList<>();

    public SimplePushClient(String simplePushServerURL) {

        try {
        websocketClient = new SimpleWebSocketClient(simplePushServerURL);
        websocketClient.setWebSocketHandler(new WebSocketHandlerAdapter() {

            @Override
            public void onOpen() {
            }

            @Override
            public void onMessage(String message) {
                final MessageType messageType = JsonUtil.parseFrame(message);
                switch (messageType.getMessageType()) {
                case REGISTER:
                    final RegisterResponseImpl registerResponse = JsonUtil.fromJson(message, RegisterResponseImpl.class);
                    if (registrationListener != null) {
                        registrationListener.onRegistered(registerResponse.getChannelId(), registerResponse.getPushEndpoint());
                    }
                    break;
                case NOTIFICATION:
                    final NotificationMessageImpl notificationMessage = JsonUtil.fromJson(message, NotificationMessageImpl.class);
                    for (Ack ack : notificationMessage.getAcks()) {
                        websocketClient.sendText(JsonUtil.toJson(ack));
                        listener.onMessage(ack);
                    }
                    break;
                case UNREGISTER:
                    UnregisterMessageImpl unregisterMessage = JsonUtil.fromJson(message, UnregisterMessageImpl.class);
                    registeredChannels.remove(unregisterMessage.getChannelId());
                    break;
                }
            }
        });
        }
        catch (URISyntaxException e) {
             throw new IllegalArgumentException("simplePushServerURL is an invalid URL");
        }
    }

    /**
     * Create a connection with the Simple Push Server.
     */
    public void connect() {
        websocketClient.connect();
        final HelloMessageImpl helloMessage = new HelloMessageImpl(UUIDUtil.newUAID());
        websocketClient.sendText(JsonUtil.toJson(helloMessage));
    }

    /**
     * Get the channel id by index.
     * 
     * @param index the index of the registered channel
     * @return the index or null
     */
    public String getChannelId(int index) {
        return registeredChannels.get(index);
    }

    /**
     * Register to a new channel for notification by the simple push server
     * 
     * @param registrationListener called when the registration is complete
     */
    public void register(RegistrationListener registrationListener) {
        this.registrationListener = registrationListener;
        String channelID = UUIDUtil.newUAID();
        registeredChannels.add(channelID);
        final String register = JsonUtil.toJson(new RegisterMessageImpl(channelID));
        websocketClient.sendText(register);
    }

    /**
     * Remove the registered channel
     * 
     * @param channelId the previously registered channel id
     */
    public void unregister(String channelId) {
        UnregisterMessageImpl unregisterMessage = new UnregisterMessageImpl(channelId);
        websocketClient.sendText(JsonUtil.toJson(unregisterMessage));
    }

    /**
     * Close the communication
     */
    public void close() {
        websocketClient.close();
    }

    public void addMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public SimpleWebSocketClient getWebsocketClient(){
        return websocketClient;
    }
}
