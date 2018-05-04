package com.example.aymen.melanome;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by aymen on 26/04/2018.
 */
public class Progressbar extends Dialog {
    private ImageView imageView ;

    public Progressbar(Context context, int paramInt) {
        super(context,R.style.TransparentProgressDialog);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.gravity = 1;
        getWindow().setAttributes(localLayoutParams);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout localLinearLayout = new LinearLayout(context);
        localLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(-1, -2);
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(paramInt);
        localLinearLayout.addView(this.imageView, localLayoutParams1);
        addContentView(localLinearLayout, localLayoutParams1);
    }
    public void show() {
        super.show();
        RotateAnimation localRotateAnimation = new RotateAnimation(0.0F, 360.0F, 1, 0.5F, 1, 0.5F);
        localRotateAnimation.setInterpolator(new LinearInterpolator());
        localRotateAnimation.setRepeatCount(-1);
        localRotateAnimation.setDuration(3000L);
        this.imageView.setAnimation(localRotateAnimation);
        this.imageView.startAnimation(localRotateAnimation);
    }
}
