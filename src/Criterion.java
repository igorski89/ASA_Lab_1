/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Dec 1, 2009
 * Time: 6:47:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Criterion extends Object {
    public Criterion() {
    }

    public Criterion(String name) {
        this.name = name;
    }

    public String getConfidienceLimits() {
//        return "["+bottomConfidienceLimit+";"+topConfidienceLimit+"]";
        return String.format("[%.3f;%.3f]",bottomConfidienceLimit,topConfidienceLimit);
    }

    public double getBottomConfidienceLimit() {
        return bottomConfidienceLimit;
    }

    public void setBottomConfidienceLimit(double bottomConfidienceLimit) {
        this.bottomConfidienceLimit = bottomConfidienceLimit;
    }

    public double getTopConfidienceLimit() {
        return topConfidienceLimit;
    }

    public void setTopConfidienceLimit(double topConfidienceLimit) {
        this.topConfidienceLimit = topConfidienceLimit;
    }

    public Boolean isSignificant() {
        return significant;
    }

    public void setSignificant(Boolean significant) {
        this.significant = significant;
    }

    public double getQuantile() {
        return quantile;
    }

    public void setQuantile(double quantile) {
        this.quantile = quantile;
    }

    public double getStatistic() {
        return statistic;
    }

    public void setStatistic(double statistic) {
        this.statistic = statistic;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected String name;
    protected double value;
    protected double statistic;
    protected double quantile;
    protected Boolean significant = false;
    protected double topConfidienceLimit = Double.NaN;
    protected double bottomConfidienceLimit = Double.NaN;
}
