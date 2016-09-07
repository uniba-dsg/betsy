package peal.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.ZipTasks;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import peal.identifier.ProcessModelId;
import peal.packages.DeploymentPackage;
import peal.packages.LogPackage;
import peal.packages.Package;
import peal.packages.ProcessModelPackage;

public class ZipFileHelper {

    public static Package buildFromFolder(Path folder) throws IOException {
        Path tempZipFile = Files.createTempDirectory("peal").resolve("zip-file.zip");
        ZipTasks.zipFolder(tempZipFile, folder);
        return createZipFileFromArchive(tempZipFile);
    }

    public static Package createZipFileFromArchive(Path tempZipFile) throws IOException {
        Package aPackage = new Package();
        aPackage.setData(Files.readAllBytes(tempZipFile));
        return aPackage;
    }

    public static ProcessModelPackage zipToProcessModelPackage(Package aPackage) {
        ProcessModelPackage result = new ProcessModelPackage();
        result.setData(aPackage.getData());
        return result;
    }

    public static DeploymentPackage zipToDeployablePackage(Package aPackage, String fileExtension, ProcessModelId processModelId) {
        DeploymentPackage result = new DeploymentPackage();
        result.setData(aPackage.getData());
        result.fileExtension = fileExtension;
        result.processModelId = processModelId;
        return result;
    }

    public static LogPackage zipToLog(Package aPackage) {
        LogPackage result = new LogPackage();
        result.setData(aPackage.getData());
        return result;
    }

    public static Path extractIntoTemporaryFolder(Package aPackage) throws IOException {
        Path tempZipFile = storeDataAsZipFile(aPackage);

        // unpack in another temporary folder
        Path tempExtractedFolder = Files.createTempDirectory("peal");

        ZipTasks.unzip(tempZipFile, tempExtractedFolder);

        findOtherFilesInPath(tempExtractedFolder).stream().filter(path -> path.toString().endsWith(".zip")).forEach(path -> {
            Path tmpDir = Paths.get(path.toString() + ".EXTRACT/");
            FileTasks.mkdirs(tmpDir);
            ZipTasks.unzip(path, tmpDir);
        });

        return tempExtractedFolder;
    }

    public static Path storeDataAsZipFile(Package aPackage) throws IOException {
        Path tempZipFile = Files.createTempDirectory("peal").resolve("zip-file.zip");

        // write zip file to temporary folder
        Files.write(tempZipFile, aPackage.getData());
        return tempZipFile;
    }

    public static Path findBpelFileInPath(Path path) throws IOException {
        return Files.find(path, 10, ZipFileHelper::isBpelFile).findFirst().
                orElseThrow(() -> new IllegalStateException("could not find any bpel files in path " + path));
    }

    public static Path findBpmnFileInPath(Path path) throws IOException {
        return Files.find(path, 10, ZipFileHelper::isBpmnFile).findFirst().
                orElseThrow(() -> new IllegalStateException("could not find any bpel files in path " + path));
    }

    public static String findBpmnProcessNameInPath(Path path) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        if(Files.isDirectory(path)) {
            return findBpmnProcessNameInPath(findBpmnFileInPath(path));
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(path.toFile());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext( new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if("bpmn2".equals(prefix)) {
                    return "http://www.omg.org/spec/BPMN/20100524/MODEL";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        XPathExpression expr = xpath.compile("/*[local-name()='definitions']/*[local-name()='process']/@id");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        return nl.item(0).getTextContent();
    }

    public static String findBpelProcessNameInPath(Path path) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        if(Files.isDirectory(path)) {
            return findBpelProcessNameInPath(findBpelFileInPath(path));
        }

        return findBpelProcessName(path);
    }

    public static String findBpelProcessName(Path path)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(path.toFile());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext( new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if("bpel".equals(prefix)) {
                    return "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        XPathExpression expr = xpath.compile("/*[local-name()='process']/@name");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        if(nl.getLength() <= 0) {
            return ""; // not found
        }

        return nl.item(0).getTextContent();
    }

    public static String findBpelTargetNameSpaceInPath(Path path) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        if(Files.isDirectory(path)) {
            return findBpelTargetNameSpaceInPath(findBpelFileInPath(path));
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(path.toFile());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext( new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if("bpel".equals(prefix)) {
                    return "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        XPathExpression expr = xpath.compile("/*[local-name()='process']/@targetNamespace");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        return nl.item(0).getTextContent();
    }

    public static String findBpmnTargetNameSpaceInPath(Path path) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        if(Files.isDirectory(path)) {
            return findBpmnTargetNameSpaceInPath(findBpmnFileInPath(path));
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(path.toFile());
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext( new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if("bpmn2".equals(prefix)) {
                    return "http://www.omg.org/spec/BPMN/20100524/MODEL";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        XPathExpression expr = xpath.compile("/*[local-name()='definitions']/@targetNamespace");
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        return nl.item(0).getTextContent();
    }

    public static void adjustFileNameOfBpelToProcessName(Path folder) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        Path bpelFile = findBpelFileInPath(folder);
        String processName = findBpelProcessNameInPath(bpelFile);
        String correctBpelFileName = processName + ".bpel";
        if(!bpelFile.toString().endsWith(correctBpelFileName)){
            Files.move(bpelFile, bpelFile.getParent().resolve(correctBpelFileName));
        }
    }

    public static void adjustFileNameOfBpmnToProcessName(Path folder) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        Path bpmnFile = findBpmnFileInPath(folder);
        String processName = findBpmnProcessNameInPath(bpmnFile);
        String correctBpmnFileName = processName + ".bpmn";
        if(!bpmnFile.toString().endsWith(correctBpmnFileName)){
            Files.move(bpmnFile, bpmnFile.getParent().resolve(correctBpmnFileName));
        }
    }

    public static List<Path> findWsdlFilesInPath(Path path) throws IOException {
        return Files.find(path, 10, ZipFileHelper::isWsdlFile).collect(Collectors.toList());
    }

    public static List<Path> findOtherFilesInPath(Path path) throws IOException {
        return Files.find(path, 10, ZipFileHelper::isNeitherWsdlNorDirectoryNorBpelFile).collect(Collectors.toList());
    }

    private static boolean isWsdlFile(Path path, BasicFileAttributes a) {
        return path.getFileName().toString().endsWith(".wsdl");
    }

    private static boolean isBpelFile(Path path, BasicFileAttributes a) {
        return path.getFileName().toString().endsWith(".bpel");
    }

    private static boolean isBpmnFile(Path path, BasicFileAttributes a) {
        return path.getFileName().toString().endsWith(".bpmn");
    }

    private static boolean isNeitherWsdlNorDirectoryNorBpelFile(Path path, BasicFileAttributes a) {
        return !isBpelFile(path, a) && !isWsdlFile(path, a) && !Files.isDirectory(path);
    }

    public static LogPackage createLogPackage(List<Path> logs) {
        Path result;
        try {
            result = Files.createTempDirectory("peal").resolve("logs");
        } catch (IOException e1) {
            throw new RuntimeException("could not create log folder", e1);
        }
        FileTasks.mkdirs(result);
        Path logFolder = result;
        for(Path file : logs) {
            try {
                FileTasks.copyFileIntoFolder(file, logFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return zipToLog(buildFromFolder(logFolder));
        } catch (IOException e) {
            throw new RuntimeException("could not create zip file", e);
        }
    }
}
