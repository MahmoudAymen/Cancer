package com.example.aymen.melanome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

public class Analyse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG="Analyse";
    ImageView imageView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Bitmap startImage,imageBmp;
    byte[]arrayOfByte;
    Bitmap BitmapResult1;
    Bitmap BitmapResult2;
    Bitmap BitmapResult3;
    Bitmap BitmapResult4;
    Bitmap BitmapResult5;
    Bitmap BitmapResult6;
    Mat startMat,maskMat ,segmentation,endMat;
    double[]tabFeature;
    Button analyse;
    lesionDetector lesionDetector;
    Progressbar dialog;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override

        public void onManagerConnected(int status) {
            switch (status) {
                default:
                    super.onManagerConnected(status);
                    return;
                case 0:
                    break;

            }
            Log.i(TAG, "OpenCV loaded successfully");
            ActionButton();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        imageView = (ImageView) findViewById(R.id.imageView1);
        if (getIntent().hasExtra("ImageGallery"))
        {
            arrayOfByte = getIntent().getByteArrayExtra("ImageGallery");
            startImage = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
            imageView.setImageBitmap(startImage);

        }
        if (getIntent().hasExtra("imgCamera"))
        {
            arrayOfByte = getIntent().getByteArrayExtra("imgCamera");
            startImage = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
            imageView.setImageBitmap(startImage);

        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionButton();
    }
    private void ActionButton()
    {
        analyse= (Button) findViewById(R.id.Analyse);
        analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage p = new processImage(Analyse.this);
                p.execute(new Void[0]);
            }
        });
    }
    private void DetectLesion()
    {
        startMat = new Mat();
        endMat = new Mat();
        maskMat = new Mat();
        segmentation=new Mat() ;
        new Mat();
        Log.i(TAG, "OpenCV 1 ");
        this.imageBmp = this.startImage;
        Utils.bitmapToMat(imageBmp, startMat);
        lesionDetector = new lesionDetector();
        lesionDetector.Detect(startMat);
        Log.i(TAG, "OpenCV 2 ");
        endMat = lesionDetector.getRGB();
        Mat localMat2 = lesionDetector.NoramlisImage(endMat);
        BitmapResult1 = Bitmap.createBitmap(localMat2.width(), localMat2.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(localMat2, BitmapResult1);
        endMat = lesionDetector.getImageFilter();
        Mat localMat3 = lesionDetector.NoramlisImage(endMat);;
        BitmapResult2 = Bitmap.createBitmap(localMat3.width(), localMat3.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(localMat3, BitmapResult2);
        maskMat=lesionDetector.getThresholdMat();
        segmentation=lesionDetector.NoramlisImage(maskMat);
        BitmapResult3 = Bitmap.createBitmap(segmentation.width(), segmentation.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(segmentation, this.BitmapResult3);
        Mat localMat4=new Mat(segmentation.width(),segmentation.height(),segmentation.type() );
        Imgproc.Canny(segmentation, localMat4, 50, 100);
        BitmapResult4 = Bitmap.createBitmap(localMat4.width(), localMat4.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(localMat4, BitmapResult4);
        lesionDetector.BorderAnalyse();
        endMat = lesionDetector.getshape1();
        Mat localMat5 = lesionDetector.NoramlisImage(endMat);
        BitmapResult5 = Bitmap.createBitmap(localMat5.width(), localMat5.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(localMat5, BitmapResult5);
        lesionDetector.BorderLesion();
        endMat = lesionDetector.getShape2();
        Mat localMat6= lesionDetector.NoramlisImage(endMat);
        BitmapResult6 = Bitmap.createBitmap(localMat6.width(), localMat6.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(localMat6, BitmapResult6);
        this.tabFeature =lesionDetector.getvals();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "opencv not Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, this.mLoaderCallback);
        } else {
            Log.d(TAG, "opencv Loaded");

        }

    }
    private class processImage extends AsyncTask<Void, Void, String> {
        Context context;

        public processImage(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Progressbar(this.context, R.drawable.bar);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void[] paramArrayOfVoid) {
            try {
                DetectLesion();
                return null;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Intent localIntent = new Intent(getBaseContext(), Resultat.class);
            ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
            BitmapResult1.compress(Bitmap.CompressFormat.PNG, 50, localByteArrayOutputStream1);
            localIntent.putExtra("image1", localByteArrayOutputStream1.toByteArray());
            ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
            BitmapResult2.compress(Bitmap.CompressFormat.PNG, 50, localByteArrayOutputStream2);
            localIntent.putExtra("image2", localByteArrayOutputStream2.toByteArray());
            ByteArrayOutputStream localByteArrayOutputStream3 = new ByteArrayOutputStream();
            BitmapResult3.compress(Bitmap.CompressFormat.PNG, 50, localByteArrayOutputStream3);
            localIntent.putExtra("image3", localByteArrayOutputStream3.toByteArray());
            ByteArrayOutputStream localByteArrayOutputStream4 = new ByteArrayOutputStream();
            BitmapResult4.compress(Bitmap.CompressFormat.PNG, 50, localByteArrayOutputStream4);
            localIntent.putExtra("image4", localByteArrayOutputStream4.toByteArray());
            ByteArrayOutputStream localByteArrayOutputStream5 = new ByteArrayOutputStream();
            BitmapResult5.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream5);
            localIntent.putExtra("image5", localByteArrayOutputStream5.toByteArray());
            ByteArrayOutputStream localByteArrayOutputStream6 = new ByteArrayOutputStream();
            BitmapResult6.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream6);
            localIntent.putExtra("image6", localByteArrayOutputStream6.toByteArray());
            localIntent.putExtra("value",tabFeature);
            startActivity(localIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.galleryOrCmera:
                Intent i=new Intent(Analyse.this,CameraOrGallery.class);
                startActivity(i);
                break;
            case R.id.aide:
                Intent i1=new Intent(Analyse.this,Aide.class);
                startActivity(i1);
                break;
            case R.id.aprop:
                Intent i2=new Intent(Analyse.this,Apropos.class);
                startActivity(i2);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
