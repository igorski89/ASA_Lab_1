import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class TwoDSample extends MDSample {

    /**
	 * створює двовимірну вибірку
	 */
	public TwoDSample() {
		super(2);
//		rankCorrelationCoefficients = new double[2][];
	}

    public double getX(int i) { return get(i).getParam(0); }
    public double getY(int i) { return get(i).getParam(1); }

    protected ArrayList<ArrayList<Double>> classArray = new ArrayList<ArrayList<Double>>();
    protected void calculateClassArray() {
        classArray.clear();
        final int N = size();

        int M;
        if (N < 100) M = (int)Math.sqrt(N);
        else M = (int) pow(N, 1.0 / 3.0);
        if (M % 2 == 0) M--;

        double minX = getX(0);
        double maxX = getX(N-1);
        for (int i=0; i<N; i++) {
            double x = getX(i);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }
        double lel = minX;
        double ril;
        double h = (maxX - lel) / M;
        for (int i = 1; i < M; i++) {
            ril = lel + h;
            classArray.add(new ArrayList<Double>());
            for (int j = 0; j < N; j++)
                if ((getX(j) < ril) && (getX(j) >= lel))
                    classArray.get(i - 1).add(getY(j));
            lel = ril;
        }
        ril = lel + h;
        classArray.add(new ArrayList<Double>());
        for (int i = 0; i < N; i++)
            if ((getX(i) < ril) && (getX(i) >= lel))
                classArray.get(classArray.size() - 1).add(getY(i));
    }

    protected double alpha = 0.05;
    /**
     * @return помилка першого роду
     */
    public double getAlhpa() { return alpha; }
    /**
     * встановлює нове значення помилки першого роду для вибірки
     * @param newAlpha нове значення помилки першого роду
     */
    public void setAlpha(double newAlpha){ alpha = newAlpha; }

    
    protected Criterion corelation = new Criterion("Pair Corelation");
    public Criterion getCorelation() { return corelation; }
    /**
     * вираховує коефіцієнт кореляції та його значимість.
     */
	protected void calculateCorelation() {    
		double meanxy = 0;
        for (MDObject aData : data) {
            meanxy += aData.getParam(0) * aData.getParam(1);
        }
        meanxy /= data.size();
        
//        corelation.setValue((meanxy - mean[0]*mean[1])/(disp[0]*disp[1]));
        corelation.setValue((meanxy - mean[0]*mean[1])/(sigma[0]*sigma[1]));
        
        //вычисляем значимость кореляции
        double t = (corelation.getValue()*Math.sqrt(data.size()-2))/Math.sqrt(1 - pow(corelation.getValue(), 2));
        corelation.setStatistic(t);
//        double quantStud = Quantiles.Student(data.size()-2, alpha/2.0);
        double quantStud = Quantiles.t(alpha/2.0, data.size()-2);
        corelation.setQuantile(Quantiles.Student(alpha/2.0, data.size()-2));
        corelation.setSignificant(!(abs(t) <= quantStud));
        if (corelation.isSignificant()) {
        	//кореляция значима, считаем доверительные интервалы
        	double corelationBottomConfidenceLimit = corelation.getValue() + (corelation.getValue()*(1- pow(corelation.getValue(), 2)))/(2*data.size()) - Quantiles.norm(alpha/2.0)*(1- pow(corelation.getValue(), 2))/Math.sqrt(data.size()-1);
        	double corelationTopConfidenceLimit    = corelation.getValue() + (corelation.getValue()*(1- pow(corelation.getValue(), 2)))/(2*data.size()) + Quantiles.norm(alpha/2.0)*(1- pow(corelation.getValue(), 2))/Math.sqrt(data.size()-1);
            corelation.setTopConfidienceLimit(corelationTopConfidenceLimit);
            corelation.setBottomConfidienceLimit(corelationBottomConfidenceLimit);
        }

                
	}
    
    protected Criterion corelationRatio = new Criterion("Corelation ratio");
    public Criterion getCorealtionRatio() { return corelationRatio; }
	/**
	 * вираховує кореляційне відношення та його значимість
	 */
	protected void calculateCorelationRatio() {
        calculateClassArray();
        final int k = classArray.size();
		//yi с чертой
        double[] yimean = new double[k];
        for(int i=0; i<k; i++){
        	yimean[i] = 0;
        	ArrayList<Double> yj = classArray.get(i);
        	int mi = yj.size();
        	for (int j=0; j<mi; j++){
        		yimean[i] += yj.get(j);
        	}
        	yimean[i] /= mi;
        }

        //y с чертой
        double ymean = 0;
        for(int i=0; i<yimean.length; i++) ymean += yimean[i];
        ymean /= yimean.length;

        //кореляционное отношение
        double numerator = 0.0; //числитель
        for (int i = 0; i < k; i++){
        	numerator += classArray.get(i).size() * pow(yimean[i] - ymean,2);
        }

        double denominator = 0.0; //знаменатель
        for (int i = 0; i < k; i++){
            ArrayList<Double> yi = classArray.get(i);
            double mi =  yi.size();
        	for (int j = 0; j < mi; j++) {
        		denominator += pow(yi.get(j) - ymean, 2);
        	}
        }


        corelationRatio.setValue(sqrt( numerator/denominator ));

        //вычисляем значимость кореляционного соотношения
        corelationRatio.setQuantile(Quantiles.t(alpha/2.0, size()-2));
        double t = (corelationRatio.getValue()*sqrt(data.size()-2))/Math.sqrt(1.0 - pow(corelationRatio.getValue(), 2));
        corelationRatio.setStatistic(t);
        corelationRatio.setSignificant(!(abs(t) <= corelationRatio.getQuantile()));
	}

    protected Criterion spirmanCoefficient = new Criterion("Spirman's coefficient");
    public Criterion getSpirmanCoefficient() { return spirmanCoefficient; }
    protected double[] rangedX;
    protected double[] rangedY;
    protected ArrayList<Integer> a = new ArrayList<Integer>();
    protected ArrayList<Integer> b = new ArrayList<Integer>();
    private void setR(int [] r, double [] x){
        for (int i = 0; i < x.length; i++){
            int temp = 1;
            for(int j = 0; j < x.length; j++){
                if (x[i]==x[j]&& i!=j){
                    temp++;
                }
            }
            r[i] = temp;
        }
    }
    private void setRange(double[] x, double[] rx, ArrayList<Integer> A){
        int[]r = new int[x.length];
        setR(r,x);

        for(int i = 0; i< x.length; i++){
            double tempIndex = i;
            rx[i] = i+1;
            if(r[i]!=1){
                for (int j = 1; j < r[i]; j++) tempIndex += i+j;
                tempIndex/=r[i];

                for(int j = 0; j<r[i];j++) rx[i+j] = tempIndex+1;

                A.add(r[i]);

                i = i + r[i] - 1;
            }
        }
    }
    private void rearrangeRangedY(double[] y, double[]x){
        final int N = size();
        double [] temp = new double[N];
        double tempY = 0;        
        ArrayList<Double[]> tempData = new ArrayList<Double[]>();
        for (int i = 0; i<N; i++){
            tempData.add(new Double[] { getX(i), getY(i) });
        }
        
        for (int i = 0; i < rangedY.length; i++){
            for (int j = 0; j< tempData.size(); j++){
                if (x[i] == tempData.get(j)[0]){
                    tempY = tempData.get(j)[1];
                    tempData.remove(j);
                    break;
                }
            }

            int index  = 0;
            for (int j = 0; j< y.length; j++){
                if (tempY == y[j]){
                    index = j;
                    break;
                }
            }

            temp[i] = rangedY[index];
        }

        System.arraycopy(temp, 0, rangedY, 0, N);
    }
    protected void calculateRanges() {
        final int N = size();
        double x[] = new double[N];
        double y[] = new double[N];
        for (int i=0; i<N; i++) {
            x[i] = this.getX(i);
            y[i] = this.getY(i);
        }
        Arrays.sort(x);
        Arrays.sort(y);
        rangedX = new double[N];
        rangedY = new double[N];
        a.clear();
        b.clear();

        setRange(x, rangedX, a);
        setRange(y, rangedY, b);
        rearrangeRangedY(y, x);
    }
    public void calculateSpirmanCorelation() {
        calculateRanges();
        final int N = size();
        double D = 0;
        for(int i = 0; i < N;i++) D += pow(rangedX[i]-rangedY[i],2);


        if (a.size()==0 && b.size()==0){
            spirmanCoefficient.setValue(1.0 - 6.0*D/(double)(N*(N*N-1)));
        }
        else {
            double A = 0;
            for (Integer ai:a) A += pow(ai, 3) - ai;
            A /= 12.0;

            double B = 0;
            for (Integer bi:b) B += pow(bi, 3) - bi;
            B /= 12.0;

            spirmanCoefficient.setValue( ((double)(N*(N*N-1))/6.0 - D - A - B)/ Math.sqrt( ((double)(N*(N*N-1))/6.0-2*A)*((double)(N*(N*N-1))/6.0-2*B)) );
        }
        spirmanCoefficient.setStatistic(spirmanCoefficient.getValue() * Math.sqrt( (N-2.0)/(1.0 - spirmanCoefficient.getValue() * spirmanCoefficient.getValue()) ) );
        spirmanCoefficient.setQuantile(Quantiles.t(alpha/2.0, size()-2));
        spirmanCoefficient.setSignificant( ! (abs(spirmanCoefficient.getStatistic()) <= spirmanCoefficient.getQuantile()) );
    }


    protected Criterion kendallCoefficient = new Criterion("Kendall's coefficient");
    public Criterion getKendallCoefficient() { return kendallCoefficient; }
    public void calculateKendallCorelation() {
        double s = 0;
        final int N = size();
        for (int i=0; i < N-1; i++)
            for (int j=i+1; j < N; j++)
                if (rangedY[i] < rangedY[j]) s++;
                else if (rangedY[i] > rangedY[j]) s--;

        kendallCoefficient.setValue(2.0*s/(N*(N-1.0)));
        kendallCoefficient.setStatistic(3 * kendallCoefficient.getValue() * Math.sqrt(N*(N-1)/(4.0*N + 10.0)));
        kendallCoefficient.setQuantile(Quantiles.CalcU(alpha/2.0));
        kendallCoefficient.setSignificant(abs(kendallCoefficient.getStatistic()) > kendallCoefficient.getQuantile());

    }

    protected Criterion corelationCoefficients[] = new Criterion[]{corelation, corelationRatio, spirmanCoefficient, kendallCoefficient};
    public Criterion[] getCorelationCoefficients() { return corelationCoefficients; }
    
    /**
     * вираховує характеристики вибірки: середнє, дисперсію та ін.
     */
    public void calculateParams() {
        super.calculateParams();
        
        //вычисляем коэфициент кореляции
        calculateCorelation();
        
        //вычисляем кореляционное соотношение
        calculateCorelationRatio();
        
//        calcRankCorrelationCoefficients();

        calculateSpirmanCorelation();

        calculateKendallCorelation();
    }


}
