package betsy.data;

public class TestStep {
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * just for documentation purposes
     */
    private String description;

    @Override
    public String toString() {
        return "TestStep{" +
                "description='" + description + '\'' +
                '}';
    }
}
