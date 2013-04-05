// #############################
//   ALL TIMES ARE IN SECONDS
// #############################

PARTNER_IP_AND_PORT = "0.0.0.0:2000"

virtualisation {

	partnerIp = "10.0.2.2"
	partnerPort = "2000"

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
		requestTimeout = 60
	}

	engines {
		example_engine {
			// supported formats are .ova, .zip and .tar.bz2
			// NO DEFAULT !
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/example.tar.bz2"

			// time in seconds
			// Default: 300
			serviceTimeout = 300

			// a valid MAC address, groups seperated with :
			// NO DEFAULT !
			// if set, the mac address will be applied to the first network adapter (eth0)
			// --> /etc/udev/rules.d/70-persistent-net.rules must not be deleted manually
			mac = "08:00:27:c9:86:06"

			// Where the pkg files should be copied to
			// Default different per engine
			deploymentDir = "/usr/share/tomcat7/bpr"

			// How long to wait on deployment process
			// Default: 15
			deploymentTimeout = 15

			// Where the engines logfiles can be collected from
			// Default different per engine
			logfileDir = "/var/lib/tomcat7/logs"

			// Running vbox without the gui?
			// Default: false
			headless = true

			// Saving the vm's state instead of poweringOff?
			// Default: false
			shutdownSaveState = true
		}

		active_bpel_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-activebpel_v.tar.bz2"
			serviceTimeout = 300
			mac = "08:00:27:e5:61:b6"
			deploymentDir = "/usr/share/tomcat5.5/bpr"
			deploymentTimeout = 30
			logfileDir = "/usr/share/tomcat5.5/logs"
			headless = true
			shutdownSaveState = true
		}

		bpelg_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-bpelg_v.ova"
			serviceTimeout = 240
			mac = "08:00:27:5a:9d:70"
			deploymentDir = "/usr/share/tomcat7/bpr"
			deploymentTimeout = 15
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = true
		}

		ode_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-ode_v.tar.bz2"
			serviceTimeout = 300
			mac = "08:00:27:6f:6c:5a"
			deploymentDir = "/var/lib/tomcat7/webapps/ode/WEB-INF/processes"
			deploymentTimeout = 15
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = true
		}

		openesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-openesb_v.tar.bz2"
			serviceTimeout = 300
			mac = "08:00:27:ce:1e:55"
		}

		orchestra_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-orchestra_v.tar.bz2"
			serviceTimeout = 300
			mac = "08:00:27:5c:76:8a"

			deploymentDir = "/home/betsy/orchestra-cxf-tomcat"
			deploymentTimeout = 15
			logfileDir = "/var/lib/tomcat7/logs"
			headless = true
			shutdownSaveState = false
		}

		petalsesb_v {
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-petalsesb_v.tar.bz2"
			serviceTimeout = 300
			mac = "08:00:27:eb:f4:c8"
		}
	}
}

test_setup {
	run_ode = false
	run_bpelg = false
	run_activebpel = false
	run_orchestra = false
	run_openesb = false
	run_petalsesb = false

	run_ode_virtualized = false 
	run_bpelg_virtualized = false
	run_activebpel_virtualized = false
	run_orchestra_virtualized = false
	run_openesb_virtualized = false
	run_petalsesb_virtualized = false
}
