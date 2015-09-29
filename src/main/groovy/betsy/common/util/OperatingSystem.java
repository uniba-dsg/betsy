package betsy.common.util;

public class OperatingSystem {

    public static final String osName = System.getProperty("os.name", "unknown").toLowerCase();
    public static final boolean WINDOWS = osName.startsWith("win");
    public static final boolean NOT_WINDOWS = !WINDOWS;

}
