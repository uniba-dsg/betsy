package betsy.common.tasks;

import betsy.common.config.Configuration;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

public class ConsoleTasks {
    private static final Logger log = Logger.getLogger(ConsoleTasks.class);
    public static final String WINDOWS = "windows";
    public static final String UNIX = "unix";

    public static void executeOnWindows(CliCommand cliCommand) {
        execute(WINDOWS, cliCommand, true, Collections.emptyMap());
    }

    public static void executeOnWindows(CliCommand cliCommand, Map<String, String> environment) {
        execute(WINDOWS, cliCommand, true, environment);
    }

    private static void execute(String osfamily, CliCommand cliCommand, boolean failOnError, Map<String, String> environment) {
        log.info("Executing on " + osfamily + " " + cliCommand);

        FileTasks.assertDirectory(cliCommand.getDir());

        if (WINDOWS.equals(osfamily)) {
            internalExecuteOnWindows(failOnError, cliCommand, environment);
        } else {
            internalExecuteOnUnix(failOnError, cliCommand, environment);
        }
    }

    private static void internalExecuteOnWindows(boolean failOnError, final CliCommand cliCommand, Map<String, String> environment) {
        ExecTask execute = new ExecTask();

        execute.setOsFamily(WINDOWS);
        execute.setExecutable("cmd");
        execute.setFailonerror(failOnError);
        execute.setDir(cliCommand.getDir().toFile());

        execute.createArg().setValue("/c");
        execute.createArg().setValue(cliCommand.getCommand());

        for (String value : cliCommand.getValues()) {
            execute.createArg().setValue(value);
        }

        for (Map.Entry<String, String> entry : environment.entrySet()) {
            Environment.Variable variable = new Environment.Variable();
            variable.setKey(entry.getKey());
            variable.setValue(entry.getValue());
            execute.addEnv(variable);
        }

        execute.setTaskName("exec");
        execute.setProject(AntUtil.builder().getProject());

        execute.execute();
    }

    private static void internalExecuteOnUnix(boolean failOnError, final CliCommand cliCommand, Map<String, String> environment) {
        ExecTask execute = new ExecTask();

        execute.setOsFamily(UNIX);
        execute.setExecutable(cliCommand.getCommand());
        execute.setFailonerror(failOnError);
        execute.setDir(cliCommand.getDir().toFile());

        for (String value : cliCommand.getValues()) {
            execute.createArg().setValue(value);
        }

        for (Map.Entry<String, String> entry : environment.entrySet()) {
            Environment.Variable variable = new Environment.Variable();
            variable.setKey(entry.getKey());
            variable.setValue(entry.getValue());
            execute.addEnv(variable);
        }

        execute.setTaskName("exec");
        execute.setProject(AntUtil.builder().getProject());

        execute.execute();
    }

    public static void executeOnWindowsAndIgnoreError(CliCommand cliCommand) {
        execute(WINDOWS, cliCommand, false, Collections.emptyMap());
    }

    public static void executeOnWindowsAndIgnoreError(CliCommand cliCommand, Map<String, String> environment) {
        execute(WINDOWS, cliCommand, false, environment);
    }

    public static void executeOnUnix(CliCommand cliCommand) {
        execute(UNIX, cliCommand, true, Collections.emptyMap());
    }

    public static void executeOnUnixAndIgnoreError(CliCommand cliCommand) {
        execute(UNIX, cliCommand, false, Collections.emptyMap());
    }

    public static void executeOnUnixAndIgnoreError(CliCommand cliCommand, Map<String, String> environment) {
        execute(UNIX, cliCommand, false, environment);
    }

    public static void setupAnt(Path antPath) {
        Path antBinFolder = antPath.toAbsolutePath();
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(antBinFolder, "chmod").values("+x", antBinFolder.resolve("ant").toString()));
    }

    /**
     * Represents "dir $ command values[0] values[1] ..."
     */
    public static class CliCommand {
        private static final Logger log = Logger.getLogger(CliCommand.class);
        private Path dir;
        private String command;
        private String[] values;

        public static CliCommand build(String command) {
            return build(Paths.get("."), command);
        }

        /**
         * Execute currentdir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path command) {
            return build(Paths.get("."), command);
        }

        /**
         * Execute dir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path dir, String command) {
            CliCommand cmd = new CliCommand();
            cmd.setDir(dir.toAbsolutePath());
            cmd.setCommand(command);
            cmd.setValues(new String[0]);

            return cmd;
        }

        /**
         * Execute dir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path dir, Path command) {
            try {
                FileTasks.assertExecutableFile(command);
            } catch (IllegalArgumentException e) {
                log.warn("executable file not found: " + e.getMessage());
            }

            return build(dir, command.toAbsolutePath().toString());
        }

        public CliCommand values(String... args) {
            values = args;

            return this;
        }

        public String toString() {
            return dir.toString() + " $ " + command + " " + getSpaceSeparatedValues();
        }

        private String getSpaceSeparatedValues() {
            StringJoiner joiner = new StringJoiner(" ");
            for (String value : values) {
                joiner.add(value);
            }

            return joiner.toString();
        }

        public Path getDir() {
            return dir;
        }

        public void setDir(Path dir) {
            this.dir = dir;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String[] getValues() {
            return values;
        }

        public void setValues(String[] values) {
            this.values = values;
        }
    }
}
