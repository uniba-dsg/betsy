package betsy.executables.analytics.textables

class BpelNodeCounter {

    private static final String[] commonNodes = ["process", "partnerLinks", "partnerLink", "variables", "import", "variable"]

    /**
     * Nodes that are present anyway, given more important nodes are there
     */
    private static final String[] minorNodes = ["copy", "from", "to", "for", "until", "literal", "toPart", "messageExchange",
            "fromPart", "correlation", "correlations", "correlationSet", "condition", "extension", "ex:foo", "addr:Address",
            "foo:barEPR", "documentation", "finalCounterValue", "startCounterValue", "branches", "source", "sources", "target",
            "targets", "link"]

    /**
     * Computes a set of all node names
     *
     * @return a list of all node names
     */
    private static SortedSet<String> getNodes(File file) {
        def xml = new XmlSlurper(false, false).parse(file)

        xml.depthFirst().collect { it.name().trim() }.unique().sort() as SortedSet
    }

    private static SortedSet<String> getUncommonNodes(File file) {
        def nodes = getNodes(file)
        nodes.removeAll(minorNodes)
        nodes.removeAll(commonNodes)

        nodes
    }

    public static String getUncommonNames(File file) {
        getUncommonNodes(file).join(" ")
    }

}
