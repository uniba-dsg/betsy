package betsy.common.util;

public final class OperatingSystem {

    public static final String osName = System.getProperty("os.name", "unknown").toLowerCase();
    public static final boolean WINDOWS = osName.startsWith("win");
    public static final boolean NOT_WINDOWS = !WINDOWS;

    private OperatingSystem() {}

}
