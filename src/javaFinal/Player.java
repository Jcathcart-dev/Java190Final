package javaFinal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends gameObject {
	BufferedImage sprite;
	double angle = 0;
	double direction = 0;
	double scale = 1;
	static int timeSinceAction = 0;
	static int combo;
	static Player me; // this is to allow for other objects to interact with player without foreaching
						// through the entire steplist
	private boolean changeBPM = false;

	public Player() {
		super();
		create();
	}

	public Player(int _x, int _y) {
		super(_x,_y);
		create();
	}

	public String getName() {
		return "Player";	
	}
	
	public void create() {
		me = this;
		xsize = 32;
		ysize = 32;
		y = main.yDimension / 2;
		x = main.xDimension / 2;

		File rootName = new File("./sprites/Player.png");
		try {
			sprite = ImageIO.read(rootName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new ComboCounter(550,50);
	}
	
	
	public void step() {

		if (keyListen.getKeyPressed(KeyEvent.VK_LEFT)) {
			direction = 270.0;
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_RIGHT)) {
			direction = 90.0;
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_DOWN)) {
			direction = 180.0;
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_UP)) {
				direction = 0.0;

		}
		if (keyListen.getKeyPressed(KeyEvent.VK_F1)) {

			new Overseer(20, 20);
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_F2)) {
			Editor.writeMapFile(gameGame.stepList);
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_F3)) {
			try {
				Editor.readMapFile(new File("./maps/map1.map"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(keyListen.getKeyPressed(KeyEvent.VK_F12)) {
			if(!changeBPM) {
				keyListen.resetKeyboardString();
				changeBPM = true;
			}
			else {
				main.BPM = Integer.parseInt(keyListen.keyboard_string);
				changeBPM = false;
			}
		}
		timeSinceAction++;
		if (keyListen.getKeyPressed(KeyEvent.VK_Z)) {
			// TODO add the actual game part
			timeSinceAction = 0;
			scale += 3;
		}
		if (keyListen.getKeyPressed(KeyEvent.VK_F5)) {
			gameGame.resetStep();
			new BeatMapMaker();
		}
		angle += util.betterAngle(direction, angle) * 30.0;
		angle %= 360;
		if (angle < 0.0) {
			angle += 360;
		}
		
		scale -= 0.1;
		scale = util.clamp(scale, 1.0, 1.5);
		if (main.timeInFrames % gameGame.timeBetweenArrows == 3) {
			int result = (int) (Math.random() * 11);
			if (result < 9) {
				new Arrow((int) (Math.random() * 4), 5.0);
			}
			if (result == 10) {
				new BounceArrow((int) (Math.random() * 4), 5.0);
			}
		}
	}

	public void paint(Graphics2D g2d, BufferedImage imageLayer) {
		//g2d.setColor(Color.BLUE);
		// source: https://stackoverflow.com/questions/8639567/java-rotating-images
		
		AffineTransform a = new AffineTransform(); // AffineTransform.getRotateInstance(angle, x + xsize / 2, y + ysize
													// / 2);
		a.translate(x + xsize / 2, y + ysize / 2);
		a.rotate(Math.toRadians(angle)); // S2: rotate around anchor
		a.translate(-(x + xsize / 2), -(y + ysize / 2));
		a.translate(x + xsize / 2.0, y + ysize / 4.0);
		a.scale(scale, scale);
		a.translate(-(x + xsize / 2), -(y + ysize / 4));


		// origin

		g2d.setTransform(a);
		g2d.drawImage(sprite, (int) x, (int) y, main);
		g2d.setTransform(new AffineTransform());
		// g2d.setColor(Color.black);
		// g2d.drawString(scale + "", 60, 60);


		// g2d.setTransform(null);
		// AffineTransform testy = new AffineTransform();
		//g2d.drawRect(x, y, xsize, ysize);

	}
	public void paintGUI(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.drawString(main.FPS+"", 60, 60);
		g2d.drawString(main.BPM + "", 30, 60);
	}
	public static void increaseCombo() {
		combo++;
		ComboCounter.scale = 2.5;
	}

}
