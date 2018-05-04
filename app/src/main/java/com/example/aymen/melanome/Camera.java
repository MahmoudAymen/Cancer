package com.example.aymen.melanome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Camera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener,View.OnTouchListener {
    static final String TAG = "Camera";
    Bitmap BitmapResult;
    double M00,M10,M01;
    Scalar colorW = new Scalar(255.0D, 255.0D, 255.0D, 255.0D);
    List<MatOfPoint> contours;
    Mat mHierarchy,mRgba,resultsMat;
    CameraBridgeViewBase mOpenCvCameraView;
    Moments moments;
    double posX,posY;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        public void onManagerConnected(int paramInt)
        {
            switch (paramInt)
            {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.d("OCVSample::Activity", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(Camera.this);
                    break;
                }
                default: {
                    super.onManagerConnected(paramInt);
                    break;
                }

            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug())
        {
            Log.d(TAG, "opencv not Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this,mLoaderCallback);

        }
        else
        {
            Log.d(TAG, "opencv Loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.mRgba = new Mat(height, width, CvType.CV_8UC4);
        this.mHierarchy = new Mat();

    }

    @Override
    public void onCameraViewStopped() {
        this.mRgba.release();

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        this.mRgba = inputFrame;
        this.resultsMat = null;
        Mat localMat1 = new Mat();
        Rect localRect1 = new Rect(inputFrame.width() / 2 - 100, inputFrame.height() / 2 - 100, 200, 200);
        Mat localMat2 = this.mRgba.submat(localRect1);
        Imgproc.cvtColor(localMat2, localMat1, 1);
        Imgproc.cvtColor(localMat1, localMat1, 7);
        Imgproc.adaptiveThreshold(localMat1, localMat1, 255, 0, 0, 13, 4);
        Core.bitwise_not(localMat1, localMat1);
        this.contours = new ArrayList<>();
        Imgproc.findContours(localMat1, this.contours, this.mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        int i = 0;
        Rect localRect2 = new Rect();
        int j = 0;
        double d1 = 0.0D;
        double d2 = 0.0D;
        for (int k = 0; ; k++)
        {
            int m = this.contours.size();
            if (k >= m)
            {
                if (j != 0)
                {
                    Core.mean(localMat2.submat(localRect2).clone());
                    Core.rectangle(localMat2, localRect2.tl(), localRect2.br(), this.colorW);
                    Core.circle(localMat2, new Point(d1, d2), 1, this.colorW);
                }
                Core.rectangle(this.mRgba, new Point(-100 + inputFrame.width() / 2, -100 + inputFrame.height() / 2), new Point(100 + inputFrame.width() / 2, 100 + inputFrame.height() / 2), this.colorW);
                Core.circle(this.mRgba, new Point(inputFrame.width() / 2, inputFrame.height() / 2), 40, this.colorW, 1);
                return this.mRgba;
            }
            double d3 = Imgproc.contourArea(this.contours.get(k));
            this.moments = Imgproc.moments( this.contours.get(k));
            this.M00 = this.moments.get_m00();
            this.M10 = this.moments.get_m10();
            this.M01 = this.moments.get_m01();
            this.posX = (int)(this.M10 / this.M00);
            this.posY = (int)(this.M01 / this.M00);
            int n = (int)Math.sqrt(Math.pow(this.posX - 100.0D, 2.0D) + Math.pow(this.posY - 100.0D, 2.0D));//distance eli bech yb3ad rajel bech y3amel sorwa
            if ((d3 <= i) || (n >= 20) || (d3 <= 70.0D)) {
                continue;
            }
            i = (int)d3;
            int i1 = k;
            localRect2 = Imgproc.boundingRect(this.contours.get(i1));
            int i2 = (int)Math.max(localRect2.tl().x - 20, 1.0D);
            int i3 = (int)Math.max(localRect2.tl().y - 20 , 1.0D);
            int i4 = (int)Math.min(localRect2.br().x + 20, -1 + localMat2.width());
            int i5 = (int)Math.min(localRect2.br().y + 20, -1 + localMat2.height());
            Rect localRect3 = new Rect(new Point(i2, i3), new Point(i4, i5));
            this.resultsMat = localMat2.submat(localRect3).clone();
            j = 1;
            d1 = this.posX;
            d2 = this.posY;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.resultsMat != null)
        {
            Intent localIntent = new Intent(getBaseContext(),Analyse.class);
            Mat localMat = new Mat(new Size(300.0D, 300.0D), this.resultsMat.type());
            Imgproc.resize(this.resultsMat, localMat, localMat.size());
            this.BitmapResult = Bitmap.createBitmap(localMat.width(), localMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(localMat, this.BitmapResult);
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            this.BitmapResult.compress(Bitmap.CompressFormat.PNG,50, localByteArrayOutputStream);
            localIntent.putExtra("imgCamera", localByteArrayOutputStream.toByteArray());
            startActivity(localIntent);

        }
        return false;
    }
}
