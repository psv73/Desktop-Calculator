package calculator;

public enum Operator {

    ADDITION ('\u002B', 3),
    SUBTRACTION ('-', 3),
    MULTIPLICATION ('\u00D7', 2),
    DIVISION ('\u00F7', 2),
    EXPONENTIATION ('^', 0),
    SQUARE_ROOT ('\u221A', 0);

    private final char operator;
    private final int level;

    Operator(char operator, int level) {
        this.operator = operator;
        this.level = level;
    }

    public char getOperator() {
        return operator;
    }

    public int getLevel() {
        return level;
    }

}
