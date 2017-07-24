import java.util.Scanner;

import javax.swing.JPanel;

import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;


public class Test extends JPanel {
	
	public void paint(Graphics g){
		Graphics2D solarSystem = (Graphics2D)g;
		solarSystem.setPaint(Color.BLACK); 
		Shape drawLine = new Line2D.Float(20, 90, 55, 250);
		Shape drawOval = new Ellipse2D.Float(50, 50, 55, 50);
		solarSystem.draw(drawLine);
		solarSystem.draw(drawOval);
	}
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        paint(g);
    }
	public void Input() {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter the object's x position:");
		String xPosition = scanner.nextLine();
		
		System.out.println("Enter the object's y position:");
		String yPosition = scanner.nextLine();
		
		System.out.println("Enter the object's x vector:");
		String xVector = scanner.nextLine();
		
		System.out.println("Enter the object's y vector:");
		String yVector = scanner.nextLine();
		
		System.out.println("Enter the object's mass:");
		String mass = scanner.nextLine();
		
	}
}


