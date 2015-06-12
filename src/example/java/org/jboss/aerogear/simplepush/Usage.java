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

import org.jboss.aerogear.simplepush.MessageListener;
import org.jboss.aerogear.simplepush.RegistrationListener;
import org.jboss.aerogear.simplepush.SimplePushClient;
import org.jboss.aerogear.simplepush.protocol.Ack;
import org.jboss.aerogear.unifiedpush.PushConfig;
import org.jboss.aerogear.unifiedpush.UnifiedPushClient;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * This is a example of usage.
 */
public class Usage {
    private static JFrame frame = new JFrame("HelloWorldSwing");
    private static SimplePushClient client;
    private static UnifiedPushClient unifiedPushClient;

    public static void main(String... args) {
        unifiedPushClient = new UnifiedPushClient("http://localhost:8080/ag-push/rest/registry/device",
            "4f766e2c-b4da-42f5-8bfb-d7adc4030939", "c9af3659-e7ce-4861-bcde-22b4b8b29492");

        client = new SimplePushClient("ws://localhost:7777/simplepush/websocket");
        client.connect();
        client.register(new RegistrationListener() {
            @Override
            public void onRegistered(String channelId, String simplePushEndPoint) {
                final PushConfig config = new PushConfig();
                config.setDeviceToken(simplePushEndPoint);

                config.setAlias("john");
                config.setCategories(Arrays.asList("lead"));
                unifiedPushClient.register(config);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Ack ack) {
                showMessageDialog(frame, "Message received for " + ack);
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //when closing clean up the resources
                String channelId = client.getChannelId(0);
                unifiedPushClient.unRegister(channelId);
                client.unregister(channelId);
            }
        });

        JLabel panel = new JLabel();
        panel.setIcon(new ImageIcon(Usage.class.getResource("./logo.jpeg")));
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setVisible(true);
    }

}
