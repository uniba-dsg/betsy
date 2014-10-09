package ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Condition to wait for a HTTP request to succeed.
 *
 * @since Ant 1.5
 * <p>
 * Referring to: <br>
 * http://stackoverflow.com/questions/2160514/how-can-i-get-ant-to-load-and-check-a-url-resource-in-a-waitfor-task
 */
public class HttpContains extends ProjectComponent implements Condition {
    /**
     * Set the url attribute
     *
     * @param url the url of the request
     */
    public void setUrl(String url) {
        spec = url;
    }

    public void setContains(String s) {
        contains = s;
    }

    /**
     * @return true if the HTTP request succeeds
     * @throws BuildException if an error occurs
     */
    public boolean eval() throws BuildException {
        if (spec == null) {
            throw new BuildException("No url specified in http condition");
        }

        log("Checking for " + spec, Project.MSG_VERBOSE);
        try {
            URL url = new URL(spec);
            try {
                URLConnection conn = url.openConnection();
                if (conn instanceof HttpURLConnection) {
                    HttpURLConnection http = (HttpURLConnection) conn;
                    int code = http.getResponseCode();
                    log("Result code for " + spec + " was " + code, Project.MSG_VERBOSE);
                    int errorsBeginAt = 400;
                    return code > 0 && code < errorsBeginAt && evalContents(url);
                }

            } catch (IOException ignored) {
                return false;
            }

        } catch (MalformedURLException e) {
            throw new BuildException("Badly formed URL: " + spec, e);
        }

        return true;
    }

    public boolean evalContents(URL mUrl) {
        try {

            String contents;

            StringBuilder buffer = new StringBuilder();
            byte[] buf = new byte[1024];
            int amount;
            InputStream input = mUrl.openStream();

            while ((amount = input.read(buf)) > 0) {
                buffer.append(new String(buf, 0, amount));
            }

            input.close();
            contents = buffer.toString();

            log("Result code for " + contents, Project.MSG_VERBOSE);

            if (contents.contains(contains)) {
                log("Result code for " + contents.indexOf(contains), Project.MSG_VERBOSE);
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log("Result code for " + e.toString(), Project.MSG_VERBOSE);

            return false;
        }

    }

    private String spec = null;
    private String contains = null;
}
