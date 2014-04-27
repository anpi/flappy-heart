package hk.ust.model;

import java.util.Random;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Pipe extends Scrollable {

	private Random r;

	private Rectangle skullUp, skullDown, barUp, barDown;

	public static final int ORIGINAL_VERTICAL_GAP=70;
	public static final int NARROWEST_VERTICAL_GAP=60;
	public float VERTICAL_GAP = ORIGINAL_VERTICAL_GAP; //static final 45
	public static final int SKULL_WIDTH = 24;
	public static final int SKULL_HEIGHT = 11;
	private float pipeAcceleration=0.2f;
	private float bmp;
	private boolean accelerateChange=false;
	private float groundY;
	

	private boolean isScored = false;

	// When Pipe's constructor is invoked, invoke the super (Scrollable)
	// constructor
	public Pipe(float x, float y, int width, int height, float scrollSpeed,
			float groundY) {
		super(x, y, width, height, scrollSpeed);
		// Initialize a Random object for Random number generation
		r = new Random();
		skullUp = new Rectangle();
		skullDown = new Rectangle();
		barUp = new Rectangle();
		barDown = new Rectangle();

		this.groundY = groundY;
	}

	@Override
	public void update(float delta) {
		// Call the update method in the superclass (Scrollable)
		super.update(delta);
		pipeAcceleration=caculateAcceleration(bmp);
		//Change the accelerate while reaching the biggest or the smallest gap
		if(VERTICAL_GAP<=NARROWEST_VERTICAL_GAP)
		{
			if(!accelerateChange)
			{
				accelerateChange=true;
			}
		}
		else if(VERTICAL_GAP>=ORIGINAL_VERTICAL_GAP)
		{
			if(accelerateChange)
			{
				accelerateChange=false;
			}
		}
		if(accelerateChange)
		{
			VERTICAL_GAP+=pipeAcceleration;
		}
		else
		{
			VERTICAL_GAP-=pipeAcceleration;
		}
		
		// The set() method allows you to set the top left corner's x, y
		// coordinates,
		// along with the width and height of the rectangle
		
		barUp.set(position.x, position.y, width, height+(ORIGINAL_VERTICAL_GAP-VERTICAL_GAP)/2);
		barDown.set(position.x, position.y + height + ORIGINAL_VERTICAL_GAP-(ORIGINAL_VERTICAL_GAP-VERTICAL_GAP)/2, width,
				groundY - (position.y + height + ORIGINAL_VERTICAL_GAP-(ORIGINAL_VERTICAL_GAP-VERTICAL_GAP)/2));

		// Our skull width is 24. The bar is only 22 pixels wide. So the skull
		// must be shifted by 1 pixel to the left (so that the skull is centered
		// with respect to its bar).

		// This shift is equivalent to: (SKULL_WIDTH - width) / 2
		skullUp.set(position.x - (SKULL_WIDTH - width) / 2, position.y + height
				- SKULL_HEIGHT, SKULL_WIDTH, SKULL_HEIGHT);
		skullDown.set(position.x - (SKULL_WIDTH - width) / 2, barDown.y,
				SKULL_WIDTH, SKULL_HEIGHT);

	}

	@Override
	public void reset(float newX) {
		// Call the reset method in the superclass (Scrollable)
		super.reset(newX);
		// Change the height to a random number
		height = r.nextInt(90) + 15;
		isScored = false;
	}

	public void onRestart(float x, float scrollSpeed) {
		velocity.x = scrollSpeed;
		reset(x);
	}

	public Rectangle getSkullUp() {
		return skullUp;
	}

	public Rectangle getSkullDown() {
		return skullDown;
	}

	public Rectangle getBarUp() {
		return barUp;
	}

	public Rectangle getBarDown() {
		return barDown;
	}

	public boolean collides(Bird bird) {
		if (position.x < bird.getX() + bird.getWidth()) {
			return (Intersector.overlaps(bird.getBoundingCircle(), barUp)
					|| Intersector.overlaps(bird.getBoundingCircle(), barDown)
					|| Intersector.overlaps(bird.getBoundingCircle(), skullUp) || Intersector
						.overlaps(bird.getBoundingCircle(), skullDown));
		}
		return false;
	}

	public boolean isScored() {
		return isScored;
	}

	public void setScored(boolean b) {
		isScored = b;
	}
	public float getBmp()
	{
		return bmp;
	}
	public void setBmp(float bmp)
	{
		this.bmp=bmp;
	}
	public float caculateAcceleration(float bmp)
	{
		return pipeAcceleration=bmp/100;
	}
	
}
