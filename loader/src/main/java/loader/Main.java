package loader;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

public class Main {

    public static void main(String... args) throws JAXBException, IOException, SAXException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        if ("load".equalsIgnoreCase(args[0])) {
            PEBLMergerMain.main(createArgsWithoutFirstValue(args));
        } else if ("load-multiple".equalsIgnoreCase(args[0])) {
            PEBLMergerDockerMain.main(createArgsWithoutFirstValue(args));
        } else if ("add-images".equalsIgnoreCase(args[0])) {
            PEBLBpmnPngImageAdderMain.main(createArgsWithoutFirstValue(args));
        } else if ("store-files".equalsIgnoreCase(args[0])) {
            PEBLStoreFilesAlongMain.main(createArgsWithoutFirstValue(args));
        } else {
            printUsage();
        }
    }

    private static String[] createArgsWithoutFirstValue(String... args) {
        String[] bpelArgs = new String[args.length - 1];
        System.arraycopy(args, 1, bpelArgs, 0, bpelArgs.length);
        return bpelArgs;
    }

    private static void printUsage() {
        System.out.println("The first argument must be load, load-multiple, add-images, or store-files");
        System.out.println("");
        System.out.println("\tload\t\t\tLoads a single run into database");
        System.out.println("\tload-multiple\t\t\tLoads multiple runs into database");
        System.out.println("");
        System.out.println("\tadd-images\t\t\tAdd images for process models");
        System.out.println("\tstore-files\t\t\tCopy files to database folder structure");
    }

}
