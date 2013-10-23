package main;


import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RenderWindow window = new RenderWindow();
		
		VideoMode mode = new VideoMode(800, 600);
		
		window.create(mode, "Test");
		
		window.setFramerateLimit(60);
		
		boolean RUNNING = true;
		
		while (RUNNING) {
			
			for (Event event : window.pollEvents()) {
				
				if (event.type == Event.Type.CLOSED) {
					RUNNING = false;
				}
			}
			
			window.clear();
			window.display();
		}
		
		window.close();
		
		//test
		System.out.println("this is a test message");
	}

}
