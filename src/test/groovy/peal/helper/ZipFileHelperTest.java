package peal.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZipFileHelperTest {

    private static final Path TEST_BPEL_SEQUENCE = Paths.get("src/test/resources/Sequence/Sequence.bpel");

    @Test
    public void findBpelProcessName() throws Exception {
        assertEquals("Sequence", ZipFileHelper.findBpelProcessName(Paths.get("src/test/resources/Sequence/Sequence.bpel")));
    }

    @Test
    public void findBpelProcessNamespace() throws Exception {
        assertEquals("http://dsg.wiai.uniba.de/betsy/activities/bpel/sequence",
                ZipFileHelper.findBpelTargetNameSpaceInPath(TEST_BPEL_SEQUENCE));
    }

}
