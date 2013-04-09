package betsy.util


class IPAddress {

	private static String publicAddress;

	public static String getPublicAddress() {
		if (publicAddress == null) {
			def host = 'http://www.comitservices.com/ip.php'
			try {
				def results = new URL(host).getText()
				println "According to '${host}', this computer's IP address is: ${results}"
				publicAddress = results
			} catch (Exception e) {
				println "Sorry, there was a problem connecting to: ${host}: ${e.toString()}"
				publicAddress = "localhost"
			}
		}

		return publicAddress
	}

	private static InetAddress getLocalInetAddress() {
		String hostName = InetAddress.getLocalHost().getHostName();
		InetAddress[] addrs = InetAddress.getAllByName(hostName);
		for (InetAddress addr: addrs) {
			if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
				return addr;
			}
		}
	}
	
	public static String getLocalAddress() {
		// return resolved value
		return getLocalInetAddress().hostAddress
	}
	
}