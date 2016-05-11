package betsy.common.virtual.docker;

import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a docker {@link Container}.
 *
 */
public class Container {

    private String id;
    private String name;
    private DockerMachine dockerMachine;

    private static final Logger LOGGER = Logger.getLogger(Container.class);

    /**
     *
     * @param id The id of the {@link Container}.
     * @param name The name of the {@link Container}.
     * @param dockerMachine The dockerMachine, where the {@link Container} is located.
     */
    public Container(String id, String name, DockerMachine dockerMachine) {
        this.id = id;
        this.name = name;
        this.dockerMachine = dockerMachine;
    }

    /**
     * A method to restart this container
     */
    public void restart() {
        String[] cmds = {"restart", name};
        Tasks.doDockerTask(dockerMachine, cmds);
    }

    /**
     * A method to start this container
     *
     * @param startSilent To start this {@link Container} without console output.
     */
    public void start(boolean startSilent) {
        if(startSilent){
            String[] cmds = {"start", name};
            Tasks.doDockerTask(dockerMachine, cmds);
        }else{
            String[] cmds = {"start", "-i", name};
            Scanner scanner = Tasks.doDockerTaskWithOutput(dockerMachine, cmds);
            while(scanner.hasNextLine()){
                LOGGER.info(scanner.nextLine());
            }
        }
    }

    /**
     * A method to stop this container
     */
    public void stop() {
        String[] cmds = {"stop", name};
        Tasks.doDockerTask(dockerMachine, cmds);
    }

    /**
     *
     * A method to run a command in this container.
     *
     * @param args The commandds to execute.
     */
    public void exec(String... args) {
        String[] commands = {"exec", "-d", name};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerTask(dockerMachine, cmds);
    }

    /**
     *
     * @return Returns the id of the {@link Container}.
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return Returns the name of the {@link Container}.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Returns the actual {@link Status} of the {@link Container}.
     */
    public Status getStatus() {
        String[] cmds = {"ps", "--all", "--filter", "\"name=" + name + "\""};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
        String outPut = "";
        int counter = 0;
        int begin = 0;
        int line = 0;
        if(scanner.isPresent()){
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                System.out.println(nextLine);
                if (nextLine.contains("CONTAINER ID")) {
                    begin = nextLine.indexOf("STATUS");
                    line = counter;
                }
                if (counter > line && line > 0) {
                    outPut = nextLine.substring(begin);
                    outPut = outPut.substring(0, outPut.indexOf(" "));
                }
                counter++;
            }
        }else{
            throw new DockerException("Executing the docker task 'ps' failed.");
        }
        if (outPut.contains("Created")) {
            return Status.CREATED;
        } else if (outPut.contains("Restarting")) {
            return Status.RESTARTING;
        } else if (outPut.contains("Paused")) {
            return Status.PAUSED;
        } else if (outPut.contains("Up")) {
            return Status.RUNNING;
        } else if (outPut.contains("Exited")) {
            return Status.EXITED;
        } else {
            return Status.DEAD;
        }
    }

    /**
     *
     * @return Returns the command of the {@link Container}.
     */
    public String getCmd() {
        String[] cmds = {"ps", "--all", "--filter", "\"name=" + name + "\""};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
        String cmd = "";
        int counter = 0;
        int begin = 0;
        if(scanner.isPresent()){
            while (scanner.get().hasNextLine() && counter < 28) {
                String nextLine = scanner.get().nextLine();
                if (counter == 26) {
                    begin = nextLine.indexOf("COMMAND");
                }
                if (counter == 27) {
                    cmd = nextLine.substring(begin + 1);
                    cmd = cmd.substring(0, cmd.indexOf("\""));
                }
                counter++;
            }
        }else{
            throw new DockerException("Executing the task 'ps' failed.");
        }
        return cmd;
    }

    /**
     *
     * This method copies a file from the container.
     *
     * @param filePath The path of the file to copy.
     * @param path The destination {@link Path} of the file.
     */
    public void copyFromContainer(Path filePath, Path path){
        String[] cmds = {"cp", name+ ":" + filePath, path.toString()};
        Tasks.doDockerTask(dockerMachine ,cmds);
    }

    /**
     *
     * This method copies a file to the container.
     *
     * @param filePath The path of file to copy.
     * @param path The destination {@link Path} of the file.
     */
    public void copyToContainer(Path filePath, Path path){
        String[] cmds = {"cp", filePath.toString(), name+ ":" + path.toString()};
        Tasks.doDockerTask(dockerMachine ,cmds);
    }

    public enum Status {
        CREATED, RESTARTING, RUNNING, PAUSED, EXITED, DEAD,
    }
}

