package calculator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Calculator extends JFrame {
    private final JLabel resultLabel;
    private final JLabel equationLabel;

    public Calculator() {
        super("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(330, 450);
        setLayout(null);

        resultLabel = new JLabel();
        resultLabel.setName("ResultLabel");
        resultLabel.setText("0");
        resultLabel.setFont(new Font(resultLabel.getFont().getFontName(), Font.BOLD, 32));
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        resultLabel.setBounds(15, 10, getWidth() - 45, 40);

        add(resultLabel);

        equationLabel = new JLabel();
        equationLabel.setForeground(Color.GREEN.darker());
        equationLabel.setName("EquationLabel");
        equationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        equationLabel.setBounds(15, 60, getWidth() - 45, 20);

        add(equationLabel);

        for (JButton button : createButtons()) {
            if ("".equals(button.getText())) {
                continue;
            }
            add(button);
        }

        setVisible(true);
    }

    private void calculate() {

        // last is operator or the equation contains division by zero
        if (!equationLabel.getText().isEmpty()) {
            if ((MathUtils.isOperator(equationLabel.getText().charAt(equationLabel.getText().length() - 1)) &&
                    isBracket(equationLabel.getText().charAt(equationLabel.getText().length() - 1))) ||
                    equationLabel.getText().matches("(.*)" + Operator.DIVISION.getOperator() + "0(.*)")) {
                equationLabel.setForeground(Color.RED.darker());
                return;
            }
        } else {
            return;
        }

        checkOperand();

        String equation = equationLabel.getText();

        if (equation.matches(".*\u221A\\(.*")) {
            equation = MathUtils.computeSquareRoot(equation);
        }

        if ("NaN".equals(equation)) {
            equationLabel.setForeground(Color.RED.darker());
        }

        float result = MathUtils.computeEquation(equation);


        if (result % 1 == 0) {
            resultLabel.setText(String.valueOf(Math.round(result)));
        } else {
            resultLabel.setText(String.valueOf(result));
        }
    }


    private List<JButton> createButtons() {
        List<JButton> buttons = new ArrayList<>();
        String[] names = {"Parentheses", "", "Clear", "Delete",
                "PowerTwo", "PowerY", "SquareRoot", "Divide",
                "Seven", "Eight", "Nine", "Multiply",
                "Four", "Five", "Six", "Subtract",
                "One", "Two", "Three", "Add",
                "PlusMinus", "Zero", "Dot", "Equals"};

        String[] text = {"( )", "CE", "C", "Del",
                "x\u00B2", "x\u207F", "\u221A", "\u00F7",
                "7", "8", "9", "\u00D7",
                "4", "5", "6", "-",
                "1", "2", "3", "\u002B",
                "\u00B1", "0", ".", "="};

        final int xBeginPosition = 25;
        final int xOffset = 70;
        final int yOffset = 50;

        int yPosition = 60;
        int xPosition = xBeginPosition;

        for (int i = 0; i < names.length; i++) {
            JButton button = new JButton(text[i]);
            button.setName(names[i]);
            if (i % 4 == 0) {
                xPosition = xBeginPosition;
                yPosition += yOffset;
            } else {
                xPosition += xOffset;
            }

            button.setBounds(xPosition, yPosition, 60, 40);

            switch (names[i]) {
                case "Equals":
                    button.addActionListener(e -> calculate());
                    break;
                case "SquareRoot":
                    button.addActionListener(e -> equationLabel.setText(equationLabel.getText() +
                            button.getText() + '('));
                    break;
                case "Clear":
                    button.addActionListener(e -> {
                        equationLabel.setText("");
                        resultLabel.setText("0");
                    });
                    break;
                case "Delete":
                    button.addActionListener(e -> {
                        if (equationLabel.getText().length() > 0) {
                            equationLabel.setText(equationLabel.getText()
                                    .substring(0, equationLabel.getText().length() - 1));
                            checkEquationLabelColor();
                        }
                    });
                    break;
                case "PowerTwo":
                    button.addActionListener(e -> equationLabel.setText(equationLabel.getText() +
                            "^(2)"));
                    break;
                case "PowerY":
                    button.addActionListener(e -> equationLabel.setText(equationLabel.getText() +
                            "^("));
                    break;
                case "Parentheses":
                    button.addActionListener(e -> addParentheses());
                    break;
                case "PlusMinus":
                    button.addActionListener(e -> checkPlusMinus());
                    break;
                default:
                    button.addActionListener(e -> setEquationLabel(button));
                    break;
            }

            buttons.add(button);
        }
        return buttons;
    }

    private void setEquationLabel(JButton button) {

        if (MathUtils.isOperator(button.getText().charAt(button.getText().length() - 1))) {
            checkOperand();
        }

        String equation = equationLabel.getText();

        checkEquationLabelColor();

        if (MathUtils.isOperator(button.getText().charAt(0))) {
            if (equation.isEmpty()) {
                return;
            }

            char lastChar = equation.charAt(equation.length() - 1);

            if (MathUtils.isOperator(lastChar)) {
                if (isBracket(lastChar)) {
                    equation = equation.substring(0, equation.length() - 1);
                }
            }

            checkOperand();
        }

        equationLabel.setText(equation + button.getText());
    }

    private boolean isBracket(char c) {
        return c != '(' && c != ')';
    }

    private void checkEquationLabelColor() {
        if (equationLabel.getForeground().equals(Color.RED.darker())) {
            equationLabel.setForeground(Color.GREEN.darker());
        }
    }

    private void checkOperand() {
        String equation = equationLabel.getText();

        // 5....6 - remove dotes
        if (equation.matches("(.*)(\\.{2,})(.*)")) {
            equation = equation.replaceAll("\\.{2,}", ".");
        }

        // start from dot -> 0.
        if (equation.matches("^\\..*")) {
            equation = equation.replaceAll("\\.", "0.");
        }

        // .5 -> 0.5   add leading zero
        if (equation.matches(".*\\D\\.\\d.*")) {
            int position = 0;
            boolean operator = true;

            for (char c : equation.toCharArray()) {
                if (c == '.' && operator) {
                    break;
                } else {
                    operator = MathUtils.isOperator(c);
                }
                position++;
            }

            if (position > 0) {
                equation = equation.substring(0, position) + "0" + equation.substring(position);
            }
        }

        // 5. -> 5   remove dot
        if (equation.matches(".*\\d+\\.\\D*")) {
            equation = equation + "0";
        }

        equationLabel.setText(equation);
    }

    private void addParentheses() {
        String equation = equationLabel.getText();
        String bracket;

        if (equation.isEmpty() || isParenthesesCountEqual() ||
                equation.matches(".*\\($") ||
                MathUtils.isOperator(equation.charAt(equation.length() - 1))) {
            bracket = "(";
        } else {
            bracket = ")";
        }
        equationLabel.setText(equation + bracket);
    }

    private boolean isParenthesesCountEqual() {
        int count = 0;

        for (String p : equationLabel.getText().split("")) {
            if ("(".equals(p)) {
                count++;
            } else if (")".equals(p)) {
                count--;
            }
        }

        return count == 0;
    }

    private void checkPlusMinus() {
        String equation = equationLabel.getText();

        if (equation.matches(".*\\(-$")) {
            equationLabel.setText(equation.substring(0, equation.length() - 2));
        } else if (equation.matches(".*\\(-\\d+\\.?\\d*")) {
            int index = equation.lastIndexOf("(-");
            equation = equation.substring(0, index) + equation.substring(index + 2);
            equationLabel.setText(equation);
        } else if (equation.matches(".*\\d")) {
            String[] array = equation.split("");
            int count = 0;
            int i = array.length - 1;

            while (i >= 0 && array[i].matches("\\d||\\.")) {
                count++;
                i--;
            }
            equation = equation.substring(0, equation.length() - count) +
            "(-" + equation.substring(equation.length() - count);

            equationLabel.setText(equation);
        } else {
            equationLabel.setText(equation + "(-");
        }

    }
}


