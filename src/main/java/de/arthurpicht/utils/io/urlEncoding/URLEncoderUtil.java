package de.arthurpicht.utils.io.urlEncoding;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class URLEncoderUtil {

    /**
     * Encodes key/value pairs as required for x-www-form-urlencoded form data,
     * as submitted by HTTP POST requests. Key/value pairs are encoded in key/value
     * tuples separated by a '&' sign with a '=' sign between key and value.
     * Non alphanumeric characters are encoded by standard URLEncoder.
     *
     * @param keyValuePairs
     * @return
     */
    public static String xWWWFormEncode(Map<String, String> keyValuePairs) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> keyValuePair : keyValuePairs.entrySet()) {

            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }

            String keyEncoded = URLEncoder.encode(keyValuePair.getKey(), StandardCharsets.UTF_8);
            stringBuilder.append(keyEncoded);

            stringBuilder.append("=");

            String valueEncoded = URLEncoder.encode(keyValuePair.getValue(), StandardCharsets.UTF_8);
            stringBuilder.append(valueEncoded);
        }

        return stringBuilder.toString();

    }

}
