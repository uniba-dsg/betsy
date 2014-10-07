package betsy.common.tasks;

import groovy.util.AntBuilder;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;

import java.nio.file.Path;

public class ZipTasks {
    public static void zipFolder(Path tempZipFile, Path folder) {
        log.info("Creating zip archive " + String.valueOf(tempZipFile) + " using the contents of " + String.valueOf(folder));

        Zip zip = new Zip();
        zip.setDestFile(tempZipFile.toFile());
        zip.setBasedir(folder.toFile());
        Zip.WhenEmpty whenEmpty = new Zip.WhenEmpty();
        whenEmpty.setValue("create");
        zip.setWhenempty(whenEmpty);

        zip.setProject(new AntBuilder().getAntProject());
        zip.setTaskName("zip");

        zip.execute();
    }

    public static void unzip(Path tempZipFile, Path tempExtractedFolder) {
        log.info("Unzipping " + String.valueOf(tempZipFile) + " to " + String.valueOf(tempExtractedFolder));

        Expand expand = new Expand();
        expand.setDest(tempExtractedFolder.toFile());
        expand.setSrc(tempZipFile.toFile());

        expand.setProject(new AntBuilder().getAntProject());
        expand.setTaskName("unzip");

        expand.execute();
    }

    private static final Logger log = Logger.getLogger(ZipTasks.class);
}
