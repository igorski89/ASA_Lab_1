
import java.util.ArrayList;

//import java.util.Hashtable;

/**
 * клас, що описує вибірку
 * @author igorevsukov
 */
public class MDSample {
    protected ArrayList<MDObject> data;
//    private ArrayList<MDObject> backup;

    /**
     * розмірність елементів у вибірці
     */
    protected int dimension;
    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * стрворює нову вибірку з елементами заданої розмірності
     * @param d розмірність елементів вибірки
     */
    public MDSample(int d) {
        dimension = d;
        data = new ArrayList<MDObject>();
        mean = new double[d];
        xmin = new double[d];
        xmax = new double[d];
        disp = new double[d];
        sigma = new double[d];
        assym = new double[d];
        exces = new double[d];
    }

    /**
     * повертає елемент вибірки
     * @param i порядковий номер необхідного елементу
     * @return
     */
    public MDObject get(int i) {
        return data.get(i);
    }

    /**
     * розмір вибірки
     * @return кількість елементів у вибірці
     */
    public int size() {
        return data.size();
    }

    /**
     * додає об'єкт до вибірки
     * @param newObj новий об'єкт
     */
    public void add(MDObject newObj) throws Exception {
        if (newObj.getParams().length != dimension)
            throw new Exception("MDobject dimension is different to this MDSample");
        
        data.add(newObj);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param i індекс об'єкту
     */
    public void remove(int i) {
        data.remove(i);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param obj об'єкт, що видаляється
     */
    public void remove(MDObject obj) {
        data.remove(obj);
    }

    /**
     * видаляє всі об'єкти з вибірки
     */
    public void removeAll() {
        data.clear();
    }
    

    
    protected double[] mean;
    /**
     * @return середнє значення за признаками
     */
    public double[] getMean(){ return mean; }
    public double getMean(int i) { return mean[i]; }
    
    protected double[] xmin;
    /**
     * @return мінімальне значення за признаками
     */
    public double[] getMin() { return xmin; }
    public double getMin(int i) { return xmin[i]; }
    
    protected double[] xmax;
    /**
     * @return максимальне значення за признаками
     */
    public double[] getMax(){ return xmax; }
    public double getMax(int i) { return xmax[i]; }

    protected double[] disp;
    /**
     * @return дисперсія за признаками
     */
    public double[] getDispersion() { return disp; }
    public double getDispersion(int i) { return disp[i]; }

    protected double[] sigma;
    public double[] getSigma() { return sigma; }
    public double getSigma(int i) { return sigma[i]; }

    protected double[] assym;
    public double[] getAssymentry() { return assym; }
    public double getAssymentry(int i) { return assym[i]; }

    protected double[] exces;
    public double[] getExces() { return exces; }
    public double getExces(int i) { return exces[i]; }


    public double getParam(int objectNum, int paramNum) {
        return get(objectNum).getParam(paramNum);
    }
    
    /**
     * вираховує характеристики вибірки: середнє, дисперсію та ін.
     */
    public void calculateParams() {
        final int N = data.size();

        //присваиваем начальные значения
        for (int j=0; j<dimension; j++) {
            double tmp = data.get(0).getParams()[j];
            mean[j] = tmp;
            xmin[j] = tmp;
            xmax[j] = tmp;
            disp[j] = 0;
            sigma[j] = 0;
            assym[j] = 0;
            exces[j] = 0;
        }

        //вычисляем реальные значения
        for (int j=0; j<dimension; j++) {
            for(int i=1; i<N; i++) {
                double tmp = data.get(i).getParams()[j];
                mean[j] += tmp;
                if (tmp < xmin[j])
                    xmin[j] = tmp;
                else if (tmp > xmax[j])
                    xmax[j] = tmp;
            }
            mean[j] /= data.size();
        }


        for (int j=0; j<dimension; j++) {
            for (int i=0; i<N; i++)
                disp[j] += Math.pow(data.get(i).getParams()[j]-mean[j], 2);

            disp[j] = Math.sqrt(disp[j]/(N-1.0));
        }

        for (int j=0; j<dimension; j++) {
            for (int i=0; i<N; i++)
                sigma[j] += Math.pow(data.get(i).getParam(j)-mean[j], 2);

            sigma[j] = Math.sqrt(sigma[j]/N);
//            sigma[j] = sigma[j]/N;
        }


        //Assymentry
        for (int j=0; j<dimension; j++) {
            for (int i=0; i<N; i++)             
                assym[j] += Math.pow(data.get(i).getParam(j)-mean[j],3);

            assym[j] *= Math.sqrt(N * (N - 1)) / ((N - 2) * Math.pow(sigma[j],3) * N);
        }

        //Excess
        for (int j=0; j<dimension; j++) {
            for (int i=0; i<N; i++)
                exces[j] += Math.pow(data.get(i).getParam(j)-mean[j],4);

            exces[j] /= Math.pow(sigma[j],4)*N;
            exces[j] = ((double)(N * N - 1) /(double)((N - 2) * (N - 3))) * ((exces[j] - 3) + 6.0 / (N + 1));
        }

    }

}
