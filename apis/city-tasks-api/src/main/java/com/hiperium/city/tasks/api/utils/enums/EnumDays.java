package com.hiperium.city.tasks.api.utils.enums;

import java.util.Arrays;

public enum EnumDays {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    public static EnumDays getEnumFromString(String dayOfWeek) {
        return Arrays.stream(EnumDays.values())
                .filter(enumDays -> enumDays.name().equals(dayOfWeek))
                .findFirst()
                .orElse(null);
    }
}
