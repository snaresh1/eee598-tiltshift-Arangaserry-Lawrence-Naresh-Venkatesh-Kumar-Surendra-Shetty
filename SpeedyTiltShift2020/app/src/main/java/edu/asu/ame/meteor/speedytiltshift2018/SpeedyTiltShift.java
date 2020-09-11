package edu.asu.ame.meteor.speedytiltshift2018;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import android.widget.TextView;

public class SpeedyTiltShift {
    static SpeedyTiltShift Singleton = new SpeedyTiltShift();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static long javaElapsedTime=0;

    public static Bitmap tiltshift_java(Bitmap input, float sigma_far, float sigma_near, int a0, int a1, int a2, int a3){
        long javaStart = System.currentTimeMillis(); //start time
        Bitmap outBmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        //cannot write to input Bitmap, since it may be immutable
        //if you try, you may get a java.lang.IllegalStateException

        Log.i("TILTSHIFT_JAVA","hey1:"+input.getWidth()+","+input.getHeight());

        int[] pixels = new int[input.getHeight()*input.getWidth()];
        int[] pixelsOut = new int[input.getHeight()*input.getWidth()];
        float sigma;
        double[] kernelMatrix;
        input.getPixels(pixels,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());
        for (int i=0; i<input.getHeight(); i++){
            sigma= meteor.asu.edu.speedytiltshift.TiltShiftJava.getSigma(i,a0,a1,a2,a3,sigma_far,sigma_near);
            Log.i("Info","sigma,i"+sigma+","+i);
            //if(sigma<=0.7) continue;        //Gaussian blur not required if sigma is less than 0.7
            if(i>=a1 &&  i<=a2)
            continue;
            int[] k= meteor.asu.edu.speedytiltshift.TiltShiftJava.getK(sigma);     //calculating radius vector
            kernelMatrix = meteor.asu.edu.speedytiltshift.TiltShiftJava.GaussianWeightCalculator(k, sigma);

            for (int x = 0; x<input.getWidth(); x++) {
                int pBB = meteor.asu.edu.speedytiltshift.TiltShiftJava.pVector("BB", x, i, input.getWidth(), k, kernelMatrix, pixels);
                int pRR = meteor.asu.edu.speedytiltshift.TiltShiftJava.pVector("RR", x, i, input.getWidth(), k, kernelMatrix, pixels);
                int pGG = meteor.asu.edu.speedytiltshift.TiltShiftJava.pVector("GG", x, i, input.getWidth(), k, kernelMatrix, pixels);
                int pAA = meteor.asu.edu.speedytiltshift.TiltShiftJava.pVector("AA", x, i, input.getWidth(), k, kernelMatrix, pixels);


                int color = (pAA & 0xff) << 24 | (pRR & 0xff) << 16 | (pGG & 0xff) << 8 | (pBB & 0xff);
                pixelsOut[i * input.getWidth() + x] = color;
            }
        }
        outBmp.setPixels(pixelsOut,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());
        //outBmp.setPixels(pixelsOut,a1,input.getWidth(),0,a2,input.getWidth(),a2-a1);
        Log.i("TILTSHIFT_JAVA","hey2");
        long javaEnd = System.currentTimeMillis();  //end time
        javaElapsedTime = javaEnd - javaStart;   //elapsed time calculation
        return outBmp;
    }
    public static Bitmap tiltshift_cpp(Bitmap input, float sigma_far, float sigma_near, int a0, int a1, int a2, int a3){
        Bitmap outBmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels = new int[input.getHeight()*input.getWidth()];
        int[] pixelsOut = new int[input.getHeight()*input.getWidth()];
        input.getPixels(pixels,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());

        tiltshiftcppnative(pixels,pixelsOut,input.getWidth(),input.getHeight(),sigma_far,sigma_near,a0,a1,a2,a3);

        outBmp.setPixels(pixelsOut,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());
        return outBmp;
    }
    public static Bitmap tiltshift_neon(Bitmap input, float sigma_far, float sigma_near, int a0, int a1, int a2, int a3){
        Bitmap outBmp = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels = new int[input.getHeight()*input.getWidth()];
        int[] pixelsOut = new int[input.getHeight()*input.getWidth()];
        input.getPixels(pixels,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());

        tiltshiftneonnative(pixels,pixelsOut,input.getWidth(),input.getHeight(),sigma_far,sigma_near,a0,a1,a2,a3);

        outBmp.setPixels(pixelsOut,0,input.getWidth(),0,0,input.getWidth(),input.getHeight());
        return outBmp;
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native int tiltshiftcppnative(int[] inputPixels, int[] outputPixels, int width, int height, float sigma_far, float sigma_near, int a0, int a1, int a2, int a3);
    public static native int tiltshiftneonnative(int[] inputPixels, int[] outputPixels, int width, int height, float sigma_far, float sigma_near, int a0, int a1, int a2, int a3);

}
