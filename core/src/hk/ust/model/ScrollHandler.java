package hk.ust.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hk.ust.screen.GameScreen;
import hk.ust.world.GameWorld;
import hk.ust.helper.AssetLoader;

public class ScrollHandler {

    private Grass frontGrass, backGrass;
    private ArrayList<Pipe> pipes;
    public static final int SCROLL_SPEED = -59;
    public static final int PIPE_GAP = 49;

    private GameWorld gameWorld;

    public ScrollHandler(GameWorld gameWorld, float yPos) {
        this.gameWorld = gameWorld;
        frontGrass = new Grass(0, yPos, GameScreen.GAME_WIDTH, 11, SCROLL_SPEED);
        backGrass = new Grass(frontGrass.getTailX(), yPos,
                GameScreen.GAME_WIDTH, 11, SCROLL_SPEED);

        pipes = new ArrayList<Pipe>();
        int pipeCount = GameScreen.GAME_WIDTH / PIPE_GAP;
        float lastPipeX = GameScreen.GAME_WIDTH + 5;
        for (int i = 0; i < pipeCount; i++) {
            if (i > 0) {
                lastPipeX = pipes.get(i - 1).getTailX();
            }
            pipes.add(new Pipe(lastPipeX + i * PIPE_GAP, 0, 22, 60,
                    SCROLL_SPEED, yPos));
        }
    }

    public void updateReady(float delta) {

        frontGrass.update(delta);
        backGrass.update(delta);

        // Same with grass
        if (frontGrass.isScrolledLeft()) {
            frontGrass.reset(backGrass.getTailX());

        } else if (backGrass.isScrolledLeft()) {
            backGrass.reset(frontGrass.getTailX());

        }

    }

    public void update(float delta) {
        // Update our objects
        frontGrass.update(delta);
        backGrass.update(delta);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.update(delta);
            if (pipe.isScrolledLeft()) {
                pipe.reset(pipes.get((i + pipes.size() - 1) % pipes.size()).getTailX()
                        + PIPE_GAP);
            }
        }

        // Same with grass
        if (frontGrass.isScrolledLeft()) {
            frontGrass.reset(backGrass.getTailX());

        } else if (backGrass.isScrolledLeft()) {
            backGrass.reset(frontGrass.getTailX());

        }
    }

    public void stop() {
        frontGrass.stop();
        backGrass.stop();
        for (Pipe p : pipes) {
            p.stop();
        }
    }

    public boolean collides(Bird bird) {
        for (Pipe p : pipes) {
            if (!p.isScored()
                    && p.getX() + (p.getWidth() / 2) < bird.getX()
                            + bird.getWidth()) {
                addScore(1);
                p.setScored(true);
                AssetLoader.coin.play();
            }
        }
        for (Pipe p : pipes) {
            if (p.collides(bird)) {
                return true;
            }
        }
        return false;
    }

    private void addScore(int increment) {
        gameWorld.addScore(increment);
    }

    public Grass getFrontGrass() {
        return frontGrass;
    }

    public Grass getBackGrass() {
        return backGrass;
    }

    public List<Pipe> getPipes() {
        return pipes;
    }

    public void onRestart() {
        frontGrass.onRestart(0, SCROLL_SPEED);
        backGrass.onRestart(frontGrass.getTailX(), SCROLL_SPEED);
        float x = GameScreen.GAME_WIDTH + 5;
        for (Pipe p : pipes) {
            p.onRestart(x, SCROLL_SPEED);
            x = p.getTailX() + PIPE_GAP;
        }
    }
}
