package betsy.common.model;

import betsy.common.HasName;
import betsy.common.HasPath;
import betsy.common.model.feature.Group;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;

public interface ProcessFolderStructure extends HasName, HasID {

    HasPath getEngine();
    Path getProcess();

    String getGroup();
    Group getGroupObject();

    default String getName() { return getProcessFileNameWithoutExtension(); }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return the id without / or \ signs
     */
    default String getNormalizedId() {
        return getGroup() + "__" + getProcessFileNameWithoutExtension();
    }

    default String getID() {
        return String.join(SEPARATOR, getGroupObject().getID(), getProcessFileNameWithoutExtension());
    }

    /**
     * A bpel path as "language_features/structured_activities/Sequence.bpel" is used to extract "Sequence.bpel"
     *
     * @return the file name of the process file
     */
    default String getProcessFileName() {
        return getProcess().getFileName().toString();
    }

    default String getProcessFileNameWithoutExtension() {
        return FileTasks.getFilenameWithoutExtension(getProcess());
    }

    /**
     * The path <code>test/$engine/$process</code>
     *
     * @return the path <code>test/$engine/$process</code>
     */
    default Path getTargetPath() {
        return getEngine().getPath().resolve(getNormalizedId());
    }

    default Path getTargetLogsPath() {
        return getTargetPath().resolve("logs");
    }

    /**
     * The path <code>test/$engine/$process/pkg</code>
     *
     * @return the path <code>test/$engine/$process/pkg</code>
     */
    default Path getTargetPackagePath() {
        return getTargetPath().resolve("pkg");
    }

    /**
     * The path <code>test/$engine/$process/tmp</code>
     *
     * @return the path <code>test/$engine/$process/tmp</code>
     */
    default Path getTargetTmpPath() {
        return getTargetPath().resolve("tmp");
    }

    /**
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    default Path getTargetReportsPath() {
        return getTargetPath().resolve("reports");
    }

    /**
     * The path <code>test/$engine/$process/bpel</code>
     *
     * @return the path <code>test/$engine/$process/bpel</code>
     */
    default Path getTargetProcessPath() {
        return getTargetPath().resolve("process");
    }

    default Path getTargetProcessFilePath() {
        return getTargetProcessPath().resolve(getProcessFileName());
    }


}
