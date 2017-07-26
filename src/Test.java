import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
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
import java.awt.geom.Rectangle2D;


public class Test extends JPanel {
	
	ArrayList<OrbitalBody> listOfBodies = new ArrayList<>();
	ArrayList<Shape> rePaintList = new ArrayList<>();
	
	public void paint(Graphics g){
		setVisible(true);
		System.out.println("Paint");
		Graphics2D solarSystem = (Graphics2D)g;
		solarSystem.setPaint(Color.BLACK); 
		//Shape drawRectangle = new Rectangle2D.Float(500,500,900,900);
		//solarSystem.draw(drawRectangle);
		//solarSystem.fill(drawRectangle);
		Shape drawEllipse = new Ellipse2D.Float(500, 500, 50, 50);
		solarSystem.draw(drawEllipse);
		solarSystem.fill(drawEllipse);
		Shape drawLine;
		for(int i = 0; i < listOfBodies.size(); i++) {
			drawLine = new Line2D.Float(listOfBodies.get(i).getXOldPosition() + 500, listOfBodies.get(i).getYOldPosition() + 500, listOfBodies.get(i).posVect.getX() + 500, listOfBodies.get(i).getYPosition() + 500);		
			rePaintList.add(drawLine);
			for(int n = 0; n < rePaintList.size(); n++) {
				solarSystem.draw(rePaintList.get(n));
				setVisible(true);
			}
		}
	}
	
	public void passList(ArrayList<OrbitalBody> list) {
		listOfBodies = list;
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


