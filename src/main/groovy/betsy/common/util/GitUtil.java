package betsy.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitUtil {

    public static String getGitCommit() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("git rev-parse HEAD");

            try (BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                String line = input.readLine();
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
