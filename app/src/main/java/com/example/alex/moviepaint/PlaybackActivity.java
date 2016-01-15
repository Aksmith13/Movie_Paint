package com.example.alex.moviepaint;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlaybackActivity extends Activity {

    PaintAreaView _canvas = null;
    List<PolyLine> _lines = null;
    int _canvasPointCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        final ValueAnimator animator = new ValueAnimator();

        _canvas = new PaintAreaView(this, 0);
        rootLayout.addView(_canvas, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 7));

        //seek bar
        final SeekBar bar = new SeekBar(this);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    animator.setIntValues(progress, _canvas.getTotalPointCount()-1);
                    animator.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rootLayout.addView(bar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,3));

        //menu strip
        LinearLayout menuStrip = new LinearLayout(this);

        //play button
        ImageView play = new ImageView(this);
        BitmapDrawable ob;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play);
        ob = new BitmapDrawable(this.getResources(), bitmap);
        play.setBackground(ob);



        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final List<PolyLine> lines = _lines;
                final int canvasPointCount = _canvasPointCount;

                if(event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    if(animator.isPaused()&&lines.size() !=0 && canvasPointCount != 0)
                    {
                        animator.resume();
                    }
                    else
                    {
                        if(event.getActionMasked() == MotionEvent.ACTION_UP) {
                            animator.setIntValues(0, canvasPointCount);
                            //10 seconds
                            animator.setDuration(10000);
                            bar.setMax(canvasPointCount);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation)
                                {
                                    int pointIndex = (Integer) animation.getAnimatedValue();

                                    bar.setProgress(pointIndex);

                                    List<PolyLine> tempLines = new ArrayList<PolyLine>();
                                    int segment = pointIndex;
                                    for(int i=0;i<lines.size(); i++)
                                    {
                                        int length =lines.get(i).getPoints().size();
                                        if(length <= segment)
                                        {
                                            segment -= length;
                                            tempLines.add(lines.get(i));
                                        }
                                        else
                                        {
                                            int color = lines.get(i).getColor();
                                            List<PointF> tempPoints = lines.get(i).getPoints();
                                            tempPoints = tempPoints.subList(0, segment);
                                            PolyLine tempLine = new PolyLine(tempPoints, color);
                                            tempLines.add(tempLine);
                                            break;
                                        }
                                    }
                                    _canvas.setLines(tempLines);
                                }
                            });
                            animator.start();
                        }
                    }

                }
                return true;
            }
        });
        menuStrip.addView(play, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,5));

        //pause
        ImageView pause = new ImageView(this);
        BitmapDrawable ob2;
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        ob2 = new BitmapDrawable(this.getResources(), bitmap2);
        pause.setBackground(ob2);
        pause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                animator.pause();
                return true;
            }
        });
        menuStrip.addView(pause, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 5));

        //clear
        ImageView clear = new ImageView(this);
        BitmapDrawable ob3;
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        ob3 = new BitmapDrawable(this.getResources(), bitmap3);
        clear.setBackground(ob3);
        clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    File f = new File(getFilesDir(), "moviePaint.txt");
                    f.delete();
                    _canvas.clearPaint();
                    _lines = _canvas.getLines();
                    _canvasPointCount = 0;
                }
                    return true;
                }
            }

            );
            menuStrip.addView(clear,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,5));

            rootLayout.addView(menuStrip,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1));
        }

        @Override
    protected void onResume()
    {
        super.onResume();
        _canvas.loadPaintArea(new File(getFilesDir(), "moviePaint.txt").getPath());
        _lines = _canvas.getLines();
        _canvasPointCount = _canvas.getTotalPointCount();
        _canvas._first = true;
    }
}
