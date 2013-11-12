package betsy.logging

import org.apache.log4j.MDC

class LogContext {

    public static final String CONTEXT_KEY_PATH = "path"

    public static void init() {
        // default log context is BETSY
        MDC.put(CONTEXT_KEY_PATH, "betsy");
    }

    public static String getContext() {
        return MDC.get(CONTEXT_KEY_PATH);
    }

    public static void setContext(String context) {
        MDC.put(CONTEXT_KEY_PATH, context.replace("\\", "/"));
    }

}
