package peal.impl.processmodel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.engines.EngineAPI;
import betsy.common.tasks.FileTasks;
import org.xml.sax.SAXException;
import peal.DeploymentException;
import peal.ProcessLanguage;
import peal.ProcessModelService;
import peal.helper.ZipFileHelper;
import peal.identifier.EngineId;
import peal.identifier.ProcessModelId;
import peal.impl.engine.EngineServiceImpl;
import peal.observer.ProcessModelState;
import peal.packages.DeploymentPackage;
import peal.packages.ProcessModelPackage;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Group;
import pebl.benchmark.feature.Language;
import pebl.benchmark.test.Test;

@WebService
public class ProcessModelServiceImpl implements ProcessModelService {

    private final EngineServiceImpl engineService;

    public ProcessModelServiceImpl(EngineServiceImpl engineService) {
        this.engineService = engineService;
    }

    @Override
    public DeploymentPackage makeDeployable(EngineId engineId, ProcessModelPackage processModelPackage) {
        EngineAPI<?> engine = engineService.getEngineByID(engineId);
        try {
            Path folder = ZipFileHelper.extractIntoTemporaryFolder(processModelPackage);

            ProcessLanguage processLanguage = engineService.getSupportedLanguage(engineId);
            if(processLanguage == ProcessLanguage.BPEL) {
                AbstractLocalBPELEngine bpelEngine = (AbstractLocalBPELEngine) engine;

                ProcessModelId processModelId = determineProcessModelIdInBPELProcess(engineId, folder);

                ZipFileHelper.adjustFileNameOfBpelToProcessName(folder);
                Path bpelFile = ZipFileHelper.findBpelFileInPath(folder);

                String language = processLanguage.toString();
                Feature feature = new Feature(new FeatureSet(new Group("group", new Language(new Capability("capability"), language), ""), "featureset"), "feature");
                List<Path> wsdlFilesInPath = ZipFileHelper.findWsdlFilesInPath(folder);
                List<Path> otherFilesInPath = ZipFileHelper.findOtherFilesInPath(folder);
                List<Path> files = new LinkedList<>();
                files.addAll(wsdlFilesInPath);
                files.addAll(otherFilesInPath);

                Test test = new Test(bpelFile, "", Collections.emptyList(), feature, files, Collections.emptyList());
                BPELProcess process = new BPELProcess(test);
                process.setEngine(bpelEngine);

                Path deployableArchivePath = bpelEngine.buildArchives(process);
                return ZipFileHelper.zipToDeployablePackage(ZipFileHelper.createZipFileFromArchive(deployableArchivePath),
                        FileTasks.getFileExtension(deployableArchivePath.getFileName()),
                        processModelId);
            } else {
                AbstractBPMNEngine bpmnEngine = (AbstractBPMNEngine) engine;

                ProcessModelId processModelId = determineProcessModelIdInBPMNProcess(engineId, folder);

                ZipFileHelper.adjustFileNameOfBpmnToProcessName(folder);
                Path bpmnFile = ZipFileHelper.findBpmnFileInPath(folder);

                String language = processLanguage.toString();
                Feature feature = new Feature(new FeatureSet(new Group("group", new Language(new Capability("capability"), language), ""), "featureset"), FileTasks.getFilenameWithoutExtension(bpmnFile));
                List<Path> wsdlFilesInPath = ZipFileHelper.findWsdlFilesInPath(folder);
                List<Path> otherFilesInPath = ZipFileHelper.findOtherFilesInPath(folder);
                List<Path> files = new LinkedList<>();
                files.addAll(wsdlFilesInPath);
                files.addAll(otherFilesInPath);

                Test test = new Test(bpmnFile, "", Collections.emptyList(), feature, files, Collections.emptyList());
                BPMNProcess process = new BPMNProcess(test);
                process.setEngine(bpmnEngine);

                Path deployableArchivePath = bpmnEngine.buildArchives(process);
                return ZipFileHelper.zipToDeployablePackage(ZipFileHelper.createZipFileFromArchive(deployableArchivePath),
                        FileTasks.getFileExtension(deployableArchivePath.getFileName()),
                        processModelId);
            }


        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            throw new RuntimeException("error due to io", e);
        }
    }

    @Override
    public ProcessModelId deploy(EngineId engineId, DeploymentPackage bpelPackage)
            throws DeploymentException {
        try {
            Path file = ZipFileHelper.storeDataAsZipFile(bpelPackage);

            ProcessModelId processModelId = bpelPackage.processModelId;

            // rename zip file to the name of the process
            Path newFileName = file.getParent().resolve(processModelId.getProcessId().getLocalPart() + "." + bpelPackage.fileExtension);
            Files.copy(file, newFileName);

            EngineAPI<?> engine = engineService.getEngineByID(engineId);
            engine.deploy(processModelId.getProcessId().getLocalPart(), newFileName);

            System.out.println("FOUND " + processModelId);

            return processModelId;
        } catch (Exception e) {
            throw new DeploymentException(e.getMessage(), e);
        }
    }

    private ProcessModelId determineProcessModelIdInBPELProcess(EngineId engineId, Path extractedPath)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        String bpelName = ZipFileHelper.findBpelProcessNameInPath(extractedPath);
        String bpelNamespace = ZipFileHelper.findBpelTargetNameSpaceInPath(extractedPath);
        QName processId = new QName(bpelNamespace, bpelName);
        return new ProcessModelId(engineId.getEngineId(), processId);
    }

    private ProcessModelId determineProcessModelIdInBPMNProcess(EngineId engineId, Path extractedPath)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        String bpmnName = ZipFileHelper.findBpmnProcessNameInPath(extractedPath);
        String bpmnNamespace = ZipFileHelper.findBpmnTargetNameSpaceInPath(extractedPath);
        QName processId = new QName(bpmnNamespace, bpmnName);
        return new ProcessModelId(engineId.getEngineId(), processId);
    }

    @Override
    public void undeploy(ProcessModelId processModelId) {
        EngineAPI<?> engine = engineService.getEngineByID(processModelId.toEngineId());
        engine.undeploy(processModelId.getProcessId());
    }

    @Override
    public ProcessModelState getState(ProcessModelId processModelId) {
        EngineAPI<?> engine = engineService.getEngineByID(processModelId.toEngineId());
        if (engine.isDeployed(processModelId.getProcessId())) {
            return ProcessModelState.DEPLOYED;
        } else {
            return ProcessModelState.NOT_DEPLOYED;
        }
    }

    @Override
    public List<ProcessModelId> getDeployedProcessModels() {
        return null;
    }
}
