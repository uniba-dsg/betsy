package betsy.common.virtual;

import betsy.bpel.cli.BPELCliParameter;
import betsy.bpel.cli.BPELCliParser;
import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpmn.cli.BPMNCliParameter;
import betsy.bpmn.cli.BPMNCliParser;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.virtual.calibration.Calibrator;
import betsy.common.virtual.calibration.Properties;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class generates the workerTemplates based of the given arguments.
 *
 */
public class WorkerTemplateGenerator {

    private HashMap<String, Boolean> engines = new HashMap<>();
    private ArrayList<WorkerTemplate> workerTemplates = new ArrayList<>();
    private String[] args;

    /**
     *
     * @param args The arguments to execute.
     */
    public WorkerTemplateGenerator(String... args) {
        this.args = args;
        if ("all".equalsIgnoreCase(args[0])) {
            BPELCliParser bpelParser = new BPELCliParser("locals", "all");
            BPELCliParameter bpelParams = bpelParser.parse();
            for (AbstractBPELEngine engine : bpelParams.getEngines()) {
                engines.put(engine.getName(), true);
                for (EngineIndependentProcess process : bpelParams.getProcesses()) {
                    WorkerTemplate workerTemplate = new WorkerTemplate(process, engine);
                    workerTemplates.add(workerTemplate);
                }
            }
            BPMNCliParser bpmnParser = new BPMNCliParser("all", "all");
            BPMNCliParameter bpmnParams = bpmnParser.parse();
            for (AbstractBPMNEngine engine : bpmnParams.getEngines()) {
                engines.put(engine.getName(), false);
                for (EngineIndependentProcess process : bpmnParams.getProcesses()) {
                    WorkerTemplate workerTemplate = new WorkerTemplate(process, engine);
                    workerTemplates.add(workerTemplate);
                }
            }
        } else if ("bpel".equalsIgnoreCase(args[0])) {
            BPELCliParser parser = new BPELCliParser(createArgsWithoutFirstValue(args));
            BPELCliParameter params = parser.parse();
            for (AbstractBPELEngine engine : params.getEngines()) {
                engines.put(engine.getName(), true);
                for (EngineIndependentProcess process : params.getProcesses()) {
                    WorkerTemplate workerTemplate = new WorkerTemplate(process, engine);
                    workerTemplates.add(workerTemplate);
                }
            }
        } else if ("bpmn".equalsIgnoreCase(args[0])) {
            BPMNCliParser parser = new BPMNCliParser(createArgsWithoutFirstValue(args));
            BPMNCliParameter params = parser.parse();
            for (AbstractBPMNEngine engine : params.getEngines()) {
                engines.put(engine.getName(), false);
                for (EngineIndependentProcess process : params.getProcesses()) {
                    WorkerTemplate workerTemplate = new WorkerTemplate(process, engine);
                    workerTemplates.add(workerTemplate);
                }
            }
        } else {
            printUsage();
        }
    }

    /**
     *
     * The method returns the created workerTemplates.
     *
     * @return Returns the workerTemplates as {@link ArrayList}.
     */
    public ArrayList<WorkerTemplate> getWorkerTemplates(){
        return workerTemplates;
    }

    /**
     *
     * The method returns the created workerTemplates with the values from the properties.
     *
     * @return Returns the workerTemplates as {@link ArrayList}.
     */
    public ArrayList<WorkerTemplate> getWorkerTemplatesWithValues(){
        Properties.read(Paths.get("worker.properties"), workerTemplates);
        List<Double> memories = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        workerTemplates.forEach(k -> memories.add(k.getMemory()));
        workerTemplates.forEach(k -> times.add(k.getTime()));
        long countMemory = memories.stream().filter(element -> element == 0).count();
        long countTime = times.stream().filter(element -> element == 0).count();
        if(countMemory > 0 && countTime > 0){
            Calibrator.main(args);
            Properties.read(Paths.get("worker.properties"), workerTemplates);
        }
        Collections.sort(workerTemplates, (o1, o2) -> new Double(o1.getMemory() - o2.getMemory()).intValue());
        return workerTemplates;
    }



    /**
     *
     * This method returns the used engines.
     *
     * @return Returns the used engines as {@link HashMap}.
     */
    public HashMap<String, Boolean> getEngines(){
        return engines;
    }

    /**
     *
     * This method removes the first of the arguments.
     *
     * @param args The arguments to change.
     * @return The changed arguments.
     */
    private static String[] createArgsWithoutFirstValue(String... args) {
        String[] bpelArgs = new String[args.length - 1];
        System.arraycopy(args, 1, bpelArgs, 0, bpelArgs.length);
        return bpelArgs;
    }

    /**
     *  This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The first argument must be bpel, bpmn, engine, process, analytics, docker or calibrate");
    }
}
