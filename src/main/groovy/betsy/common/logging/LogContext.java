package betsy.common.logging;

import betsy.common.util.StringUtils;
import org.apache.log4j.MDC;

import java.util.Objects;

public final class LogContext {

    public static final String CONTEXT_KEY_PATH = "path";

    static {
        // default log context is BETSY
        MDC.put(CONTEXT_KEY_PATH, "betsy");
    }

    private LogContext() {}

    public static String getContext() {
        Object context = MDC.get(CONTEXT_KEY_PATH);
        return Objects.requireNonNull(context, "no context found for " + CONTEXT_KEY_PATH).toString();
    }

    public static void setContext(String context) {
        MDC.put(CONTEXT_KEY_PATH, StringUtils.toUnixStyle(context.replace("\\", "/")));
    }


}
