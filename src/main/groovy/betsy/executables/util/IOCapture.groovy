package betsy.executables.util

import org.apache.log4j.Logger


class IOCapture {

    private static final Logger log = Logger.getLogger(IOCapture)

    public static void captureIO(Closure closure) {
        //stdout
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream()
        PrintStream newOut = new PrintStream(bufOut)
        PrintStream saveOut = System.out

        //stderr
        ByteArrayOutputStream bufErr = new ByteArrayOutputStream()
        PrintStream newErr = new PrintStream(bufErr)
        PrintStream saveOErr = System.err

        // capture stdout and stderr
        System.out = newOut
        System.err = newErr

        try {
            closure.run()
        } finally {
            System.out = saveOut
            System.err = saveOErr
        }

        log.trace "System.out Output:\n\n${bufOut}"
        log.trace "System.err Output:\n\n${bufErr}"
    }

}
