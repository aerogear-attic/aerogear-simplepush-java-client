package org.jboss.aerogear.unifiedpush;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * UnifiedPushClient test
 */
public class UnifiedPushClientTest {

  @Test
  public void shouldPostSettingsToServer() {
    //given
    final String simplePushEndpoint = "ws://localhost";
    UnifiedPushClient client = new UnifiedPushClient("", "123", "123") {
      @Override
      void send(String method, String credentials, byte[] body, String url) {
        //then
        assertEquals("", url);
        assertTrue(new String(body).contains(simplePushEndpoint));
      }
    };
    PushConfig config = new PushConfig();
    config.setSimplePushEndpoint(simplePushEndpoint);

    //when
    client.register(config);

  }

}
