package javaFinal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;
/**
 * @author jonah cathcart
 * 
 * the main class, this runs everything, and also turns itself into a jframe, i should have split this up into 2 seperate classes, since in the future, it will be hard to navigate.\
 * a lot of this is duct taped together, and should be reworked in the future.
 * 
 * the general gist:
 * there is an array called a steplist, this keeps references to all objects that want to be shown on screen and have logic, to delete an object, you only have to delete its reference.
 * all objects are drawn in the steplist order, and there is a stepListLength variable to stop the game from trying to go through 20,000 indexes each frame, if it hits a null index, it isnt much of a problem however.
 * 
 */
public class GameGame extends JFrame implements ActionListener {
	double prevTime = 0;
	int fpsInProgress = 0;
	double timeUntilNextFPS = 0;
	double x;
	double y;
	double jumpVel = 0;
	private Timer timer;
	private int wait;
	Graphics offGraphics;
	Image offImage;
	int xDimension = 800;
	int yDimension = 600;
	static KeyStep keyListen; //all of these statics can and should be accessed by objects in order for the game to work correctly
	static GameObject stepList[] = new GameObject[20000]; // use this like depth yeah?
	static int stepListLength = 0;
	static GameGame mainGame; 
	static int camX = 0;
	static int camY = 0;
	boolean debug = false;
	boolean speedTest = true;
	int FPS; // usually finals are all caps, but FPS is usually like this
	int timeInFrames;
	static int BPM = 300;
	static int timeBetweenArrows = 60 / (BPM / 60); //
	static Instant lastTime = Instant.now();

	// 60/(bpm/60);
	
	


	/**
	 * main method, if the bpm is specified as a launch argument, here it will take it
	 * @param args launch arguments
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
		BPM = Integer.parseInt(args[0]);
		timeBetweenArrows = 60 / (BPM / 60);
		}

		new GameGame();

	}
	/**
	 * here is where jframe is initialized and where the frame timer is set.
	 */
	public GameGame() {
		super("test game"); // this is the game title also this has to be first
		mainGame = this;
		

		// setIgnoreRepaint(true);
		x = 0.0;
		wait = 10;

		keyListen = new KeyStep(this);

		setSize(xDimension, yDimension); // this is the window size

		setVisible(true); // this makes the window show
		
		// these lines is wizardry and i do not expect to know how it works yet
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		new Player();
		timer = new Timer(16, this); // this is what requires the need to implement actionlistener
		timer.start();


	}
	/**
	 * the step method runs through the stepList and runs the step method of each GameObject
	 */
	public void step() { 
		if (keyListen.getKey(KeyEvent.VK_RIGHT)) {
			camX++;
		}
		if (keyListen.getKey(KeyEvent.VK_LEFT)) {
			camX--;
		}
		timeBetweenArrows = 60 / (BPM / 60);
		if(debug) {
			for(int i = 0; i < stepList.length;i++) {	
				if(stepList[i] != null) {
					System.out.print(i + " " +stepList[i].getName() + " ");
				}
			}
			System.out.println();
		}
			
//			camX-=5;
		if (stepListLength != 0) {
			for (int i = 0; i < stepListLength; i++) {
				if (stepList[i] != null) {
					stepList[i].step();
				}
			}
		}
		
	}

