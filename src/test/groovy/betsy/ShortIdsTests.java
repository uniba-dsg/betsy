package betsy;

import betsy.data.BetsyProcess;
import configuration.ProcessRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ShortIdsTests {

    @Test
    public void testUniquenessOfShortIds() {
        List<String> shortIds = new LinkedList<>();

        ProcessRepository processRepository = new ProcessRepository();
        List<BetsyProcess> processed = processRepository.getByName("ALL");
        for(BetsyProcess process : processed){
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
