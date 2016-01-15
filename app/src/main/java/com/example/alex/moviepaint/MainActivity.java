package com.example.alex.moviepaint;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

public class MainActivity extends Activity {


    PaintAreaView _canvas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        int paintColor = i.getIntExtra("color", Color.RED);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        _canvas = new PaintAreaView(this, paintColor);
        rootLayout.addView(_canvas, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 7));

        //menu strip
        LinearLayout menuStrip = new LinearLayout(this);

        //play button
        ImageView play = new ImageView(this);
        BitmapDrawable ob;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play);
        ob = new BitmapDrawable(this.getResources(), bitmap);
        play.setBackground(ob);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playbackIntent = new Intent(MainActivity.this, PlaybackActivity.class);
                startActivity(playbackIntent);
            }
        });

        menuStrip.addView(play, new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/3, ViewGroup.LayoutParams.MATCH_PARENT,5));

        //balnk view
        View blank = new View(this);
        menuStrip.addView(blank, new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/3, ViewGroup.LayoutParams.MATCH_PARENT, 6));
        //paint swatches
        PaintView p = new PaintView(this, paintColor, null);
        menuStrip.addView(p, new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/6, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        p.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent paletteIntent = new Intent(MainActivity.this, PaletteViewActivity.class);
                startActivity(paletteIntent);
                return true;
            }
        });

        rootLayout.addView(menuStrip, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _canvas.loadPaintArea(new File(getFilesDir(), "moviePaint.txt").getPath());
        _canvas._first = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _canvas.savePaintArea(new File(getFilesDir(), "moviePaint.txt").getPath());
    }
}
