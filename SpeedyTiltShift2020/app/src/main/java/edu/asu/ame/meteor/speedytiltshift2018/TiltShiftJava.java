package meteor.asu.edu.speedytiltshift;


import android.util.Log;

//Implementation of functions for Java implementation
public class TiltShiftJava {

    //getSigma function calculates sigma values for different values of y
    public static float getSigma(int y, int a0, int a1, int a2, int a3, float sigma_far, float sigma_near){
        float sigma=0;

//        a0 = 100;
//        a1 = 200;
//        a2 = 400;
//        a3 = 500;
//        sigma_far = (float) 0.5;
//        sigma_near = (float) 0.8;
        Log.i("A vales",a0+","+a1+","+a2+","+a3);
        if(y<=a0) sigma=sigma_far;
        if(y>=a3) sigma=sigma_near;
        if(y>a0 && y<a1) sigma=sigma_far*((float) (a1-y)/(a1-a0));

        if(y>a2 && y<a3) sigma=sigma_near*((float) (y-a2)/(a3-a2));
        Log.i("Info inside class","sigma,i"+sigma+","+y);
        return sigma;
    }

    //getK function calculates kernet radius vector from sigma
    public static int[] getK(float sigma){
        int r=(int)Math.ceil(3*sigma);
        int size=2*r+1;
        int[] k = new int[size];             // k vector for G calculation
        int m=-r;
        for(int i=0; i<size; i++){
            k[i]=m++;
        }
        return k;
    }
    //function to calculate the Gaussian weight
    public static double[] GaussianWeightCalculator(int[] k, double sigma){
        int len = k.length;
        double[] weight = new double[len];
        for(int i=0; i<len; i++){
            weight[i]=(Math.exp((-1*k[i]*k[i])/(2*sigma*sigma))/Math.sqrt(2*Math.PI*sigma*sigma));
        }
        return weight;
    }

    //calculates the intermediate vector in weight vector approach of Gaussian Blur Algorithm
    public static double qVector(String argb, int x, int y, int width, int[] k, double[] G, int[] pixels){
        double q=0;
        int p;
        int len = k.length;
        //The following switch-case is operated based on the color zone AA RR BB GG
        switch(argb){
            case "BB": for(int i=0; i<len; i++){
                if((y+k[i])<0 || x<0 || (y+k[i])*width+x>= pixels.length) p=0;
                else p=pixels[(y+k[i])*width+x];
                int BB = p & 0xff;
                q+=BB*G[i];
            }
                return q;

            case "GG": for(int i=0; i<len; i++){
                if((y+k[i])<0 || x<0 || (y+k[i])*width+x>= pixels.length) p=0;
                else p=pixels[(y+k[i])*width+x];
                int GG = (p>>8) & 0xff;
                q+=GG*G[i];
            }
                return q;

            case "RR": for(int i=0; i<len; i++){
                if((y+k[i])<0 || x<0 || (y+k[i])*width+x>= pixels.length) p=0;
                else p=pixels[(y+k[i])*width+x];
                int RR = (p>>16) &0xff;
                q+=RR*G[i];
            }
                return q;

            case "AA": for(int i=0; i<len; i++){
                if((y+k[i])<0 || x<0 || (y+k[i])*width+x>= pixels.length) p=0;
                else p=pixels[(y+k[i])*width+x];
                int AA = (p>>24) &0xff;
                q+=AA*G[i];
            }
                return q;
        }
        return q;
    }

    //calculates the final weighted sum using the output of q vector
    public static int pVector(String argb, int x, int y, int width, int[] k, double[] G, int[] pixel){
        double P1=0;
        int P;
        int len = k.length;
        for(int i=0; i<len; i++){
            if(y*width+x+k[i]<pixel.length)
                P1=P1+G[i]*qVector(argb, x+k[i],y, width, k, G, pixel);
        }
        P=(int)P1;
        return P;
    }

}