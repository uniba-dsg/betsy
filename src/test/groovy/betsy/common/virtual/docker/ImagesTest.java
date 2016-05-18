package betsy.common.virtual.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ImagesTest {

    private DockerMachine dockerMachine;

    @Before
    public void setUp() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
    }

    @After
    public void tearDown() throws Exception {
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void build() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            Images.remove(dockerMachine, betsyImage.get());
        }
        int size = Images.getAll(dockerMachine).size();
        betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy"));
        assertEquals("The method should return two images.", ++size, Images.getAll(dockerMachine).size());

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
    }

    @Test
    public void buildEngine() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy"));
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        boolean engineImageWasCreated = false;
        if (engineImage.isPresent()) {
            engineImageWasCreated = true;
            Images.remove(dockerMachine, engineImage.get());
        }

        int size = Images.getAll(dockerMachine).size();
        engineImage = Optional.ofNullable(Images.buildEngine(dockerMachine, Paths.get("docker/image/engine").toAbsolutePath(), "ode__1_3_5"));
        assertEquals("The method should return three images.", ++size, Images.getAll(dockerMachine).size());

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(dockerMachine, engineImage.get());
        }

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
    }

    @Test
    public void remove() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
        Images.remove(dockerMachine, betsyImage.get());
        assertNull("The image should be null.", Images.getAll(dockerMachine).get(betsyImage.get().getName()));

        if (betsyImageWasCreated) {
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
    }

    @Test
    public void getAll() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            Images.remove(dockerMachine, betsyImage.get());
        }
        int size = Images.getAll(dockerMachine).size();
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");

        assertEquals("The method should return the same size.", ++size, Images.getAll(dockerMachine).size());

        if (betsyImageWasCreated) {
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
    }

    @Test
    public void removeAll() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = false;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy"));
        }
        ArrayList<Image> images = new ArrayList<>();
        images.add(betsyImage.get());

        Images.removeAll(dockerMachine, images);
        assertNull("The image should be null.", Images.getAll(dockerMachine).get(betsyImage.get().getName()));

        if (betsyImageWasCreated) {
            Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        }
    }
}