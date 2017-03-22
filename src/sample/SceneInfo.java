package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.List;

public class SceneInfo {
    private List<Integer> perm;
    private Point[] screenMap;
    private int n;
    private String s = "";

    private double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();            // Window Width
    private double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();          // Window Height
    private double fieldHeight;
    private double fieldWidth;
    private Canvas canvas;

    public SceneInfo(Canvas canvas, Point[] map)
    {
        this.canvas = canvas;
        fieldHeight = canvas.getHeight() / height;
        fieldWidth = canvas.getWidth() / width;

        n = map.length;
        screenMap = transform(map);
    }

    void passToGraphics(List<Integer> perm, String s) {
        this.perm = perm;
        this.s = s;
    }

    public void paintComponent(GraphicsContext g) {
        g.clearRect(0, 0, (int) width, (int) height);
        g.setFont(Font.font("Console", FontWeight.BOLD, 16));

        g.strokeText(s, 10, 24);
        g.strokeLine((int) screenMap[perm.get(n - 1)].x,
                (int) screenMap[perm.get(n - 1)].y,
                (int) screenMap[perm.get(0)].x,
                (int) screenMap[perm.get(0)].y);

        for (int j = 0; j < n - 1; j++) {
            g.setFill(Color.BLACK);
            g.strokeLine((int) screenMap[perm.get(n - 1)].x,
                    (int) screenMap[perm.get(n - 1)].y,
                    (int) screenMap[perm.get(0)].x,
                    (int) screenMap[perm.get(0)].y);

            g.setFill(Color.RED);
            g.fillOval(screenMap[perm.get(j)].x - 2,
                    screenMap[perm.get(j)].y - 2, 4, 4);
        }

    }

    private Point[] transform(Point[] map) {
        Point[] screenPoints = new Point[map.length];
        double xMin = map[0].x;
        double xMax = map[0].x;
        double yMin = map[0].y;
        double yMax = map[0].y;

        for (int i = 1; i < map.length; i++) {
            if (map[i].x < xMin) xMin = map[i].x;
            if (map[i].x > xMax) xMax = map[i].x;
            if (map[i].y < yMin) yMin = map[i].y;
            if (map[i].y > yMax) yMax = map[i].y;
        }

        int xSpan = (int) (xMax - xMin);
        int xMargin = (int) xMin;
        int ySpan = (int) (yMax - yMin);
        int yMargin = (int) yMin;
        double xf = (width - 100) / xSpan;
        double yf = (height - 150) / ySpan;

        for (int i = 0; i < map.length; i++) {
            screenPoints[i] = new Point((map[i].x - xMargin) * xf + 50,
                    (map[i].y - yMargin) * yf + 50);
        }

        return screenPoints;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public double  getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}


