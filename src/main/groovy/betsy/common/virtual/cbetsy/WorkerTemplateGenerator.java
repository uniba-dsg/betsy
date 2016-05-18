package betsy.common.virtual.cbetsy;

import betsy.bpel.cli.BPELCliParameter;
import betsy.bpel.cli.BPELCliParser;
import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpmn.cli.BPMNCliParameter;
import betsy.bpmn.cli.BPMNCliParser;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.virtual.calibration.Calibrator;
import betsy.common.virtual.calibration.DockerProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class generates the workerTemplates based of the given arguments.
 *
 */
public class WorkerTemplateGenerator {

    private HashSet<DockerEngine> engines = new HashSet<>();
    private HashSet<DockerEngine> bpelEngines = new HashSet<>();
    private HashSet<DockerEngine> bpmnEngines = new HashSet<>();
    private HashSet<String> processes = new HashSet<>();
    private HashSet<String> bpelProcesses = new HashSet<>();
    private HashSet<String> bpmnProcesses = new HashSet<>();
    private ArrayList<WorkerTemplate> workerTemplates = new ArrayList<>();
    private static Path docker = Paths.get(get("docker.dir")).toAbsolutePath();
    private String[] args;

    /**
     *
     * @param args The arguments to execute.
     */
    public WorkerTemplateGenerator(String... args) {
        if(args.length > 0){
            this.args = args;
            if ("all".equalsIgnoreCase(args[0])) {
                BPELCliParser bpelParser = new BPELCliParser("locals", "all");
                BPELCliParameter bpelParams = bpelParser.parse();
                for (AbstractBPELEngine engine : bpelParams.getEngines()) {
                    DockerEngine dockerEngine = new DockerEngine(engine.getName(), DockerEngine.TypeOfEngine.BPEL);
                    engines.add(dockerEngine);
                    bpelEngines.add(dockerEngine);
                    for (EngineIndependentProcess process : bpelParams.getProcesses()) {
                        processes.add(process.getName());
                        bpelProcesses.add(process.getName());
                        WorkerTemplate workerTemplate = new WorkerTemplate(process, dockerEngine);
                        workerTemplates.add(workerTemplate);
                    }
                }
                BPMNCliParser bpmnParser = new BPMNCliParser("all", "all");
                BPMNCliParameter bpmnParams = bpmnParser.parse();
                for (AbstractBPMNEngine engine : bpmnParams.getEngines()) {
                    DockerEngine dockerEngine = new DockerEngine(engine.getName(), DockerEngine.TypeOfEngine.BPMN);
                    engines.add(dockerEngine);
                    bpmnEngines.add(dockerEngine);
                    for (EngineIndependentProcess process : bpmnParams.getProcesses()) {
                        processes.add(process.getName());
                        bpmnProcesses.add(process.getName());
                        WorkerTemplate workerTemplate = new WorkerTemplate(process, dockerEngine);
                        workerTemplates.add(workerTemplate);
                    }
                }
            } else if ("bpel".equalsIgnoreCase(args[0])) {
                BPELCliParser parser = new BPELCliParser(createArgsWithoutFirstValue(args));
                BPELCliParameter params = parser.parse();
                for (AbstractBPELEngine engine : params.getEngines()) {
                    DockerEngine dockerEngine = new DockerEngine(engine.getName(), DockerEngine.TypeOfEngine.BPEL);
                    engines.add(dockerEngine);
                    bpelEngines.add(dockerEngine);
                    for (EngineIndependentProcess process : params.getProcesses()) {
                        processes.add(process.getName());
                        bpelProcesses.add(process.getName());
                        WorkerTemplate workerTemplate = new WorkerTemplate(process, dockerEngine);
                        workerTemplates.add(workerTemplate);
                    }
                }
            } else if ("bpmn".equalsIgnoreCase(args[0])) {
                BPMNCliParser parser = new BPMNCliParser(createArgsWithoutFirstValue(args));
                BPMNCliParameter params = parser.parse();
                for (AbstractBPMNEngine engine : params.getEngines()) {
                    DockerEngine dockerEngine = new DockerEngine(engine.getName(), DockerEngine.TypeOfEngine.BPMN);
                    engines.add(dockerEngine);
                    bpmnEngines.add(dockerEngine);
                    for (EngineIndependentProcess process : params.getProcesses()) {
                        processes.add(process.getName());
                        bpmnProcesses.add(process.getName());
                        WorkerTemplate workerTemplate = new WorkerTemplate(process, dockerEngine);
                        workerTemplates.add(workerTemplate);
                    }
                }
            }
        }
            printUsage();
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
    public ArrayList<WorkerTemplate> getSortedTemplates(){
        workerTemplates = DockerProperties.readWorkerTemplates(docker.resolve("worker.properties"), workerTemplates);
        List<Long> times = new ArrayList<>();
        workerTemplates.forEach(k -> times.add(k.getDockerEngine().getTime()));
        long countTime = times.stream().filter(element -> element == 0).count();
        if(countTime > 0){
            Calibrator.main(args);
            DockerProperties.readWorkerTemplates(docker.resolve("worker.properties"), workerTemplates);
        }
        Collections.sort(workerTemplates, (o1, o2) -> new Double(o1.getDockerEngine().getTime() - o2.getDockerEngine().getTime()).intValue());
        return workerTemplates;
    }

    /**
     *
     * This method returns the used engines.
     *
     * @return Returns the used engines as {@link HashSet}.
     */
    public HashSet<DockerEngine> getEngines(){
        return engines;
    }

    /**
     *
     * This method returns the used engines with values.
     *
     * @return Returns the used engines as {@link HashSet}.
     */
    public HashSet<DockerEngine> getEnginesWithValues(){
        engines = DockerProperties.readEngines(docker.resolve("worker.properties"), engines);
        List<Integer> memories = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        engines.forEach(k -> memories.add(k.getMemory()));
        engines.forEach(k -> times.add(k.getTime()));
        long countMemory = memories.stream().filter(element -> element == 0).count();
        long countTime = times.stream().filter(element -> element == 0).count();
        if(countMemory > 0 && countTime > 0){
            Calibrator.main(args);
            DockerProperties.readEngines(docker.resolve("worker.properties"), engines);
        }
        return engines;
    }

    /**
     *
     * This method returns the used BPEL engines.
     *
     * @return Returns the used engines as {@link HashSet}.
     */
    public HashSet<DockerEngine> getBPELEngines(){
        return bpelEngines;
    }

    /**
     *
     * This method returns the used BPMN engines.
     *
     * @return Returns the used engines as {@link HashSet}.
     */
    public HashSet<DockerEngine> getBPMNEngines(){
        return bpmnEngines;
    }

    /**
     *
     * This method returns the used processes.
     *
     * @return Returns the used processes as {@link ArrayList}.
     */
    public ArrayList<String> getProcesses(){
        return new ArrayList<>(processes);
    }

    /**
     *
     * This method returns the used bpel processes.
     *
     * @return Returns the used processes as {@link ArrayList}.
     */
    public ArrayList<String> getBPELProcesses(){
        return new ArrayList<>(bpelProcesses);
    }

    /**
     *
     * This method returns the used bpmn processes.
     *
     * @return Returns the used processes as {@link ArrayList}.
     */
    public ArrayList<String> getBPMNProcesses(){
        return new ArrayList<>(bpmnProcesses);
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
        System.out.println("The third argument must be bpel, bpmn");
    }
}
