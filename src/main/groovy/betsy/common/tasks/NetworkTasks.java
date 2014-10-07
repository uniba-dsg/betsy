package betsy.common.tasks;

import betsy.common.config.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class NetworkTasks {
    public static void downloadFile(URL url, Path fileOrFolder) {
        // ensure that the folders in this path are created
        if (Files.isDirectory(fileOrFolder)) {
            FileTasks.mkdirs(fileOrFolder);
        }

        URLTasks.downloadInto(url, fileOrFolder);
    }

    public static void downloadFileFromBetsyRepo(String filename) {
        try {
            downloadFile(new URL(BETSY_SVN_BASE_URL + filename), Configuration.getDownloadsDir());
        } catch (MalformedURLException e) {
            throw new RuntimeException("could not create url", e);
        }
    }

    public static final String BETSY_SVN_BASE_URL = "https://lspi.wiai.uni-bamberg.de/svn/betsy/";
}
