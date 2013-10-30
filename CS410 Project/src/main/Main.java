package main;


import java.io.File;
import java.io.IOException;


import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.jsfml.graphics.*;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;


public class Main {
	
	private static String localRepoUrl ="C:/Documents and Settings/user/git/CS410/.git";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoHeadException 
	 */
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		
		Parser parser = new Parser();
//		parser.startParsingClass(localRepoUrl, "CS410 Project/src/main/Floor.java");
		
		
		
//		File currentDir = new File(System.getProperty("user.dir")).getParentFile();
//		String gitDir = currentDir + "/.git";
	
		
		//quinn test start
		//  String[] strArray = lg.rawCode();
		//	LogGatherer lg = new LogGatherer(gitDir, "CS410 Project/src/main/Main.java");

		
		LogGatherer lg = new LogGatherer(localRepoUrl, "CS410 Project/src/main/LogGatherer.java");
	
		
		for(int i=0; i<lg.numLinesOfCode(); i++){
			System.out.print(lg.getAuthor(i).getName());
		//	System.out.print(lg.getAuthor(i).getName() + ": " + lg.rawCode(i));
		}
		
		//quinn test end		

		RenderWindow window = new RenderWindow();
		
		VideoMode mode = new VideoMode(800, 600);
		
		window.create(mode, "Test");
		
		window.setFramerateLimit(60);
		
		Color backGroundColor = new Color (178,224,250);
		
		Floor floor1 = new Floor("foo");
		Floor floor2 = new Floor("bar");
		Floor floor3 = new Floor("boo");
		Floor floor4 = new Floor("abc");
		floor1.setFloorPosition(window.getSize().x/2, window.getSize().y); // should be set based on a buildings(class) position field?
		floor1.splitFloorOwnership();
		floor2.setFloorPosition(window.getSize().x/2, floor1.getFloorBoundaries().top-floor1.getFloorBoundaries().height);
		floor2.splitFloorOwnership();
		floor3.setFloorPosition(window.getSize().x/2, floor2.getFloorBoundaries().top-floor2.getFloorBoundaries().height);
		floor3.splitFloorOwnership();
		floor4.setFloorPosition(window.getSize().x/2, floor3.getFloorBoundaries().top-floor3.getFloorBoundaries().height);
		floor4.splitFloorOwnership();
		boolean RUNNING = true;

		while (RUNNING) {
			
			for (Event event : window.pollEvents()) {
				
				if (event.type == Event.Type.CLOSED) {
					RUNNING = false;
				}
				
			}

			window.clear(backGroundColor);
			window.draw(floor1);
			window.draw(floor2);
			window.draw(floor3);
			window.draw(floor4);
			window.display();
		}
		
		window.close();
	}

}
