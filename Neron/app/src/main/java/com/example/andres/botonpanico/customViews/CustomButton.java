package com.example.andres.botonpanico.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Andres on 03/05/2016.
 */
public class CustomButton extends Button {
    public CustomButton(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf"));
    }
}
