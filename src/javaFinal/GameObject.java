package javaFinal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
/**
 * superclass for all objects, has a base of everything an object needs to have, in hindsight, i probably should have made this an interface instead of a superclass, since all GameObjects need all of these
 */
public class GameObject {
	GameGame main;
	KeyStep keyListen;
	int x;
	int y;
	int ysize;
	int xsize; // this is used for hitboxes
	boolean alive = false;
	BufferedImage objectImage;
	int stepNum;
/**
 * blank constructor, sets positions to arbitrary number, sets the sprite to a default image, sets up keylistener, and runs @see javaFinal.GameGame.steps(GameObject) in order to get them running correctly
 */
	public GameObject() {
		x = 200;
		y = 200;
		try {
			objectImage = ImageIO.read(new File("./sprites/default.png"));
		} catch (IOException e) {

			BufferedImage redX = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics redGraphics = redX.getGraphics();
			redGraphics.setColor(Color.red);

			redGraphics.drawLine(0, 0, 32, 32);
			redGraphics.drawLine(32, 0, 0, 32);

			objectImage = redX;
			// e.printStackTrace();
		}
		xsize = 20;
		keyListen = GameGame.keyListen;
		main = GameGame.mainGame;
		stepNum = main.steps(this); // place this object into the steps list, which is a list of every object in the
									// game
		alive = true;
		create();
		

	}

	/**
	 * identical to the parameterless constructor, but this places the object at a specified position
	 * @param _x x position to start object at
	 * @param _y y position to start object at
	 */
	public GameObject(int _x, int _y) {// this is to allow gameObjects to be initialized at a certain place, the create
										// method is mainly supposed to be overridden, allowing gameObjects to
										// initialize variables inside an "event" rather than in their constructors,
										// which they have 2 of
		x = _x;
		y = _y;
		ysize = 20;
		xsize = 20;
		keyListen = GameGame.keyListen;
		main = GameGame.mainGame;
		main.steps(this); //place this object into the steps list, which is a list of every object in the game
		alive = true;
		// question: if i run super on a child, what will happen on this next line? even
		// if the child class overrides create?
		// answer: it executes the child class create, niiice
		// create();
	}
	/**
	 * create step for anything that runs no matter the constructor used, this is used more in the children than in the superclass
	 */
	public void create() {
		System.out.println(
				"superclass executed create! you should override this by making a create method in your gameobject child");
	}
	/**
	 * this is to get a string version of the class name, which has actually been replaced by this.getClass().getName();, which works way better, not needing someone to update the name of the object every time
	 * @return the name of the object in a string
	 * @deprecated
	 */

	public String getName() {
		return "gameObject";	

	}
	
	public void step() {
		if (keyListen.getKey(KeyEvent.VK_LEFT)) {
			x--;
		}
		if (keyListen.getKey(KeyEvent.VK_RIGHT)) {
			x++;
		}
		if (keyListen.getKey(KeyEvent.VK_UP)) {
			y--;
		}
		if (keyListen.getKey(KeyEvent.VK_DOWN)) {
			y++;
		}
	}

	public void paint(Graphics2D g2d, BufferedImage imageLayer) { // it is expected that all objects with step will have
																	// paint

		// System.out.println(wait); // yeah, you only print once

		// casting graphics to graphics2d to use graphics2d

		
		//g2d.setColor(Color.BLACK);
		//g2d.drawRect(x, y, xsize, ysize);

		// x += 0.01;

	}
	public void paintGUI(Graphics2D g2d) {
		
		
	}
	
	public static boolean checkCollision(GameObject obj1, GameObject obj2) {// check if object2 and object1 collide at
		boolean xCol = false;																	// any point.
		boolean yCol = false;

		if (obj1.x > obj2.x && obj1.x < obj2.x + obj2.xsize) {xCol = true;} 
		if (obj1.x + obj1.xsize > obj2.x && obj1.x < obj2.x) {xCol = true;}

		if (obj1.y > obj2.y && obj1.y < obj2.y + obj2.ysize) {yCol = true;}
		if (obj1.y + obj1.ysize > obj2.y && obj1.y < obj2.y) {yCol = true;}

		if(xCol && yCol) {
			System.out.println(yCol + " " + xCol);

		return true;
		}
		return false;
	}

	public static boolean checkCollision(GameObject obj, int targx, int targy) { // check if you want a specific point
																					// if collisioned
		boolean xCol = false;																	
		boolean yCol = false;

		if (targx > obj.x && targx < obj.x + obj.xsize) {xCol = true;}
		if (targy > obj.y && targy < obj.y + obj.ysize) {yCol = true;}


		if(xCol && yCol) {
		return true;
		}
		return false;
	
	}	
}

	


