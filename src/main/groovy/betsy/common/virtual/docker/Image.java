package betsy.common.virtual.docker;

import java.util.Objects;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a docker {@link Image}.
 *
 */
public class Image {

    private String id;
    private String name;

    /**
     *
     * @param id The id of the {@link Image}.
     * @param name The name of {@link Image}.
     */
    public Image(String id, String name){
        this.id = Objects.requireNonNull(id, "The id can't be null.");
        this.name = Objects.requireNonNull(name, "The name can't be null.");
    }

    /**
     *
     * Returns the id of the {@link Image}.
     *
     * @return Returns the id as {@link String}.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the {@link Image}.
     *
     * @return Returns the name as {@link String}.
     */
    public String getName() {
        return name;
    }
}
