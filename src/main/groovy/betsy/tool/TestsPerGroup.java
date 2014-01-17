package betsy.tool;

import configuration.ProcessRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestsPerGroup {

    public static void main(String[] args) {

        ProcessRepository processRepository = new ProcessRepository();
        List<String> names = processRepository.getNames();

        List<String> output = new LinkedList<>();


        for(String name : names) {
            int size = processRepository.getByName(name).size();
            if(size < 10) {
                output.add("00" + size + "\t" + name );
            } else if(size < 100) {
                output.add("0" + size + "\t" + name );
            } else {
                output.add("" + size + "\t" + name );
            }

        }

        Collections.sort(output);

        for(String out : output) {
            System.out.println(out);
        }

    }

}
