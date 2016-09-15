package betsy.bpel;

import betsy.bpel.model.BPELIdShortener;
import pebl.test.Test;
import configuration.bpel.BPELProcessRepository;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;

public class ShortIdsTests {

    @org.junit.Test
    public void testUniquenessOfShortIds() {
        List<String> shortIds = new LinkedList<>();

        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<Test> processed = processRepository.getByName("ALL");
        for(Test process : processed){
            shortIds.add(new BPELIdShortener(process.getName()).getShortenedId());
        }

        for(String shortId : shortIds) {
            List<String> copyOfShortIds = new LinkedList<>(shortIds);
            copyOfShortIds.remove(shortId);
            copyOfShortIds.remove(shortId);

            Assert.assertEquals("ID " + shortId + " is used more than once", shortIds.size() - 1, copyOfShortIds.size());
        }

    }

}
