partner {
    ipAndPort = "0.0.0.0:2000"
}

downloads {
    dir = "downloads"
}

soapui {
    container = "soapui"
    home = "soapui/SoapUI-4.6.1"
    download {
        url = "https://lspi.wiai.uni-bamberg.de/svn/betsy/SoapUI-4.6.1-windows-bin.zip"
        filename = "SoapUI-4.6.1-windows-bin.zip"
    }
}

ant {
    container = "ant"
    home = "ant/apache-ant-1.9.2"
    download {
        url = "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-ant-1.9.2-bin.zip"
        filename = "apache-ant-1.9.2-bin.zip"
    }
}

virtualisation {
	vbox {
		// The folder containing the binary files
		home = "C:\\Program Files\\Oracle\\VirtualBox\\"

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
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-active_bpel_v.ova"
			deploymentDir = "/usr/share/tomcat5.5/bpr"
			logfileDir = "/usr/share/tomcat5.5/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
		bpelg_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-bpelg_v.ova"
			deploymentDir = "/usr/share/tomcat7/bpr"
			logfileDir = "/var/lib/tomcat7/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
		ode_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-ode_v.ova"
			deploymentDir = "/var/lib/tomcat7/webapps/ode/WEB-INF/processes"
			logfileDir = "/var/lib/tomcat7/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
		openesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-openesb_v.ova"
			deploymentFile = "/opt/openesb/glassfish/bin/asadmin"
			logfileDir = "/opt/openesb/glassfish/domains/domain1/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
		orchestra_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-orchestra_v.ova"
			deploymentDir = "/home/betsy/orchestra-cxf-tomcat"
			logfileDir = "/var/lib/tomcat7/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
		petalsesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-petalsesb_v.ova"
			deploymentDir = "/opt/petalsesb/install"
			logfileDir = "/opt/petalsesb/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
		}
	}
}
