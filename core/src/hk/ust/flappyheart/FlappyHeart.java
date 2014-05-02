package hk.ust.flappyheart;

import hk.ust.helper.AssetLoader;
import hk.ust.screen.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badub.heartrate.monitor.HeartMonitor;

public class FlappyHeart extends Game {
    SpriteBatch batch;
    Texture img;
    private HeartMonitor monitor;

    public FlappyHeart(HeartMonitor monitor) {
        this.monitor = monitor;
    }

    public HeartMonitor getHeartMonitor() {
        return monitor;
    }

    @Override
    public void create() {
        AssetLoader.load();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}
