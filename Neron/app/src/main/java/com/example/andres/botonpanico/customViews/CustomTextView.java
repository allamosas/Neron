package com.example.andres.botonpanico.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Andres LLamosas on 03/05/2016.
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf"));
    }
}
