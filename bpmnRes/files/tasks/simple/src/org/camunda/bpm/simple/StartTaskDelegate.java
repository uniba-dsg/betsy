package org.camunda.bpm.simple;
import java.io.File;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class StartTaskDelegate implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		
		File f = new File("log.txt");
		f.createNewFile();
		
	}

}
