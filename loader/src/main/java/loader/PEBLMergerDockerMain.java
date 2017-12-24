package loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

public class PEBLMergerDockerMain {

    public static void main(String[] args) throws IOException {
        Files.find(Paths.get(args[1]), 3, (f,x) -> f.getFileName().toString().equals("pebl.xml")).forEach(f -> {
            try {
                PEBLMergerMain.main(new String[]{f.toString(), args[0]});
            } catch (IOException | JAXBException | SAXException e) {
                e.printStackTrace();
            }
        });
    }

}
