package betsy.common.engines

import com.sun.org.apache.xml.internal.security.Init
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer


class Util {

    private static String canonicalizeXML(String text) {
        Init.init();
        Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);

        return new String(canon.canonicalize(text.bytes));
    }

}
