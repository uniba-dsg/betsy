package betsy.tools;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import betsy.common.tasks.FileTasks;
import loader.PEBLBpmnPngImageAdderMain;
import loader.PEBLStoreFilesAlongMain;
import org.xml.sax.SAXException;

public class CreateInitialDatabase {

    public static void main(String[] args)  {
        try {
            FileTasks.deleteDirectory(Paths.get("database"));
            System.out.println("PEBL Write");
            PEBLWriterMain.main(new String[] {"database"});
            System.out.println("PEBL Files");
            PEBLStoreFilesAlongMain.main(new String[] {"database/pebl.xml"});
            System.out.println("PEBL PNGs");
            PEBLBpmnPngImageAdderMain.main(new String[] {"database/pebl.xml"});
        } catch (JAXBException | IOException | SAXException e) {
            System.err.println("Error during creating initial database " + e.getMessage());
            e.printStackTrace();
        }
    }

}
