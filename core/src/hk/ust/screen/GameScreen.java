package hk.ust.screen;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import hk.ust.world.GameRenderer;
import hk.ust.world.GameWorld;
import hk.ust.helper.InputHandler;

public class GameScreen implements Screen {
	public static final int GAME_WIDTH = 250;
	public static final int READY_WIDTH = 68;
	public static final int SCORE_BOARD_WIDTH = 97;

	private GameWorld world;
	private GameRenderer renderer;
	private float runTime;

	public GameScreen(AtomicInteger bpm) {
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		
		float gameWidth = GAME_WIDTH;

		float gameHeight = screenHeight / (screenWidth / gameWidth);
		int midPointY = (int) (gameHeight / 2);

		world = new GameWorld(midPointY, bpm);
		Gdx.input.setInputProcessor(new InputHandler(world, screenWidth / gameWidth, screenHeight / gameHeight));
		renderer = new GameRenderer(world, (int) gameHeight, midPointY);
		world.setRenderer(renderer);
	}

	@Override
	public void render(float delta) {
		runTime += delta;
		world.update(delta);
		renderer.render(delta, runTime);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
