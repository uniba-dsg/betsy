package betsy;

import betsy.data.engines.Engine;
import betsy.data.engines.ode.OdeEngine;
import betsy.repositories.EngineRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EngineRepositoryTests {

    @Test
    public void testRepo() {
        OdeEngine ode = new OdeEngine();
        Assert.assertEquals("ode", ode.getName());
        Assert.assertEquals("ode", ode.toString());

        EngineRepository repo = new EngineRepository();
        List<Engine> all = repo.getByName("ALL");
        Assert.assertNotNull(all);
        Assert.assertEquals(18, all.size());
    }
}
