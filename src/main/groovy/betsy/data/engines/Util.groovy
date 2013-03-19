package betsy.data.engines

import betsy.data.WsdlOperation
import com.sun.org.apache.xml.internal.security.Init
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer


class Util {

    public static String computeMatchingPattern(betsy.data.Process process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = new File(process.bpelFilePath).getText()
        String canonicalText = canonicalizeXML(text)

        boolean implementsSyncOperation = false;
        boolean implementsAsyncOperation = false;

        canonicalText.eachLine { line ->
            if (line.contains(WsdlOperation.ASYNC.name) && !line.contains("invoke")) {
                implementsAsyncOperation = true;
            }

            if (line.contains(WsdlOperation.SYNC.name) && !line.contains("invoke")) {
                implementsSyncOperation = true;
            }
        }

        if (!implementsSyncOperation) {
            return WsdlOperation.SYNC.name
        } else if (!implementsAsyncOperation) {
            return WsdlOperation.ASYNC.name
        } else {
            return ""
        }
    }

    public static String canonicalizeXML(String text) {
        Init.init();
        Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);

        return new String(canon.canonicalize(text.bytes));
    }
}