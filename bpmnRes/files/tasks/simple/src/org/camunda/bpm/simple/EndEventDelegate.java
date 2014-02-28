package org.camunda.bpm.simple;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class EndEventDelegate implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
	
		BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt", true));
		bw.append("success");
		bw.close();
		
	}
}
