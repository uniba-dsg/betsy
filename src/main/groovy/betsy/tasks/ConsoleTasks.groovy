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
            FileTasks.assertExecutableFile(command)

            build(dir, command.toAbsolutePath().toString())
        }

        public String toString() {
            "${dir} \$ ${command} ${values.join(" ")}"
        }
    }

    private static final Logger log = Logger.getLogger(ConsoleTasks.class)

    public static void executeOnWindows(CliCommand cliCommand) {
        executeOnWindowsWithErrorOption(cliCommand, new HashMap<>(), true)
    }

    public static void executeOnWindows(CliCommand cliCommand, HashMap<String, String> environment) {
        executeOnWindowsWithErrorOption(cliCommand, environment, true)
    }

    private
    static void executeOnWindowsWithErrorOption(CliCommand cliCommand, HashMap<String, String> environment, boolean failOnError) {
        log.info("Executing on windows $cliCommand")

        FileTasks.assertDirectory(cliCommand.dir)

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

    public static void executeOnWindowsAndIgnoreError(CliCommand cliCommand) {
        executeOnWindowsWithErrorOption(cliCommand, new HashMap<>(), false)
    }

    public static void executeOnUnix(CliCommand cliCommand) {
        log.info("Executing on unix $cliCommand")

        AntUtil.builder().exec(executable: cliCommand.command, failOnError: "true", osfamily: "unix", dir: cliCommand.dir) {
            for (String value : cliCommand.values) {
                arg(value: value)
            }
        }
    }

}
