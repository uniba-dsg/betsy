package betsy.common.tasks

import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import org.apache.log4j.Logger

import java.nio.file.Path

class XMLTasks {

    private static final Logger LOGGER = Logger.getLogger(WaitTasks.class);

    public static void updatesNameAndNamespaceOfRootElement(Path from, Path to, String nameAndTargetNamespace) {
        LOGGER.info("Update @name='$nameAndTargetNamespace' and @targetNamespace='$nameAndTargetNamespace' in file $from and store the result in $to");

        GPathResult root = new XmlSlurper(false, false).parse(from.toFile())
        root.@name = nameAndTargetNamespace
        root.@targetNamespace = nameAndTargetNamespace

        to.withPrintWriter { out ->
            out.print(XmlUtil.serialize(root))
        }
    }

}
