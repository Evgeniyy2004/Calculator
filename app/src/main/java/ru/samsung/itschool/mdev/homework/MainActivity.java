package ru.samsung.itschool.mdev.homework;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private TextView resultDisplay;
    private TextView historyDisplay;
    private Spinner numberSystemSpinner;
    private Spinner operationTypeSpinner;

    private StringBuilder currentInput = new StringBuilder();
    private boolean isDegreeMode = true;
    private String lastResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinners();
        setupButtonListeners();
    }

    private void initializeViews() {
        display = findViewById(R.id.display);
        resultDisplay = findViewById(R.id.resultDisplay);
        historyDisplay = findViewById(R.id.historyDisplay);
        numberSystemSpinner = findViewById(R.id.numberSystemSpinner);
        operationTypeSpinner = findViewById(R.id.operationTypeSpinner);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> opAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.operation_types,
                android.R.layout.simple_spinner_item
        );
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationTypeSpinner.setAdapter(opAdapter);

        ArrayAdapter<CharSequence> sysAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.number_systems,
                android.R.layout.simple_spinner_item
        );
        sysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSystemSpinner.setAdapter(sysAdapter);

        // Слушатель изменения системы счисления
        numberSystemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateHexButtonsVisibility(position == 3); // Показывать A-F только для HEX
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateHexButtonsVisibility(boolean show) {
        int[] hexButtons = {
                R.id.btnHexA, R.id.btnHexB, R.id.btnHexC,
                R.id.btnHexD, R.id.btnHexE, R.id.btnHexF
        };

        for (int id : hexButtons) {
            findViewById(id).setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void setupButtonListeners() {
        // Цифры 0-9
        int[] digitButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : digitButtons) {
            findViewById(id).setOnClickListener(v -> appendToDisplay(((Button) v).getText().toString()));
        }

        // Буквы для шестнадцатеричной системы (переименованы с btnHex префиксом)
        findViewById(R.id.btnHexA).setOnClickListener(v -> appendToDisplay("A"));
        findViewById(R.id.btnHexB).setOnClickListener(v -> appendToDisplay("B"));
        findViewById(R.id.btnHexC).setOnClickListener(v -> appendToDisplay("C"));
        findViewById(R.id.btnHexD).setOnClickListener(v -> appendToDisplay("D"));
        findViewById(R.id.btnHexE).setOnClickListener(v -> appendToDisplay("E"));
        findViewById(R.id.btnHexF).setOnClickListener(v -> appendToDisplay("F"));

        // Основные операции
        findViewById(R.id.btnAdd).setOnClickListener(v -> appendOperation("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> appendOperation("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> appendOperation("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> appendOperation("/"));
        findViewById(R.id.btnDot).setOnClickListener(v -> appendToDisplay("."));

        // Функции
        findViewById(R.id.btnSin).setOnClickListener(v -> appendFunction("sin("));
        findViewById(R.id.btnCos).setOnClickListener(v -> appendFunction("cos("));
        findViewById(R.id.btnTan).setOnClickListener(v -> appendFunction("tan("));
        findViewById(R.id.btnLog).setOnClickListener(v -> appendFunction("log("));
        findViewById(R.id.btnLn).setOnClickListener(v -> appendFunction("ln("));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> appendFunction("sqrt("));
        findViewById(R.id.btnPower).setOnClickListener(v -> appendOperation("^"));
        findViewById(R.id.btnPi).setOnClickListener(v -> appendToDisplay("π"));
        findViewById(R.id.btnEuler).setOnClickListener(v -> appendToDisplay("e")); // Переименован

        // Побитовые операции
        findViewById(R.id.btnAnd).setOnClickListener(v -> appendOperation("&"));
        findViewById(R.id.btnOr).setOnClickListener(v -> appendOperation("|"));
        findViewById(R.id.btnXor).setOnClickListener(v -> appendOperation("^"));
        findViewById(R.id.btnNot).setOnClickListener(v -> appendFunction("~"));
        findViewById(R.id.btnShiftLeft).setOnClickListener(v -> appendOperation("<<"));
        findViewById(R.id.btnShiftRight).setOnClickListener(v -> appendOperation(">>"));

        // Скобки
        findViewById(R.id.btnOpenBracket).setOnClickListener(v -> appendToDisplay("("));
        findViewById(R.id.btnCloseBracket).setOnClickListener(v -> appendToDisplay(")"));

        // Управление
        findViewById(R.id.btnClear).setOnClickListener(v -> clearDisplay());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> backspace());
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());

        // Конвертация
        findViewById(R.id.btnConvert).setOnClickListener(v -> convertNumberSystem());

        // Переключение режима градусы/радианы
        findViewById(R.id.btnMode).setOnClickListener(v -> toggleMode());

        // Интеграл
        findViewById(R.id.btnIntegral).setOnClickListener(v -> showIntegralDialog());
    }

    private void appendToDisplay(String text) {
        currentInput.append(text);
        display.setText(currentInput.toString());
    }

    private void appendOperation(String op) {
        if (currentInput.length() == 0 && !op.equals("-")) {
            return;
        }
        currentInput.append(op);
        display.setText(currentInput.toString());
    }

    private void appendFunction(String func) {
        currentInput.append(func);
        display.setText(currentInput.toString());
    }

    private void clearDisplay() {
        currentInput.setLength(0);
        display.setText("");
        resultDisplay.setText("");
    }

    private void backspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            display.setText(currentInput.toString());
        }
    }

    private void toggleMode() {
        isDegreeMode = !isDegreeMode;
        String mode = isDegreeMode ? "DEG" : "RAD";
        Toast.makeText(this, "Режим: " + mode, Toast.LENGTH_SHORT).show();
    }

    private void calculateResult() {
        try {
            String expression = currentInput.toString()
                    .replace("π", String.valueOf(Math.PI))
                    .replace("e", String.valueOf(Math.E));

            String operationType = operationTypeSpinner.getSelectedItem().toString();
            String numberSystem = numberSystemSpinner.getSelectedItem().toString();

            double result = 0;

            if (operationType.equals("Арифметика")) {
                result = evaluateArithmeticExpression(expression);
            } else if (operationType.equals("Побитовые операции")) {
                long intResult = evaluateBitwiseExpression(expression);
                result = intResult;
            }

            // Преобразуем результат в нужную систему счисления
            String formattedResult = formatResult(result, numberSystem);

            resultDisplay.setText("= " + formattedResult);
            lastResult = formattedResult;

            // Добавляем в историю
            String historyEntry = expression + " = " + formattedResult + "\n";
            historyDisplay.append(historyEntry);

        } catch (Exception e) {
            resultDisplay.setText("Ошибка!");
            Toast.makeText(this, "Ошибка в выражении", Toast.LENGTH_SHORT).show();
        }
    }

    private double evaluateArithmeticExpression(String expression) {
        try {
            return eval(expression);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка вычисления: " + e.getMessage());
        }
    }

    // ДОБАВЛЕН МЕТОД evaluateExpression который вызывается из createMathFunction
    private double evaluateExpression(String expression) {
        try {
            return eval(expression);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка вычисления выражения: " + e.getMessage());
        }
    }

    private long evaluateBitwiseExpression(String expression) {
        // Простая реализация для побитовых операций
        String system = numberSystemSpinner.getSelectedItem().toString();
        if (expression.contains("&")) {
            String[] parts = expression.split("&");
            long a = parseNumber(parts[0].trim());
            long b = parseNumber(parts[1].trim());
            return a & b;
        } else if (expression.contains("|")) {
            String[] parts = expression.split("\\|");
            long a = parseNumber(parts[0].trim());
            long b = parseNumber(parts[1].trim());
            return a | b;
        } else if (expression.contains("^") && !expression.contains("<<")) {
            String[] parts = expression.split("\\^");
            long a = parseNumber(parts[0].trim());
            long b = parseNumber(parts[1].trim());
            return a ^ b;
        } else if (expression.contains("<<")) {
            String[] parts = expression.split("<<");
            long a = parseNumber(parts[0].trim());
            long b = parseNumber(parts[1].trim());
            return a << b;
        } else if (expression.contains(">>")) {
            String[] parts = expression.split(">>");
            long a = parseNumber(parts[0].trim());
            long b = parseNumber(parts[1].trim());
            return a >> b;
        } else if (expression.contains("~")) {
            String part = expression.replace("~", "").trim();
            long a = parseNumber(part);
            return ~a;
        }

        throw new RuntimeException("Неизвестная операция");
    }



    // Метод для вычисления математических выражений
    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('^')) x = Math.pow(x, parseFactor());
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(isDegreeMode ? Math.toRadians(x) : x);
                    else if (func.equals("cos")) x = Math.cos(isDegreeMode ? Math.toRadians(x) : x);
                    else if (func.equals("tan")) x = Math.tan(isDegreeMode ? Math.toRadians(x) : x);
                    else if (func.equals("log")) x = Math.log10(x);
                    else if (func.equals("ln")) x = Math.log(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    private long parseNumber(String numStr) {
        try {
            String system = numberSystemSpinner.getSelectedItem().toString();
            switch (system) {
                case "Двоичная":
                    return Long.parseLong(numStr, 2);
                case "Восьмеричная":
                    return Long.parseLong(numStr, 8);
                case "Шестнадцатеричная":
                    return Long.parseLong(numStr, 16);
                default:
                    return Long.parseLong(numStr);
            }
        } catch (NumberFormatException e) {
            return Long.parseLong(numStr);
        }
    }

    private String formatResult(double result, String numberSystem) {
        long intValue = (long) result;

        switch (numberSystem) {
            case "Десятичная":
                // Для десятичных чисел показываем с плавающей точкой если нужно
                if (result == (long) result) {
                    return String.valueOf((long) result);
                } else {
                    return String.format("%.6f", result).replace(",", ".");
                }
            case "Двоичная":
                return Long.toBinaryString(intValue);
            case "Восьмеричная":
                return Long.toOctalString(intValue);
            case "Шестнадцатеричная":
                return Long.toHexString(intValue).toUpperCase();
            default:
                return String.valueOf(result);
        }
    }

    private void convertNumberSystem() {
        try {
            String input = display.getText().toString();
            if (input.isEmpty()) return;

            String sourceSystem = numberSystemSpinner.getSelectedItem().toString();
            long decimalValue;

            try {
                switch (sourceSystem) {
                    case "Десятичная":
                        decimalValue = Long.parseLong(input);
                        break;
                    case "Двоичная":
                        decimalValue = Long.parseLong(input, 2);
                        break;
                    case "Восьмеричная":
                        decimalValue = Long.parseLong(input, 8);
                        break;
                    case "Шестнадцатеричная":
                        decimalValue = Long.parseLong(input, 16);
                        break;
                    default:
                        decimalValue = Long.parseLong(input);
                }
            } catch (NumberFormatException e) {
                // Если число содержит точку, преобразуем его в double и возьмем целую часть
                try {
                    double doubleValue = Double.parseDouble(input);
                    decimalValue = (long) doubleValue;
                } catch (NumberFormatException e2) {
                    throw new RuntimeException("Неверный формат числа");
                }
            }

            String binary = Long.toBinaryString(decimalValue);
            String octal = Long.toOctalString(decimalValue);
            String hex = Long.toHexString(decimalValue).toUpperCase();

            String conversionResult = String.format(
                    "Dec: %d\nBin: %s\nOct: %s\nHex: %s",
                    decimalValue, binary, octal, hex
            );

            resultDisplay.setText(conversionResult);

        } catch (Exception e) {
            resultDisplay.setText("Ошибка конвертации: " + e.getMessage());
        }
    }

    private void showIntegralDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вычисление интеграла от a до b");

        View view = getLayoutInflater().inflate(R.layout.dialog_integral, null);
        builder.setView(view);

        EditText edtFunction = view.findViewById(R.id.edtFunction);
        EditText edtFrom = view.findViewById(R.id.edtFrom);
        EditText edtTo = view.findViewById(R.id.edtTo);
        Spinner spinnerMethod = view.findViewById(R.id.spinnerMethod);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.integration_methods,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMethod.setAdapter(adapter);

        builder.setPositiveButton("Вычислить", (dialog, which) -> {
            try {
                String function = edtFunction.getText().toString();
                double from = parseDouble(edtFrom.getText().toString());
                double to = parseDouble(edtTo.getText().toString());
                String method = spinnerMethod.getSelectedItem().toString();


                IntegralSolver.MathFunction f = createMathFunction(function);

                double result;
                switch (method) {
                    case "Метод Симпсона":
                        result = IntegralSolver.simpson(f, from, to, 1000);
                        break;
                    case "Метод трапеций":
                        result = IntegralSolver.trapezoid(f, from, to, 1000);
                        break;
                    case "Адаптивный метод":
                        result = IntegralSolver.adaptive(f, from, to, 1e-10, 1e-10);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный метод");
                }

                // Форматируем результат
                String formattedResult = formatResult(result);
                String expression = "∫(" + function + ")dx от " + formatNumber(from) + " до " + formatNumber(to);

                // Обновляем дисплей
                currentInput = new StringBuilder(expression);
                display.setText(expression);
                resultDisplay.setText("= " + formattedResult);

                // Добавляем в историю
                historyDisplay.append(expression + " = " + formattedResult + "\n");

            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private IntegralSolver.MathFunction createMathFunction(String expression) {
        return x -> {
            try {
                // Заменяем x на значение и вычисляем
                String expr = expression
                        .replace("x", "(" + x + ")")
                        .replace("X", "(" + x + ")")
                        .replace("π", String.valueOf(Math.PI))
                        .replace("e", String.valueOf(Math.E));

                // Убираем Math. префиксы которые были добавлены ранее
                expr = expr.replace("Math.sqrt", "sqrt")
                        .replace("log10", "log");

                return evaluateExpression(expr);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка в функции: " + e.getMessage());
            }
        };
    }



    private String formatNumber(double num) {
        if (Double.isInfinite(num)) {
            return num > 0 ? "+∞" : "-∞";
        }

        if (num == Math.PI) return "π";
        if (num == Math.E) return "e";

        if (Math.abs(num) < 1e-10) return "0";

        // Для целых чисел
        if (Math.abs(num - Math.round(num)) < 1e-10) {
            return String.valueOf(Math.round(num));
        }

        return String.format("%.6f", num).replaceAll("0*$", "").replaceAll("\\.$", "");
    }


    private String formatResult(double result) {
        if (Double.isNaN(result)) {
            return "NaN";
        }

        if (Double.isInfinite(result)) {
            return result > 0 ? "+∞" : "-∞";
        }

        if (Math.abs(result) < 1e-10) {
            return "0";
        }


        if (Math.abs(result) > 1e10 || Math.abs(result) < 1e-10) {
            return String.format("%.6e", result);
        }

        String formatted = String.format("%.10f", result);

        formatted = formatted.replaceAll("0*$", "");
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        text = text.trim()
                .replace("π", String.valueOf(Math.PI))
                .replace("e", String.valueOf(Math.E))
                .replace("inf", "Infinity")
                .replace("∞", "Infinity");

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}