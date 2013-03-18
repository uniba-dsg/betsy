package betsy.executables.util


class IOUtil {

    static String[] captureSystemOutAndErr(Closure closure) {
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

        closure.call()

        System.out = saveOut
        System.err = saveOErr

        [bufOut.toString(), bufErr.toString()] as String[]
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

}