	/**
	 * same thing as step, but the camera position is subtracted for normal draw, and not for gui draw.
	 * @param g this is the graphics that the jframe paint method is given, whatever you draw onto it appears on the screen.
	 */
	public void paint(Graphics g) { // the graphics object originates here, cant make your own, also this is called
		
		// automagically :)
		GameGame main = GameGame.mainGame;
		offImage = createImage(xDimension, yDimension);
		offGraphics = offImage.getGraphics();
		BufferedImage imageLayer = new BufferedImage(main.xDimension, main.yDimension, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) offGraphics; // draw everything onto a seperate canvas
		g2d.setColor(new Color(0, 0, 0, Util.clamp(Player.combo/10, 0, 255)));
		// g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, xDimension, yDimension); // wipe the previous screen
	
		//g2d.setBackground();
		if (stepListLength != 0) {
			for (int i = 0; i < stepListLength; i++) {
				if(stepList[i] != null) {
				stepList[i].x-= GameGame.camX; stepList[i].y-= GameGame.camY; //camera movement
				// TODO dont draw things outside of the screen border
				stepList[i].paint(g2d, imageLayer);
				stepList[i].x+= GameGame.camX; stepList[i].y+= GameGame.camY;
				}
			}
		}
		// paintGUI does not take camera movement into account and also draws ontop of
		// paint elements
		if (stepListLength != 0) {
			for (int i = 0; i < stepListLength; i++) { //camera movement
				{
					if (stepList[i] != null) {

				// TODO dont draw things outside of the screen border
				stepList[i].paintGUI(g2d);
					}
				}
			}
		}
		offGraphics.drawImage(imageLayer, 0, 0, this);
		g.drawImage(offImage, (int) x, (int) y, this);
		// repaint(); // this line makes it paint many times :))) very good
		 // draw the seperate canvas onto the screen, removing flickering.
		
		// http://journals.ecs.soton.ac.uk/java/tutorial/ui/drawing/doubleBuffer.html
		// thanks
	}
	/**
	 * this is dangerous and also why i should have split this into 2 classes, since actionPerformed catches all actionEvents instead of just the timer like i would like it to.
	 * this is also what happens every frame, the code about 10 lines down keeps the game running a smooth ~60 fps.
	 */
	@Override
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 *      actionPerformed is from Timer, which i only barely use. I could go
	 *      without this, but it could break something to remove right now
	 * 
	 *      Timing is done at ~6 lines down from here instead of using the setTimer
	 *      method because its too imprecise.
	 */
	public void actionPerformed(ActionEvent arg0) { // this runs when timer is done

		// while (lastTime.plus((long) 0.160,
		// ChronoUnit.MILLIS).isBefore(Instant.now())) {

		// }
		// lastTime-Instant.now();
		// Instant.now().un
		// long howLong = lastTime.until(Instant.now(), ChronoUnit.MILLIS);
		// this finds out how long until the next frame should play
		long howLong = Instant.now().until(lastTime.plus((long) (1000.0f / 60.0f), ChronoUnit.MILLIS),
				ChronoUnit.MILLIS);
		//System.out.println(Instant.now());
		//System.out.println(lastTime);
		howLong = Math.max(howLong, 0); // makes sure its not negative
		//System.out.println(howLong);
		//System.out.println(lastTime.until(Instant.now(), ChronoUnit.MILLIS));
		try {
			//Thread.sleep(5000);
			Thread.sleep(howLong); // waits for however long
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lastTime = Instant.now();
		// System.out.println(Instant.now().compareTo(lastTime));
		// lastTime = Instant.now();
		// System.out.println(Instant.now().getNano());
		timeInFrames++;
		keyListen.frameCount();
		double i = arg0.getWhen() - prevTime;
		prevTime = arg0.getWhen();
		timeUntilNextFPS += i;

		fpsInProgress++;
		if (timeUntilNextFPS >= 1000) {
			System.out.println("fps: " + fpsInProgress);
			FPS = fpsInProgress;
			fpsInProgress = 0;
			timeUntilNextFPS = 0;

			
		}
		// calling the step and paint methods, which in turn call every other object's
		// step and paint methods
		step();
		repaint();

		 // this is roughly 60 fps
		if(speedTest) {
			timer.setDelay(0); // this used to read 16, but that would cause the game to run at 63 fps, and 17
								// would cause 57 fps.
		}

		

	}
	/**
	 * this is how you add an object to the steplist, typically this is done in the constructor of the object, with steps(this).
	 * @param l the object to add to the list
	 * @return the length of the steplist, just in case that information is needed
	 */
	public int steps(GameObject l) {
		stepList[stepListLength] = l;
		stepListLength++;
		return stepListLength;
	}
	/**
	 * this does not work due to the nature of the stepList right now, but this could allow the objects to choose the order they are created in, and thus change the draw and logic order.
	 * @param l object to add to stepList
	 * @param k position to place at
	 * @deprecated
	 */
	public void steps(GameObject l,int k) {
		if(stepList[k] != null) {
			System.out.println("created " + l.getName());
		}
		stepList[k] = l;
		stepListLength++;

	}
	/**
	 * same thing as the one before, but this changes the depth instead of initiating at the depth
	 * @param l object to change stepList index
	 * @param k index to change to
	 * @deprecated
	 */
	public void changeDepth(GameObject l, int k) {
		for(int i = 0;i != stepList.length;i++) {
			if(stepList[i].equals(l)) {
				stepList[i] = null;
				break;
			}
		}
		
	}

	/**
	 * i had an idea where i would make image "packs", but i see no advantage to
	 * that, so this sits here until i do see a reason to make an image pack
	 */
	public void importFiles() {
		BufferedImage test;
		File rootName = new File("./sprites.jpg");
		try {
			test = ImageIO.read(rootName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * File name = new File("./");
		 */
	}

	/**
	 * reset the stepList, no objects, completely empty
	 */
	public static void resetStep() {
		System.out.println("everything has been reset");
		stepList = new GameObject[20000];
		stepListLength = 0;

	}

	/**
	 * removes the reference to the specified object from the stepList
	 * 
	 * @param object to remove from steplist
	 */
	public static void kill(GameObject object) {
		// System.out.println(stepList[1244]);
		stepList[object.stepNum - 1] = null;
	}

}
