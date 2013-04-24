// #############################
//   ALL TIMES ARE IN SECONDS
// #############################

PARTNER_IP_AND_PORT = "0.0.0.0:2000"

virtualisation {

	vbox {
		// The folder containing the binary files
		path = "/Applications/VirtualBox.app/Contents/MacOS/"
		vboxmanage = "VBoxManage"
		vboxwebsrv = "vboxwebsrv"

		websrv {
			host = "http://127.0.0.1"
			port = "18083"
			user = "user"
			password = "password"
			// time in seconds to wait on VBoxWebSrv startup
			wait = 5
		}
	}

	bvms {
		installationDir = "/opt/betsy/"
		// time in seconds, defaults to 120
		requestTimeout = 120
	}

	engines {
		example_engine {
			// supported formats are .ova, .zip and .tar.bz2
			// NO DEFAULT !
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/example.tar.bz2"

			// time in seconds
			// Default: 300
			serviceTimeout = 300

			// Where the pkg files should be copied to
			// Default different per engine
			deploymentDir = "/usr/share/tomcat7/bpr"

			// How long to wait on deployment process
			// Default: 45
			deploymentTimeout = 45

			// Where the engines logfiles can be collected from
			// Default different per engine
			logfileDir = "/var/lib/tomcat7/logs"

			// Running vbox without the gui?
			// Default: false
			headless = false

			// Saving the vm's state instead of poweringOff?
			// Default: false
			shutdownSaveState = false
		}

		active_bpel_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-activebpel_v.tar.bz2"
			serviceTimeout = 300
			deploymentDir = "/usr/share/tomcat5.5/bpr"
			deploymentTimeout = 30
			logfileDir = "/usr/share/tomcat5.5/logs"
			headless = true
			shutdownSaveState = false
		}

		bpelg_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-bpelg_v.ova"
			serviceTimeout = 240
			deploymentDir = "/usr/share/tomcat7/bpr"
			deploymentTimeout = 30
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}

		ode_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-ode_v.ova"
			serviceTimeout = 300
			deploymentDir = "/var/lib/tomcat7/webapps/ode/WEB-INF/processes"
			deploymentTimeout = 30
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}

		openesb_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-openesb_v.ova"
			serviceTimeout = 300
			deploymentExecutable = "/opt/openesb/glassfish/bin/asadmin"
			deploymentTimeout = 30
			logfileDir = "/opt/openesb/glassfish/domains/domain1/logs"
			headless = true
			shutdownSaveState = false
		}

		orchestra_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-orchestra_v.ova"
			serviceTimeout = 300
			deploymentDir = "/home/betsy/orchestra-cxf-tomcat"
			deploymentTimeout = 30
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}

		petalsesb_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-petalsesb_v.ova"
			serviceTimeout = 300
			deploymentDir = "/opt/petalsesb/install"
			deploymentTimeout = 30
			logfileDir = "/opt/petalsesb/logs"
			headless = true
			shutdownSaveState = false
		}
	}
}
