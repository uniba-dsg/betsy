# BETSY (BPEL Engine Test System)

Betsy is a tool to check the degree of conformance of a BPEL engine against the BPEL standard.
More information can be found in the [technical report](https://github.com/downloads/uniba-dsg/betsy/techrep-betsy-final.pdf).
A [sample output](https://github.com/downloads/uniba-dsg/betsy/test-results.zip) of a run of betsy are also available. 

This software is licensed under the LGPL Version 3 Open Source License.

## Software Requirements
- Windows 7
- JDK 1.7.0_03 (64 Bit) or higher
  - `JAVA_HOME` should point to the jdk directory
  - `PATH` should include `JAVA_HOME/bin`
- SoapUI 4.5.1 (64 bit)
  - installed path should be `C:\Program Files\SmartBear\soapUI-4.5.1`
- Ant 1.8.3
  - `ANT_HOME` should point to the Ant directory
  - `PATH` should include `ANT_HOME/bin`

## Licensing
LGPL Version 3: http://www.gnu.org/licenses/lgpl-3.0.html

## Usage

Requirements have to be fulfilled in order to execute any of these `gradlew` tasks.

```bash
$ gradlew run # Running all tests for all engines
$ gradlew run -Pargs="ode" # Running all tests for Apache ODE
$ gradlew run -Pargs="ode,bpelg" # Running all tests for Apache ODE and bpel-g
$ gradlew run -Pargs="ALL Sequence" # Running Sequence test for all engines
$ gradlew run -Pargs="ALL Sequence,While" # Running Sequence and While test for all engines
$ gradlew run -Pargs="ode Sequence" # Running Sequence test for Apache ODE
$ gradlew run -Pargs="ode Invoke-Catch" # Running Invoke-Catch test for Apache ODE
$ gradlew run -Pargs="-s" # Running all tests for all engines installing engines only once
$ gradlew run -Pargs="-s ode ALL" # Running all tests for Apache ODE installing engines only once
$ gradlew run -Pargs="-o" # Opens the results in the default browser after a successful run
$ gradlew idea # Generating Intellij IDEA project files
$ gradlew eclipse # Generating Eclipse project files
$ gradlew groovydoc # Generating GroovyDoc
```

## Downloads

From public subversion directory https://svn.lspi.wiai.uni-bamberg.de/svn/betsy/

## Project Structure

    downloads/ # downloads of the engines
    server/ # engine installation directory
    test/ # execution results and reports
    src/main/tests/ # the bpel, wsdl, xsd files and test configuration
    src/main/xslt/[engine/] # common and engine specific xslt scripts
    src/main/resources/[engine/] # common and engine specific xsds and other resources
    src/main/groovy # the main source code
    src/main/java # mock web service implementation

## Test Structure

	test/
	test/reports/
    test/reports/html/ # html junit reports
	test/$engine/
	test/$engine/$process/
	test/$engine/$process/bpel/ # bpel file(s), wsdl file(s), xsd file(s)
	test/$engine/$process/pgk/ # deployable zip files
	test/$engine/$process/soapui/ # soapUI test suite
	test/$engine/$process/reports/ # soapUI test reports

    Optional directories
	[test/$engine/$process/binding/ # binding package]
	[test/$engine/$process/composite/ # composite package]

# Authors (in alphabetical order)

[Simon Harrer](http://www.uni-bamberg.de/pi/team/harrer/) and [Joerg Lenhard](http://www.uni-bamberg.de/pi/team/lenhard-joerg/)

# Contribution Guide

- Fork
- Send Pull Request
