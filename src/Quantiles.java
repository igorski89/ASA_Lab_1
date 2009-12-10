
public class Quantiles {
    /**
	 * @param nu ню //искренне Ваш, К.О.
	 * @param alpha помилка першого роду
	 * @return квантиль Стьюдента
	 */
	public static double Student(double alpha, double nu){
		double quantNorm = norm(alpha);

		double g1 = (double) (
				Math.pow(quantNorm, 3) 
				+ quantNorm
		) / 4.0;

		double g2 = (double) (

				5 * Math.pow(quantNorm, 5)
				+ 16 * Math.pow(quantNorm, 3)
				+ 3 * quantNorm

		) / 96.0;

		double g3 = (double) (

				3 * Math.pow(quantNorm, 7)
				+ 19 * Math.pow(quantNorm, 5)
				+ 17 * Math.pow(quantNorm, 3)
				- 15 * quantNorm

		) / 384.0;

		double g4 = (double) (

				79 * Math.pow(quantNorm, 9)
				+ 779 * Math.pow(quantNorm, 7)
				+ 1482 * Math.pow(quantNorm, 5)
				- 1920 * Math.pow(quantNorm, 3)
				- 945 * quantNorm

		) / 92160.0;

		double quant = quantNorm
		+ (double) g1 / nu
		+ (double) g2 / Math.pow(nu, 2)
		+ (double) g3 / Math.pow(nu, 3)
		+ (double) g4 / Math.pow(nu, 4);

		return quant;
	}


    /**
     * @param a помилка першого роду
     * @return квантиль нормальго розподілу
     */
    public static double norm(double a){

        double c0 = 2.515517;
        double c1 = 0.802853;
        double  c2 = 0.010328;
        double  d1 = 1.432788;
        double  d2 = 0.1892659;
        double  d3 = 0.001308;
        double  e = 0;//4.5e-4;
        double p = a;
        double t = Math.sqrt((-1)*Math.log(p*p));

        return (double) t - (double)(c0+c1*t+c2*t*t)/(1+d1*t+d2*t*t+d3*t*t*t)+e;
    }

    public static double Fisher(double a, int v1, int v2){
        double u = norm(a);
        double s = (double) 1/v1+1/v2;
        double d = (double) 1/v1- (double)1/v2;
        double u2 = u*u;
        double u3 = u2*u;
        double u4 = u2*u2;
        double u5 = u*u4;
        double z = u*Math.sqrt(s/2) - (double)d*(u*u+2)/6;
        z = z+Math.sqrt(s/2)*( s*(u2+3*u)/24 + (double) d*d*(u3+11*u)/(72*s) );
        z = z- (double) s*d*(u4+9*u2+8)/120 + (double) d*d*d*(3*u4+7*u2-16)/(3240*s);
        z = z+
                Math.sqrt(s/2)*(  (double) s*s*(u5+20*u3+15*u)/1920
                        +   (double) d*d*d*d*(u5+44*u3+183*u)/2880
                        +(double) d*d*d*d*(9*u5-284*u3-1513*u)/(155520*s*s)  ) ;
        return Math.exp(2*z);
    }

    public static double CalcU(double alfa) {
        double t = Math.log(alfa * alfa);
        t = Math.sqrt(-t);
        return t - (2.515517 + 0.802853 * t + 0.010328 * t * t) / (1 + 1.432788 * t + 0.1892659 * t * t + 0.001308 * t * t * t);
    }

    public static double t(double alfa, double nu) {
        double u = CalcU(alfa);
        double g1 = 0.25 * (u * u * u + u);
        double g2 = (5 * Math.pow(u, 5) + 16 * u * u * u + 3 * u) / 96;
        double g3 = (3 * Math.pow(u, 7) + 19 * Math.pow(u, 5) + 17 * u * u * u - 15 * u) / 384;
        double g4 = (79 * Math.pow(u, 9) + 779 * Math.pow(u, 7) + 1482 * Math.pow(u, 5) - 1920 * u * u * u - 945 * u) / 92160;
        return u + g1 / nu + g2 / (nu * nu) + g3 / (nu * nu * nu) + g4 / (nu * nu * nu * nu);
    }
}
