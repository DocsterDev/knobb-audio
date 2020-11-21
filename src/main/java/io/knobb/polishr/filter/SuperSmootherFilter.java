package io.knobb.polishr.filter;

public class SuperSmootherFilter {

    private double a1;
    private double b1;

    private double c2;
    private double c3;
    private double c1;

    private double filt;
    private double filt1;
    private double filt2;

    int period;

    private Double currentPrice;
    private Double currentPrice1;

    public SuperSmootherFilter(int period) {

        a1 = Math.exp(-Math.sqrt(2) * Math.PI / period);
        b1 = 2 * a1 * Math.cos(Math.sqrt(2) * 180 / period);
        c2 = b1;
        c3 = -a1 * a1;
        c1 = 1 - c2 - c3;

    }

    public SuperSmootherFilter() {

    }

    public double addAndCalculate(double value) {

        currentPrice = value;

        if (currentPrice1 == null) {
            filt1 = currentPrice;
            filt2 = currentPrice;
            currentPrice1 = currentPrice;
        }

        filt = c1 * (currentPrice + currentPrice1) / 2 + c2 * filt1 + c3 * filt2;
        currentPrice1 = currentPrice;

        filt2 = filt1;
        filt1 = filt;

        return filt;

    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;

        a1 = Math.exp(-Math.sqrt(2) * Math.PI / period);
        b1 = 2 * a1 * Math.cos(Math.sqrt(2) * 180 / period);
        c2 = b1;
        c3 = -a1 * a1;
        c1 = 1 - c2 - c3;

    }

}
