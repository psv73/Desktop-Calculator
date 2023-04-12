package calculator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class MathUtils {

    public static String computeSquareRoot(String str) {
        String equation;
        String begin = str.substring(0, str.indexOf("\u221A("));
        String end = "";
        equation = str.substring(str.indexOf("\u221A(") + 2);

        StringBuilder temp = new StringBuilder();
        int count = 1;

        for (char c : equation.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            }

            if (count == 0) {
                break;
            }

            temp.append(c);
        }

        String toCalculate = temp.toString();
        int index = str.indexOf(toCalculate) + toCalculate.length() + 1;
        if (index < str.length()) {
            end = str.substring(index);
        }

        if (toCalculate.matches(".*\u221A\\(.*")) {
            toCalculate = computeSquareRoot(temp.toString());
        }

        equation = String.valueOf(Math.sqrt(computeEquation(toCalculate)));

        return begin + equation + end;
    }

    public static float computeEquation(String str) {
        Queue<String> postfix = infixToPostfix(str);
        Deque<Float> stack = new ArrayDeque<>();

        while (!postfix.isEmpty()) {
            char c = postfix.peek().charAt(postfix.peek().length() - 1);

            if (!isOperator(c)) {
                stack.push(Float.parseFloat(postfix.remove()));
            } else {
                float b = stack.pop();
                float a = stack.pop();

                if (c == Operator.ADDITION.getOperator()) {
                    stack.push(a + b);
                    postfix.remove();
                } else if (c == Operator.SUBTRACTION.getOperator()) {
                    stack.push(a - b);
                    postfix.remove();
                } else if (c == Operator.DIVISION.getOperator()) {
                    stack.push(a / b);
                    postfix.remove();
                } else if (c == Operator.MULTIPLICATION.getOperator()) {
                    stack.push(a * b);
                    postfix.remove();
                } else if (c == Operator.EXPONENTIATION.getOperator()) {
                    stack.push((float) Math.pow(a, b));
                    postfix.remove();
                }
            }
        }
        return stack.pop();
    }

    private static Queue<String> infixToPostfix(String infix) {
        Queue<String> pf = new LinkedList<>();

        if (infix == null || infix.isEmpty()) {
            return pf;
        }

        Deque<Character> stack = new ArrayDeque<>();
        StringBuilder operand = new StringBuilder();

        for (char c : infix.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (!operand.isEmpty()) {
                    pf.add(String.valueOf(operand));
                    operand.delete(0, operand.length());
                }
                while (!stack.isEmpty() && stack.peek() != '(') {
                    pf.add(String.valueOf(stack.pop()));
                }
                stack.pop();
            } else if (('-' == c && operand.isEmpty() && '(' == stack.peek()) ||
                    !isOperator(c)) {
                operand.append(c);
            } else {
                if (!operand.isEmpty()) {
                    pf.add(String.valueOf(operand));
                    operand.delete(0, operand.length());
                }
                while (!stack.isEmpty() && getOperatorLevel(c) >= getOperatorLevel(stack.peek()) &&
                        !('(' == stack.peek())) {
                    pf.add(String.valueOf(stack.pop()));
                }
                stack.push(c);
            }
        }

        if (!operand.isEmpty()) {
            pf.add(String.valueOf(operand));
        }

        while (!stack.isEmpty()) {
            pf.add(String.valueOf(stack.pop()));
        }

        return pf;
    }

    public static boolean isOperator(char symbol) {
        for (Operator operator : Operator.values()) {
            if (symbol == operator.getOperator()) {
                return true;
            }
        }

        return false;
    }

    private static int getOperatorLevel(char c) {
        for (Operator operator : Operator.values()) {
            if (operator.getOperator() == c) {
                return operator.getLevel();
            }
        }

        return Integer.MAX_VALUE;
    }
}
