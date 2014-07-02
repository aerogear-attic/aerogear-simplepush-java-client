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
package org.jboss.aerogear.unifiedpush;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * UnifiedPushClient server
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
