package betsy.common.util;

import java.nio.file.Path;

public final class StringUtils {

    private StringUtils() {}

    public static String capitalize(String self) {
        if (self == null || self.length() == 0) {
            return self;
        }
        return Character.toUpperCase(self.charAt(0)) + self.substring(1);
    }

    public static String toUnixStyle(Path passwordFilePath) {
        return toUnixStyle(passwordFilePath.toAbsolutePath().toString());
    }

    public static String toUnixStyle(String string) {
        return string.replace("\\", "/");
    }
}
