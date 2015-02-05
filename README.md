Aerogear SimplePush Java client
===============================

Java lib for receiving SimplePush messages using WebSocket

|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Build:          | Maven  |
| Documentation:  | https://aerogear.org/push/  |
| Issue tracker:  | https://issues.jboss.org/browse/AGPUSH  |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-users))  |
|                 | [aerogear-dev](http://aerogear-dev.1069024.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-dev))  |


### Getting started

In your ```pom.xml``` file add the following:
```xml
<dependency>
  <groupId>org.jboss.aerogear</groupId>
  <artifactId>aerogear-simplepush-java-client</artifactId>
  <version>0.1.0</version>
</dependency>
```

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

## Documentation

For more details about the current release, please consult [our documentation](https://aerogear.org/push).

## Development

If you would like to help develop AeroGear you can join our [developer's mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-dev), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](http://aerogear.org/docs/guides/Contributing/)

## Questions?

Join our [user mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-users) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGPUSH) with some steps to reproduce it.
