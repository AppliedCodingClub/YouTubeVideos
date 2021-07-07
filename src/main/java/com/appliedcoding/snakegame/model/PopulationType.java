package com.appliedcoding.snakegame.model;

import com.appliedcoding.snakegame.exception.SnakeException;

public enum PopulationType {
    Type1(1), Type2(2), Type3(3), Type4(4), Type5(5), Type6(6), Type7(7);

    private int type;

    PopulationType(int type) {
        this.type = type;
    }

    public static PopulationType fromInt(int type) {
        PopulationType[] values = values();
        if (type < 1 || type > values.length) {
            throw new SnakeException("Invalid PopulationType " + type);
        }

        return values[type - 1];
    }

    public int toInt() {
        return type;
    }
}
