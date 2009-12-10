import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.log;
import static java.lang.Math.exp;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Dec 2, 2009
 * Time: 7:08:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonLinearRegression extends Object {
    protected double alpha = 0.05;
    protected TwoDSample sample = new TwoDSample();
    protected TwoDSample originalSample;

    public TwoDSample getSample() { return sample; }

    public NonLinearRegression(TwoDSample sampl) {
        this.originalSample = sampl;
        int n = sampl.size();
        for (int i=0; i<n; i++) {
            double x = sampl.getX(i);
            double y = sampl.getY(i);

            double t = 1.0/x;
            double z = log(y);

            try {
                sample.add(new MDObject(new double[] {t,z}));
            }catch (Exception ex) {
                System.out.println("can't add object to sample:" + ex.getMessage());
            }
        }
        sample.calculateParams();
        calculateAB();
        calculateOriginalAB();
        determinationCoefficient = pow(sample.getCorelation().getValue(),2);
        calculateAdequacy();
        calculateRegressionLine();
        calculateConfidienceLimits();
        calculateNewConfidienceLimits();
        calculateTolerantLimits();
    }

    //квадрат остаточной дисперсии
    protected double residualDispSqr = 0;
    public double getResidualDispSqr() { return residualDispSqr; }
    public void calculateResidualDispSqr() {
        final int N = sample.size();
        double a = getA().getValue();
        double b = getB().getValue();
        for (int i=0; i<N; i++) {
            double x = sample.getX(i);
            double y = sample.getY(i);
            residualDispSqr += pow(y - (a+x*b),2);
        }
        residualDispSqr /= N-2;
    }

    protected Criterion a = new Criterion("A");
    public Criterion getA() { return a; }
    protected Criterion b = new Criterion("B");
    public Criterion getB() { return b; }
    public void calculateAB() {
        final int N = sample.size();
        b.setValue(sample.getCorelation().getValue() * sample.getSigma(1)/sample.getSigma(0));
        b.setQuantile(Quantiles.t(alpha/2.0,N-2));

        a.setValue(sample.getMean(1) - b.getValue()*sample.getMean(0));
        a.setQuantile(Quantiles.t(alpha/2.0,N-2));

        calculateResidualDispSqr();
        double Sa = sqrt(residualDispSqr) * sqrt(1/N + pow(sample.getMean(0),2)/(pow(sample.getSigma(0),2)*(N-1)));
        a.setStatistic(abs(a.getValue()-0)/Sa);
        a.setSignificant(!(a.getStatistic() <= a.getQuantile()));
        a.setTopConfidienceLimit(a.getValue() + a.getQuantile()*Sa);
        a.setBottomConfidienceLimit(a.getValue() - a.getQuantile()*Sa);

        double Sb = sqrt(residualDispSqr) / (sample.getSigma(0) * sqrt(N-1));
        b.setStatistic(abs(b.getValue() - 0)/Sb);
        b.setSignificant(!(b.getStatistic() <= b.getQuantile()));
        b.setTopConfidienceLimit(b.getValue() + b.getQuantile() * Sb);
        b.setBottomConfidienceLimit(b.getValue() - b.getQuantile() * Sb);
    }

    protected Criterion originalA = new Criterion("a");
    public Criterion getOriginalA() { return originalA; }
    protected Criterion originalB = new Criterion("b");
    public Criterion getOriginalB() { return originalB; }
    public void calculateOriginalAB() {
        originalA.setValue(exp(a.getValue()));
//        originalA.setStatistic(a.getValue()/sqrt(sample.getDispersion()));
        originalA.setQuantile(Double.NaN);
        originalA.setBottomConfidienceLimit(exp(a.getBottomConfidienceLimit()));
        originalA.setTopConfidienceLimit(exp(a.getTopConfidienceLimit()));

        originalB.setValue(b.getValue());
        originalB.setStatistic(b.getStatistic());
        originalB.setQuantile(b.getQuantile());
        originalB.setSignificant(b.isSignificant());
        originalB.setBottomConfidienceLimit(b.getBottomConfidienceLimit());
        originalB.setTopConfidienceLimit(b.getTopConfidienceLimit());                                                                           
    }

    protected double determinationCoefficient;
    public double getDeterminationCoefficient() { return determinationCoefficient*100; }

    protected Criterion adequacy = new Criterion("adequacy");
    public Criterion getAdequacy() { return adequacy; }
    public void calculateAdequacy() {
        final int N = sample.size();
        adequacy.setValue(Double.NaN);
        adequacy.setStatistic(pow(sample.getSigma(1),2)/residualDispSqr);
        adequacy.setQuantile(Quantiles.Fisher(0.05/2,N-1,N-2));
        adequacy.setSignificant(adequacy.getStatistic() > adequacy.getQuantile());
    }

    protected Criterion params[] = new Criterion[] {a, b, adequacy, originalA, originalB};
    public Criterion[] getParams() { return params; }

    protected ArrayList<Point2D> regressionLine = new ArrayList<Point2D>();
    public ArrayList<Point2D> getRegressionLine() { return regressionLine; }
    public void calculateRegressionLine() {
        regressionLine.clear();
        final int N = sample.size();
        double a = getOriginalA().getValue();
        double b = getOriginalB().getValue();
        for(int i=0; i<N; i++) {
            double x = originalSample.getX(i);
            double y = a*exp(b/x);
//            double y = a*pow(x,b);
            regressionLine.add(new Point2D.Double(x,y));
        }
    }

    protected ArrayList<Point2D> topConfidienceLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getTopConfidienceLimit() { return topConfidienceLimit; }
    protected ArrayList<Point2D> bottomConfidienceLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getBottomConfidienceLimit() { return bottomConfidienceLimit; }
    public void calculateConfidienceLimits() {
        final int N = sample.size();
        topConfidienceLimit.clear();
        bottomConfidienceLimit.clear();
        double Sb = sqrt(residualDispSqr) / (sample.getSigma(0) * sqrt(N-1));
        double quantileT = Quantiles.t(alpha/2.0, N-2);
        double a = getA().getValue();
        double b = getB().getValue();
        for (int i=0; i<N; i++) {
            double x = sample.getX(i);
            double S = sqrt(residualDispSqr/N + pow(Sb,2)*pow(x-sample.getMean(0),2));

            double bottomY = exp(a + b * x - quantileT*S);
            bottomConfidienceLimit.add(new Point2D.Double(originalSample.getX(i),bottomY));

            double topY = exp(a + b * x + quantileT*S);
            topConfidienceLimit.add(new Point2D.Double(originalSample.getX(i),topY));
        }
    }

    protected ArrayList<Point2D> topNewConfidienceLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getTopNewConfidienceLimit() { return topNewConfidienceLimit; }
    protected ArrayList<Point2D> bottomNewConfidienceLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getBottomNewConfidienceLimit() { return bottomNewConfidienceLimit; }
    public void calculateNewConfidienceLimits() {
        final int N = sample.size();
        topNewConfidienceLimit.clear();
        bottomNewConfidienceLimit.clear();

        double Sb = sqrt(residualDispSqr) / (sample.getSigma(0) * sqrt(N-1));
        double quantileT = Quantiles.t(alpha/2.0, N-2);
        double a = getA().getValue();
        double b = getB().getValue();
        for (int i=0; i<N; i++) {
            double x = sample.getParam(i, 0);
            double S = sqrt(residualDispSqr*(1.0+1.0/(double)N) + pow(Sb,2)*pow(x-sample.getMean(0),2));

            double bottomY = exp(a + b*x - quantileT*S);
            bottomNewConfidienceLimit.add(new Point2D.Double(originalSample.getX(i),bottomY));

            double topY = exp(a + b*x + quantileT*S);
            topNewConfidienceLimit.add(new Point2D.Double(originalSample.getX(i),topY));
        }
    }

    protected ArrayList<Point2D> topTolerantLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getTopTolerantLimit() { return topTolerantLimit; }
    protected ArrayList<Point2D> bottomTolerantLimit = new ArrayList<Point2D>();
    public ArrayList<Point2D> getBottomTolerantLimit() { return bottomTolerantLimit; }
    public void calculateTolerantLimits() {
        final int N = sample.size();
        topTolerantLimit.clear();
        bottomTolerantLimit.clear();

        double S = sqrt(residualDispSqr);

        double quantileT = Quantiles.t(alpha/2.0, N-2);

        for (int i=0; i<N; i++) {
            double x = sample.getParam(i, 0);

            double bottomY = exp(getA().getValue() + getB().getValue() * x - quantileT*S);
            bottomTolerantLimit.add(new Point2D.Double(originalSample.getX(i),bottomY));

            double topY = exp(getA().getValue() + getB().getValue() * x + quantileT*S);
            topTolerantLimit.add(new Point2D.Double(originalSample.getX(i),topY));
        }
    }

}
