package betsy.bpel.model;

public class BPELIdShortener {

    public static final int SHORT_ENOUGH_ID_LENGTH = 8;
    private final String name;

    public BPELIdShortener(String name) {
        this.name = name;
    }

    public String getShortenedId() {
        // already short names are OK as they are
        if (name.length() < SHORT_ENOUGH_ID_LENGTH) {
            return name;
        }

        // abbreviate common names
        String name = this.name.replaceAll("Receive", "REC");
        name = name.replaceAll("Rec", "REC");
        name = name.replaceAll("Request", "REQ");
        name = name.replaceAll("Req", "REQ");
        name = name.replaceAll("Reply", "REP");
        name = name.replaceAll("Invoke", "INV");

        name = name.replaceAll("Stop", "STP");

        name = name.replaceAll("ForEach", "FE");
        name = name.replaceAll("For", "FOR");

        return name.replaceAll("[a-z]", "");
    }
}
