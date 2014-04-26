package hk.ust.flappyheart.desktop;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import hk.ust.flappyheart.FlappyHeart;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new FlappyHeart(new AtomicInteger()), config);
	}
}
