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
		
		CityModel city = new CityModel(window);
		city.setTowers(towers);
		CityController controller = new CityController(city);
		
		while (window.isOpen()) {
			controller.updateModel();
			city.drawCity();
			window.display();
		}
	}

}
