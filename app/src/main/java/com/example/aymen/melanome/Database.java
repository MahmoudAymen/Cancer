package com.example.aymen.melanome;

/**
 * Created by aymen on 05/05/2018.
 */
public class Database {
    String type;
    String ImageUri;
    double A;
    double SX;
    double SY;
    double KX;
    double KY;
    double CI;
    double Entropy;

    public Database(double CI, String type, String imageUri, double a, double SX, double SY, double KX, double KY, double entropy) {
        this.CI = CI;
        this.type = type;
        ImageUri = imageUri;
        A = a;
        this.SX = SX;
        this.SY = SY;
        this.KX = KX;
        this.KY = KY;
        Entropy = entropy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getSX() {
        return SX;
    }

    public void setSX(double SX) {
        this.SX = SX;
    }

    public double getSY() {
        return SY;
    }

    public void setSY(double SY) {
        this.SY = SY;
    }

    public double getKX() {
        return KX;
    }

    public void setKX(double KX) {
        this.KX = KX;
    }

    public double getKY() {
        return KY;
    }

    public void setKY(double KY) {
        this.KY = KY;
    }

    public double getCI() {
        return CI;
    }

    public void setCI(double CI) {
        this.CI = CI;
    }

    public double getEntropy() {
        return Entropy;
    }

    public void setEntropy(double entropy) {
        Entropy = entropy;
    }
}
