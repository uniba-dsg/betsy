package peal.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.xml.ws.Endpoint;

import betsy.common.tasks.URLTasks;
import peal.impl.engine.EngineServiceImpl;
import peal.impl.instance.InstanceServiceImpl;
import peal.impl.processmodel.ProcessModelServiceImpl;

public class PEALWebServices {

    public static void main(String[] args) throws Exception {
        EngineServiceImpl engineService = new EngineServiceImpl();
        ProcessModelServiceImpl processModelService = new ProcessModelServiceImpl(engineService);
        InstanceServiceImpl instanceService = new InstanceServiceImpl(engineService);

        Endpoint.publish("http://localhost:1337/engineService", engineService);
        Endpoint.publish("http://localhost:1337/processModelService", processModelService);
        Endpoint.publish("http://localhost:1337/instanceService", instanceService);
        System.out.println("Published");

        Path dir = Paths.get("peal/src/main/resources/schemas/");
        Files.createDirectories(dir);

        Files.write(Paths.get("peal/src/main/resources/schemas/engineService.wsdl"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/engineService?wsdl"))));
        Files.write(Paths.get("peal/src/main/resources/schemas/engineService.xsd"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/engineService?xsd=1"))));

        Files.write(Paths.get("peal/src/main/resources/schemas/processModelService.wsdl"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/processModelService?wsdl"))));
        Files.write(Paths.get("peal/src/main/resources/schemas/processModelService.xsd"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/processModelService?xsd=1"))));

        Files.write(Paths.get("peal/src/main/resources/schemas/instanceService.wsdl"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/instanceService?wsdl"))));
        Files.write(Paths.get("peal/src/main/resources/schemas/instanceService.xsd"), Collections.singleton(URLTasks.getContentAtUrl(new URL("http://localhost:1337/instanceService?xsd=1"))));

        String everything = Files.find(dir, 1, (a, b) -> Files.isRegularFile(a))
                .map(p -> {
                    try {
                        return Files.readAllLines(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(l -> String.join("\n", l))
                .collect(Collectors.joining("\n"));
        Files.write(dir.resolve("everything.xml"), Collections.singleton(everything));
    }

}
