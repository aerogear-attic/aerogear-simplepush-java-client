aerogear-simplepush-java
========================

Java lib for receiving simple push messages using web sockets

Usage example:

```java
final UnifiedPushClient unifiedPushClient = new UnifiedPushClient("http://localhost:8080/ag-push/rest/registry/device", "4dc61049-4be9-456b-9661-caa5c8e5b91f",
        "2c5af89f-b2e6-40a7-9d6a-6b7b54c15624");

    SimplePushClient client = new SimplePushClient("ws://localhost:7777/simplepush/websockets");
    client.register(new RegistrationListener() {
      @Override
      public void onRegistered(String channelId, String simplePushEndPoint) {
        final PushConfig config = new PushConfig();
        config.setDeviceToken(channelId);
        config.setSimplePushEndpoint(simplePushEndPoint);

        config.setAlias("john");
        config.setCategories(Arrays.asList("lead"));
        unifiedPushClient.register(config);
      }
    });

    client.addMessageListener(new MessageListener() {
      @Override
      public void onMessage() {
        //display a message to the client for this channel
      }
    });
  }

```