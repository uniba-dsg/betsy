package betsy.common.tasks;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;

import java.nio.file.Path;

public class ZipTasks {
    public static void zipFolder(Path tempZipFile, Path folder) {
        LOGGER.info("Creating zip archive " + tempZipFile + " using the contents of " + folder);

        Zip zip = new Zip();
        zip.setDestFile(tempZipFile.toFile());
        zip.setBasedir(folder.toFile());
        Zip.WhenEmpty whenEmpty = new Zip.WhenEmpty();
        whenEmpty.setValue("create");
        zip.setWhenempty(whenEmpty);

        zip.setProject(AntUtil.builder().getProject());
        zip.setTaskName("zip");

        zip.execute();
    }

    public static void unzip(Path tempZipFile, Path tempExtractedFolder) {
        LOGGER.info("Unzipping " + tempZipFile + " to " + tempExtractedFolder);

        Expand expand = new Expand();
        expand.setDest(tempExtractedFolder.toFile());
        expand.setSrc(tempZipFile.toFile());

        expand.setProject(AntUtil.builder().getProject());
        expand.setTaskName("unzip");

        expand.execute();
    }

    private static final Logger LOGGER = Logger.getLogger(ZipTasks.class);
}
