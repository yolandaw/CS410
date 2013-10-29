package main;


import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoHeadException 
	 */
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		//quinn test start
		LogGatherer lg = new LogGatherer("C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git", "CS410 Project/src/main/Main.java");
		String[] strArray = lg.rawCode();
		for(int i=0; i<lg.numLinesOfCode(); i++){
			System.out.print(lg.getAuthor(i).getName() + ": " + strArray[i]);
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
