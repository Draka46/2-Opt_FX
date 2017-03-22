package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class TSP extends Task {

    /****************** BEST RESULTS WITH CURRENT SETUP ***********/
    /*
    PRECISION MAP          OPTIMAL   PRECISION MAP           OPTIMAL
    (100.0%)  kroA100........21282   (100.0%)  XQF131............564
    (99.57%)  qa194...........9352   (97.75%)  XQG237...........1019
    (94.79%)  XQL662..........2513   (94.86%)  uy734...........79114
    (95.14%)  zi929..........95345   (94.86%)  lu980...........11340
    (93.67%)  rw1621.........26051   (93.74%)  mu1979..........86891
    (.....%)  ar9152........837475
    */

    /*
    MAIN FUNCTION. Input: *.tsp file and known optimum. Starting
    with two random solutions and corrects them using 2 opt moves.
    When local optimum is reached, permutations are disturbed by
    random moves and optimized again, saving solutions only if they
    got better. Function returns the length of best permutation to
    the screen as goes along and terminates if optimal solution is
    found.
    */

    Canvas canvas;

    public TSP(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    protected Object call() throws Exception {
        try {
            optimize("lu980.tsp", 11340); }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    public void optimize(String file, int optimum) throws IOException {

        Point[] map = readInstance(file);
        int[][] dist = mapToDist(map);                                     // Distance matrix
        int n = dist.length;                                               // Number of cities
        List<Integer> permA = randPerm(n);                                 // Permutation array of cities (Work-on)
        List<Integer> permB = randPerm(n);                                 // Permutation array of cities (pre-channges)
        float best = Integer.MAX_VALUE;                                    // Best tour length found

        GraphicsContext g = canvas.getGraphicsContext2D();
        SceneInfo sceneInfo = new SceneInfo(canvas, map);

        System.out.println("TSP " + file + " optimal " + optimum);

        while (best > optimum) {
            for (int i = 0; i < n * 6; i++) { opt2(permA, dist); draw(best, optimum, permA, g, sceneInfo); }
            int cost = sum(permA, dist);
            if (cost < best) {
                best = cost;
                System.out.println(s(best, optimum));
                permB = permA;
            } else {
                permA = permB;
                randomKick(permA);
            }
        }
    }

    private void draw(float best, int optimum, List<Integer> perm, GraphicsContext g, SceneInfo sceneInfo) {

        String s = "length=" + (int) (best) + " "
                + " optimum=" + optimum + " precision=" + s(best, optimum) + "%";
        sceneInfo.passToGraphics(perm, s);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sceneInfo.paintComponent(g);
            }
        });

        try { Thread.sleep(0); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void opt2(List<Integer> p, int[][] dist) {
        int n = dist.length;
        int x = dist[p.get(0)][p.get(1)];
        for (int i = 2; i < (n - 2); i++)
            if (x + dist[p.get(i)][p.get(i + 1)] >
                    dist[p.get(0)][p.get(i)] +
                            dist[p.get(1)][p.get(i + 1)]) {
                flip(1, i, p);
                break;
            }
        p.add(0, p.get(n - 1));
        p.remove(n);
    }

    /** Reverses the permutation list, between two values */
    private void flip(int a, int b, List<Integer> p) {
        List<Integer> q = new ArrayList<>();
        for (int i = a; i <= b; i++) q.add(0, p.get(i));
        for (int i = a; i <= b; i++) p.set(i, q.get(i - a));
    }

    private int sum(List<Integer> p, int[][] dist) {
        int n = dist.length;
        int sum = dist[p.get(n - 1)][p.get(0)];
        for (int i = 0; i < n - 1; i++)
            sum += dist[p.get(i)][p.get(i + 1)];
        return sum;
    }

    /** Compares the best tour length with the optimal */
    private String s(float bestSoFar, int opt) {
        float pct = (int)(10000 * (bestSoFar - opt) / opt);
        String p = Float.toString(100 - pct / 100);
        return (int) (bestSoFar) + " --> " + p + "%";
    }

    /** Disrupts the tour, by rearranging four random edges */
    private List<Integer> randomKick(List<Integer> p) {
        double s = p.size() / 4;
        int a = (int) (Math.random() * s);
        int b = (int) (s + Math.random() * s);
        int c = (int) (2 * s + Math.random() * s);
        int d = (int) (3 * s + Math.random() * s);
        flip(a, d, p);
        flip(b, c, p);
        flip(d, a, p);
        return p;
    }

    /** Generates a random permutation of cities list */
    private List<Integer> randPerm(int n) {
        List<Integer> p0 = new ArrayList<>();
        for (int i = 0; i < n; i++) p0.add(i);
        List<Integer> p = new ArrayList<>();

        // Creates the list, by moving random elements of "p0" into permutation list
        for (int i = 0; i < n; i++) {
            int v = (int) (Math.random() * (n - i));
            p.add(p0.get(v));
            p0.remove(v);
        }
        return p;
    }

    /** Creates a distance matrix, for every city */
    private int[][] mapToDist(Point[] map) {
        int n = map.length;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = dist(map[i], map[j]);
        return dist;
    }

    private int dist(Point a, Point b) {
        return (int) (Math.sqrt(
                ((a.x - b.x) * (a.x - b.x)) +
                        ((a.y - b.y) * (a.y - b.y))) + 0.5);
    }

    /** Translates ".tsp" file into an array of Point objects, containing  */
    private Point[] readInstance(String fileName) {
        Point[] map = new Point[0];
        try {
            Scanner input = new Scanner(new File(fileName));
            String str = input.nextLine();
            int n = Integer.parseInt(str);
            map = new Point[n];
            for (int i = 0; i < n; i++) {
                str = input.nextLine();
                String[] data;
                data = str.split(" ");
                double x = Double.parseDouble(data[1].trim());
                double y = Double.parseDouble(data[2].trim());
                map[i] = new Point(x, y);
            }
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return map;
    }
}