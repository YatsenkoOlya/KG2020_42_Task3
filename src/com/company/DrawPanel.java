package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private ArrayList<Line> lines = new ArrayList<>();
    private ScreenConverter sc = new ScreenConverter(-2, 2, 4, 4, 800, 600);
    private Line yAxis = new Line(0, -1, 0, 1);
    private Line xAxis = new Line(-1, 0, 1, 0);

    private ScreenPoint prevDrag;
    private Line currentLine;
    private int x = 0, y = 0;
    private List<Triangle> triangles = new ArrayList<>();
    private Figure figure = new Figure();
    private boolean finish = true;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double coef = clicks > 0 ? 0.9 : 1.1;
        for(int i = 0; i < Math.abs(clicks); i++) {
            scale *= coef;
        }
        sc.setW(sc.getW() * scale);
        sc.setH(sc.getH() * scale);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ScreenPoint current = new ScreenPoint(e.getX(), e.getY());
        if (prevDrag != null) {
            ScreenPoint delta = new ScreenPoint(current.getX() - prevDrag.getX(), current.getY() - prevDrag.getY());
            RealPoint deltaReal = sc.s2r(delta);
            RealPoint zeroReal = sc.s2r(new ScreenPoint(0, 0));
            RealPoint vector = new RealPoint(deltaReal.getX() - zeroReal.getX(), deltaReal.getY() - zeroReal.getY());
            sc.setX(sc.getX() - vector.getX());
            sc.setY(sc.getY() - vector.getY());
            prevDrag = current;
        }
        if (currentLine != null) {
            currentLine.setP2(sc.s2r(current));
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        repaint();
    }

    private void drawTriangle(LineDrawer ld) {
        drawLine(ld, xAxis);
        drawLine(ld, yAxis);
        drawCompletedTriangles(ld);
        drawLastTriangle(ld);

        drawFigure(ld);
    }

    private void drawFigure(LineDrawer ld) {
        List<RealPoint> points = figure.getPoints();
        for (int i = 0; i < points.size() - 1; i++) {
            ScreenPoint sp = sc.r2s(points.get(i));
            ScreenPoint sp2 = sc.r2s(points.get(i + 1));
            ld.drawLine(sp, sp2, Color.RED);
        }
        if (points.size() != 0) {
            ScreenPoint sp1 = sc.r2s(points.get(0));
            ScreenPoint sp3 = sc.r2s(points.get(points.size() - 1));
            ld.drawLine(sp1, sp3, Color.RED);
        }
    }

    private int countPoints = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            x = e.getX(); y = e.getY();
            if (finish) {
                countPoints++;
                triangles.add(new Triangle());
                finish = false;
            } else {
                if (countPoints == 1) {
                    countPoints++;
                } else {
                    if (countPoints == 2) {
                        finish = true;
                        countPoints = 0;
                    }
                }
            }
            RealPoint p = sc.s2r(new ScreenPoint(x, y));
            triangles.get(triangles.size() - 1).addPoint(p);
            if (triangles.size() == 2 && (triangles.get(0).getList().size() == 3) && (triangles.get(1).getList().size() == 3)) {
                figure.setPoints(TriangleDrawer.getSortedPoints(triangles.get(0), triangles.get(1)));
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3)
            prevDrag = new ScreenPoint(e.getX(), e.getY());
        else if (e.getButton() == MouseEvent.BUTTON1) {
            currentLine = new Line(sc.s2r(new ScreenPoint(e.getX(), e.getY())), sc.s2r(new ScreenPoint(e.getX(), e.getY())));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3)
            prevDrag = null;
        else if(e.getButton() == MouseEvent.BUTTON1) {
            lines.add(currentLine);
            currentLine = null;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public DrawPanel() {
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        sc.setScreenW(getWidth());
        sc.setScreenH(getHeight());
        Graphics bi_g = bi.getGraphics();
        bi_g.setColor(Color.WHITE);
        bi_g.fillRect(0, 0, getWidth(), getHeight());
        bi_g.dispose();
        PixelDrawer pd = new BufferedImagePixelDrawer(bi);
        LineDrawer ld = new DDALineDrawer(pd);
        drawTriangle(ld);
        g.drawImage(bi, 0, 0, null);
        g.dispose();
    }

    private void drawLine(LineDrawer ld, Line l) {
        ld.drawLine(sc.r2s(l.getP1()), sc.r2s(l.getP2()), Color.BLACK);
    }

    private void drawCompletedTriangles(LineDrawer ld) {
        int lines = 0;
        int isComplete;
        for (Triangle t : triangles) {
            if (finish) {
                isComplete = 0;
            } else {
                isComplete = 1;
            }
            if (lines != triangles.size() - isComplete) {
                TriangleDrawer.drawFinal(sc, ld, t);
            }
            lines++;
        }
    }

    private void drawLastTriangle(LineDrawer ld) {
        if (triangles.size() > 0 && !finish) {
            Triangle t = triangles.get(triangles.size() - 1);
            TriangleDrawer.draw(sc, ld, t);
            List<RealPoint> points = t.getList();
            if (points.size() > 0) {
                RealPoint p = points.get(points.size() - 1);
                ScreenPoint sp = sc.r2s(p);
                ScreenPoint sp2 = new ScreenPoint(x, y);
                ld.drawLine(sp, sp2, Color.BLUE);
            }
        }
    }
}

