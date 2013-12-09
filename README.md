# betsy (BPEL Engine Test System)

Betsy is a tool to check the degree of conformance of a BPEL engine against the BPEL standard.
More information can be found in the [technical report](https://svn.lspi.wiai.uni-bamberg.de/svn/betsy/techrep-betsy-final.pdf).
A [sample output](https://svn.lspi.wiai.uni-bamberg.de/svn/betsy/test-results.zip) of a run of betsy are also available.
The tool has been subject to the paper [BPEL Conformance in Open Source Engines](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/12-02-lenhard-wirtz-harrer/) and [presentation](https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-presentation-soca-2012.pdf) for which these [test results](https://svn.lspi.wiai.uni-bamberg.de/svn/betsy/test-results-soca-2012.zip) have been used.

This software is licensed under the LGPL Version 3 Open Source License.

## Releases

- [Release December 2013 (latest)](https://github.com/uniba-dsg/betsy/archive/icsoc-2013.zip)
- [Release December 2012](https://github.com/uniba-dsg/betsy/archive/soca-2012.zip)
- [Release July 2012](https://github.com/uniba-dsg/betsy/archive/techrep-july-2012.zip)

## Software Requirements
- Windows 7
- JDK 1.7.0_03 (64 Bit) or higher
  - `JAVA_HOME` should point to the jdk directory
  - `PATH` should include `JAVA_HOME/bin`

## Licensing
LGPL Version 3: http://www.gnu.org/licenses/lgpl-3.0.html

## Usage

Requirements (see above) have to be fulfilled to execute `betsy` on the command line.

See `Config.groovy` for more detailed configuration options.

```
usage: betsy [options] <engines> <processes>

Options:
 -p,--partner-address <ip-and-port>    Partner IP and Port (defaults to
                                       141.13.4.93:2000)
 -t,--to-core-bpel <transformations>   Transform to Core BPEL
 -o,--open-results-in-browser          Opens results in default browser
 -c,--check-deployment                 Verifies deployment instead of test
                                       success
 -h,--help                             Print out usage information

GROUPS for <engines> and <processes> are in CAPITAL LETTERS.
<engines>:
LOCALS (install and execute all engines locally),
RECENT (install and execute all engines, in their most recent versions only, locally)
VMS (install and execute all engines in virtual machines),
ALL (install and execute all engines, in all versions supported, locally and in virtual machines),
ode, bpelg, openesb, petalsesb, orchestra,
active-bpel, openesb23, petalsesb41, ode_v, bpelg_v, openesb_v,
petalsesb_v, orchestra_v, active_bpel_v

<processes>: ALL, BASIC_ACTIVITIES_WAIT, BASIC_ACTIVITIES_THROW,
BASIC_ACTIVITIES_RECEIVE, BASIC_ACTIVITIES_INVOKE,
BASIC_ACTIVITIES_ASSIGN, BASIC_ACTIVITIES, SCOPES_EVENT_HANDLERS,
SCOPES_FAULT_HANDLERS, SCOPES, STRUCTURED_ACTIVITIES_FLOW,
STRUCTURED_ACTIVITIES_IF, STRUCTURED_ACTIVITIES_FOR_EACH,
STRUCTURED_ACTIVITIES_PICK, STRUCTURED_ACTIVITIES, CONTROL_FLOW_PATTERNS,
STATIC_ANALYSIS, FAULTS, WITH_EXIT_ASSERTION

# Examples
$ betsy # Running all tests for all engines
$ betsy ode # Running all tests for Apache ODE
$ betsy ode,bpelg # Running all tests for Apache ODE and bpel-g
$ betsy ALL Sequence # Running Sequence test for all engines
$ betsy ALL Sequence,While # Running Sequence and While test for all engines
$ betsy ode Sequence # Running Sequence test for Apache ODE
$ betsy ode Invoke-Catch # Running Invoke-Catch test for Apache ODE
$ betsy -t sequence.xsl,pick.xsl ode_v # Running all tests for the virtualised Apache ODE with sequence.xsl and pick.xsl CoreBPEL transformations
$ betsy -o # Opens the results in the default browser after a successful run

# Administrative gradlew tasks
$ gradlew idea # Generating Intellij IDEA project files
$ gradlew eclipse # Generating Eclipse project files
$ gradlew groovydoc # Generating GroovyDoc
```

## Downloads

From public subversion directory https://lspi.wiai.uni-bamberg.de/svn/betsy/

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

[Simon Harrer](http://www.uni-bamberg.de/pi/team/harrer/), [Joerg Lenhard](http://www.uni-bamberg.de/pi/team/lenhard-joerg/), Christian Preißinger and Cedric Röck

# Contribution Guide

- Fork
- Send Pull Request
