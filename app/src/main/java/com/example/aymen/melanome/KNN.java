package com.example.aymen.melanome;

import android.content.Context;

/**
 * Created by aymen on 05/05/2018.
 */
public class KNN {
    Context context ;
    public KNN(Context context)
    {
        this.context=context;
    }
    public double distance(double[] vect1, double[] vect2) {
        double res = 0;
        double k;
        double kcarre;
        double rest=0;

        for (int i = 0; i < 7; i++) {
            k=vect1[i]-vect2[i];
            kcarre=Math.pow(k,2);
            rest+=kcarre;
        }
        res=Math.sqrt(rest);
        return res;
    }
}
