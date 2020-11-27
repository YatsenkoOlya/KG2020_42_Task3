package com.company;
import java.util.*;

public class Triangle {
    private List<RealPoint> listPoints = new ArrayList<>();

    public Triangle() {
    }

    public List<RealPoint> getList() {
        return listPoints;
    }

    public void addPoint(RealPoint p) {
        listPoints.add(p);
    }
}
