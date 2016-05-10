package betsy.common.virtual;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.AbstractProcess;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a WorkerTemplate with the information to start a {@link Worker}.
 *
 */
public class WorkerTemplate {

    private AbstractProcess process;
    private Optional<AbstractBPMNEngine> bpmnEngine;
    private Optional<AbstractBPELEngine> bpelEngine;
    private long time = 0;
    private double memory = 0;

    /**
     *
     * @param process The process to execute.
     * @param bpmnEngine The engine to test.
     */
    public WorkerTemplate(AbstractProcess process, AbstractBPMNEngine bpmnEngine){
        this.process = process;
        this.bpmnEngine = Optional.of(bpmnEngine);
        this.bpelEngine = Optional.empty();
    }

    /**
     *
     * @param process The process to execute.
     * @param bpelEngine The engine to test.
     */
    public WorkerTemplate(AbstractProcess process, AbstractBPELEngine bpelEngine){
        this.process = process;
        this.bpelEngine = Optional.of(bpelEngine);
        this.bpmnEngine = Optional.empty();
    }

    /**
     *
     * This method returns the process this {@link WorkerTemplate}.
     *
     * @return The {@link Process} to execute.
     */
    public AbstractProcess getProcess() {
        return process;
    }

    /**
     *
     * This method returns the BPELEngine of this {@link WorkerTemplate}.
     *
     * @return The {@link AbstractBPELEngine} to execute the {@link Process} on.
     */
    public AbstractBPELEngine getBPELEngine() {
        if(bpelEngine.isPresent()){
            return bpelEngine.get();
        }else{
            throw new NoSuchElementException();
        }
    }

    /**
     *
     * This method returns the BPMNEngine of this {@link WorkerTemplate}.
     *
     * @return The {@link AbstractBPMNEngine} to execute the {@link Process} on.
     */
    public AbstractBPMNEngine getBPMNEngine() {
        if(bpmnEngine.isPresent()){
            return bpmnEngine.get();
        }else{
            throw new NoSuchElementException();
        }
    }

    /**
     * This method returns the name of this {@link WorkerTemplate}.
     *
     * @return The name of the engine.
     */
    public String getEngineName(){
        if(bpelEngine.isPresent()){
            return bpelEngine.get().getName();
        }else{
            return bpmnEngine.get().getName();
        }
    }

    /**
     *
     * This method returns the duration of this {@link WorkerTemplate}.
     *
     * @return The duration of execution.
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * With this method it is possible to change the duration of the workerTemplate.
     *
     * @param time The duration of execution.
     */
    public void setTime(long time) {
        this.time = time;
    }


    /**
     * This method returns the command to execute with betsy.
     *
     * @return Returns the command as array.
     */
    public String[] getCmd(){
        String[] cmds = new String[3];
        bpmnEngine.ifPresent(e -> cmds[0] = "bpmn");
        bpmnEngine.ifPresent(e -> cmds[1] = bpmnEngine.get().getName());
        bpelEngine.ifPresent(e -> cmds[0] = "bpel");
        bpelEngine.ifPresent(e -> cmds[1] = bpelEngine.get().getName());
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
        bpmnEngine.ifPresent(e -> builder.append(bpmnEngine.get().getName().replace("_", "")));
        bpelEngine.ifPresent(e -> builder.append(bpelEngine.get().getName().replace("_", "")));
        builder.append(process.getName().replace("_", ""));
        return builder.toString();
    }

    /**
     *
     * With this method it is possible to change the memory of the workerTemplate.
     *
     * @param memory The memory to set.
     */
    public void setMemory(double memory) {
        this.memory = memory;
    }

    /**
     *
     * This method returns the memory to execute the workerTemplate.
     *
     * @return Returns the memory as {@link double}.
     */
    public double getMemory() {
        return memory;
    }

    /**
     *
     * This method allows to proof, if the engines is a BPELEngine.
     *
     * @return Returns true, if the engine of workerTemplate is a BPELEngine.
     */
    public boolean isBPELEngine(){
        return bpelEngine.isPresent();
    }

}
