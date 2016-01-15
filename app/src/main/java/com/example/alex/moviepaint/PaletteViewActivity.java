package com.example.alex.moviepaint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by Alex on 10/4/2015.
 */
public class PaletteViewActivity extends Activity
{
    PaletteView _paletteView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        _paletteView = new PaletteView(this);
        LinearLayout.LayoutParams paletteLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 10);
        paletteLayout.gravity = Gravity.CENTER;
        rootLayout.addView(_paletteView);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent paletteIntent = new Intent(this, MainActivity.class);
        if(_paletteView.GetSelectedColor() != null)
        {
            paletteIntent.putExtra("color", _paletteView.GetSelectedColor().getColor());
        }
        startActivity(paletteIntent);
    }
}

