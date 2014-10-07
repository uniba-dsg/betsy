package betsy.common.tasks

import ant.tasks.AntUtil
import betsy.common.config.Configuration

import java.nio.file.Files
import java.nio.file.Path

class NetworkTasks {

    public static final String BETSY_SVN_BASE_URL = "https://lspi.wiai.uni-bamberg.de/svn/betsy/"

    public static void downloadFile(URL url, Path fileOrFolder) {
        // ensure that the folders in this path are created
        if (Files.isDirectory(fileOrFolder)) {
            FileTasks.mkdirs(fileOrFolder);
        }

        AntUtil.builder().get(dest: fileOrFolder, src: url, skipexisting: true);
    }

    public static void downloadFileFromBetsyRepo(String filename) {
        downloadFile(new URL(BETSY_SVN_BASE_URL + filename), Configuration.getDownloadsDir());
    }

}
