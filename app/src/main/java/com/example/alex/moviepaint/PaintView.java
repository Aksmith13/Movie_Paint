package com.example.alex.moviepaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Alex on 9/20/2015.
 */
public class PaintView extends View
{
    private static final int SELECTEDCOLOR = 0xFF8AD3EB;

    private int _color;
    private boolean _selected = false;
    private boolean _mixing = false;
    private RectF _paintRect = new RectF();
    private PaletteView _paletteView;

    private boolean _deleter = false;

    public PaintView(Context context, int color, PaletteView palette)
    {
        super(context);
        _color = color;
        _paletteView = palette;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint blotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blotPaint.setColor(_color);

        _paintRect.left = getPaddingLeft();
        _paintRect.top = getPaddingTop();
        _paintRect.right = getWidth() - getPaddingRight();
        _paintRect.bottom = getHeight() - getPaddingBottom();

        if(_paintRect.width() < _paintRect.height())
        {
            _paintRect.bottom -= _paintRect.height() - _paintRect.width();
        }
        else
        {
            _paintRect.right -= _paintRect.width() - _paintRect.height();
        }

        if(_mixing)
        {
            setBackgroundColor(Color.RED);
        }
        else if(_selected)
        {

            setBackgroundColor(SELECTEDCOLOR);
        }
        else
        {
            setBackgroundColor(0x00000000);
        }
        if(_deleter)
        {
            blotPaint.setStyle(Paint.Style.STROKE);
            blotPaint.setStrokeWidth(5.0f * getResources().getDisplayMetrics().density);
            canvas.drawLine(0 + _paintRect.width() * 1 / 4, 0 + _paintRect.height() * 1 / 4, _paintRect.width() * 3 / 4, _paintRect.height() * 3 / 4, blotPaint);
            canvas.drawLine(0 +_paintRect.width()*1/4, _paintRect.height()*3/4 , _paintRect.width()*3/4, 0 + _paintRect.height()*1/4, blotPaint);
        }
        else
        {
            canvas.drawOval(_paintRect, blotPaint);
        }
    }

    public int getColor()
    {
        if(_deleter)
        {
            return 0x00000000;
        }
        else
        {
            return _color;
        }

    }


    public void Selected(boolean  isSelected)
    {
        _selected = isSelected;
        invalidate();
    }

    public boolean isMixing()
    {
        return _mixing;
    }

    public void setMixing(boolean mixing)
    {
        _mixing = mixing;
        invalidate();
    }

    public void setAsDeleter()
    {
        _deleter = true;
    }

    public boolean isDeleter()
    {
        return _deleter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(_paletteView == null)
        {
            //you're on the paint screen
        }
        else
        {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_UP:
                    _paletteView.setSelectedColor(this);
                    break;
            }
        }
        return true;
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
            //find the max of all 3 in width
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
