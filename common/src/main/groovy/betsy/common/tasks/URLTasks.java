package betsy.common.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Get;

public class URLTasks {

    private static final Logger LOGGER = Logger.getLogger(URLTasks.class);

    public static boolean isUrlAvailable(URL url) {
        LOGGER.info("Checking whether the url " + url + " returns HTTP 200");
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            TrustModifier.relaxHostChecking(connection);

            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            LOGGER.info("Got response code " + responseCode);
            return responseCode == 200;
        } catch (Exception e) {
            LOGGER.info(e);
            return false;
        }
    }

    public static boolean isUrlAvailable(String url) {
        try {
            return isUrlAvailable(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Given url " + url + " is not well formed", e);
        }
    }

    public static boolean hasUrlSubstring(URL url, String substring) {
        LOGGER.info("Checking whether the url " + url + " has substring " + substring);
        try {
            String result = getContentAtUrl(url);

            return result.contains(substring);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getContentAtUrl(URL url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        TrustModifier.relaxHostChecking(connection);

        InputStream inputStream = connection.getInputStream();
        return inputStreamToString(inputStream);
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                result.append(inputLine);
            }
        }
        return result.toString();
    }

    public static void downloadInto(URL url, Path downloadFolder) {
        FileTasks.mkdirs(downloadFolder);

        Get get = new Get();
        get.setSrc(url);
        get.setSkipExisting(true);
        get.setDest(downloadFolder.toFile());

        get.setProject(AntUtil.builder().getProject());
        get.setTaskName("get");

        get.execute();
    }
}
