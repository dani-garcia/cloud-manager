package com.cloudmanager.core.services.login;

public class LoginField {

    public enum FieldType {
        PLAIN_TEXT, INPUT, OUTPUT
    }

    private String name, value;

    private FieldType type;

    public LoginField(FieldType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
