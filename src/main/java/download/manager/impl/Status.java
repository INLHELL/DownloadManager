package download.manager.impl;

public enum Status {

    CREATED("Created"),

    DOWNLOADING("Downloading"),

    PAUSED("Paused"),

    COMPLETED("Completed"),

    CANCELLED("Cancelled"),

    ERROR("Error");

    private final String value;

    private Status(String value) {
	this.value = value;
    }

    public boolean equalsName(String otherValue) {
	return (otherValue == null) ? false : value.equals(otherValue);
    }

    @Override
    public String toString() {
	return value;
    }

}
