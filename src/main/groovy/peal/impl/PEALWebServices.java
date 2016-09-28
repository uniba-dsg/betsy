package peal.impl;

import javax.xml.ws.Endpoint;

import peal.impl.engine.EngineServiceImpl;
import peal.impl.instance.InstanceServiceImpl;
import peal.impl.processmodel.ProcessModelServiceImpl;

public class PEALWebServices {

    public static void main(String[] args) {
        EngineServiceImpl engineService = new EngineServiceImpl();
        ProcessModelServiceImpl processModelService = new ProcessModelServiceImpl(engineService);
        InstanceServiceImpl instanceService = new InstanceServiceImpl(engineService);

        Endpoint.publish("http://localhost:1337/engineService", engineService);
        Endpoint.publish("http://localhost:1337/processModelService", processModelService);
        Endpoint.publish("http://localhost:1337/instanceService", instanceService);
        System.out.println("Published");
    }

}
