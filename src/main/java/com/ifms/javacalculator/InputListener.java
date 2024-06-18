/*
 * Copyright (c) June 2024
 */
package com.ifms.javacalculator;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.JLabel;

/**
 *
 * @author adhep
 */
public class InputListener {
    static final double EPSILON = 0.000000000001;
    int brackState = 0;
    static boolean result = false;
    JLabel display;
    JLabel expression;
    StringBuilder expString = new StringBuilder();
    Deque<CalcButton> expArray = new ArrayDeque<>();

    private int decimal(String text) {
        return text.indexOf('.') + 1;
    }

    private void displayExpression(CalcButton calcButton) {
        expString.append(calcButton.display);
        display.setText("");
        if (expString.length() > 64) {
            expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
        } else {
            expression.setText(expString.toString());
        }
    }

    private void displayExpression() {
        if (expString.length() > 64) {
            expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
        } else {
            expression.setText(expString.toString());
        }
    }

    public InputListener() {
    }

    public InputListener(JLabel display, JLabel expression) {
        this.display = display;
        this.expression = expression;
    }

    void action(CalcButton calcButton) {
        System.out.println(expString);
        if (result) {
            result = false;
            display.setText("");
            expression.setText("");
            expString = new StringBuilder();
        }
        System.out.println(expString);
        switch (calcButton.name) {
            case "pi":
                if (display.getText().isEmpty()) {
                    display.setText(String.valueOf(Math.PI));
                }
                break;
            case "clearAll":
                display.setText("");
                expression.setText("");
                expString = new StringBuilder();
                expArray.clear();
                break;
            case "clearInput":
                display.setText("");
                break;
            case "pm":
                if (!display.getText().contains("-")) {
                    display.setText("-" + display.getText());
                } else if (display.getText().equals("-")) {
                    display.setText("");
                } else {
                    display.setText(display.getText().substring(1, display.getText().length()));
                }
                break;
            case "delete":
                delete();
                return;
            case "equal":
                if (display.getText().isEmpty() && expression.getText().isEmpty()) {
                    return;
                }
                if (display.getText().equals("-")) {
                    display.setText("0");
                }
                if (display.getText().length() > 0) {
                    expArray.addLast(new CalcButton(null, "number", 0, display.getText(), 0));
                    expString.append(display.getText());
                }
                while (expArray.peekLast().isBinaryOp()
                        || expArray.peekLast().isUnaryOp()) {
                    delete();
                }
                displayExpression(calcButton);
                display.setText("");
                CalcButton output;
                try {
                    output = new ExpressionSolver().ShuntingYard(expArray);
                } catch (java.lang.NumberFormatException | java.lang.ArithmeticException e) {
                    output = null;
                }
                if (output == null || Double.isInfinite(Double.parseDouble(output.display))) {
                    display.setText("ERROR: OVERFLOW OR ...");
                    result = true;
                } else {
                    Double num = Double.valueOf(output.display);
                    if (num <= Double.valueOf(Long.toString(Long.MAX_VALUE))
                            && num - num.longValue() < EPSILON) {
                        display.setText(Long.toString(num.longValue()));
                    } else {
                        display.setText(Double.toString(num));
                    }
                }
                result = true;
                expArray.clear();
                System.out.println(expString);
                return;
            case "closeBrack":
                if (display.getText().isEmpty()) {
                    boolean ret = true;
                    if (!expArray.isEmpty()) {
                        if ("closeBracknumber".contains(expArray.peekLast().name)) {
                            if (brackState > 0) {
                                ret = false;
                            }
                        }
                    }
                    if (ret) {
                        return;
                    }
                }
                if (brackState
                        > 0) {
                    brackState--;
                } else {
                    return;
                }

                expString.append(display.getText());
                if (!display.getText().isEmpty()) {
                    expArray.addLast(new CalcButton(null, "number", 0, display.getText(), 0));
                }

                expArray.addLast(calcButton);

                display.setText("");
                displayExpression(calcButton);

                return;
            case "openBrack":
                if (!display.getText().isEmpty()) {
                    return;
                }
                displayExpression(calcButton);

                expArray.addLast(calcButton);
                brackState++;
                return;
            case "point":
                if (!expArray.isEmpty()) {
                    if ("closeBrack".contains(expArray.peekLast().name)) {
                        return;
                    }
                }

                if (display.getText()
                        .length() == 0) {
                    display.setText("0.");
                }

                if (!(display.getText()
                        .contains("."))) {
                    if (display.getText().length() < 42) {
                        display.setText(display.getText() + calcButton.display);
                    }
                }

                return;
            case "sqr":
            case "fact":
                if (display.getText().isEmpty()) {
                    return;
                } else {
                    if(!expArray.isEmpty() && expArray.peekLast().isUnaryOp())
                        return;
                    expString.append(display.getText()).append(calcButton.display);
                }
                expArray.addLast(calcButton);
                expArray.addLast(new CalcButton(null, "number", 0, display.getText(), 0));
                display.setText("");
                if (expString.length() > 64) {
                    expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
                } else {
                    expression.setText(expString.toString());
                }
                return;
        }
        if (calcButton.isUnaryOp()) {
            if (!(display.getText().length() == 0)) {
                return;
            }
            if (!expArray.isEmpty()) {
                if ("closeBracksqr".contains(expArray.peekLast().name)) {
                    return;
                }
                if (expArray.peekLast().isUnaryOp()) {
                    int len = expArray.peekLast().display.length();
                    expArray.pollLast();
                    expString.delete(expString.length() - len, expString.length());
                }
            }

            displayExpression(calcButton);
            display.setText("");
            expArray.addLast(calcButton);
        } else if (calcButton.isBinaryOp()) {
            if (display.getText().length() == 0) {
                if (!expArray.isEmpty()) {
                    if (!("closeBrack".contains(expArray.peekLast().name))) {
                        if (expArray.peekLast().isBinaryOp()) {
                            int len = expArray.peekLast().display.length();
                            expArray.pollLast();
                            expString.delete(expString.length() - len, expString.length());
                        } else if (!(expArray.peekLast().isNumber())) {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }

            if (display.getText().length() > 0) {
                expArray.addLast(new CalcButton(null, "number", 0, display.getText(), 0));
            }
            expString.append(display.getText());
            expArray.addLast(calcButton);
            display.setText("");
            displayExpression(calcButton);
        }

        if (calcButton.isNumber()) {
            if (!expArray.isEmpty() && expArray.peekLast().isNumber()) {
                return;
            }
            if (!expArray.isEmpty()) {
                if ("closeBrack".contains(expArray.peekLast().name)) {
                    return;
                }
            }
            if (display.getText().compareTo("0") == 0) {
                display.setText("");
            }

            if (decimal(display.getText()) == 0) {
                if (display.getText().length() < 18) {
                    display.setText(display.getText() + calcButton.display);
                }
            } else if (display.getText().length() < 42) {
                display.setText(display.getText() + calcButton.display);
            }
        }
    }

    private void delete() {
        if (expression.getText().isEmpty() && display.getText().isEmpty()) {
            return;
        }
        if (!display.getText().isEmpty()) {
            display.setText(display.getText().substring(0, display.getText().length() - 1));
        } else {
            if (expArray.peekLast().isNumber()) {
                display.setText(expArray.pollLast().display);
                int len = display.getText().length();
                //factorial and square postfix operator
                if (!expArray.isEmpty() && expArray.peekLast().isUnaryOp()
                        && "sqrfact".contains(expArray.peekLast().name)) {
                    len += expArray.pollLast().display.length();
                }
                expString.delete(expString.length() - len, expString.length());
                displayExpression();
            } else if (expArray.peekLast().isUnaryOp() || expArray.peekLast().isBinaryOp()) {
                int len = expArray.peekLast().display.length();
                expArray.pollLast();
                expString.delete(expString.length() - len, expString.length());
                displayExpression();
            }
        }
    }    
}
