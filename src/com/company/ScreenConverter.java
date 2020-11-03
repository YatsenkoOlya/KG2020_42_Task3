package com.company;

public class ScreenConverter {
    private double x, y, w, h;
    private int screenW, screenH;

    public ScreenConverter(double x, double y, double w, double h, int screenW, int screenH) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.screenW = screenW;
        this.screenH = screenH;
    }

    public ScreenPoint r2s(RealPoint p) {
        int px = (int)((p.getX() - x) * screenW / w);
        int py = (int)((y - p.getY()) * screenH / h);
        return new ScreenPoint(px, py);
    }

    public RealPoint s2r(ScreenPoint p) {
        double px = p.getX() * w / screenW + x;
        double py = y - p.getY() * h / screenH;
        return new RealPoint(px, py);
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setW(double w) {
        this.w = w;
    }
    public void setH(double h) {
        this.h = h;
    }
    public void setScreenW(int screenW) {
        this.screenW = screenW;
    }
    public void setScreenH(int screenH) {
        this.screenH = screenH;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getW() {
        return w;
    }
    public double getH() {
        return h;
    }
    public int getScreenW() {
        return screenW;
    }
    public int getScreenH() {
        return screenH;
    }
}