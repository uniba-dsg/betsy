package betsy.tasks

import ant.tasks.AntUtil
import org.apache.log4j.Logger

import java.nio.file.Path
import java.nio.file.Paths

class ConsoleTasks {

    /**
     * Represents "dir $ command values[0] values[1] ..."
     */
    public static class CliCommand {

        private static final Logger log = Logger.getLogger(CliCommand.class);

        Path dir
        String command
        String[] values

        public CliCommand values(String... args) {
            values = args

            this
        }


        public static CliCommand build(String command) {
            build(Paths.get("."), command)
        }

        /**
         * Execute currentdir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path command) {
            build(Paths.get("."), command)
        }

        /**
         * Execute dir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path dir, String command) {
            new CliCommand(dir: dir.toAbsolutePath(), command: command, values: new String[0])
        }

        /**
         * Execute dir $ command args[0] args[1] ...
         */
        public static CliCommand build(Path dir, Path command) {
            try {
                FileTasks.assertExecutableFile(command)
            } catch (IllegalArgumentException e) {
                log.warn("executable file not found: ${e.message}")
            }

            build(dir, command.toAbsolutePath().toString())
        }

        public String toString() {
            "${dir} \$ ${command} ${values.join(" ")}"
        }
    }

    private static final Logger log = Logger.getLogger(ConsoleTasks.class)

    public static void executeOnWindows(CliCommand cliCommand) {
        execute("windows", cliCommand, true, new HashMap<>())
    }

    public static void executeOnWindows(CliCommand cliCommand, HashMap<String, String> environment) {
        execute("windows", cliCommand, true, environment)
    }

 private static void execute(String osfamily, CliCommand cliCommand, boolean failOnError, environment) {
        log.info("Executing on $osfamily $cliCommand")

        FileTasks.assertDirectory(cliCommand.dir)

        if ("windows".equals(osfamily)) {
            internalExecuteOnWindows(failOnError, cliCommand, environment)
        } else {
            internalExecuteOnUnix(failOnError, cliCommand, environment)
        }

    }

    private
    static Object internalExecuteOnWindows(boolean failOnError, CliCommand cliCommand, environment) {
        AntUtil.builder().exec(executable: "cmd", failOnError: failOnError, osfamily: "windows", dir: cliCommand.dir) {
            arg(value: "/c")
            arg(value: cliCommand.command)
            for (String value : cliCommand.values) {
                arg(value: value)
            }
            for (Map.Entry<String, String> entry : environment.entrySet()) {
                env key: entry.key, path: entry.value
            }
        }
    }

    private
    static Object internalExecuteOnUnix(boolean failOnError, CliCommand cliCommand, environment) {
        AntUtil.builder().exec(executable: cliCommand.command, failOnError: failOnError, osfamily: "unix", dir: cliCommand.dir) {
            for (String value : cliCommand.values) {
                arg(value: value)
            }
            for (Map.Entry<String, String> entry : environment.entrySet()) {
                env key: entry.key, path: entry.value
            }
        }
    }

    public static void executeOnWindowsAndIgnoreError(CliCommand cliCommand) {
        execute("windows", cliCommand, false, new HashMap<>())
    }

    public static void executeOnUnix(CliCommand cliCommand) {
        execute("unix", cliCommand, true, new HashMap<>())
    }

    public static void executeOnUnixAndIgnoreError(CliCommand cliCommand) {
        execute("unix", cliCommand, false, new HashMap<>())
    }

}
