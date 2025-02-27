package minesweeper.model;

public class GameTimer extends Thread {
    private int elapsedTime;
    private boolean running;

    public GameTimer() {
        this.elapsedTime = 0;
        this.running = false;
    }

    public void startTimer() {
        running = true;
        this.start();
    }

    public void stopTimer() {
        running = false;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
                elapsedTime++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}