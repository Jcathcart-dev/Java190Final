package javaFinal;
//oversees everything
import java.awt.Color;
import java.awt.Graphics2D;

public class Overseer extends gameObject {
	boolean console;
	public Overseer() {
		super();
	}
	public Overseer(int hruhx, int hruhy) {
		super(hruhx,hruhy);
	}

	public void create() {

	}
	

	public void step() {
		if (keyListen.getKeyPressed(192)) {
			console = util.toggle(console);
			keyListen.resetKeyboardString(); // dont even need to reset on true, will work either way
			System.out.println("console is " + (console ? "on" : "off")); // ternary operator, super advanced cool
																			// stuff, absolutely radical
		}
	}
	public String getName() {
		return "gameObject";	
	}
	public void paint(Graphics2D g2d) {

		g2d.setColor(Color.BLUE);
		g2d.drawRect(x, y, xsize, ysize);

	}

	public void paintGUI(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.drawString(main.FPS + "", 20, 60);
		if (console) {
			g2d.drawString(keyListen.keyboardString(), 20, 90); // be careful, this draws from some weird coordinates :/

		}
	}

}
