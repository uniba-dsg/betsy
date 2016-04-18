package betsy.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitUil {

    public static String getGitCommit() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("git rev-parse HEAD");

            try (BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                String line = input.readLine();
                System.out.println("GIT COMMIT: " + line);
                if (line.startsWith("fatal:")) {
                    //no commit hash
                    return "";
                } else {
                    return line;
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
}
