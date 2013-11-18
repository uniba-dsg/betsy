package betsy.logging;

import org.apache.log4j.MDC;

public class LogContext {

    public static void init() {
        // default log context is BETSY
        MDC.put(CONTEXT_KEY_PATH, "betsy");
    }

    public static String getContext() {
        return MDC.get(CONTEXT_KEY_PATH).toString();
    }

    public static void setContext(String context) {
        MDC.put(CONTEXT_KEY_PATH, context.replace("\\", "/"));
    }

    public static final String CONTEXT_KEY_PATH = "path";
}
