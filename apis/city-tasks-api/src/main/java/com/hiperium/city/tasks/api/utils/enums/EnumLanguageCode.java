package com.hiperium.city.tasks.api.utils.enums;

public enum EnumLanguageCode {

    EN("en"),
    ES("es");

    private final String code;

    EnumLanguageCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
