package org.jboss.aerogear.simplepush.test;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jboss.aerogear.simplepush.protocol.MessageType;
import org.jboss.aerogear.simplepush.protocol.impl.HelloResponseImpl;
import org.jboss.aerogear.simplepush.protocol.impl.RegisterMessageImpl;
import org.jboss.aerogear.simplepush.protocol.impl.RegisterResponseImpl;
import org.jboss.aerogear.simplepush.protocol.impl.StatusImpl;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Test server side implementation of simple push for integration testing.
 */
public class IntegrationServer extends WebSocketServer {
  private Logger log = LoggerFactory.getLogger(IntegrationServer.class);

  public IntegrationServer(int port) throws UnknownHostException {
    super(new InetSocketAddress(port));
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    log.info("{} connected", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    log.info("{} connection closed", reason);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    log.debug("received message {}", message);
    final MessageType messageType = JsonUtil.parseFrame(message);
    switch (messageType.getMessageType()) {
      case HELLO:
        send(JsonUtil.toJson(new HelloResponseImpl(UUIDUtil.newUAID())));
        break;
      case REGISTER:
        final RegisterMessageImpl registerMessage = JsonUtil.fromJson(message, RegisterMessageImpl.class);
        final StatusImpl status = new StatusImpl(200, "N/A");
        final String channelId = registerMessage.getChannelId();
        final RegisterResponseImpl response = new RegisterResponseImpl(channelId, status, "ws://localhost:" + getPort());
        send(JsonUtil.toJson(response));
        break;
      case UNREGISTER:
        break;
    }
  }

  public void send(String text) {
    synchronized (this) {
      for (WebSocket socket : connections()) {
        socket.send(text);
      }
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    throw new RuntimeException("onError occurred", ex);
  }
}