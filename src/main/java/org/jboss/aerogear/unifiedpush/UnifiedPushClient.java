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

import net.iharder.Base64;
import org.jboss.aerogear.simplepush.protocol.impl.json.JsonUtil;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * UnifiedPushClient to register the simple push endpoint with the Unified Push Server.
 */
public class UnifiedPushClient {
    private static final String UTF_8 = "UTF-8";
    private final String variantId;
    private final String variantSecret;
    private final String pushServerURL;

    public UnifiedPushClient(String pushServerURL, String variantId, String variantSecret) {
        this.pushServerURL = pushServerURL;
        this.variantId = variantId;
        this.variantSecret = variantSecret;
    }

    /**
     * Register this simple push end point with given config to the unified push server
     * @param config configuration show / used by unified push server
     */
    public void register(PushConfig config) {
        try {
            final byte[] configJson = JsonUtil.toJson(config).getBytes(UTF_8);
            post(getCredentials(), configJson);
        } catch (UnsupportedEncodingException e) {
            //ignore utf-8 is supported
        }
    }

    private String getCredentials() throws UnsupportedEncodingException {
        return Base64.encodeBytes((variantId + ":" + variantSecret).getBytes(UTF_8));
    }

    /**
     * Remove simple push end point from Unified push server.
     * @param channelId of the endpoint to be removed
     */
    public void unRegister(String channelId) {
        try {
            delete(getCredentials(), channelId);
        } catch (UnsupportedEncodingException e) {
            //ignore utf-8 is supported
        }
    }

    private void delete(String credentials, String id) {
        send("DELETE", credentials, null, pushServerURL + "/" + id);
    }

    private void post(String credentials, byte[] bytes) {
        send("POST", credentials, bytes, pushServerURL);
    }

    void send(String method, String credentials, byte[] body, String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Authorization", "Basic " + credentials);
            conn.setRequestProperty("Content-Type",  "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod(method);
            if (body != null) {
                conn.setFixedLengthStreamingMode(body.length);
                try (OutputStream out = new BufferedOutputStream(conn.getOutputStream())) {
                    out.write(body);
                }
            } else {
                conn.connect();
            }
        } catch (IOException e) {
            throw new RuntimeException("could not register simple end point with unified push server", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}
