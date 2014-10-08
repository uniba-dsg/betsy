package betsy.common.engines;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Util {
    public static String canonicalizeXML(String text) {
        Init.init();
        try {
            Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
            return new String(canon.canonicalize(text.getBytes()));
        } catch (InvalidCanonicalizerException | CanonicalizationException | ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalStateException("could not create a canonical version of the xml", e);
        }
    }
}
