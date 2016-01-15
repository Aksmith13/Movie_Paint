package com.example.alex.moviepaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 9/20/2015.
 */
public class PaintAreaView extends View
{

    private List<PointF> _points = new ArrayList<>();
    private List<PolyLine> _lines = new ArrayList<PolyLine>();

    private Paint _paint;
    private int _paintColor = Color.RED;
    //private PaletteView _palette;

    public boolean _first = false;

    public PaintAreaView(Context context, int color)
    {
        super(context);
        _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _paint.setStyle(Paint.Style.STROKE);
        _paintColor = color;
        _paint.setColor(_paintColor);
        _paint.setStrokeWidth(5.0f * getResources().getDisplayMetrics().density);

    }

    public List<PolyLine> getLines() {
        return _lines;
    }

    public void setLines(List<PolyLine> lines) {
        _lines = lines;
        invalidate();
    }

    public void clearPaint()
    {
        _lines.clear();
        _points.clear();
        invalidate();
    }

    public int getTotalPointCount()
    {
        int count = 0;
        for(PolyLine l: _lines)
        {
            count += l.getPoints().size();
        }
        return count;
    }

    public List<PointF> getPoints() {
        return _points;
    }

    public void setPoints(ArrayList<PointF> points) {
        _points = points;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        PointF touchPoint = new PointF(event.getX(), event.getY());
        _points.add(touchPoint);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                PolyLine line = new PolyLine(new ArrayList<PointF>(_points), _paint.getColor());
                if(validLine(line))
                {
                    _lines.add(line);
                }
                _points.clear();
                break;
        }

        invalidate();

        return true;
    }

    private boolean validLine(PolyLine line)
    {
        for(PointF p: line.getPoints())
        {
            if(!(p.x == 0 && p.y ==0))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(_first)
        {
            convertLines();
        }
        for (PolyLine l: _lines)
        {
            Path path = new Path();

            for (int pointIndex = 0; pointIndex < l.getPoints().size(); pointIndex++)
            {
                PointF point = l.getPoints().get(pointIndex);
                if(pointIndex == 0)
                {
                    path.moveTo(point.x, point.y);
                }
                else
                {
                    path.lineTo(point.x, point.y);
                }
            }

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(l.getColor());
            paint.setStrokeWidth(5.0f * getResources().getDisplayMetrics().density);
            canvas.drawPath(path, paint);
        }

        if(!_points.isEmpty())
        {
            Path path = new Path();

            for (int pointIndex = 0; pointIndex < _points.size(); pointIndex++)
            {
                PointF point = _points.get(pointIndex);
                if(pointIndex == 0)
                {
                    path.moveTo(point.x, point.y);
                }
                else
                {
                    path.lineTo(point.x, point.y);
                }
            }

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(_paint.getColor());
            paint.setStrokeWidth(5.0f * getResources().getDisplayMetrics().density);
            canvas.drawPath(path, paint);
        }
    }

    public void savePaintArea(String path)
    {
        for(PolyLine l: _lines)
        {
            for(PointF p: l.getPoints())
            {
                p.x = p.x/this.getWidth();
                p.y = p.y/this.getHeight();
            }
        }

        Gson gson = new Gson();
        String jsonPaintPath = gson.toJson(_lines);

        //Write to File
        try
        {
            File drawingFile = new File(path);
            FileWriter fileWriter = new FileWriter(drawingFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonPaintPath);
            writer.close();
        }
        catch (Exception e)
        {
            Log.e("Persistance", "Error saving drawing file: " + e.getMessage());
        }

    }

    public void loadPaintArea(String path)
    {
        try
        {
            File drawingFile = new File(path);
            FileReader fileReader = new FileReader(drawingFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String polyLineJson = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<PolyLine>>(){}.getType();
            ArrayList<PolyLine> lines = gson.fromJson(polyLineJson, collectionType);

            _lines = lines;
            reader.close();
        }
        catch (Exception e)
        {
            if(e instanceof FileNotFoundException)
            {
                clearPaint();
            }
            Log.e("Persistence", "Couldn't read polyLine. Error: " + e.getMessage());
        }
    }

    public void convertLines()
    {
        for(PolyLine l: _lines)
        {
            for(PointF p: l.getPoints())
            {
                p.x = p.x*getWidth();
                p.y = p.y*this.getHeight();
            }
        }
        _first = false;
        invalidate();
    }
}
