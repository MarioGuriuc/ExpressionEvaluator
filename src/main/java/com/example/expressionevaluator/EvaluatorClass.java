package com.example.expressionevaluator;

import java.util.Stack;

public class EvaluatorClass {
    public static boolean invalidExpression = false;
    private final StringBuilder expression;
    StringBuilder subExpression = new StringBuilder();
    Stack<Integer> parentheses;

    public EvaluatorClass(StringBuilder expression) {
        this.expression = new StringBuilder(expression);
        if (!checkValidity()) {
            normalize();
        }
    }

    private boolean checkValidity() {
        int nrParaRight = 0, nrParaLeft = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == ')') {
                nrParaRight++;
            } else if (expression.charAt(i) == '(') {
                nrParaLeft++;
            }
        }
        if (nrParaRight != nrParaLeft) {
            invalidExpression = true;
            MainPageController.success = false;
            return true;
        }
        for (int i = 1; i < expression.length(); i++) {
            if ((expression.charAt(i - 1) == '+' && expression.charAt(i) == '+')
                    || (expression.charAt(i - 1) == '*' && expression.charAt(i) == '*')
                    || (expression.charAt(i - 1) == '/' && expression.charAt(i) == '/')
                    || (expression.charAt(i - 1) == '%' && expression.charAt(i) == '%')
                    || (expression.charAt(i - 1) == '^') && (expression.charAt(i) == '^')) {
                invalidExpression = true;
                MainPageController.success = false;
                return true;
            } else if (expression.charAt(i - 1) == '-' && expression.charAt(i) == '-') {
                expression.deleteCharAt(i - 1).deleteCharAt(i - 1);
            } else if (expression.charAt(i - 1) == '+' && expression.charAt(i) == '-') {
                expression.deleteCharAt(i - 1);
            } else if (expression.charAt(i - 1) == '-' && expression.charAt(i) == '+') {
                expression.deleteCharAt(i);
            }
        }
        return invalidExpression;
    }

    private boolean divisionByZeroCheck(StringBuilder s1) {
        if (s1 == null || s1.isEmpty()) return false;
        for (int i = 0; i < "Division by zero!".length(); i++) {
            if (s1.charAt(i) != "Division by zero!".charAt(i)) return false;
        }
        expression.replace(0, expression.length(), "");
        return true;
    }

    private StringBuilder compute() {
        int firstParenthesis = parentheses.pop(), secondParenthesis = expression.length();
        for (int i = firstParenthesis + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == ')') {
                secondParenthesis = i;
                break;
            }
        }
        if (divisionByZeroCheck(subExpression)) {
            MainPageController.success = false;
            return subExpression;
        }
        subExpression = new StringBuilder(expression.substring(firstParenthesis + 1, secondParenthesis));
        expression.delete(firstParenthesis, secondParenthesis + 1);
        expression.insert(firstParenthesis, simpleOperations(priorityOperations(subExpression)));
        if (expression.indexOf("+") == -1 && expression.indexOf("-") == -1 && expression.indexOf("/") == -1
                && expression.indexOf("*") == -1 && expression.indexOf("^") == -1 && expression.indexOf("%") == -1) {
            if (!divisionByZeroCheck(subExpression))
                MainPageController.success = true;
            return subExpression;
        }
        return compute();
    }

    public StringBuilder getResult() {
        if (invalidExpression) {
            invalidExpression = false;
            return new StringBuilder("Invalid expression!");
        }
        StringBuilder result = compute();
        if (result.charAt(0) == '{') {
            result.deleteCharAt(0).deleteCharAt(0).deleteCharAt(result.length() - 1);
            result.insert(0, '-');
        }
        return result;
    }

    private void normalize() {
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == ' ') expression.deleteCharAt(i);
        }
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(' && expression.charAt(i + 1) == '-') {
                for (int j = i + 3; j < expression.length(); j++) {
                    if (expression.charAt(j) == ')') {
                        expression.delete(i, i + 2);
                        expression.insert(i, "{~");
                        expression.delete(j, j + 1);
                        expression.insert(j, '}');
                        break;
                    }
                }
            }
        }
        parentheses = new Stack<>();
        expression.insert(0, '(').append(')');
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                parentheses.push(i);
            }
        }
    }

    private StringBuilder simpleOperations(StringBuilder subExpression) {
        if (subExpression.indexOf("-") == -1 && subExpression.indexOf("+") == -1) return subExpression;
        int maybePlus = subExpression.indexOf("+");
        int maybeMinus = subExpression.indexOf("-");
        int operatorPos;
        if (maybeMinus == -1 && maybePlus != -1) operatorPos = maybePlus;
        else if (maybePlus == -1 && maybeMinus != -1) operatorPos = maybeMinus;
        else operatorPos = Math.min(maybeMinus, maybePlus);
        if (subExpression.charAt(0) == '{' && subExpression.charAt(operatorPos + 1) == '{') {
            simpleOperationsClass.bothNegative(subExpression, operatorPos);
        } else if (subExpression.charAt(0) == '{' && subExpression.charAt(operatorPos + 1) != '{') {
            simpleOperationsClass.firstNegSecondPos(subExpression, operatorPos);
        } else if (subExpression.charAt(0) != '{' && subExpression.charAt(operatorPos + 1) == '{') {
            simpleOperationsClass.firstPosSecondNeg(subExpression, operatorPos);
        } else {
            simpleOperationsClass.bothPositive(subExpression, operatorPos);
        }
        return simpleOperations(subExpression);
    }

    private StringBuilder priorityOperations(StringBuilder subExpression) {
        if (subExpression.indexOf("/") == -1 && subExpression.indexOf("*") == -1 && subExpression.indexOf("^") == -1 && subExpression.indexOf("%") == -1)
            return subExpression;
        int operatorPos = 0;
        StringBuilder number1 = null, number2 = null;
        for (int i = 0; i < subExpression.length(); i++) {
            if (subExpression.charAt(i) == '/' || subExpression.charAt(i) == '*' || subExpression.charAt(i) == '^' || subExpression.charAt(i) == '%') {
                operatorPos = i;
                break;
            }
        }
        boolean foundOperator = false;
        for (int i = operatorPos - 2; i >= 0; i--) {
            if (subExpression.charAt(i) == '+' || subExpression.charAt(i) == '-' || subExpression.charAt(i) == '*' || subExpression.charAt(i) == '/' || subExpression.charAt(i) == '^' || subExpression.charAt(i) == '%' || subExpression.charAt(i + 1) == '{') {
                number1 = new StringBuilder(subExpression.substring(i + 1, operatorPos));
                foundOperator = true;
                break;
            }
        }
        if (!foundOperator) {
            number1 = new StringBuilder(subExpression.substring(0, operatorPos));
        }
        foundOperator = false;
        for (int i = operatorPos + 1; i < subExpression.length(); i++) {
            if (subExpression.charAt(i) == '+' || subExpression.charAt(i) == '-' || subExpression.charAt(i) == '*' || subExpression.charAt(i) == '/' || subExpression.charAt(i) == '^' || subExpression.charAt(i) == '%' || subExpression.charAt(i - 1) == '}') {
                number2 = new StringBuilder(subExpression.substring(operatorPos + 1, i));
                foundOperator = true;
                break;
            }
        }
        if (!foundOperator) {
            number2 = new StringBuilder(subExpression.substring(operatorPos + 1, subExpression.length()));
        }
        if (number1.charAt(0) != '{' && number2.charAt(0) != '{') {
            priorityOperationsClass.bothPositive(subExpression, operatorPos, number1, number2);
        } else if (number1.charAt(0) == '{' && number2.charAt(0) == '{') {
            priorityOperationsClass.bothNegative(subExpression, operatorPos, number1, number2);
        } else if (number1.charAt(0) == '{' && number2.charAt(0) != '{') {
            priorityOperationsClass.firstNegSecondPos(subExpression, operatorPos, number1, number2);
        } else if (number1.charAt(0) != '{' && number2.charAt(0) == '{') {
            priorityOperationsClass.firstPosSecondNeg(subExpression, operatorPos, number1, number2);
        }
        if (divisionByZeroCheck(subExpression)) {
            MainPageController.success = false;
            return subExpression;
        }

        return priorityOperations(subExpression);
    }

    private static class simpleOperationsClass {
        public static void bothPositive(StringBuilder subExpression, int operatorPos) {
            char operator = subExpression.charAt(operatorPos);
            String number1 = subExpression.substring(0, operatorPos);
            subExpression.deleteCharAt(operatorPos);
            String number2;
            if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos, Math.min(subExpression.indexOf("+"), subExpression.indexOf("-")));
            } else if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") == -1) {
                number2 = subExpression.substring(operatorPos, subExpression.indexOf("+"));
            } else if (subExpression.indexOf("+") == -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos, subExpression.indexOf("-"));
            } else {
                number2 = subExpression.substring(operatorPos, subExpression.length());
            }
            subExpression.delete(0, number1.length() + number2.length());
            if (operator == '+') {
                String result = String.valueOf(Double.parseDouble(number1) + Double.parseDouble(number2));
                subExpression.insert(0, result);
            } else {
                double number1Double = Double.parseDouble(number1);
                double number2Double = Double.parseDouble(number2);
                if (number1Double < number2Double) {
                    String result = String.valueOf(-(number1Double - number2Double));
                    subExpression.insert(0, "{~" + result + "}");
                } else {
                    String result = String.valueOf(number1Double - number2Double);
                    subExpression.insert(0, result);
                }
            }
        }

        public static void bothNegative(StringBuilder subExpression, int operatorPos) {
            char operator = subExpression.charAt(operatorPos);
            String number1 = subExpression.substring(2, operatorPos - 1);
            subExpression.deleteCharAt(operatorPos);
            String number2;
            if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos + 2, Math.min(subExpression.indexOf("+"), subExpression.indexOf("-")) - 1);
            } else if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") == -1) {
                number2 = subExpression.substring(operatorPos + 2, subExpression.indexOf("+") - 1);
            } else if (subExpression.indexOf("+") == -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos + 2, subExpression.indexOf("-") - 1);
            } else {
                number2 = subExpression.substring(operatorPos + 2, subExpression.length() - 1);
            }
            subExpression.delete(0, number1.length() + number2.length() + 6);
            if (operator == '+') {
                String result = String.valueOf(Double.parseDouble(number1) + Double.parseDouble(number2));
                StringBuilder negativeResult = new StringBuilder().append(result);
                negativeResult.insert(0, "{~");
                negativeResult.insert(negativeResult.length(), "}");
                subExpression.insert(0, negativeResult);
            } else {
                String result;
                double number1Double = Double.parseDouble(number1);
                double number2Double = Double.parseDouble(number2);
                if (number1Double < number2Double) {
                    result = String.valueOf(number2Double - number1Double);
                    subExpression.insert(0, result);
                } else {
                    result = String.valueOf(-(number2Double - number1Double));
                    subExpression.insert(0, "{~" + result + "}");
                }
            }
        }

        public static void firstNegSecondPos(StringBuilder subExpression, int operatorPos) {
            char operator = subExpression.charAt(operatorPos);
            String number1 = subExpression.substring(2, operatorPos - 1);
            subExpression.deleteCharAt(operatorPos);
            String number2;
            if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos, Math.min(subExpression.indexOf("+"), subExpression.indexOf("-")));
            } else if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") == -1) {
                number2 = subExpression.substring(operatorPos, subExpression.indexOf("+"));
            } else if (subExpression.indexOf("+") == -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos, subExpression.indexOf("-"));
            } else {
                number2 = subExpression.substring(operatorPos, subExpression.length());
            }
            subExpression.delete(0, number1.length() + number2.length() + 3);
            double number1Double = Double.parseDouble(number1);
            double number2Double = Double.parseDouble(number2);
            if (operator == '+') {
                String result;
                if (number2Double > number1Double) {
                    result = String.valueOf(number2Double - number1Double);
                    subExpression.insert(0, result);
                } else {
                    result = String.valueOf(number1Double - number2Double);
                    subExpression.insert(0, "{~" + result + "}");
                }
            } else {
                String result = String.valueOf(number1Double + number2Double);
                subExpression.insert(0, "{~" + result + "}");
            }
        }

        public static void firstPosSecondNeg(StringBuilder subExpression, int operatorPos) {
            char operator = subExpression.charAt(operatorPos);
            String number1 = subExpression.substring(0, operatorPos);
            subExpression.deleteCharAt(operatorPos);
            String number2;
            if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos + 2, Math.min(subExpression.indexOf("+"), subExpression.indexOf("-")) - 1);
            } else if (subExpression.indexOf("+") != -1 && subExpression.indexOf("-") == -1) {
                number2 = subExpression.substring(operatorPos + 2, subExpression.indexOf("+") - 1);
            } else if (subExpression.indexOf("+") == -1 && subExpression.indexOf("-") != -1) {
                number2 = subExpression.substring(operatorPos + 2, subExpression.indexOf("-") - 1);
            } else {
                number2 = subExpression.substring(operatorPos + 2, subExpression.length() - 1);
            }
            subExpression.delete(0, number1.length() + number2.length() + 3);
            double number1Double = Double.parseDouble(number1);
            double number2Double = Double.parseDouble(number2);
            if (operator == '+') {
                String result;
                if (number1Double > number2Double) {
                    result = String.valueOf(number1Double - number2Double);
                    subExpression.insert(0, result);
                } else {
                    result = String.valueOf(number2Double - number1Double);
                    subExpression.insert(0, "{~" + result + "}");
                }
            } else {
                String result = String.valueOf(number1Double + number2Double);
                subExpression.insert(0, result);
            }
        }
    }

    private static class priorityOperationsClass {
        public static void bothPositive(StringBuilder subExpression, int operatorPos, StringBuilder number1, StringBuilder number2) {
            String result = "";
            double number1Double = Double.parseDouble(String.valueOf(number1));
            double number2Double = Double.parseDouble(String.valueOf(number2));
            int number1Start = operatorPos - number1.length();
            char operator = subExpression.charAt(operatorPos);
            subExpression.delete(operatorPos - number1.length(), operatorPos + number2.length() + 1);
            switch (operator) {
                case '*' -> result = String.valueOf(number1Double * number2Double);
                case '/' -> {
                    if (number2Double == 0) {
                        subExpression.replace(0, subExpression.length(), "Division by zero!");
                        return;
                    }
                    result = String.valueOf(number1Double / number2Double);
                }
                case '^' -> result = String.valueOf(Math.pow(number1Double, number2Double));
                case '%' -> result = String.valueOf(number1Double % number2Double);
            }
            subExpression.insert(number1Start, result);
        }

        public static void bothNegative(StringBuilder subExpression, int operatorPos, StringBuilder number1, StringBuilder number2) {
            number1.deleteCharAt(0).deleteCharAt(0).deleteCharAt(number1.length() - 1);
            number2.deleteCharAt(0).deleteCharAt(0).deleteCharAt(number2.length() - 1);
            String result = "";
            double number1Double = Double.parseDouble(String.valueOf(number1));
            double number2Double = Double.parseDouble(String.valueOf(number2));
            int number1Start = operatorPos - number1.length() - 3;
            char operator = subExpression.charAt(operatorPos);
            subExpression.delete(number1Start, operatorPos + number2.length() + 4);
            switch (operator) {
                case '*' -> result = String.valueOf(number1Double * number2Double);
                case '/' -> {
                    if (number2Double == 0) {
                        subExpression.replace(0, subExpression.length(), "Division by zero!");
                        return;
                    }
                    result = String.valueOf(number1Double / number2Double);
                }
                case '^' -> result = String.valueOf(Math.pow(number1Double, number2Double));
                case '%' -> result = String.valueOf(number1Double % number2Double);
            }
            subExpression.insert(number1Start, result);
        }

        public static void firstNegSecondPos(StringBuilder subExpression, int operatorPos, StringBuilder number1, StringBuilder number2) {
            String result = "";
            number1.deleteCharAt(0).deleteCharAt(0).deleteCharAt(number1.length() - 1);
            double number1Double = Double.parseDouble(String.valueOf(number1));
            double number2Double = Double.parseDouble(String.valueOf(number2));
            int number1Start = operatorPos - number1.length() - 3;
            char operator = subExpression.charAt(operatorPos);
            subExpression.delete(number1Start, operatorPos + number2.length() + 1);
            boolean power = false;
            switch (operator) {
                case '*' -> result = String.valueOf(number1Double * number2Double);
                case '/' -> {
                    if (number2Double == 0) {
                        subExpression.replace(0, subExpression.length(), "Division by zero!");
                        return;
                    }
                    result = String.valueOf(number1Double / number2Double);
                }
                case '^' -> {
                    result = String.valueOf(Math.pow(number1Double, number2Double));
                    power = true;
                }
                case '%' -> result = String.valueOf(number1Double % number2Double);
            }
            if (!power) {
                subExpression.insert(number1Start, "{~" + result + "}");
            } else {
                subExpression.insert(number1Start, result);
            }
        }

        public static void firstPosSecondNeg(StringBuilder subExpression, int operatorPos, StringBuilder number1, StringBuilder number2) {
            String result = "";
            number2.deleteCharAt(0).deleteCharAt(0).deleteCharAt(number2.length() - 1);
            double number1Double = Double.parseDouble(String.valueOf(number1));
            double number2Double = Double.parseDouble(String.valueOf(number2));
            int number1Start = operatorPos - number1.length();
            char operator = subExpression.charAt(operatorPos);
            subExpression.delete(number1Start, operatorPos + number2.length() + 4);
            switch (operator) {
                case '*' -> result = String.valueOf(number1Double * number2Double);
                case '/' -> {
                    if (number2Double == 0) {
                        subExpression.replace(0, subExpression.length(), "Division by zero!");
                        return;
                    }
                    result = String.valueOf(number1Double / number2Double);
                }
                case '^' -> result = String.valueOf(1 / (Math.pow(number1Double, number2Double)));
                case '%' -> result = String.valueOf(number1Double % number2Double);
            }
            subExpression.insert(number1Start, "{~" + result + "}");
        }
    }
}