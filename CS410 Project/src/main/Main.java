package main;


import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.jsfml.graphics.RenderWindow;
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
		/*
		Parser parser = new Parser();
		parser.startParsingClass(localRepoUrl, "CS410 Project/src/main/LogGatherer.java");
		*/
		
		//quinn test start
		LogGatherer lg = new LogGatherer(localRepoUrl, "CS410 Project/src/main/LogGatherer.java");
		String[] strArray = lg.rawCode();
		
		for(int i=0; i<lg.numLinesOfCode(); i++){
			System.out.print(lg.getAuthor(i) + ": " + lg.rawCode(i) + " time: " + lg.getCommitTime(i));
		}
		//quinn test end
		
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
	}

}
