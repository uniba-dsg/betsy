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

virtual {
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

    // development option: set to true for using an active VM instead of loading a snapshot
    useRunningVM = false

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
            logfileDir = "/var/log/tomcat7"
			// Running vbox without the gui?
			headless = false
			// Saving the vm's state instead of poweringOff?
			shutdownSaveState = false
			// Where the bVMS application is installed
			bvmsDir = "/opt/betsy"

            serviceTimeout = 300
		}
		active_bpel_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-active_bpel_v.ova"
			deploymentDir = "/usr/share/tomcat5.5/bpr"
            deploymentLogFile = "/home/tomcat55/AeBpelEngine/deployment-logs/aeDeployment.log"
			logfileDir = "/usr/share/tomcat5.5/logs"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy"
            serviceTimeout = 30
		}
		bpelg_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-bpelg_v.ova"
			deploymentDir = "/usr/share/tomcat7/bpr"
            deploymentLogFile = "/var/log/tomcat7/bpelg.log"
            logfileDir = "/var/log/tomcat7"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy"
            serviceTimeout = 30
		}
		ode_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-ode_v.ova"
			deploymentDir = "/var/lib/tomcat7/webapps/ode/WEB-INF/processes"
            deploymentLogFile = "/var/log/tomcat7/ode.log"
            logfileDir = "/var/log/tomcat7"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy"
            serviceTimeout = 30
		}
		openesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-openesb_v.ova"
			deploymentFile = "/opt/openesb/glassfish/bin/asadmin"
			logfileDir = "/opt/openesb/glassfish/domains/domain1/logs"
            deploymentDir = ""
            deploymentTimeout = 30
            deploymentLogFile = ""
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy"
            serviceTimeout = 30
		}
		orchestra_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-orchestra_v.ova"
			deploymentDir = "/home/betsy/orchestra-cxf-tomcat"
			logfileDir = "/var/log/tomcat7"
            deploymentLogFile = ""
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy/"
            serviceTimeout = 30
		}
		petalsesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/ova/betsy-petalsesb_v.ova"
			deploymentDir = "/opt/petalsesb/install"
			logfileDir = "/opt/petalsesb/logs"
            deploymentLogFile = "/opt/petalsesb/logs/petals.log"
            deploymentTimeout = 30
			headless = true
			shutdownSaveState = false
            bvmsDir = "/opt/betsy"
            serviceTimeout = 30
		}
	}
}
