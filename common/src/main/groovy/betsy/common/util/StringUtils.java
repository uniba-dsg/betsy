package betsy.common.util;

import java.nio.file.Path;

public class StringUtils {

    public static String toUnixStyle(Path passwordFilePath) {
        return toUnixStyle(passwordFilePath.toAbsolutePath().toString());
    }

    public static String toUnixStyle(String string) {
        return string.replace("\\", "/");
    }
}
