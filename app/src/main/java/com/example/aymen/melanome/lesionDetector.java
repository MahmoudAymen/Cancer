package com.example.aymen.melanome;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aymen on 26/04/2018.
 */
public class lesionDetector {
    List<Mat> list;
    Mat MSegm,Mclose,Mthreshold,kernel,mGray,mHSL,mHierarchy,mRgba,resizeM,shape1,shape2,ImgResize;
    double x1,x2,y1,y2, posX,posY;
    List<MatOfPoint> contours;
    int contIndex;
    Moments moments;
    double M00,M01,M10,Mu11,Mu02,Mu20,Mu03,Mu30, A,SX,SY,KX,KY,PI,CI,width,height,D,E;
    float[]radius;
    Rect rect;
    double []tabFeature;
    MatOfPoint2f matOfPoint2f;
    public lesionDetector()
    {
        CreationVariable();
    }
    public void CreationVariable()
    {
        this.mGray = new Mat();
        this.mHSL = new Mat();
        this.resizeM = new Mat();
        this.mHierarchy = new Mat();
        this.list = new ArrayList();
        this.MSegm = new Mat();
        this.Mthreshold = new Mat();
        this.Mclose = new Mat();
        this.moments = new Moments();
        this.shape1 = new Mat();
        this.shape2 = new Mat();
    }
    public void Detect(Mat mat)
    {
        this.mRgba = mat;
        this.resizeM = new Mat(this.mRgba.width(), this.mRgba.height(), this.mRgba.type());
        Imgproc.resize(this.mRgba, this.resizeM, this.resizeM.size());
        this.ImgResize = new Mat(this.mRgba.width(), this.mRgba.height(), this.mRgba.type());
        Imgproc.resize(this.mRgba, this.ImgResize, this.ImgResize.size());
        this.shape1=resizeM;
        this.shape2=resizeM;
        Imgproc.cvtColor(this.resizeM, this.resizeM, 1);
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10.0D, 10.0D));
        Imgproc.medianBlur(this.resizeM, this.resizeM, 5);
        Imgproc.pyrMeanShiftFiltering(this.resizeM, this.MSegm, 20.0D, 30.0D);
        Imgproc.cvtColor(this.MSegm, this.mHSL, 3);
        Core.split(this.mHSL, this.list);
        this.list.get(1).convertTo(this.mGray, CvType.CV_8UC1);
        Imgproc.threshold(this.mGray, this.Mthreshold, 0.0D, 255.0D, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Imgproc.dilate(Mthreshold, Mclose, kernel);
        Imgproc.erode(Mthreshold, Mclose, kernel);
        Core.bitwise_not(this.Mclose, this.Mclose);
        this.contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(this.Mclose, this.contours, this.mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Calculfeatures();
    }
    public void Calculfeatures()
    {
        this.moments = Imgproc.moments(this.contours.get(this.contIndex));
        this.M00 = this.moments.get_m00();
        this.M10 = this.moments.get_m10();
        this.M01 = this.moments.get_m01();
        this.Mu11 = this.moments.get_mu11();
        this.Mu02 = this.moments.get_mu02();
        this.Mu20 = this.moments.get_mu20();
        this.Mu03= this.moments.get_mu03();
        this.Mu30 = this.moments.get_mu30();
        this.posX = (int) (this.M10 / this.M00);
        this.posY = (int) (this.M01 / this.M00);
        double N = this.Mu20 + this.Mu02 - (Math.sqrt(Math.pow(this.Mu20 - Mu02, 2) + 4 * (Math.pow(Mu11, 2)))) / 2;
        double N1 = this.Mu20 + this.Mu02 + (Math.sqrt(Math.pow(this.Mu20 - Mu02, 2) + 4 * (Math.pow(Mu11, 2)))) / 2;
        A = N / N1;
        SX = this.Mu30 / Math.pow(this.Mu20, 3 / 2);
        SY = this.Mu03 / Math.pow(this.Mu02, 3 / 2);
        KX = (Math.pow(this.Mu20, 4) / Math.pow(this.Mu20, 2)) - 3;
        KY = (Math.pow(this.Mu02, 4) / Math.pow(this.Mu02, 2)) - 3;
        PI = Imgproc.arcLength(new MatOfPoint2f((this.contours.get(this.contIndex)).toArray()), true);
        CI = Math.pow(PI, 2) / (4 * 3.141592653589793D * M00);
        matOfPoint2f = new MatOfPoint2f(contours.get(contIndex).toArray());
        this.radius = new float[1];
        Imgproc.minEnclosingCircle(matOfPoint2f, new Point(), radius);
        D = 2.0F * radius[0];
        this.rect = Imgproc.boundingRect(this.contours.get(contIndex));
        this.width = rect.width - 2;
        this.height = rect.height - 2;
        this.tabFeature=new double[]{A,SX,SY,KX,KY,CI,0};
    }
    public  double[] getvals()
    {

        return this.tabFeature;
    }
    public double getA()
    {
        return A;
    }
    public double getSX()
    {
        return SX;
    }
    public double getSY()
    {
        return SY;
    }
    public double getKX()
    {
        return KX;
    }
    public double getKY()
    {
        return KY;
    }
    public double getCI()
    {
        return CI;
    }
    public Mat NoramlisImage(Mat paramMat) {
        this.x1 = (int) Math.max(rect.tl().x , 2.0D);
        this.y1 = (int) Math.max(rect.tl().y , 2.0D);
        this.x2 = (int) Math.min(rect.br().x , -2 + paramMat.width());
        this.y2 = (int) Math.min(rect.br().y , -2 + paramMat.height());
        Mat localMat1 = paramMat.submat(new Rect(new Point(this.x1, this.y1), new Point(this.x2, this.y2))).clone();
        Mat localMat2 = new Mat(150, 150, this.mRgba.type());
        Imgproc.resize(localMat1, localMat2, localMat2.size());
        return localMat2;
    }
    public void FilltrageImage(Mat paramMat)
    {
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10.0D, 10.0D));
        Imgproc.medianBlur(paramMat, paramMat, 5);
        Imgproc.morphologyEx(paramMat, paramMat, Imgproc.MORPH_CLOSE, kernel);
    }
    public Mat getRGB() {

        return this.ImgResize;
    }
    public Mat getImageFilter()
    {
        FilltrageImage(this.ImgResize);
        return this.ImgResize;
    }
    public Mat getThresholdMat() {

        return this.Mthreshold;
    }

    public Mat getshape1() {
        return this.shape1;
    }
    public Mat getShape2()
    {
        return this.shape2;
    }
    public void BorderLesion()
    {
        Imgproc.minEnclosingCircle(matOfPoint2f, new Point(), this.radius);
        Point point=new Point(this.posX-this.width/2.0D,this.posY-this.height/2.0D);
        Point point1=new Point(this.posX+this.width/2.0D,this.posY+this.height/2.0D);
        Mat mat=this.shape2;
        Core.rectangle(mat, point, point1, new Scalar(255.0D, 0.0D, 0.0D, 255.0D), 2);
        Mat mat1=this.shape2;
        Core.circle(mat1, new Point(this.posX, this.posY), (int) radius[0], new Scalar(255.0D, 0.0D, 0.0D, 255.0D), 2);
    }
    public void findConvexHull(List<MatOfPoint>paramList, int paramInt)
    {
        MatOfPoint localMatOfPoint1 =  paramList.get(paramInt);
        MatOfInt localMatOfInt = new MatOfInt();
        Imgproc.convexHull(localMatOfPoint1, localMatOfInt, false);
        MatOfPoint localMatOfPoint2 = new MatOfPoint();
        localMatOfPoint2.create((int) localMatOfInt.size().height, 1, CvType.CV_32SC2);
        for (int i = 0; ; i++) {
            if (i >= localMatOfInt.size().height) {
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localMatOfPoint2);
                Imgproc.drawContours(this.shape1, localArrayList, 0, new Scalar(255.0D, 0.0D, 0.0D, 255.0D), 2);
                return;
            }
            int j = (int) localMatOfInt.get(i, 0)[0];
            double[] arrayOfDouble = new double[2];
            arrayOfDouble[0] = localMatOfPoint1.get(j, 0)[0];
            arrayOfDouble[1] = localMatOfPoint1.get(j, 0)[1];
            localMatOfPoint2.put(i, 0, arrayOfDouble);
        }
    }
    public void BorderAnalyse() {
        findConvexHull(this.contours,this.contIndex);
    }

}
