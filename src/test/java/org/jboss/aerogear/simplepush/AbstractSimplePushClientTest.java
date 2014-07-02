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

import org.jboss.aerogear.simplepush.protocol.Ack;
import org.jboss.aerogear.simplepush.protocol.impl.AckImpl;
import org.jboss.aerogear.simplepush.protocol.impl.NotificationMessageImpl;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import org.jboss.aerogear.simplepush.server.AbstractSimplePushServer;
import org.jboss.aerogear.simplepush.util.UUIDUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Integration server for the SimplePushClient
 */
public abstract class AbstractSimplePushClientTest {

    protected AbstractSimplePushServer simplePushServer;

    @Test
    public void testSimpleServerCommunication() throws InterruptedException {
        //given
        final SimplePushClient client = new SimplePushClient("ws://localhost:9999/echo");
        final CountDownLatch registerLatch = new CountDownLatch(1);

        //when
        client.connect();
        client.register(new RegistrationListener() {
            @Override
            public void onRegistered(String channelId, String pushEndpoint) {
                assertEquals(channelId, client.getChannelId(0));
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

        client.getWebsocketClient().sendText(JsonUtil.toJson(new NotificationMessageImpl(new AckImpl(UUIDUtil.newUAID(), 1))));

        //wait for the communication to happen
        messageLatch.await(1000, TimeUnit.MILLISECONDS);

        //then
        if (messageLatch.getCount() == 1) {
            fail("onMessage should have been called");
        }

        client.unregister(client.getChannelId(0));
        client.close();
    }
}
