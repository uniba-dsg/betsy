package betsy.bpmn.engines.camunda;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;

public class JsonHelper {

    static {
        // do not use this annoying thread
        SyncIdleConnectionMonitorThread syncIdleConnectionMonitorThread = (SyncIdleConnectionMonitorThread) Options.getOption(Option.SYNC_MONITOR);
        syncIdleConnectionMonitorThread.shutdown();
    }

    public static JSONObject get(String url, int expectedCode) {
        log.info("HTTP GET " + url);

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).asJson();
            assertHttpCode(expectedCode, response);
            logResponse(response.getBody());

            if (response.getBody().isArray()) {
                return response.getBody().getArray().getJSONObject(0);
            } else {
                return response.getBody().getObject();
            }
        } catch (UnirestException e) {
            throw new RuntimeException("rest call to " + url + " failed", e);
        }
    }

    public static String getStringWithAuth(String url, int expectedCode, String username, String password) {
        log.info("HTTP GET " + url);

        try {
            HttpResponse<String> response = Unirest.get(url).basicAuth(username, password).asString();
            assertHttpCode(expectedCode, response);
            logResponse(response.getBody());

            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("rest call to " + url + " failed", e);
        }
    }

    public static JSONObject post(String url, JSONObject requestBody, int expectedCode) {
        log.info("HTTP POST " + url);
        log.info("CONTENT: " + requestBody.toString(2));

        try {
            HttpResponse<JsonNode> response = Unirest.post(url).header("Content-Type", "application/json").body(requestBody.toString()).asJson();
            assertHttpCode(expectedCode, response);
            logResponse(response.getBody());
            return response.getBody().getObject();
        } catch (UnirestException e) {
            throw new RuntimeException("rest call to " + url + " failed", e);
        }
    }

    public static String postStringWithAuth(String url, JSONObject requestBody, int expectedCode, String username, String password) {
        log.info("HTTP POST " + url);
        log.info("CONTENT: " + requestBody.toString(2));

        try {
            HttpResponse<String> response = Unirest.post(url).header("Content-Type", "application/json").basicAuth(username, password).body(requestBody.toString()).asString();
            assertHttpCode(expectedCode, response);
            logResponse(response.getBody());
            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("rest call to " + url + " failed", e);
        }
    }

    public static JSONObject post(String url, Path path, int expectedCode) {
        log.info("HTTP POST " + url);
        log.info("FILE: " + path);

        try {
            HttpResponse<JsonNode> response = Unirest.post(url).field("file", path.toFile()).asJson();
            assertHttpCode(expectedCode, response);
            logResponse(response.getBody());
            return response.getBody().getObject();
        } catch (UnirestException e) {
            throw new RuntimeException("rest call to " + url + " failed", e);
        }
    }

    private static void assertHttpCode(int expectedCode, HttpResponse<?> response) {
        int code = response.getCode();
        if (expectedCode == code) {
            log.info("Response returned with expected status code " + expectedCode);
        } else {
            throw new RuntimeException("expected " + expectedCode + ", got " + code + "; " +
                    "reason: " + response.getBody());
        }
    }

    private static void logResponse(JSONArray response) {
        if (response == null) {
            log.info("HTTP RESPONSE is empty.");
        } else {
            log.info("HTTP RESPONSE: " + response.toString(2));
        }
    }

    private static void logResponse(String response) {
        if (response == null) {
            log.info("HTTP RESPONSE String is empty.");
        } else {
            log.info("HTTP RESPONSE: " + response);
        }
    }

    private static void logResponse(JSONObject response) {
        if (response == null) {
            log.info("HTTP RESPONSE is empty.");
        } else {
            log.info("HTTP RESPONSE: " + response.toString(2));
        }
    }

    private static void logResponse(JsonNode response) {
        if (response.isArray()) {
            logResponse(response.getArray());
        } else {
            logResponse(response.getObject());
        }
    }

    private static final Logger log = Logger.getLogger(JsonHelper.class);

}
