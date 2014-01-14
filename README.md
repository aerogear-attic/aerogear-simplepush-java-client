Aerogear Simple push Java
=========================

Java lib for receiving simple push messages using web sockets

Usage example:

```java
  unifiedPushClient = new UnifiedPushClient("http://localhost:8080/ag-push/rest/registry/device", "4f766e2c-b4da-42f5-8bfb-d7adc4030939",
        "c9af3659-e7ce-4861-bcde-22b4b8b29492");

  client = new SimplePushClient("ws://localhost:7777/simplepush/websocket");
  client.connect();
  client.register(new RegistrationListener() {
    @Override
    public void onRegistered(String channelId, String simplePushEndPoint) {
      final PushConfig config = new PushConfig();
      config.setDeviceToken(channelId);
      config.setSimplePushEndpoint(simplePushEndPoint);

      unifiedPushClient.register(config);
    }
  });

  client.addMessageListener(new MessageListener() {
    @Override
    public void onMessage(Ack ack) {
      showMessageDialog(frame, "Message received for " + ack);
    }
  });
```