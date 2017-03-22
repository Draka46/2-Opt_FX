package sample;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;

public class Controller {

    public Canvas canvas_TSP;

    public void startGame(ActionEvent event) {
        TSP tsp = new TSP(canvas_TSP);
        Thread tspThread = new Thread(tsp);
        tspThread.start();
    }
}
