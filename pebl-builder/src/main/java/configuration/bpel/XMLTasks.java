package configuration.bpel;

import java.nio.file.Files;
import java.nio.file.Path;

import betsy.common.tasks.WaitTasks;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import groovy.xml.XmlUtil;
import org.apache.log4j.Logger;

public class XMLTasks {

    public static void updatesNameAndNamespaceOfRootElement(Path from, Path to, String nameAndTargetNamespace) {
        LOGGER.info("Update @name=\'" + nameAndTargetNamespace + "\' and @targetNamespace=\'" + nameAndTargetNamespace + "\' in file " + String.valueOf(from) + " and store the result in " + String.valueOf(to));

        try {
            final GPathResult root = new XmlSlurper(false, false).parse(from.toFile());
            root.setProperty("name", nameAndTargetNamespace);
            root.setProperty("targetNamespace", nameAndTargetNamespace);

            Files.write(to, XmlUtil.serialize(root).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(WaitTasks.class);
}
