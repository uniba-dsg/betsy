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

    /**
     * This method creates a {@link Image} from a dockerfile for an engine.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param imagePath     The path of the dockerfile.
     * @param engineName    The name of the {@link Engine}.
     * @return Returns the created {@link Image}.
     */
    public static Image buildEngine(DockerMachine dockerMachine, Path imagePath, String engineName) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(imagePath, "The imagePath can't be null.");
        Objects.requireNonNull(engineName, "The engineName can't be null.");
        Image image = null;
        String[] cmds = {imagePath.toString(), engineName.replace("_", ""), engineName};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doEngineImageTask(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Successfully built")) {
                    String id = nextLine.substring(nextLine.lastIndexOf(" "));
                    image = new Image(id, engineName.replace("_", ""));
                    dockerMachine.addImage(image);
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
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param imagePath     The path of the dockerfile.
     * @param name          the name of the image.
     * @return Returns the created {@link Image}.
     */
    public static Image build(DockerMachine dockerMachine, Path imagePath, String name) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(imagePath, "The imagePath can't be null.");
        Objects.requireNonNull(name, "The name can't be null.");
        Image image = null;
        String[] cmds = {imagePath.toString(), name};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doImageTask(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Successfully built")) {
                    String id = nextLine.substring(nextLine.lastIndexOf(" "));
                    image = new Image(id, name);
                    dockerMachine.addImage(image);
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
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param image         The {@link Image} to remove.
     */
    public static void remove(DockerMachine dockerMachine, Image image) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(image, "The image can't be null.");
        String[] cmds = {"rmi", image.getName()};
        dockerMachine.removeImage(image);
        Tasks.doDockerTask(dockerMachine, cmds);
    }

    /**
     * This method removes the given {@link Image}.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param images         The images to remove.
     */
    public static void removeAll(DockerMachine dockerMachine, List<Image> images) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(images, "The images can't be null.");
        images.forEach(e -> {
            String[] cmds = {"rmi", e.getName()};
            Tasks.doDockerTask(dockerMachine, cmds);
            dockerMachine.removeImage(e);
        });

    }

    /**
     * This method returns all images.
     *
     * @param dockerMachine The do
     * @return A {@link HashMap} with the name of the image and the image.
     */
    public static HashMap<String, Image> getAll(DockerMachine dockerMachine) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        HashMap<String, Image> images = dockerMachine.getImages();
        String[] cmds = {"images"};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
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
                    images.putIfAbsent(image.getName(), image);
                }
            }
        } else {
            throw new DockerException("Executing the task 'images' failed.");
        }
        return images;
    }
}
