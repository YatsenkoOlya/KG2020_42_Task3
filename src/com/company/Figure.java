package com.company;

import java.util.ArrayList;
import java.util.List;

public class Figure {
    List<RealPoint> points = new ArrayList<>();

    public Figure() {
    }

    public void setPoints(List<RealPoint> points) {
        this.points = points;
    }

    public List<RealPoint> getPoints() {
        return points;
    }

}
