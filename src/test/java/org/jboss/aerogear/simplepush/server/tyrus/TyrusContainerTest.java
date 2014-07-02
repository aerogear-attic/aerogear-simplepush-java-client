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
package org.jboss.aerogear.simplepush.server.tyrus;

import org.glassfish.tyrus.server.Server;
import org.jboss.aerogear.simplepush.AbstractSimplePushClientTest;
import org.junit.After;
import org.junit.Before;

import javax.websocket.DeploymentException;

public class TyrusContainerTest extends AbstractSimplePushClientTest {

    private Server server;

    @Before
    public void bootTyrus() {
        server = new Server("localhost", 9999, "/", null, EchoEndpoint.class);

        try {
            server.start();
        } catch (DeploymentException e) {
            e.printStackTrace();

            server.stop();
        }
    }

    @After
    public void shutTyrus() {
        server.stop();
    }
}
