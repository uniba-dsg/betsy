package betsy.bpel.cli;

/**
 * Created by joerg on 14.08.2015.
 */
public class TestFolderParser {

    private final String[] args;

    private final String defaultFolderName = "test";

    public TestFolderParser(String[] args){
        this.args = args;
    }

    public String parse() {
        if(args.length <= 2) {
            return defaultFolderName;
        } else {
            return args[2];
        }
    }
}
