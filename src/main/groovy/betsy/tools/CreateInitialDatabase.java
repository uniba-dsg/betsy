package betsy.tools;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import betsy.common.tasks.FileTasks;
import org.xml.sax.SAXException;

public class CreateInitialDatabase {

    public static void main(String[] args) throws JAXBException, IOException, SAXException {
        FileTasks.deleteDirectory(Paths.get("database"));
        System.out.println("PEBL Write");
        PEBLWriterMain.main(new String[]{"database"});
        System.out.println("PEBL Files");
        PEBLStoreFilesAlongMain.main(new String[]{"database/pebl.xml"});
        System.out.println("PEBL PNGs");
        PEBLBpmnPngImageAdderMain.main(new String[]{"database/pebl.xml"});
    }

}
