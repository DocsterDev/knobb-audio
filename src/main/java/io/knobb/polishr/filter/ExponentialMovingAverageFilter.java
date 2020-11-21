package io.knobb.polishr.filter;

public class ExponentialMovingAverageFilter {

    private double period;
    private Double oldValue = null;
    private Double newValue = 0D;

    public ExponentialMovingAverageFilter(double period) {
        this.period = period;
    }

    public ExponentialMovingAverageFilter() {

    }

    public double addAndCalculate(double value) {

        if (oldValue == null) {
            oldValue = value;
        }

        newValue = (value - oldValue) * (2 / (period + 1)) + oldValue;

        oldValue = newValue;

        return newValue;

    }

    public double getAverage() {
        return newValue;
    }

}
