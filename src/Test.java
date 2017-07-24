import java.util.Scanner;

public class Test {
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
