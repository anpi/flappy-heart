package hk.ust.flappyheart;

import java.util.concurrent.atomic.AtomicInteger;

import hk.ust.helper.AssetLoader;
import hk.ust.screen.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyHeart extends Game {
    SpriteBatch batch;
    Texture img;
    private AtomicInteger bpm;
    
    public FlappyHeart(AtomicInteger bpm) {
        this.bpm = bpm;
    }
    
    public int getBpm() {
        return bpm.get();
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
