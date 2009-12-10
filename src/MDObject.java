


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Клас, що описує простий обєкт
 * @author I-Evsukov
 */
public class MDObject implements Cloneable {

	private double params[];
	/**
     * параметри обєкту(x1,x2,..,xp)
     */
    public double[] getParams() {
        return params;
    }
    public double getParam(int index){ return params[index]; }
    public void setParams(double[] newParams) {
        params = newParams;
    }


    /**
     * створює новий обєкт з масиву параметрів та номеру классу
     * @param objParams масив параметрів
     */
    public MDObject(double[] objParams){
//        params = new double[objParams.length];
//        for (int i = 0; i < objParams.length; i++) {
//            params[i] = objParams[i];
//        }
    	
//        System.arraycopy(objParams, 0, params, 0, objParams.length);
    	params = objParams;
    }

    /**
     * створює новий обєкт зчитуючи параметри та номер классу з строки(для
     * завантаження з файлу)
     * @param s строка, в якій записані параметри
     */
    public MDObject(String s) throws Exception{
//        Scanner sc = new Scanner(s);
//
//        // считываем параметры
//        ArrayList<Double> tempParams = new ArrayList<Double>();
//        //вот такая подпорка, а то эта сцуко считает последнее число тоже даблом
//        while(sc.hasNextDouble() && !sc.hasNextInt()){
//            tempParams.add(sc.nextDouble());
//        }
//        if (tempParams.isEmpty()) {
//            throw new Exception("there is no params of double type in "+s);
//        }
//
//        params = new double[tempParams.size()];
//        for (int i = 0; i < tempParams.size(); i++) {
//            params[i] = tempParams.get(i);
//        }
//
//        // считываем номер класса
//        if (!sc.hasNextInt()) {
//            throw new Exception("there is no classNumber of int type in "+s);
//        }
//        classN = sc.nextInt();
//
//        sc.close();
    	
//    	s = s.trim();
    	
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(s.trim());
        String[] str = matcher.replaceAll(" ").split(" ");
        params = new double[str.length];
        for(int i = 0; i< str.length; i++)
        	params[i] = Double.parseDouble(str[i]);
    }

    /**
     * повертає параметри та номер классу об'єкту у вигляді строки
     * @return (x1,x2,...,xn)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            sb.append(",");
        }
        sb.delete(sb.length()-2, sb.length()-1);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public MDObject clone() {
        double[] cloneParams = new double[params.length];
//        for (int i=0; i<params.length; i++) 
//            cloneParams[i] = params[i];
        System.arraycopy(params, 0, cloneParams, 0, params.length);
        
        return new MDObject(cloneParams);
    }
}
