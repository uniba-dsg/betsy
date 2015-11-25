package timeouts.timeout;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Timeout {

    private final String engineOrProcessGroup;
    private final String stepOrProcess;
    private Optional<String> description;
    private Integer value;
    private Optional<Integer> timeToRepetition;

    /**
     * @param engineOrProcess The {@link betsy.common.analytics.model.Engine} or the processgroup, where the {@link Timeout} is located.
     * @param stepOrProcess   The method of the engine or the {@link Process}, where the {@link Timeout} is located.
     * @param value           The value of the {@link Timeout} in milliseconds.
     */
    public Timeout(String engineOrProcess, String stepOrProcess, int value) {
        this.engineOrProcessGroup = engineOrProcess;
        this.stepOrProcess = stepOrProcess;
        this.description = Optional.empty();
        this.value = value;
        this.timeToRepetition = Optional.empty();
    }

    /**
     * @param engineOrProcess  The {@link betsy.common.analytics.model.Engine} or the processgroup, where the {@link Timeout} is located.
     * @param stepOrProcess    The method of the engine or the {@link Process}, where the {@link Timeout} is located.
     * @param description      The description of the {@link Timeout}.
     * @param value            The value of the {@link Timeout} in milliseconds.
     * @param timeToRepetition The time to wait till repetition, if the {@link Timeout} is exceeded.
     */
    public Timeout(String engineOrProcess, String stepOrProcess, String description, int value, int timeToRepetition) {
        this(engineOrProcess, stepOrProcess, value);
        this.description = Optional.of(description);
        this.timeToRepetition = Optional.of(timeToRepetition);
    }

    /**
     * @param engineOrProcess The {@link betsy.common.analytics.model.Engine} or the processgroup, where the {@link Timeout} is located.
     * @param stepOrProcess   The method of the engine or the {@link Process}, where the {@link Timeout} is located.
     * @param description     The description of the {@link Timeout}.
     * @param value           The value of the {@link Timeout} in milliseconds.
     */
    public Timeout(String engineOrProcess, String stepOrProcess, String description, int value) {
        this(engineOrProcess, stepOrProcess, value);
        this.description = Optional.of(description);
    }

    /**
     * @param engineOrProcess  The {@link betsy.common.analytics.model.Engine} or the processgroup, where the {@link Timeout} is located.
     * @param stepOrProcess    The method of the engine or the {@link Process}, where the {@link Timeout} is located.
     * @param value            The value of the {@link Timeout} in milliseconds.
     * @param timeToRepetition The time to wait till repetition, if the {@link Timeout} is exceeded.
     */
    public Timeout(String engineOrProcess, String stepOrProcess, int value, int timeToRepetition) {
        this(engineOrProcess, stepOrProcess, value);
        this.timeToRepetition = Optional.of(timeToRepetition);
    }

    /**
     * @return Returns the key of the {@link Timeout}.
     */
    public String getKey() {
        StringBuilder key = new StringBuilder(engineOrProcessGroup).append('.').append(stepOrProcess);
        if (description.isPresent() && description.get().length() > 0) {
            return key.append('.').append(description.get()).toString();
        } else {
            return key.toString();
        }
    }


    /**
     * @return Returns the engineOrProcess of the {@link Timeout}.
     */
    public String getEngineOrProcessGroup() {
        return engineOrProcessGroup;
    }


    /**
     * @return Returns the stepOrProcess of the {@link Timeout}.
     */
    public String getStepOrProcess() {
        return stepOrProcess;
    }

    /**
     * @return Returns the description of the {@link Timeout}.
     */
    public String getDescription() {
        return description.orElse("");
    }

    /**
     * @param value The value of the {@link Timeout} in ms.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return Returns the value of the {@link Timeout} in minutes as {@link BigDecimal}.
     */
    public BigDecimal getTimeoutInMinutes() {
        return new BigDecimal(value.doubleValue() / 1000 / 60);
    }

    /**
     * @return Returns the value of the {@link Timeout} in seconds.
     */
    public BigDecimal getTimeoutInSeconds() {
        return new BigDecimal(value.doubleValue() / 1000);
    }

    /**
     * @return Returns the value of the {@link Timeout} in milliseconds.
     */
    public int getTimeoutInMs() {
        return value;
    }

    /**
     * @param timeToRepetition The timeToRepetition of the {@link Timeout} in ms.
     */
    public void setTimeToRepetition(Integer timeToRepetition) {
        this.timeToRepetition = Optional.ofNullable(timeToRepetition);
    }

    /**
     * @return Returns the time in minutes to wait till repetition, if the {@link Timeout} is exceeded.
     */
    public BigDecimal getTimeToRepetitionInMinutes() {
        return new BigDecimal(timeToRepetition.orElse(0).doubleValue() / 1000 / 60);
    }

    /**
     * @return Returns the time in seconds to wait till repetition, if the {@link Timeout} is exceeded.
     */
    public BigDecimal getTimeToRepetitionInSeconds() {
        return new BigDecimal(timeToRepetition.orElse(0).doubleValue() / 1000);
    }

    /**
     * @return Returns the time in ms to wait till repetition, if the {@link Timeout} is exceeded.
     */
    public int getTimeToRepetitionInMs() {
        return timeToRepetition.orElse(0);
    }

}
