package betsy.common.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class IOCapture {
    public static void captureIO(Runnable closure) {
        //stdout
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(bufOut);
        PrintStream saveOut = System.out;

        //stderr
        ByteArrayOutputStream bufErr = new ByteArrayOutputStream();
        PrintStream newErr = new PrintStream(bufErr);
        PrintStream saveOErr = System.err;

        // capture stdout and stderr
        System.setOut(newOut);
        System.setErr(newErr);

        try {
            closure.run();
        } finally {
            System.setOut(saveOut);
            System.setErr(saveOErr);
        }


        log.trace("System.out Output:\n\n" + bufOut);
        log.trace("System.err Output:\n\n" + bufErr);
    }

    private static final Logger log = Logger.getLogger(IOCapture.class);
}
