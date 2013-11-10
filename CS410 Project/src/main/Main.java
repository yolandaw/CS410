package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;
import org.jsfml.window.VideoMode;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseEvent;


public class Main {
	
	private static String localRepoUrl = "";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoHeadException 
	 */
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		
		File currentDir = new File(System.getProperty("user.dir")).getParentFile();
		localRepoUrl = currentDir + "/.git";
		
		LogGatherer lg = new LogGatherer();
		Parser parser = new Parser();
		
		// pass the each class from the list of existing classes: in this case just passing the 'Class' class
		// repeat this using loop for each passed class:
		// 1.start gathering the log for the class
		lg.startGatheringLog(localRepoUrl, "CS410 Project/src/main/Parser.java");
		// 2.pass the gathered log information for the class to be parsed
		parser.startParsingClass(lg);
		// pass the class Object(s) (there could be nested classes, so it is Object(s)) parsed in the Parse class to the visualization class using  getParsedLog() in the Parse class
		//...

		
	/*	
		File currentDir = new File(System.getProperty("user.dir")).getParentFile();
		String gitDir = currentDir + "/.git";
		//quinn test start
		//LogGatherer lg = new LogGatherer(localRepoUrl, "CS410 Project/src/main/LogGatherer.java");
		LogGatherer lg = new LogGatherer(gitDir, "CS410 Project/src/main/Main.java");
		String[] strArray = lg.rawCode();
		
		//for(int i=0; i<lg.numLinesOfCode(); i++){
		//	System.out.print(lg.getAuthor(i) + ": " + lg.rawCode(i) + " time: " + lg.getCommitTime(i));
		//}
		//quinn test end
*/
		LinkedList<Tower> towers = parser.getParsedLog();
		RenderWindow window = new RenderWindow();
		
		VideoMode mode = new VideoMode(800, 600);
		
		window.create(mode, "Test");
		
		window.setFramerateLimit(60);
		
		Color backGroundColor = new Color (178,224,250);
		
		View changingView = new View(window.getDefaultView().getCenter(),window.getDefaultView().getSize());
		int centerX = window.getSize().x/2;
		int centerY = window.getSize().y/2;
		float newMouseX = 0;
		float newMouseY = 0;
		CityModel city = new CityModel(window);
		city.setTowers(towers);
		
		boolean RUNNING = true;
		boolean scrollMode = false;
		double scrollXVelocity = 0;
		double scrollYVelocity = 0;
		Vector2i clickedMousePos = new Vector2i(0,0);
		Floor foo = new Floor("foo");
		Author lenox = new Author("Lenox", "learn");
		Author asd = new Author("asd", "asd");
		foo.setFloorBoundaries(200, 600, 100, 300);
		foo.adjustOwnership(lenox, 5);
		foo.adjustOwnership(asd, 10);
		foo.updateNumOfLines();
		foo.setTexture(FileSystems.getDefault().getPath("resources","texture.png"),new IntRect(0, 0, 32, 24));
		foo.splitFloorOwnership();
		
		while (RUNNING) {
			
			for (Event event : window.pollEvents()) {
				
				if (event.type == Event.Type.CLOSED) {
					RUNNING = false;
				}
				
				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
					if (event.asMouseButtonEvent().button == Button.LEFT) {
						scrollMode = true;
						window.setMouseCursorVisible(false);
						clickedMousePos = Mouse.getPosition(window);
						Mouse.setPosition(new Vector2i(centerX, centerY), window);
					}
				}
				
				if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
					if (event.asMouseButtonEvent().button == Button.LEFT) {
						scrollMode = false;
						window.setMouseCursorVisible(true);
						Mouse.setPosition(clickedMousePos, window);
					}
				}
				
				if (event.type == Event.Type.MOUSE_MOVED && scrollMode) {
					MouseEvent mEvent = event.asMouseEvent();
					newMouseX =  mEvent.position.x;
					newMouseY =  mEvent.position.y;
					scrollXVelocity = newMouseX - centerX;
					scrollYVelocity = newMouseY - centerY;
				}
				
			}
			
			if (scrollXVelocity > 0) {
				scrollXVelocity = scrollXVelocity - (scrollXVelocity * 0.1);
			} else {
				if (scrollXVelocity < 0) {
					scrollXVelocity = scrollXVelocity + (scrollXVelocity * -0.1);
				}
			}
			
			if (scrollYVelocity > 0) {
				scrollYVelocity = scrollYVelocity - (scrollYVelocity * 0.1);
			} else {
				if (scrollYVelocity < 0) {
					scrollYVelocity = scrollYVelocity + (scrollYVelocity * -0.1);
				}
			}

			if (Mouse.isButtonPressed(Button.LEFT) && scrollMode) {
				Mouse.setPosition(new Vector2i(centerX, centerY), window);
			}
			
			changingView.move((float) scrollXVelocity, (float) scrollYVelocity);

			window.setView(changingView);

			window.clear(backGroundColor);
			window.draw(foo);
			city.drawCity();
			window.display();
		}
		
		window.close();
	}

}
