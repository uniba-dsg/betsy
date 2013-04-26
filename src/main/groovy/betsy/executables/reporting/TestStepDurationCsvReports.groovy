package betsy.executables.reporting;

import betsy.data.TestSuite;
import betsy.data.engines.Engine

public class TestStepDurationCsvReports {

	/**
	 * Represents one row in the resulting csv file
	 */
	static class ProcessCsvDuration {
		File csvFile
		String engine
		String name

		String toRow() {
			//load and split the file
			String[] lines = csvFile.text.split('\n')
			String durationLine = lines[1]
			
			return [engine,name].join(";") + ";" + durationLine
		}
	}

	/**
	 * path to resulting csv file (WRITE)
	 */
	String csv

	TestSuite tests

	public void create() {
		new File(csv).withPrintWriter { w ->
			w.println("Engine;Process;Total;Build;Installation;Startup;Deploy;Test;Shutdown")
		}

		tests.engines.each { Engine engine ->
			engine.processes.each { process ->
				File processDurationFile = new File("${process.getTargetPath()}/durations.csv")
				if(processDurationFile.isFile()) {
					new File(csv).withWriterAppend { w ->
						ProcessCsvDuration csvRow = new ProcessCsvDuration(
							csvFile: processDurationFile,
							name: process.normalizedId,
							engine: process.engine.toString())
						w.println(csvRow.toRow())
					}
				}
			}
		}

	}

}
