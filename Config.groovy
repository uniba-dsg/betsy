PARTNER_IP_AND_PORT = "0.0.0.0:2000"

virtualisation {

	partnerIp = "10.0.2.2"

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
		active-bpel_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-activebpel_v.tar.bz2"
			// time in seconds
			serviceTimeout = 300
			mac = "08:00:27:c9:86:06"
		}

		bpelg_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-bpelg_v.ova"
			// time in seconds
			serviceTimeout = 240
			// if set, the mac address will be applied to the first network adapter (eth0)
			// --> /etc/udev/rules.d/70-persistent-net.rules must not be deleted manually
			//mac = "08:00:27:c4:07:74"
			mac = "08:00:27:5a:9d:70"
			deploymentDir = "/usr/share/tomcat7/bpr"
			// time in seconds
			deploymentTimeout = 10

			// DEFAULT
			logfileDir = "/var/lib/tomcat7/logs"

			// DEFAULT: false
			headless = true
		}

		ode_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-ode_v.tar.bz2"
			// time in seconds
			serviceTimeout = 300
			mac = "08:00:27:6f:6c:5a"
		}

		openesb_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-openesb_v.tar.bz2"
			// time in seconds
			serviceTimeout = 300
			mac = "08:00:27:ce:1e:55"
		}

		orchestra_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-orchestra_v.tar.bz2"
			// time in seconds
			serviceTimeout = 300
			mac = "08:00:27:5c:76:8a"
		}

		petalsesb_v {
			// supported formats are .ova, .zip and .tar.bz2
			download = "https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-petalsesb_v.tar.bz2"
			// time in seconds
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
