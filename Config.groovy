PARTNER_IP_AND_PORT = "0.0.0.0:2000"

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