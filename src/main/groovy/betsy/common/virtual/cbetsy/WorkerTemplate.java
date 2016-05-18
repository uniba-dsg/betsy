package betsy.common.virtual.cbetsy;

import betsy.common.model.input.EngineIndependentProcess;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a WorkerTemplate with the information to start a {@link Worker}.
 *
 */
public class WorkerTemplate {

    private final EngineIndependentProcess process;
    private final DockerEngine dockerEngine;

    /**
     *  @param process The process to execute.
     * @param dockerEngine The engine to test.
     */
    public WorkerTemplate(EngineIndependentProcess process, DockerEngine dockerEngine){
        this.process = process;
        this.dockerEngine = dockerEngine;
    }

    /**
     *
     * This method returns the process this {@link WorkerTemplate}.
     *
     * @return The {@link Process} to execute.
     */
    public EngineIndependentProcess getProcess() {
        return process;
    }

    public DockerEngine getDockerEngine() {
        return dockerEngine;
    }

    /**
     * This method returns the command to execute with betsy.
     *
     * @return Returns the command as array.
     */
    public String[] getCmd(){
        String[] cmds = new String[3];
        if(dockerEngine.getTypeOfEngine() == DockerEngine.TypeOfEngine.BPEL) {
            cmds[0] = "bpel";
        }else{
            cmds[0] = "bpmn";
        }
        cmds[1] = dockerEngine.getName();
        cmds[2] = process.getName();
        return  cmds;
    }

    /**
     *
     * Returns the id of this {@link WorkerTemplate}.
     *
     * @return Returns the id of the workerTemplate as {@link String}.
     */
    public String getID() {
        StringBuilder builder = new StringBuilder();
        builder.append(dockerEngine.getName().replace("_", ""));
        builder.append(process.getName().replace("_", ""));
        return builder.toString();
    }



}
