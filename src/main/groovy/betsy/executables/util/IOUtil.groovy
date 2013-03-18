package betsy.executables.util


class IOUtil {

    static def captureSystemOutAndErr(Closure closure) {
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream()
        PrintStream newOut = new PrintStream(bufOut)
        PrintStream saveOut = System.out

        ByteArrayOutputStream bufErr = new ByteArrayOutputStream()
        PrintStream newErr = new PrintStream(bufErr)
        PrintStream saveOErr = System.err

        System.out = newOut
        System.err = newErr

        closure.call()

        System.out = saveOut
        System.err = saveOErr

        [bufOut.toString(), bufErr.toString()]
    }

}
