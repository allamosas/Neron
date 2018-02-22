package com.example.andres.botonpanico.customViews;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.andres.botonpanico.R;

/**
 * Created by Andres LLamosas on 04/05/2016.
 */
public class CustomImageView extends ImageView{
    private static AnimationDrawable frameAnimation;

    public CustomImageView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        this.setBackgroundResource(R.drawable.spin_animation);
        frameAnimation = (AnimationDrawable) getBackground();
    }

    public void start(){
        frameAnimation.start();
        this.setVisibility(VISIBLE);
    }

    public void stop(){
        this.setVisibility(GONE);
        frameAnimation.stop();
    }
}
