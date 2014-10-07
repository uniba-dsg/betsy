package betsy.common.logging;

import org.apache.log4j.MDC;

import java.util.Objects;

public class LogContext {

    public static final String CONTEXT_KEY_PATH = "path";

    static {
        // default log context is BETSY
        MDC.put(CONTEXT_KEY_PATH, "betsy");
    }

    public static String getContext() {
        Object context = MDC.get(CONTEXT_KEY_PATH);
        return Objects.requireNonNull(context, "no context found for " + CONTEXT_KEY_PATH).toString();
    }

    public static void setContext(String context) {
        MDC.put(CONTEXT_KEY_PATH, context.replace("\\", "/"));
    }


}
