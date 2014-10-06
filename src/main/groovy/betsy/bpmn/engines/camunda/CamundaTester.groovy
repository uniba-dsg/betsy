package betsy.bpmn.engines.camunda

import ant.tasks.AntUtil
import betsy.bpmn.model.BPMNTestCase
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks
import groovy.io.FileType
import org.codehaus.groovy.tools.RootLoader
import org.json.JSONArray
import org.json.JSONObject

import java.nio.file.Path

class CamundaTester {

    private static final AntBuilder ant = AntUtil.builder()

    BPMNTestCase testCase
    String restURL
    Path reportPath
    Path testBin
    Path testSrc
    String key
    Path logDir

    /**
     * runs a single test
     */
    void runTest(){
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        //look up log for deployment error caused by unsupported activity type
        String unsupportedMessage = null
        File dir = new File(logDir.toString())
        dir.eachFile(FileType.FILES) { file ->
            if(file.name.contains("catalina")){
                BufferedReader br = new BufferedReader(new FileReader(file))
                br.eachLine(150){ line ->
                    if(line.contains("Ignoring unsupported activity type")){
                        unsupportedMessage = line
                    }
                    if(line.startsWith("org.camunda.bpm.engine.ProcessEngineException")){
                        unsupportedMessage = line
                    }
                }
            }
        }
        if(unsupportedMessage != null){
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir.resolve("..").normalize()}/bin/log" + testCase.number + ".txt"));
                bw.writeLine(unsupportedMessage);
                bw.close();
            }catch(IOException e){}
        }else{
            //first request to get id
            JSONObject response = get(restURL + "/process-definition?key=${key}")
            String id = response.get("id")

            //assembling JSONObject for second request
            JSONObject requestBody = new JSONObject()
            requestBody.put("variables", testCase.variables)
            requestBody.put("businessKey", "key-${key}")


            if(testCase.selfStarting){
                //just wait until process starts itself
                WaitTasks.sleep(testCase.delay);
            }else{
                //second request to start process using id and Json to get the process instance id
                post(restURL + "/process-definition/${id}/start", requestBody)
                if(testCase.delay != 0){
                    WaitTasks.sleep(testCase.delay)
                }
            }
        }

        //setup path to 'tools.jar' for the javac ant task
        String javaHome = System.getProperty("java.home")
        if(javaHome.endsWith("jre")){
            javaHome = javaHome.substring(0, javaHome.length() - 4)
        }
        RootLoader rl = (RootLoader) this.class.classLoader.getRootLoader()
        if(rl == null){
            Thread.currentThread().getContextClassLoader().addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }else{
            rl.addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }

        String systemClasspath = System.getProperty('java.class.path')

        // compile test sources
        ant.javac(srcdir: testSrc, destdir: testBin, includeantruntime: false) {
            classpath{
                pathelement(path: systemClasspath)
            }
        }

        //look up log for runtime exception if process could be started
        if (unsupportedMessage == null){
            boolean runtimeExceptionFound = false
            dir.eachFile(FileType.FILES) { file ->
                if(file.name.contains("catalina")){
                    BufferedReader br = new BufferedReader(new FileReader(file))
                    br.eachLine(170){ line ->
                        if(line.startsWith("org.camunda.bpm.engine.ProcessEngineException")){
                            runtimeExceptionFound = true
                        }
                        //special case for error end event
                        if(line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")){
                            try{
                                BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir.resolve("..").normalize()}/bin/log" + testCase.number + ".txt", true));
                                bw.append("thrownErrorEvent");
                                bw.close();
                            }catch(IOException e){}
                        }
                    }
                }
            }
            if(runtimeExceptionFound){
                try{
                    BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir.resolve("..").normalize()}/bin/log" + testCase.number + ".txt", true));
                    bw.append("runtimeException");
                    bw.close();
                }catch(IOException e){}
            }
        }

        //start test
        ant.junit(printsummary: "on", fork: "true", haltonfailure: "no"){
            classpath{
                pathelement(path: systemClasspath)
                pathelement(location: testBin)
            }
            formatter(type: "xml")
            batchtest(todir: reportPath){
                fileset(dir: testSrc){
                    include(name: "**/*.java")
                }
            }
        }

    }

    /**
     * GET request to Camunda REST Interface
     * @param url Camunda REST URL
     * @return JSONObject with response data
     */
    JSONObject get(String url){
        try {
            URL requestUrl = new URL(url);
            URLConnection conn = requestUrl.openConnection();
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection http = (HttpURLConnection) conn;
                int code = http.getResponseCode();
                if (code > 0 && code < 400) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    String jsonResponse = sb.toString();
                    JSONArray jsonArray = new JSONArray(jsonResponse)
                    JSONObject obj = (JSONObject)jsonArray.get(0)

                    return obj
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException ignored) {
            ignored.printStackTrace()
        }

        return null
    }

    /**
     * POST request to Camunda REST Interface
     * @param url Camunda REST URL
     * @param requestBody JSONObject with request data to be uploaded
     * @return JSONObject with response data
     */
    JSONObject post(String url, JSONObject requestBody){

        try {

            URL startUrl = new URL(url);
            HttpURLConnection startCall = startUrl.openConnection();
            startCall.setRequestMethod("POST")
            startCall.setDoOutput(true)
            startCall.setDoInput(true)
            startCall.setRequestProperty("Content-Type", "application/json")
            //Send request
            DataOutputStream wr = new DataOutputStream (
                    startCall.getOutputStream ());
            wr.writeBytes (requestBody.toString());
            wr.flush ();
            wr.close ();

            //get response
            BufferedReader br = new BufferedReader(new InputStreamReader(startCall.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            String jsonResponse = sb.toString();
            JSONObject response = new JSONObject(jsonResponse)

            return response

        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException ignored) {
            ignored.printStackTrace()
        }

        return null

    }

}
