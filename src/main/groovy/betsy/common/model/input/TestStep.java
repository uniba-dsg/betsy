package betsy.common.model.input;

public class TestStep {

    /**
     * just for documentation purposes
     */
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "description='" + description + '\'' +
                '}';
    }

}
