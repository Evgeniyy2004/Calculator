package ru.samsung.itschool.mdev.homework;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.*;

public class IntegralSolver {

    public interface MathFunction {
        double evaluate(double x);
    }

    public static double simpson(MathFunction f, double a, double b, int iterations) {
        UnivariateFunction func = f::evaluate;
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        return integrator.integrate(iterations, func, a, b);
    }


    public static double trapezoid(MathFunction f, double a, double b, int iterations) {
        UnivariateFunction func = f::evaluate;
        TrapezoidIntegrator integrator = new TrapezoidIntegrator();
        return integrator.integrate(iterations, func, a, b);
    }

    public static double adaptive(MathFunction f, double a, double b,
                                  double relativeAccuracy, double absoluteAccuracy) {
        UnivariateFunction func = f::evaluate;
        BaseAbstractUnivariateIntegrator integrator =
                new SimpsonIntegrator(relativeAccuracy, absoluteAccuracy,
                        SimpsonIntegrator.DEFAULT_MIN_ITERATIONS_COUNT,
                        SimpsonIntegrator.SIMPSON_MAX_ITERATIONS_COUNT);
        return integrator.integrate(Integer.MAX_VALUE, func, a, b);
    }

    public static double infinite(MathFunction f, double a, boolean toPositiveInfinity) {
        UnivariateFunction func = f::evaluate;

        if (toPositiveInfinity) {
            // Интеграл от a до +∞
            return integrateToInfinity(func, a, true);
        } else {
            // Интеграл от -∞ до a
            return integrateToInfinity(func, a, false);
        }
    }


    private static double integrateToInfinity(UnivariateFunction f, double a, boolean positive) {
        // Преобразование для интегрирования с бесконечными пределами
        if (positive) {
            UnivariateFunction transformed = t -> {
                double x = 1/t;
                return f.value(x) / (t * t);
            };
            return adaptive(t -> transformed.value(t), 0, 1/a, 1e-10, 1e-10);
        } else {
            UnivariateFunction transformed = t -> {
                double x = 1/t;
                return f.value(x) / (t * t);
            };
            return adaptive(t -> transformed.value(t), -1/a, 0, 1e-10, 1e-10);
        }
    }

}