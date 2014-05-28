package betsy.tasks

import ant.tasks.AntUtil
import betsy.config.Configuration

import java.nio.file.Path
import java.nio.file.Paths

class NetworkTasks {

    public static void downloadFile(URL url, Path fileOrFolder) {
        AntUtil.builder().get(dest: fileOrFolder, src: url, skipexisting: true);
    }

    public static void downloadFileFromBetsyRepo(String filename) {
        downloadFile(new URL("https://lspi.wiai.uni-bamberg.de/svn/betsy/" + filename),
                Paths.get(Configuration.get("downloads.dir")));
    }

}
