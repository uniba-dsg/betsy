package betsy.common.virtual.docker;

import betsy.common.analytics.model.Engine;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Images {

    private static final Logger LOGGER = Logger.getLogger(Images.class);
    private static  HashMap<String, Image> images = new HashMap<>();

    /**
     * This method creates a {@link Image} from a dockerfile for an engine.
     *
     * @param imagePath     The path of the dockerfile.
     * @param engineName    The name of the {@link Engine}.
     * @return Returns the created {@link Image}.
     */
    public static Image buildEngine(Path imagePath, String engineName) {
        Objects.requireNonNull(imagePath, "The imagePath can't be null.");
        Objects.requireNonNull(engineName, "The engineName can't be null.");
        Image image = null;
        String[] cmds = {imagePath.toString(), engineName.replace("_", ""), engineName};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doEngineImageTask(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Successfully built")) {
                    String id = nextLine.substring(nextLine.lastIndexOf(" "));
                    image = new Image(id, engineName.replace("_", ""));
                    images.put(image.getName(), image);
                }
            }
        } else {
            throw new DockerException("Executing the task 'build' failed.");
        }
        return image;
    }

    /**
     * This method creates a {@link Image} from a dockerfile.
     *
     * @param imagePath     The path of the dockerfile.
     * @param name          the name of the image.
     * @return Returns the created {@link Image}.
     */
    public static Image build(Path imagePath, String name) {
        Objects.requireNonNull(imagePath, "The imagePath can't be null.");
        Objects.requireNonNull(name, "The name can't be null.");
        Image image = null;
        String[] cmds = {"build", "--tag", name, imagePath.toString()};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Successfully built")) {
                    String id = nextLine.substring(nextLine.lastIndexOf(" "));
                    image = new Image(id, name);
                    images.put(image.getName(), image);
                }
            }
        } else {
            throw new DockerException("Executing the task 'build' failed.");
        }
        return image;
    }

    /**
     * This method removes the given {@link Image}.
     *
     * @param image         The {@link Image} to remove.
     */
    public static void remove(Image image) {
        Objects.requireNonNull(image, "The image can't be null.");
        String[] cmds = {"rmi", "-f", image.getName()};
        Tasks.doDockerTask(cmds);
        images.remove(image.getName());
    }

    /**
     * This method removes the given {@link Image}.
     *
     * @param images         The images to remove.
     */
    public static void removeAll(List<Image> images) {
        Objects.requireNonNull(images, "The images can't be null.");
        images.forEach(Images::remove);

    }

    /**
     * This method returns all images.
     *
     * @return A {@link HashMap} with the name of the image and the image.
     */
    public static HashMap<String, Image> getAll() {
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput("images"));
        boolean containsRepository = false;
        int beginName = 0;
        int beginID = 0;
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                if (nextLine.contains("REPOSITORY")) {
                    beginName = nextLine.indexOf("REPOSITORY");
                    beginID = nextLine.indexOf("IMAGE ID");
                    containsRepository = true;
                }else if (containsRepository) {
                    String id = nextLine.substring(beginID);
                    id = id.substring(0, id.indexOf(" "));
                    String name = nextLine.substring(beginName);
                    name = name.substring(0, name.indexOf(" "));
                    Image image = new Image(id, name);
                    Optional<Image> oldImage = Optional.ofNullable(images.get(name));
                    if(!oldImage.isPresent()){
                        images.put(image.getName(), image);
                    }
                }
            }
        } else {
            throw new DockerException("Executing the task 'images' failed.");
        }
        return images;
    }
}
