package com.example.aymen.melanome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

public class Analyse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG="Analyse";
    ImageView imageView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Bitmap startImage;
    byte[]arrayOfByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        imageView = (ImageView) findViewById(R.id.imageView1);
        arrayOfByte = getIntent().getByteArrayExtra("ImageGallery");
        startImage = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
        imageView.setImageBitmap(startImage);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
