package com.example.alex.moviepaint;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 10/3/2015.
 */
public class PolyLine
{
    int _color;

    private List<PointF> _points = new ArrayList<PointF>();

    public PolyLine(List<PointF> points, int color)
    {
        _color = color;
       _points = points;
    }

    public int getColor() {
        return _color;
    }

    public void setColor(int color) {
        _color = color;
    }

    public List<PointF> getPoints() {
        return _points;
    }

    public void setPoints(List<PointF> points) {
        _points = points;
    }
}
