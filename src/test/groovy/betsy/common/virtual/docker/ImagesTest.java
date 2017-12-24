package betsy.common.virtual.docker;

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

    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");


    @Test
    public void build() throws Exception {
        int size = Images.getAll().size();
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> ubuntuImage;
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(betsyImage.get());
            size--;
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }else{
                size++;
            }
        }

        betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        assertEquals("The method should return two images.", ++size, Images.getAll().size());

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(ubuntuImage.get());
        }
    }

    @Test
    public void buildEngine() throws Exception {
        int size = Images.getAll().size();

        boolean engineImageWasCreated = false;
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode135"));
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (engineImage.isPresent()) {
            engineImageWasCreated = true;
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(engineImage.get());
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
                betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
            }
        }

        engineImage = Optional.ofNullable(Images.buildEngine(images.resolve("engine").toAbsolutePath(), "ode__1_3_5"));
        assertEquals("The sizes should be equal.", ++size, Images.getAll().size());

        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(engineImage.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(ubuntuImage.get());
        }
    }

    @Test
    public void remove() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
        }else{
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }
        }
        betsyImage = Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        Images.remove(betsyImage.get());
        assertNull("The image should be null.", Images.getAll().get(betsyImage.get().getName()));

        if (!betsyImage.isPresent() && betsyImageWasCreated){
            Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        }else if(betsyImage.isPresent() && !betsyImageWasCreated){
            Images.remove(betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(ubuntuImage.get());
        }
    }

    @Test
    public void getAll() throws Exception {
        int size = Images.getAll().size();
        boolean betsyImageWasCreated = false;
        boolean ubuntuWasCreated = false;

        java.util.Optional<Image> ubuntuImage;
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        if (betsyImage.isPresent()) {
            betsyImageWasCreated = true;
            ubuntuWasCreated = true;
            Images.remove(betsyImage.get());
            size--;
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                ubuntuWasCreated = true;
            }else{
                size++;
            }
        }

        Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");

        assertEquals("The method should return the same size.", ++size, Images.getAll().size());

        if (betsyImage.isPresent() && !betsyImageWasCreated) {
            Images.remove(betsyImage.get());
        }
        ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (ubuntuImage.isPresent() && !ubuntuWasCreated) {
            Images.remove(ubuntuImage.get());
        }
    }

    @Test
    public void removeAll() throws Exception {
        boolean ubuntuWasCreated = true;
        java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
        if (!ubuntuImage.isPresent()) {
            ubuntuWasCreated = false;
        }
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasCreated = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasCreated = false;
            betsyImage = java.util.Optional.ofNullable(Images.build(images.resolve("betsy").toAbsolutePath(), "betsy"));
        }
        ArrayList<Image> imageList = new ArrayList<>();
        imageList.add(betsyImage.get());

        Images.removeAll(imageList);
        assertNull("The image should be null.", Images.getAll().get(betsyImage.get().getName()));

        if (!betsyImage.isPresent() && betsyImageWasCreated) {
            Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        }else{
            ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
            if (!ubuntuImage.isPresent() && ubuntuWasCreated) {
                Tasks.doDockerTask("pull", "ubuntu");
            }
        }
    }
}