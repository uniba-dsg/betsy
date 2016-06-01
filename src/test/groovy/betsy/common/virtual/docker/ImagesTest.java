package betsy.common.virtual.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
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
    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");


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
        int size = Images.getAll(dockerMachine).size();
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> ubuntuImage;
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(dockerMachine, betsyImage.get());
            size--;
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }else{
                size++;
            }
        }

        betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy"));
        assertEquals("The method should return two images.", ++size, Images.getAll(dockerMachine).size());

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(dockerMachine, ubuntuImage.get());
        }
    }

    @Test
    public void buildEngine() throws Exception {
        int size = Images.getAll(dockerMachine).size();

        boolean engineImageWasCreated = false;
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (engineImage.isPresent()) {
            engineImageWasCreated = true;
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(dockerMachine, engineImage.get());
        }else{
            if (betsyImage.isPresent()) {
                betsyImageWasCreated = true;
                ubuntuWasCreated = true;
            }else{
                if (ubuntuImage.isPresent()) {
                    ubuntuWasCreated = true;
                }else{
                    size++;
                }
                size++;
                betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy"));
            }
        }

        engineImage = Optional.ofNullable(Images.buildEngine(dockerMachine, images.resolve("engine").toAbsolutePath(), "ode__1_3_5"));
        assertEquals("The sizes should be equal", ++size, Images.getAll(dockerMachine).size());

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(dockerMachine, engineImage.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(dockerMachine, ubuntuImage.get());
        }
    }

    @Test
    public void remove() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
        }else{
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }

        Images.remove(dockerMachine, betsyImage.get());
        assertNull("The image should be null.", Images.getAll(dockerMachine).get(betsyImage.get().getName()));

        if (!betsyImage.isPresent() && betsyImageWasCreated){
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }else if(betsyImage.isPresent() && !betsyImageWasCreated){
            Images.remove(dockerMachine, betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(dockerMachine, ubuntuImage.get());
        }
    }

    @Test
    public void getAll() throws Exception {
        int size = Images.getAll(dockerMachine).size();
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> ubuntuImage;
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(dockerMachine, betsyImage.get());
            size--;
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }else{
                size++;
            }
        }

        Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        assertEquals("The method should return the same size.", ++size, Images.getAll(dockerMachine).size());

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(dockerMachine, betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(dockerMachine, ubuntuImage.get());
        }
    }

    @Test
    public void removeAll() throws Exception {
        boolean ubuntuWasCreated = true;
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
        if (!ubuntuImage.isPresent()) {
            ubuntuWasCreated = false;
        }
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        ArrayList<Image> imageList = new ArrayList<>();
        imageList.add(betsyImage.get());

        Images.removeAll(dockerMachine, imageList);
        assertNull("The image should be null.", Images.getAll(dockerMachine).get(betsyImage.get().getName()));

        if (!betsyImage.isPresent() && betsyImageWasCreated) {
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (!ubuntuImage.isPresent() && ubuntuWasCreated) {
                Tasks.doDockerTask(dockerMachine, "pull", "ubuntu");
            }
        }
    }
}