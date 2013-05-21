// #############################
//   ALL TIMES ARE IN SECONDS
// #############################

PARTNER_IP_AND_PORT = "0.0.0.0:2000"

virtualisation {
	vbox {
		// The folder containing the binary files
		path = "C:\\Program Files\\Oracle\\VirtualBox\\"
		vboxmanage = "VBoxManage.exe"
		vboxwebsrv = "VBoxWebSrv.exe"

		websrv {
			host = "http://127.0.0.1"
			port = "18083"
			user = "user"
			password = "password"
			// time in seconds to wait on VBoxWebSrv startup
			wait = 3
		}
	}

	bvms {
		// time in seconds, defaults to 120
		requestTimeout = 120
	}

	engines {
		example_engine {
			// supported formats are .ova, .zip and .tar.bz2
			// NO DEFAULT ! MUST BE SET !
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/example.tar.bz2"
			// time in seconds
			serviceTimeout = 300
			// Where the pkg files should be copied to
			// Default different per engine
			deploymentDir = "/usr/share/tomcat7/bpr"
			// How long to wait on deployment process
			deploymentTimeout = 30
			// Where the engines logfiles can be collected from
			// Default different per engine
			logfileDir = "/var/lib/tomcat7/logs"
			// Running vbox without the gui?
			headless = false
			// Saving the vm's state instead of poweringOff?
			shutdownSaveState = false
			// Where the bVMS application is installed
			bvmsDir = "/opt/betsy/"
		}
		active_bpel_v {
			download = "ftp://141.13.162.58/betsy-active_bpel_v.ova"
			deploymentDir = "/usr/share/tomcat5.5/bpr"
			logfileDir = "/usr/share/tomcat5.5/logs"
			headless = true
			shutdownSaveState = false
		}
		bpelg_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-bpelg_v.ova"
			deploymentDir = "/usr/share/tomcat7/bpr"
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}
		ode_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-ode_v.ova"
			deploymentDir = "/var/lib/tomcat7/webapps/ode/WEB-INF/processes"
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}
		openesb_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-openesb_v.ova"
			deploymentFile = "/opt/openesb/glassfish/bin/asadmin"
			logfileDir = "/opt/openesb/glassfish/domains/domain1/logs"
			headless = true
			shutdownSaveState = false
		}
		orchestra_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-orchestra_v.ova"
			deploymentDir = "/home/betsy/orchestra-cxf-tomcat"
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}
		petalsesb_v {
			// Only working from inside the University of Bamberg
			download = "ftp://141.13.162.58/betsy-petalsesb_v.ova"
			deploymentDir = "/opt/petalsesb/install"
			logfileDir = "/opt/petalsesb/logs"
			headless = true
			shutdownSaveState = false
		}
	}
}
