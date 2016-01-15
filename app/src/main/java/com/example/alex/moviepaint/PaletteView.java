package com.example.alex.moviepaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Alex on 9/20/2015.
 */
public class PaletteView extends ViewGroup
{

    private ArrayList<PaintView> _colors = new ArrayList<PaintView>();
    PaintView _selectedColor;

    public PaletteView(Context context)
    {
        super(context);

        resetPalette();
        setWillNotDraw(false);
    }

    private void addDeleter()
    {
        PaintView newPaint = new PaintView(this.getContext(), Color.BLACK, this);

        _colors.add(newPaint);
        newPaint.setAsDeleter();

        this.addView(newPaint);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        RectF paletteRect = new RectF();
        paletteRect.left = getPaddingLeft();
        paletteRect.top = getPaddingTop();
        paletteRect.right = getWidth() - getPaddingRight();
        paletteRect.bottom = getHeight() - getPaddingBottom();

        Paint palettePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        palettePaint.setColor(0xFFD1A319);
        canvas.drawOval(paletteRect, palettePaint);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        float childSize = Math.min(getWidth(),getHeight())/getChildCount() ;

        RectF layoutOvalRect = new RectF();
        layoutOvalRect.left = (float)getPaddingLeft() + childSize * 0.5f;
        layoutOvalRect.top = (float)getPaddingTop() + childSize * 0.5f;
        layoutOvalRect.right = (float) (getWidth() - getPaddingRight()) - childSize * 0.5f;
        layoutOvalRect.bottom = (float)(getHeight() - getPaddingBottom())  - childSize * 0.5f;

        for(int childIndex = 0; childIndex < getChildCount(); childIndex ++)
        {
            View childView = getChildAt(childIndex);

            float childTheta = (float) childIndex/ (float)getChildCount() * 2.0f * (float)Math.PI;
            PointF childCenter = new PointF();
            childCenter.x = layoutOvalRect.centerX() + layoutOvalRect.width() * 0.5f * (float) Math.cos(childTheta);
            childCenter.y = layoutOvalRect.centerY() + layoutOvalRect.height() * 0.5f * (float) Math.sin(childTheta);

            Rect childRect = new Rect();
            childRect.left = (int)(childCenter.x - childSize *0.5f);
            childRect.top = (int) (childCenter.y - childSize *0.5f);
            childRect.right = (int)(childCenter.x + childSize *0.5f);
            childRect.bottom = (int)(childCenter.y + childSize *0.5f);

            childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
        }
    }

    public PaintView addColor(int paintColor)
    {
        for(PaintView paint: _colors)
        {
            if(paint.getColor() == paintColor)
            {
                paint.Selected(true);
                return paint;
            }
        }
        PaintView newPaint = new PaintView(this.getContext(), paintColor, this);
        _colors.add(newPaint);

        this.addView(newPaint);
        invalidate();
        return newPaint;
    }

    public void removeColor(PaintView paint)
    {
        _colors.remove(paint);
        this.removeView(paint);
        if(_colors.size() == 1)
        {
            resetPalette();
        }
        invalidate();
    }

    private void resetPalette()
    {
        for(PaintView paint: _colors)
        {
            removeColor(paint);
        }
        addColor(Color.WHITE);
        addColor(Color.RED);
        addColor(Color.BLUE);
        addColor(Color.GREEN);
        addColor(Color.BLACK);
        addDeleter();
        invalidate();
    }

    public void setSelectedColor(PaintView selectedPaint)
   {
       boolean isMixing = false;
       if(_selectedColor != null)
       {
           if(_selectedColor.isMixing() && _selectedColor != selectedPaint && !selectedPaint.isDeleter() && !_selectedColor.isDeleter())
           {
               _selectedColor.setMixing(false);
               _selectedColor.Selected(false);
               PaintView p = addColor(mixPaints(_selectedColor.getColor(), selectedPaint.getColor()));
               setSelectedColor(p);
               isMixing = true;
           }
           else  if(_selectedColor.isMixing() && _selectedColor != selectedPaint && (selectedPaint.isDeleter() || _selectedColor.isDeleter()))
           {
               if(_selectedColor.isDeleter())
               {
                   _selectedColor.Selected(false);
                   _selectedColor.setMixing(false);
                   _selectedColor = null;
                   removeColor(selectedPaint);
               }
               else
               {
                   removeColor(_selectedColor);
                   _selectedColor.Selected(false);
                   _selectedColor.setMixing(false);
                   _selectedColor = null;
               }
           }
           else if(_selectedColor == selectedPaint)
           {
               if(_selectedColor.isMixing())
               {
                   _selectedColor.setMixing(false);
               }
               else
               {
                   _selectedColor.setMixing(true);
               }
               isMixing = true;
           }
           else
           {
               _selectedColor.Selected(false);
               _selectedColor.setMixing(false);
           }

       }
       if(!isMixing)
       {
           _selectedColor = selectedPaint;
           selectedPaint.Selected(true);
       }


   }

    public PaintView GetSelectedColor()
    {
        return _selectedColor;
    }

    private int mixPaints(int mix1, int mix2)
    {
        Integer Red = ((Color.red(mix1) + Color.red(mix2))/2);
        Integer Green = ((Color.green(mix1) + Color.green(mix2))/2);
        Integer Blue = ((Color.blue(mix1) + Color.blue(mix2))/2);
        return Color.rgb(Red, Green, Blue);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int desiredSize = Math.max((int)(160.0f * getResources().getDisplayMetrics().density), Math.max(getSuggestedMinimumWidth(), getSuggestedMinimumHeight()));

        int measuredWidth = width;
        int measuredHeight = height;
        if(widthMode == MeasureSpec.EXACTLY)
        {
            measuredHeight = width;
        }
        else if(heightMode == MeasureSpec.EXACTLY)
        {
            measuredWidth = height;
        }
        else if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST)
        {
            measuredWidth = Math.min(width, height);
            measuredWidth = Math.min(measuredWidth, desiredSize);
            measuredHeight = measuredWidth;
        }
        else if(widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED)
        {
            //160 pixels per inch
            measuredWidth = Math.max((int)(160.0f * getResources().getDisplayMetrics().density), Math.max(getSuggestedMinimumWidth(), getSuggestedMinimumHeight()));
            measuredHeight = measuredWidth;
        }
        else if(widthMode == MeasureSpec.UNSPECIFIED && heightMode != MeasureSpec.UNSPECIFIED)
        {
            measuredWidth = height;
            measuredHeight = height;
        }
        else if(widthMode != MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED)
        {
            measuredWidth = width;
            measuredHeight = width;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
