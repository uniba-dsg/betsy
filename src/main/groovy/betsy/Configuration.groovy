package betsy

class Configuration {

    public static ConfigObject config = new ConfigSlurper().parse(new File("Config.groovy").toURI().toURL())

}
