package hk.ust.helper;

import hk.ust.screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

	public static Texture texture, logoTexture;
	public static TextureRegion logo, zbLogo, bg, grass, bird, birdDown,
			birdUp, skullUp, skullDown, bar, playButtonUp, playButtonDown,
			ready, gameOver, highScore, scoreboard, star, noStar, retry;
	public static Animation birdAnimation;
	public static Sound dead, flap, coin, fall;
	public static BitmapFont font, shadow, whiteFont;
	private static Preferences prefs;

	public static void load() {

		logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture, 0, 0, 512, 114);

		texture = new Texture(Gdx.files.internal("data/texture.png"));//modified texture.png
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		playButtonUp = new TextureRegion(texture, 0, 83, 29, 16);
		playButtonDown = new TextureRegion(texture, 29, 83, 29, 16);
		playButtonUp.flip(false, true);
		playButtonDown.flip(false, true);

		ready = new TextureRegion(texture, 59, 83, 34, 7); 
		ready.flip(false, true);

		retry = new TextureRegion(texture, 59, 110, 33, 7);
		retry.flip(false, true);
		
		gameOver = new TextureRegion(texture, 59, 92, 46, 7);
		gameOver.flip(false, true);

		scoreboard = new TextureRegion(texture, 111, 83, 97, 37);
		scoreboard.flip(false, true);

		star = new TextureRegion(texture, 152, 70, 10, 10);
		noStar = new TextureRegion(texture, 165, 70, 10, 10);

		star.flip(false, true);
		noStar.flip(false, true);

		highScore = new TextureRegion(texture, 59, 101, 48, 7);
		highScore.flip(false, true);

		zbLogo = new TextureRegion(texture, 0, 55, 135, 24);
		zbLogo.flip(false, true);

		bg = new TextureRegion(texture, 0, 0, 136, 43);
		bg.flip(false, true);

		grass = new TextureRegion(texture, 0, 43, 143, 11);
		grass.flip(false, true);

		birdDown = new TextureRegion(texture, 136, 0, 17, 12);
		birdDown.flip(false, true);	
//		Pixmap pixmap1 = fillHeart(20,20);
//		pixmap1.setColor( 0, 1, 0, 0.75f );
//		pixmap1.fillCircle(4,4,5);
//		pixmap1.fillCircle(13,4,5);
//		pixmap1.fillTriangle(9, 18, 0, 9, 18, 9);
//		birdDown=new TextureRegion(new Texture(pixmap1),0,0,20,20);
//		birdDown.flip(false, true);

		bird = new TextureRegion(texture, 153, 0, 17, 12);
		bird.flip(false, true);
//		Pixmap pixmap2 = fillHeart(18,18);
//		pixmap2.setColor( 0, 1, 0, 0.75f );
//		pixmap2.fillCircle(5,5,6);
//		pixmap2.fillCircle(15,5,6);
//		pixmap2.fillTriangle(10, 20, 0, 10, 20, 10);
//		pixmap.dispose();
//		bird = new TextureRegion(new Texture(pixmap2),0,0,20,20);
//		bird.flip(false, true);

		birdUp = new TextureRegion(texture, 170, 0, 17, 12);
		birdUp.flip(false, true);
//		Pixmap pixmap3 = fillHeart(20,20);
//		pixmap3.setColor( 0, 1, 0, 0.75f );
//		pixmap3.fillCircle(4,4,5);
//		pixmap3.fillCircle(13,4,5);
//		pixmap3.fillTriangle(9, 18, 0, 9, 18, 9);
//		birdUp=new TextureRegion(new Texture(pixmap3),0,0,20,20);
//		birdUp.flip(false, true);

		TextureRegion[] birds = { birdDown, bird, birdUp };
		birdAnimation = new Animation(0.06f, birds);//0.06f
		birdAnimation.setPlayMode(Animation.PlayMode.LOOP);

		skullUp = new TextureRegion(texture, 192, 0, 24, 14);
		// Create by flipping existing skullUp
		skullDown = new TextureRegion(skullUp);
		skullDown.flip(false, true);

		bar = new TextureRegion(texture, 136, 16, 22, 3);
		bar.flip(false, true);

		dead = Gdx.audio.newSound(Gdx.files.internal("data/dead.wav"));
		flap = Gdx.audio.newSound(Gdx.files.internal("data/flap.wav"));
		coin = Gdx.audio.newSound(Gdx.files.internal("data/coin.wav"));
		fall = Gdx.audio.newSound(Gdx.files.internal("data/fall.wav"));

		font = new BitmapFont(Gdx.files.internal("data/text.fnt"));
		font.setScale(.25f, -.25f);

		whiteFont = new BitmapFont(Gdx.files.internal("data/whitetext.fnt"));
		whiteFont.setScale(.1f, -.1f);

		shadow = new BitmapFont(Gdx.files.internal("data/shadow.fnt"));
		shadow.setScale(.25f, -.25f);

		// Create (or retrieve existing) preferences file
		prefs = Gdx.app.getPreferences("FlappyHeart");

		if (!prefs.contains("highScore")) {
			prefs.putInteger("highScore", 0);
		}
	}
	
	public static Pixmap fillHeart(int width,int height)
	{
		Pixmap pixmap=new Pixmap(width,height,Format.RGBA8888);
		int x=0,y=0;
		while(y<=height/2)
		{
			while(x<=width)
			{
				if((x-width/4)*(x-width/4)+(y-height/4)*(y-height/4)<=2*(width/4)*(width/4)||
						(x-width*3/4)*(x-width*3/4)+(y-height/4)*(y-height/4)<=2*(width/4)*(width/4))
				{
					pixmap.drawPixel(x, y);
					x++;
				}
			}
			y++;				
		}
		pixmap.fillTriangle(0, y, width, y, width/2, height);
		return pixmap;
	}

	public static void setHighScore(int val) {
		prefs.putInteger("highScore", val);
		prefs.flush();
	}

	public static int getHighScore() {
		return prefs.getInteger("highScore");
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		texture.dispose();

		// Dispose sounds
		dead.dispose();
		flap.dispose();
		coin.dispose();

		font.dispose();
		shadow.dispose();
	}

}