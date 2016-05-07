package betsy.bpel.tools;

import configuration.bpel.BPELProcessRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Prints the processes per process group to the console.
 */
public final class TestsPerGroup {

    private TestsPerGroup() {}

    public static void main(String... args) {

        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<String> names = processRepository.getNames();

        List<String> output = new LinkedList<>();


        for(String name : names) {
            int size = processRepository.getByName(name).size();
            if(size < 10) {
                output.add("00" + size + "\t" + name );
            } else if(size < 100) {
                output.add("0" + size + "\t" + name );
            } else {
                output.add(Integer.toString(size) + "\t" + name );
            }

        }

        Collections.sort(output);

        for(String out : output) {
            System.out.println(out);
        }

    }

}
