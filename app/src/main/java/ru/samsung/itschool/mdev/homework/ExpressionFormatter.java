package ru.samsung.itschool.mdev.homework;

public class ExpressionFormatter {
    public static String formatResult(double result) {
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


    public static String formatResult(double result, String numberSystem) {
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

    public static String formatNumber(double num) {
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
}
