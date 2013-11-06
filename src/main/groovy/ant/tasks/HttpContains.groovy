package ant.tasks

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Project
import org.apache.tools.ant.ProjectComponent
import org.apache.tools.ant.taskdefs.condition.Condition

/**
 * Condition to wait for a HTTP request to succeed.
 *
 * @since Ant 1.5
 *
 * Referring to: <br>
 * http://stackoverflow.com/questions/2160514/how-can-i-get-ant-to-load-and-check-a-url-resource-in-a-waitfor-task
 */
public class HttpContains extends ProjectComponent implements Condition {

    private String spec = null;
    private String contains = null;
    private int errorsBeginAt = 400;

    /**
     * Set the url attribute
     *
     * @param url
     *            the url of the request
     */
    public void setUrl(String url) {
        spec = url;
    }

    public void setContains(String s) {
        contains = s;
    }

    /**
     * @return true if the HTTP request succeeds
     * @exception BuildException
     *                if an error occurs
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
                    log("Result code for " + spec + " was " + code,
                            Project.MSG_VERBOSE);
                    if (code > 0 && code < errorsBeginAt) {
                        return evalContents(url);

                    } else {
                        return false;
                    }
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

            StringBuffer buffer = new StringBuffer();
            byte[] buf = new byte[1024];
            int amount;
            InputStream input = mUrl.openStream();

            while ((amount = input.read(buf)) > 0) {
                buffer.append(new String(buf, 0, amount));
            }
            input.close();
            contents = buffer.toString();

            log("Result code for " + contents, Project.MSG_VERBOSE);

            if (contents.indexOf(contains) > -1) {

                log("Result code for " + contents.indexOf(contains),
                        Project.MSG_VERBOSE);
                return true;

            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log("Result code for " + e.toString(), Project.MSG_VERBOSE);

            return false;
        }
    }
}
