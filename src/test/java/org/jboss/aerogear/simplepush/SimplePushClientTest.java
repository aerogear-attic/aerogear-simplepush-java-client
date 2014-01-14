/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.simplepush;

import org.jboss.aerogear.simplepush.protocol.Ack;
import org.jboss.aerogear.simplepush.protocol.impl.AckImpl;
import org.jboss.aerogear.simplepush.protocol.impl.NotificationMessageImpl;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.test.IntegrationServer;
import org.jboss.aerogear.simplepush.util.UUIDUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Integration test for the SimplePushClient
 */
public class SimplePushClientTest {
  private static final int PORT = 7778;
  private IntegrationServer integrationServer;

  @Before
  public void setup() throws IOException {
    integrationServer = new IntegrationServer(PORT);
    integrationServer.start();
  }

  @After
  public void tearDown() throws IOException, InterruptedException {
    integrationServer.stop();
  }

  @Test
  public void testSimpleServerCommunication() throws InterruptedException {
    //given
    SimplePushClient client = new SimplePushClient("ws://localhost:" + PORT);
    final CountDownLatch registerLatch = new CountDownLatch(1);

    //when
    client.register(new RegistrationListener() {
      @Override
      public void onRegistered(String channelId, String pushEndpoint) {
        registerLatch.countDown();
      }
    });

    //wait for the communication to happen
    registerLatch.await(1000, TimeUnit.MILLISECONDS);

    //then
    if (registerLatch.getCount() == 1) {
      fail("onRegistered should have been called");
    }

    //when
    final CountDownLatch messageLatch = new CountDownLatch(1);
    client.addMessageListener(new MessageListener() {
      @Override
      public void onMessage(Ack ack) {
        assertNotNull(ack);
        assertNotNull(ack.getVersion());
        messageLatch.countDown();
      }
    });
    integrationServer.send(JsonUtil.toJson(new NotificationMessageImpl(new AckImpl(UUIDUtil.newUAID(), 1))));

    //wait for the communication to happen
    messageLatch.await(1000, TimeUnit.MILLISECONDS);

    //then
    if (messageLatch.getCount() == 1) {
      fail("onMessage should have been called");
    }

    client.unregister();
  }
}
