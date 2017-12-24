package betsy.common.virtual.swarm.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class DataMessage implements Serializable {

    private HashMap<String, byte[]> files;
    private ArrayList<String> directories;

    public DataMessage(HashMap<String, byte[]> files, ArrayList<String> directories) {
        this.files = files;
        this.directories = directories;
    }

    public HashMap<String, byte[]> getFileList() {
        return files;
    }

    public ArrayList<String> getDirectories() {
        return directories;
    }
}
