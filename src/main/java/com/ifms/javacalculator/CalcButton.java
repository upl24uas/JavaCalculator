/*
 * Copyright (c) June 2024
 */
package com.ifms.javacalculator;

import javax.swing.JButton;

/**
 *
 * @author adhep
 */
public class CalcButton {
    String name;
    int type;
    String display;
    int pref;

    public CalcButton() {
    }

    public CalcButton(JButton jButton, String name, int type, String display, int pref) {
        this.name = name;
        this.type = type;
        this.display = display;
        this.pref = pref;
    }

    boolean isNumber() {
        return type == 0;
    }

    boolean isUnaryOp() {
        return type == 1;
    }

    boolean isBinaryOp() {
        return type == 2;
    }
}
