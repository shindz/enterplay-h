package acc.healthapp.notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static acc.healthapp.notification.HttpRequest.CONTENT_TYPE_JSON;
import static acc.healthapp.notification.HttpRequest.HEADER_CONTENT_TYPE;
import static acc.healthapp.notification.HttpRequest.HEADER_AUTHORIZATION;

public class GcmServerSideSender {

    private static final String GCM_SEND_ENDPOINT = "https://gcm-http.googleapis.com/gcm/send";

    private static final String PARAM_JSON_PAYLOAD = "data";

    public static final String RESPONSE_PLAINTEXT_MESSAGE_ID = "id";
    public static final String RESPONSE_PLAINTEXT_CANONICAL_REG_ID = "registration_id";
    public static final String RESPONSE_PLAINTEXT_ERROR = "Error";

    private final String key;

    public GcmServerSideSender() {
        // Important --- Hard Coded
        this.key = "AIzaSyD8bgJPmBt1aVaPpuiLvAARprMbie-Ez4Q";
    }

    public void sendHttpJSONMessage(String destination,
                                    Map<String, String> data) throws IOException {

        final JSONObject jsonBody = createGeneralRequest(destination, data);

        if (jsonBody == null) {
            return;
        }

        _sendHttpJSONRequest(jsonBody);

    }

    public void sendHttpJSONMessageWithTopic(final String topic,
                                             final Map<String, String> data) throws IOException {

        final JSONObject jsonBody = createTopicRequest(topic, data);

        if (jsonBody == null) {
            return;
        }

        _sendHttpJSONRequest(jsonBody);

    }

    private JSONObject createGeneralRequest(final String destination,
                                            final Map<String, String> data) {
        JSONObject jsonBody = new JSONObject();
        try {

            ArrayList<String> list = new ArrayList<String>();
            list.add(destination);

            jsonBody.put("registration_ids", new JSONArray(list));
            jsonBody.put(PARAM_JSON_PAYLOAD, new JSONObject(data));
        } catch (JSONException e) {
            return null;
        }

        return jsonBody;
    }

    private JSONObject createTopicRequest(final String topic,
                                          final Map<String, String> data) {
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("to", buildTopic(topic));
            jsonBody.put(PARAM_JSON_PAYLOAD, new JSONObject(data));
        } catch (JSONException e) {
            return null;
        }

        return jsonBody;
    }

    private void _sendHttpJSONRequest(final JSONObject jsonBody) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        httpRequest.setHeader(HEADER_AUTHORIZATION, "key=" + key);
        httpRequest.doPost(GCM_SEND_ENDPOINT, jsonBody.toString());

        if (httpRequest.getResponseCode() != 200) {
            throw new IOException("Invalid request."
                    + "\nStatus: " + httpRequest.getResponseCode()
                    + "\nResponse: " + httpRequest.getResponseBody());
        }

        String[] lines = httpRequest.getResponseBody().split("\n");
        if (lines.length == 0 || lines[0].equals("")) {
            throw new IOException("Received empty response from GCM service.");
        }

        String[] firstLineValues = lines[0].split("=");
        if (firstLineValues.length != 2) {
            throw new IOException("Invalid response from GCM: " + httpRequest.getResponseBody());
        }

        switch (firstLineValues[0]) {
            case RESPONSE_PLAINTEXT_MESSAGE_ID:
                // check for canonical registration id
                if (lines.length > 1) {
                    // If the response includes a 2nd line we expect it to be the CANONICAL REG ID
                    String[] secondLineValues = lines[1].split("=");
                    if (secondLineValues.length == 2
                            && secondLineValues[0].equals(RESPONSE_PLAINTEXT_CANONICAL_REG_ID)) {

                    } else {

                    }
                }
                break;
            case RESPONSE_PLAINTEXT_ERROR:
                break;
            default:
                break;
        }
    }

    private String buildTopic(final String topic) {
        StringBuilder builder = new StringBuilder("/topics/");
        builder.append(topic);
        return builder.toString();
    }

}
