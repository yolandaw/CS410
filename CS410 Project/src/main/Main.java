package main;

import java.awt.Choice;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

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

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoHeadException 
	 */
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {

		//file chooser pop-up
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")).getParentFile());
		chooser.setDialogTitle("Please select your Git repository folder. ");
		chooser.setAcceptAllFileFilterUsed(false);
		int choice = chooser.showOpenDialog(null);
		if(choice != JFileChooser.APPROVE_OPTION){
			choice = JOptionPane.showOptionDialog(chooser, "No folder was selected. Try again?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			if(choice == 1){
				return;
			}
			else {
				main(args);
				return;
			}
		}
		File chooseFile = chooser.getSelectedFile();

		String filePath = chooseFile.getAbsolutePath();
		System.out.println("filePath: " + filePath);

		//look for git meta data folder
		File repoPath = new File(filePath);
		String gitPath = null;
		File[] mainDirFolders = null;

		if(repoPath.isDirectory()){
			if(repoPath.getPath().endsWith(".git")){
				gitPath = repoPath.getAbsolutePath();
			}
			else{
				mainDirFolders = repoPath.listFiles();
				for(int i=0;i<mainDirFolders.length;i++){
					System.out.println("files in mainDir: " + mainDirFolders[i].getPath());
					if(mainDirFolders[i].getPath().endsWith(".git")){
						gitPath = mainDirFolders[i].getAbsolutePath();

					}
				}
			}
		}        
		System.out.println("Git Path: " + gitPath);
		
		LogGatherer lg = new LogGatherer();
		Parser parser = new Parser();

		// pass the each class from the list of existing classes: in this case just passing the 'Class' class
		// repeat this using loop for each passed class:
		// 1.start gathering the log for the class
		lg.startGatheringLog(gitPath, "CS410 Project/src/main/Parser.java");

		// 2.pass the gathered log information for the class to be parsed
		parser.startParsingClass(lg);
		// pass the class Object(s) (there could be nested classes, so it is Object(s)) parsed in the Parse class to the visualization class using  getParsedLog() in the Parse class

		//quinn test
		//print out all the java file paths
		List<String> paths = lg.getJavaFilePaths(gitPath);
		
		for(int i=0; i<paths.size(); i++){
			System.out.println(paths.get(i));
		}
		//quinn test end
		
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