package betsy.data.engines

import betsy.data.WsdlOperation
import com.sun.org.apache.xml.internal.security.Init
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer


class Util {

    public static String[] computeMatchingPattern(betsy.data.Process process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = new File(process.bpelFilePath).getText()
        String canonicalText = canonicalizeXML(text)

        def operations = [WsdlOperation.SYNC_STRING, WsdlOperation.SYNC, WsdlOperation.ASYNC]
        def implementedOperations = []

        canonicalText.eachLine { line ->
            operations.each { operation ->
                if (line.contains("operation=\"${operation.name}\"") && !line.contains("invoke")) {
                    implementedOperations << operation;
                }
            }
        }

        def unimplementedOperations = operations - implementedOperations
        unimplementedOperations.collect{it.name}
    }

    public static String canonicalizeXML(String text) {
        Init.init();
        Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);

        return new String(canon.canonicalize(text.bytes));
    }
}
