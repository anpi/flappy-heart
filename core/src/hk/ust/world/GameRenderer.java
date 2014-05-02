package hk.ust.world;

import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import hk.ust.model.Bird;
import hk.ust.model.Grass;
import hk.ust.model.Pipe;
import hk.ust.model.ScrollHandler;
import hk.ust.screen.GameScreen;
import hk.ust.tween.Value;
import hk.ust.tween.ValueAccessor;
import hk.ust.helper.AssetLoader;
import hk.ust.helper.InputHandler;
import hk.ust.ui.SimpleButton;

public class GameRenderer {
    private GameWorld myWorld;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch batcher;

    private int midPointY;

    // Game Objects
    private Bird bird;
    private ScrollHandler scroller;
    private Grass frontGrass, backGrass;
    private List<Pipe> pipes;

    // Game Assets
    private TextureRegion bg, grass, birdMid, skullUp, skullDown, bar, ready, logo, gameOver, highScore, scoreboard,
            star, noStar, retry;
    private Animation birdAnimation;

    // Tween stuff
    private TweenManager manager;
    private Value alpha = new Value();

    // Buttons
    private List<SimpleButton> menuButtons;
    private Color transitionColor;

    public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
        myWorld = world;

        this.midPointY = midPointY;
        this.menuButtons = ((InputHandler) Gdx.input.getInputProcessor()).getMenuButtons();

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameScreen.GAME_WIDTH, gameHeight);

        batcher = new SpriteBatch();
        batcher.setProjectionMatrix(cam.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        initGameObjects();
        initAssets();

        transitionColor = new Color();
        prepareTransition(255, 255, 255, .5f);
    }

    private void initGameObjects() {
        bird = myWorld.getBird();
        scroller = myWorld.getScroller();
        frontGrass = scroller.getFrontGrass();
        backGrass = scroller.getBackGrass();
        pipes = scroller.getPipes();
    }

    private void initAssets() {
        bg = AssetLoader.bg;
        grass = AssetLoader.grass;
        birdAnimation = AssetLoader.birdAnimation;
        birdMid = AssetLoader.bird;
        skullUp = AssetLoader.skullUp;
        skullDown = AssetLoader.skullDown;
        bar = AssetLoader.bar;
        ready = AssetLoader.ready;
        logo = AssetLoader.zbLogo;
        gameOver = AssetLoader.gameOver;
        highScore = AssetLoader.highScore;
        scoreboard = AssetLoader.scoreboard;
        retry = AssetLoader.retry;
        star = AssetLoader.star;
        noStar = AssetLoader.noStar;
    }

    private void drawGrass() {
        // Draw the grass
        batcher.draw(grass, frontGrass.getX(), frontGrass.getY(), frontGrass.getWidth(), frontGrass.getHeight());
        batcher.draw(grass, backGrass.getX(), backGrass.getY(), backGrass.getWidth(), backGrass.getHeight());
    }

    private void drawSkulls() {
        for (Pipe p : pipes) {
            batcher.draw(skullUp, p.getX() - 1, p.getY() + p.getHeight() - 14
                    + (Pipe.ORIGINAL_VERTICAL_GAP - p.VERTICAL_GAP) / 2, 24, 14);
            batcher.draw(skullDown, p.getX() - 1, p.getY() + p.getHeight() + p.VERTICAL_GAP
                    - (Pipe.ORIGINAL_VERTICAL_GAP - p.VERTICAL_GAP) / 2, 24, 14);
        }
    }

    private void drawPipes() {
        for (Pipe p : pipes) {
            batcher.draw(bar, p.getX(), p.getY(), p.getWidth(), p.getHeight()
                    + (Pipe.ORIGINAL_VERTICAL_GAP - p.VERTICAL_GAP) / 2);
            batcher.draw(
                    bar,
                    p.getX(),
                    p.getY() + p.getHeight() + p.VERTICAL_GAP - (Pipe.ORIGINAL_VERTICAL_GAP - p.VERTICAL_GAP) / 2,
                    p.getWidth(),
                    midPointY
                            + 66
                            - (p.getY() + p.getHeight() + p.VERTICAL_GAP - (Pipe.ORIGINAL_VERTICAL_GAP - p.VERTICAL_GAP) / 2));
        }
    }

    private void drawBirdCentered(float runTime) {
        batcher.draw(birdAnimation.getKeyFrame(runTime), 59, bird.getY() - 15, bird.getWidth() / 2.0f,
                bird.getHeight() / 2.0f, bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());
    }

    private void drawBird(float runTime) {
        if (bird.shouldntFlap()) {
            batcher.draw(birdMid, bird.getX(), bird.getY(), bird.getWidth() / 2.0f, bird.getHeight() / 2.0f,
                    bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());

        } else {
            batcher.draw(birdAnimation.getKeyFrame(runTime), bird.getX(), bird.getY(), bird.getWidth() / 2.0f,
                    bird.getHeight() / 2.0f, bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());
        }
    }

    private void drawMenuUI() {
        batcher.draw(logo, GameScreen.GAME_WIDTH / 2 - 56, midPointY - 50, logo.getRegionWidth() / 1.2f,
                logo.getRegionHeight() / 1.2f);

        for (SimpleButton button : menuButtons) {
            button.draw(batcher);
        }
    }

    private void drawScoreboard() {
        batcher.draw(scoreboard, getDrawX(97), midPointY - 30, 97, 37);
        batcher.draw(noStar, getDrawX(97) - 22 + 25, midPointY - 15, 10, 10);
        batcher.draw(noStar, getDrawX(97) - 22 + 37, midPointY - 15, 10, 10);
        batcher.draw(noStar, getDrawX(97) - 22 + 49, midPointY - 15, 10, 10);
        batcher.draw(noStar, getDrawX(97) - 22 + 61, midPointY - 15, 10, 10);
        batcher.draw(noStar, getDrawX(97) - 22 + 73, midPointY - 15, 10, 10);

        if (myWorld.getScore() > 2) {
            batcher.draw(star, getDrawX(97) - 22 + 73, midPointY - 15, 10, 10);
        }
        if (myWorld.getScore() > 17) {
            batcher.draw(star, getDrawX(97) - 22 + 61, midPointY - 15, 10, 10);
        }
        if (myWorld.getScore() > 50) {
            batcher.draw(star, getDrawX(97) - 22 + 49, midPointY - 15, 10, 10);
        }
        if (myWorld.getScore() > 80) {
            batcher.draw(star, getDrawX(97) - 22 + 37, midPointY - 15, 10, 10);
        }
        if (myWorld.getScore() > 120) {
            batcher.draw(star, getDrawX(97) - 22 + 25, midPointY - 15, 10, 10);
        }
        int length = ("" + myWorld.getScore()).length();

        AssetLoader.whiteFont.draw(batcher, "" + myWorld.getScore(), getDrawX(97) - 22 + 104 - (2 * length),
                midPointY - 20);

        int length2 = ("" + AssetLoader.getHighScore()).length();
        AssetLoader.whiteFont.draw(batcher, "" + AssetLoader.getHighScore(),
                getDrawX(97) - 22 + 104 - (2.5f * length2), midPointY - 3);
    }

    private void drawRetry() {
        batcher.draw(retry, getDrawX(66), midPointY + 10, 66, 14);
    }

    private void drawReady() {
        batcher.draw(ready, getDrawX(GameScreen.READY_WIDTH), midPointY - 50, GameScreen.READY_WIDTH, 14);
    }

    private void drawGameOver() {
        batcher.draw(gameOver, getDrawX(92), midPointY / 5, 92, 14);
    }

    private void drawScore() {
        int length = ("" + myWorld.getScore()).length();
        AssetLoader.shadow.draw(batcher, "" + myWorld.getScore(), GameScreen.GAME_WIDTH / 2 - (3 * length),
                midPointY / 6 + 1);
        AssetLoader.font
                .draw(batcher, "" + myWorld.getScore(), GameScreen.GAME_WIDTH / 2 - (3 * length), midPointY / 6);
    }

    private void drawBpm() {
        int length = ("" + myWorld.getBpm()).length();
        AssetLoader.shadow.draw(batcher, "" + myWorld.getBpm(), GameScreen.GAME_WIDTH / 6 - (3 * length),
                midPointY / 6 + 1);
        AssetLoader.redFont.draw(batcher, "" + myWorld.getBpm(), GameScreen.GAME_WIDTH / 6 - (3 * length),
                midPointY / 6);
    }

    private void drawHighScore() {
        batcher.draw(highScore, getDrawX(96), midPointY - 50, 96, 14);
    }

    public void render(float delta, float runTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeType.Filled);

        // Draw Background color
        shapeRenderer.setColor(200 / 255.0f, 200 / 255.0f, 200 / 255.0f, 1);
        shapeRenderer.rect(0, 0, GameScreen.GAME_WIDTH, midPointY + 66);

        // Draw Grass
        shapeRenderer.setColor(111 / 255.0f, 186 / 255.0f, 45 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 66, GameScreen.GAME_WIDTH, 11);

        // Draw Dirt
        shapeRenderer.setColor(147 / 255.0f, 80 / 255.0f, 27 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 77, GameScreen.GAME_WIDTH, 52);

        shapeRenderer.end();

        batcher.begin();
        batcher.disableBlending();

        batcher.draw(bg, 0, midPointY + 23, GameScreen.GAME_WIDTH, 43);

        drawPipes();

        batcher.enableBlending();
        drawSkulls();

        drawBpm();

        if (myWorld.isRunning()) {
            drawBird(runTime);
            drawScore();
        } else if (myWorld.isReady()) {
            drawBird(runTime);
            drawReady();
        } else if (myWorld.isMenu()) {
            drawBirdCentered(runTime);
            drawMenuUI();
        } else if (myWorld.isGameOver()) {
            drawScoreboard();
            drawBird(runTime);
            drawGameOver();
            drawRetry();
        } else if (myWorld.isHighScore()) {
            drawScoreboard();
            drawBird(runTime);
            drawHighScore();
            drawRetry();
        }

        drawGrass();

        batcher.end();
        drawTransition(delta);
    }

    public void prepareTransition(int r, int g, int b, float duration) {
        transitionColor.set(r / 255.0f, g / 255.0f, b / 255.0f, 1);
        alpha.setValue(1);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        Tween.to(alpha, -1, duration).target(0).ease(TweenEquations.easeOutQuad).start(manager);
    }

    private void drawTransition(float delta) {
        if (alpha.getValue() > 0) {
            manager.update(delta);
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(transitionColor.r, transitionColor.g, transitionColor.b, alpha.getValue());
            shapeRenderer.rect(0, 0, GameScreen.GAME_WIDTH, 300);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL30.GL_BLEND);

        }
    }

    private int getDrawX(int itemWidth) {
        return (GameScreen.GAME_WIDTH - itemWidth) / 2;
    }

}
