package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TriangleDrawer {

    public static void draw(ScreenConverter sc, LineDrawer ld, Triangle t) {
        RealPoint prev = null;
        for (RealPoint p : t.getList()) {
            if (prev != null) {
                ScreenPoint p1 = sc.r2s(prev);
                ScreenPoint p2 = sc.r2s(p);
                ld.drawLine(p1, p2, Color.BLUE);
            }
            prev = p;
        }
    }

    public static void drawFinal(ScreenConverter sc, LineDrawer ld, Triangle t) {
        draw(sc, ld, t);
        ScreenPoint p1 = sc.r2s(t.getList().get(t.getList().size() - 1));
        ScreenPoint p2 = sc.r2s(t.getList().get(0));
        ld.drawLine(p1, p2, Color.BLUE);
    }

    public static List<RealPoint> getSortedPoints(Triangle t1, Triangle t2) {
        List<RealPoint> allPoints = new ArrayList<>();

        RealPoint a1 = t1.getList().get(0);
        RealPoint a2 = t1.getList().get(1);
        RealPoint a3 = t1.getList().get(2);
        RealPoint b1 = t2.getList().get(0);
        RealPoint b2 = t2.getList().get(1);
        RealPoint b3 = t2.getList().get(2);

        List<RealPoint> p1 = new ArrayList<>(Arrays.asList(a1, a2, a3));
        List<RealPoint> p2 = new ArrayList<>(Arrays.asList(b1, b2, b3));

        Line l1t1 = new Line(a1, a2);
        Line l2t1 = new Line(a1, a3);
        Line l3t1 = new Line(a2, a3);
        List<Line> linesT1 = new ArrayList<>(Arrays.asList(l1t1, l2t1, l3t1));

        Line l1t2 = new Line(b1, b2);
        Line l2t2 = new Line(b1, b3);
        Line l3t2 = new Line(b2, b3);
        List<Line> linesT2 = new ArrayList<>(Arrays.asList(l1t2, l2t2, l3t2));

        if (isBelong(t2, a1)) {
            allPoints.add(a1);
        }
        if (isBelong(t2, a2)) {
            allPoints.add(a2);
        }
        if (isBelong(t2, a3)) {
            allPoints.add(a3);
        }

        if (isBelong(t1, b1)) {
            allPoints.add(b1);
        }
        if (isBelong(t1, b2)) {
            allPoints.add(b2);
        }
        if (isBelong(t1, b3)) {
            allPoints.add(b3);
        }

        for (int i = 0; i < linesT1.size(); i++) {
            for (int j = 0; j < linesT2.size(); j++) {
                RealPoint crossingPoint = getCrossingPoint(linesT1.get(j), linesT2.get(i));
                if (crossingPoint != null) {
                    System.out.println("crossPoint " + crossingPoint.getX());
                }
                if (crossingPoint != null) {
                    allPoints.add(crossingPoint);
                }
            }
        }
        //return sort(allPoints);
        return sortPoints(allPoints);
    }

    private static RealPoint getCrossingPoint(Line l1, Line l2) { // точки пересечения сторон
        RealPoint crossPoint = null;

        RealPoint p1 = l1.getP1();
        RealPoint p2 = l1.getP2();
        RealPoint p3 = l2.getP1();
        RealPoint p4 = l2.getP2();

        double a1 = p2.getY() - p1.getY();
        double b1 = p1.getX() - p2.getX();
        double c1 = a1 * p1.getX() + b1 * (p1.getY());

        double a2 = p4.getY() - p3.getY();
        double b2 = p3.getX() - p4.getX();
        double c2 = a2 * p3.getX() + b2 * (p3.getY());

        double det = a1 * b2 - a2 * b1;

        if (det != 0) {
            double x = (b2 * c1 - b1 * c2) / det;
            double y = (a1 * c2 - a2 * c1) / det;

            if (Math.min(p1.getX(), p2.getX()) <= x && x <= Math.max(p1.getX(), p2.getX()) &&
                    Math.min(p3.getX(), p4.getX()) <= x && x <= Math.max(p3.getX(), p4.getX())) {
                crossPoint = new RealPoint(x, y);
            }
        }
        return crossPoint;
    }

    private static double square(RealPoint p1, RealPoint p2, RealPoint p3) { // площадь треугольников по Герону
        double l1 = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
        double l2 = Math.sqrt((p1.getX() - p3.getX()) * (p1.getX() - p3.getX()) + (p1.getY() - p3.getY()) * (p1.getY() - p3.getY()));
        double l3 = Math.sqrt((p2.getX() - p3.getX()) * (p2.getX() - p3.getX()) + (p2.getY() - p3.getY()) * (p2.getY() - p3.getY()));
        double p = (l1 + l2 + l3) / 2;
        double s = Math.sqrt(p * (p - l1) * (p - l2) * (p - l3));
        return s;
    }

    private static boolean isBelong(Triangle t, RealPoint p) { // если вершина лежит внутри другого треугольника
        RealPoint p1 = t.getList().get(0);
        RealPoint p2 = t.getList().get(1);
        RealPoint p3 = t.getList().get(2);

        double s1 = square(p1, p2, p);
        double s2 = square(p1, p3, p);
        double s3 = square(p2, p3, p);
        double s = square(p1, p2, p3);

        if ((s1 + s2 + s3) - s <= 0.0001) {
            return true;
        }
        return false;
    }

    private static List<RealPoint> sort(List<RealPoint> points) { // сортируем точки в том порядке, в котором будем соединять
        List<RealPoint> sortedPoints = new ArrayList<>();
        sortedPoints.add(points.get(0));
        points.remove(0);
        int i = 0;
        while (points.size() > 0) {
            RealPoint minP = findNear(sortedPoints.get(i), points);
            if (minP != null) {
                sortedPoints.add(minP);
                points.remove(minP);
                i++;
            }
        }
        return sortedPoints;
    }

    private static RealPoint findNear(RealPoint point, List<RealPoint> points) { // находим рядом лежащие точки
        double min = -1;
        RealPoint minP = null;
        for (RealPoint p : points) {
            double l = Math.sqrt((point.getX() - p.getX()) * (point.getX() - p.getX()) + (point.getY() - p.getY()) * (point.getY() - p.getY()));
            if (min == -1 || min > l || l > 0) {
                minP = p;
                min = l;
            }
        }
        return minP;
    }

    public static List<RealPoint> sortPoints(List<RealPoint> points) { // сортируем точки в том порядке, в котором будем соединять
                                                                                                             // (по часовой стрелке)
        float averageX = 0;
        float averageY = 0;

        for (RealPoint point : points) {
            averageX += point.getX();
            averageY += point.getY();
        }

        final float finalAverageX = averageX / points.size();
        final float finalAverageY = averageY / points.size();

        Comparator<RealPoint> comparator = new Comparator<RealPoint>() {
            public int compare(RealPoint lhs, RealPoint rhs) {
                double lhsAngle = Math.atan2(lhs.getY() - finalAverageY, lhs.getX() - finalAverageX);
                double rhsAngle = Math.atan2(rhs.getY() - finalAverageY, rhs.getX() - finalAverageX);

                if (lhsAngle < rhsAngle) return -1;
                if (lhsAngle > rhsAngle) return 1;

                return 0;
            }
        };
        points.sort(comparator);
        return points;
    }
}

