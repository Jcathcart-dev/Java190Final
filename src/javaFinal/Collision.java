package javaFinal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
/**
 * this is for the platformer i was going to make, very shortlived however.
 * @author jonah
 *
 */
public class Collision extends GameObject {
	public Collision() {
		super();
	}

	public Collision(int _x, int _y) {
		super(_x, _y);
	}
	public void create() {
		try {
			objectImage = ImageIO.read(new File("./sprites/Floor.png"));
		} catch (IOException e) {
			System.out.println("read of floor sprite failed, make Floor.png");
			e.printStackTrace();
		}

	}
	public void step() {
	}
	public String getName() {
		return "Collision";
	}
	public void paint(Graphics2D g2d) {

		g2d.setColor(Color.BLUE);
		g2d.drawRect(x, y, xsize, ysize);

	}

}
