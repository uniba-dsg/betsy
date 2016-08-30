package betsy.common.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import betsy.common.config.Configuration;

public class NetworkTasks {

    public static final String BETSY_SVN_BASE_URL = "https://lspi.wiai.uni-bamberg.de/svn/betsy/";

    public static void downloadFile(String url, Path fileOrFolder) {
        try {
            URLTasks.downloadInto(new URL(url), fileOrFolder);
        } catch (MalformedURLException e) {
            throw new RuntimeException("url " + url + " is malformed", e);
        }
    }

    public static void downloadFile(URL url, Path fileOrFolder) {
        // ensure that the folders in this path are created
        if (Files.isDirectory(fileOrFolder)) {
            FileTasks.mkdirs(fileOrFolder);
        }

        URLTasks.downloadInto(url, fileOrFolder);
    }

    public static void downloadFileFromBetsyRepo(String filename) {
        downloadFile(BETSY_SVN_BASE_URL + filename, Configuration.getDownloadsDir());
    }

}
