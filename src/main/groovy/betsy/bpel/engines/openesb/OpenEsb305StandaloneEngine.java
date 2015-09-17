package betsy.bpel.engines.openesb;

public class OpenEsb305StandaloneEngine extends OpenEsb301StandaloneEngine{

    public OpenEsb305StandaloneEngine(){
        super("OpenESB-SE-3.0.5", "OpenESB-SE-3.0.5-server-only.zip");
    }

    @Override
    public String getName() {
        return "openesb305standalone";
    }

}
