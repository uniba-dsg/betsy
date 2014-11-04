package betsy.bpel;

import betsy.bpel.model.BPELProcess;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ShortIdsTests {

    @Test
    public void testUniquenessOfShortIds() {
        List<String> shortIds = new LinkedList<>();

        BPELProcessRepository processRepository = new BPELProcessRepository();
        List<BPELProcess> processed = processRepository.getByName("ALL");
        for(BPELProcess process : processed){
            shortIds.add(process.getShortId());
        }

        for(String shortId : shortIds) {
            List<String> copyOfShortIds = new LinkedList<>(shortIds);
            copyOfShortIds.remove(shortId);
            copyOfShortIds.remove(shortId);

            Assert.assertEquals("ID " + shortId + " is used more than once", shortIds.size() - 1, copyOfShortIds.size());
        }

    }

}
