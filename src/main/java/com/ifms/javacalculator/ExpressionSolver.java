/*
 * Copyright (c) June 2024
 */
package com.ifms.javacalculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.Deque;

/**
 *
 * @author adhep
 */
public class ExpressionSolver {
    Deque<CalcButton> output = new ArrayDeque<>();
    Stack<CalcButton> stack = new Stack<>();

    public ExpressionSolver() {
    }

    CalcButton ShuntingYard(Deque<CalcButton> expression) {
        while (!expression.isEmpty()) {
            CalcButton button1 = expression.pollFirst();
            if (button1.isNumber()) {
                output.addFirst(button1);
            } else {
                while (!stack.isEmpty()) {
                    CalcButton button2 = stack.peek();
                    if ((!"exproot".contains(button1.name) && button1.pref <= button2.pref)
                            || ("exproot".contains(button1.name) && button1.pref < button2.pref)) {
                        if (!button1.name.equals("closeBrack")) {
                            if (!button2.name.equals("openBrack")) {
                                stack.pop();
                                output.addFirst(button2);
                            } else {
                                break;
                            }
                        } else {
                            if (!button2.name.equals("openBrack")) {
                                stack.pop();
                                output.addFirst(button2);
                            } else {
                                stack.pop();
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                if (!button1.name.equals("closeBrack")) {
                    stack.push(button1);
                } else {
                }
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek().name.contains("Brack")) {
                return null;
            }
            output.addFirst(stack.pop());
        }
        stack = new Stack<>();
        return solveRPN();
    }

    private CalcButton solveRPN() {
        while (!output.isEmpty()) {
            CalcButton button = output.pollLast();
            if (button.isNumber()) {
                stack.push(button);
            } else if (button.isUnaryOp()) {
                if (stack.size() < 1) {
                    return null;
                } else {
                    CalcButton number = stack.pop();
                    stack.push(evalUnary(button, number));
                }

            } else if (button.isBinaryOp()) {
                if (stack.size() < 2) {
                    return null;
                } else {
                    CalcButton num1 = stack.pop();
                    CalcButton num2 = stack.pop();
                    stack.push(evalBinary(button, num2, num1));
                }
            }
        }
        if (stack.size() == 1) {
            Double.valueOf(stack.peek().display);
            return stack.pop();
        } else {
            return null;
        }
    }

    private CalcButton evalBinary(CalcButton button, CalcButton num1, CalcButton num2) {
        BigDecimal val1 = new BigDecimal(num1.display);
        BigDecimal ret = new BigDecimal("0");
        BigDecimal val2 = new BigDecimal(num2.display);
        switch (button.name) {
            case "plus":
                ret = val1.add(val2);
                break;
            case "minus":
                ret = val1.subtract(val2);
                break;
            case "div":
                ret = val1.divide(val2, 16, RoundingMode.HALF_UP);
                break;
            case "times":
                ret = val1.multiply(val2);
                break;
            case "exp":
                if (val1.doubleValue() == 0 && val1.doubleValue() == 0) {
                    throw new java.lang.ArithmeticException();
                }
                ret = new BigDecimal(Math.pow(val1.doubleValue(), val2.doubleValue()));
                break;
            case "root":
                ret = new BigDecimal(Math.pow(val2.doubleValue(), 1 / val1.doubleValue()));
                break;
        }
        return new CalcButton(null, "number", 0, ret.toString(), 0);
    }

    private CalcButton evalUnary(CalcButton button, CalcButton num) {
        Double val = Double.valueOf(num.display);
        Double ret = 0.0;
        switch (button.name) {
            case "euler":
                ret = Math.exp(val);
                break;
            case "sqrt":
                ret = Math.sqrt(val);
                break;
            case "fact":
                String str = fact(new BigInteger(num.display)).toString();
                return new CalcButton(null, "number", 0, str, 0);
            case "inverse":
                ret = 1.0 / val;
                break;
            case "antiLog":
                ret = Math.pow(10, val);
                break;
            case "sqr":
                ret = val * val;
                break;
            case "nLog":
                ret = Math.log(val);
                break;
            case "log":
                ret = Math.log10(val);
                break;
            case "root":
                ret = Math.sqrt(val);
                break;
        }
        if ("R".equals(JavaCalculator.jLabelDegRadToggle.getText())) {
            switch (button.name) {
                case "cosine":
                    ret = Math.cos(val % (2 * Math.PI));
                    break;
                case "tangent":
                    ret = Math.tan(val % (2 * Math.PI));
                    break;
                case "sine":
                    ret = Math.sin(val % (2 * Math.PI));
                    break;
                case "arcSine":
                    ret = Math.asin(val);
                    break;
                case "arcTangent":
                    ret = Math.atan(val);
                    break;
                case "arcCosine":
                    ret = Math.acos(val);
                    break;
            }
        } else {
            switch (button.name) {
                case "cosine":
                    ret = Math.cos(Math.toRadians(val) % 360);
                    break;
                case "tangent":
                    ret = Math.tan(Math.toRadians(val) % 360);
                    break;
                case "sine":
                    ret = Math.sin(Math.toRadians(val) % 360);
                    break;
                case "arcSine":
                    ret = Math.asin(val) * 180 / Math.PI;
                    break;
                case "arcTangent":
                    ret = Math.atan(val) * 180 / Math.PI;
                    break;
                case "arcCosine":
                    ret = Math.acos(val) * 180 / Math.PI;
                    break;
            }
        }
        return new CalcButton(null, "number", 0, ret.toString(), 0);
    }

    private BigInteger fact(BigInteger val) {
        if (val.equals(new BigInteger("0"))
                || val.equals(new BigInteger("1"))) {
            return new BigInteger("1");
        } else {
            return val.multiply(fact(val.subtract(new BigInteger("1"))));
        }
    }
}
