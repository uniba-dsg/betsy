package betsy.virtual.common.messages.collect_log_files;

import java.io.Serializable;
import java.util.List;

public class LogFile implements Serializable{

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private final String filename;
    private final List<String> content;

    public LogFile(String filename, List<String> content) {
        this.filename = filename;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public List<String> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "filename='" + filename + '\'' +
                ", content-lines=" + content.size() +
                '}';
    }
}
