package com.cloudmanager.core.services.login;

/**
 * Represents a field on the login form.
 */
public class LoginField {

    /**
     * The field type.
     * <p>
     * - Plain text: Useful for information messages (In this case, only the name is used, not the value).
     * <p>
     * - Input: Useful for user input. The name represents the label and the value represents the user input value.
     * <p>
     * -Output: Useful for OAuth URLs, as it includes a copy button and selects all the text by default.
     * The name represents the label and the value represents the text to copy
     */
    public enum FieldType {
        PLAIN_TEXT, INPUT, OUTPUT
    }

    private String name, value;

    private FieldType type;

    /**
     * Constructs a field from a type, name and a value
     *
     * @param type  The type of the field
     * @param name  The name of the field
     * @param value The value of the field
     */
    public LoginField(FieldType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the field type.
     *
     * @return The type
     */
    public FieldType getType() {
        return type;
    }

    /**
     * Sets the field type.
     *
     * @param type The type
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * Returns the field name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the field name.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the field value.
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the field value.
     *
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
