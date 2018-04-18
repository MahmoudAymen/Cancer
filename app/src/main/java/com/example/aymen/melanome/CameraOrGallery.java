package com.example.aymen.melanome;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class CameraOrGallery extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    Button gallery,camera;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_or_gallery);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imageView=(ImageView)findViewById(R.id.imageView1);
        gallery= (Button) findViewById(R.id.gallery);
        camera= (Button) findViewById(R.id.camera);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zom);
        imageView.startAnimation(anim);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void OuvrirGallery()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == -1) && (requestCode == 1) && (data != null)&& (data.getData()!=null)) {
            Intent i = new Intent(getBaseContext(), Analyse.class);
            Uri uri = data.getData();
            imageView.setImageURI(uri);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            i.putExtra("ImageGallery", byteArrayOutputStream.toByteArray());
            i.putExtra("uri",uri.toString());
            i.putExtra("file",getFileExtension(uri));
            startActivity(i);

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.gallery:
                OuvrirGallery();
                break;
            case R.id.camera:
                Intent i1=new Intent(CameraOrGallery.this,Camera.class);
                startActivity(i1);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.aide:
                Intent i=new Intent(CameraOrGallery.this,Aide.class);
                startActivity(i);
                break;
            case R.id.aprop:
                Intent i1=new Intent(CameraOrGallery.this,Apropos.class);
                startActivity(i1);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
