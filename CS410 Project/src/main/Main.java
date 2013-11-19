package main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.jsfml.graphics.*;
import org.jsfml.window.VideoMode;


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
		
		//quinn test
		//print out all the java file paths
		List<String> paths = lg.getJavaFilePaths(gitPath);
		
		for(int i=0; i<paths.size(); i++){
			System.out.println(paths.get(i));
		}
		//quinn test end

		// pass the each class from the list of existing classes: in this case just passing the 'Class' class
		// repeat this using loop for each passed class:
		// 1.start gathering the log for the class
		
		
		//hack to see all towers
		LinkedList<Tower> towers = new LinkedList<Tower>();
		
		for(String path:paths){
			System.out.println(path.toString());
			lg.startGatheringLog(gitPath, path);
			parser.startParsingClass(lg);
			
			// adds all the towers including nested towers
			towers.addAll(parser.getParsedLog()); 
		}
		
		for(Tower t:towers){
			System.out.println(t.getTowerName());
		}
		//end hack to see all towers

		// 2.pass the gathered log information for the class to be parsed
		//parser.startParsingClass(lg);
		// pass the class Object(s) (there could be nested classes, so it is Object(s)) parsed in the Parse class to the visualization class using  getParsedLog() in the Parse class
		
		RenderWindow window = new RenderWindow();

		VideoMode mode = new VideoMode(1280, 600); //can't change the height, breaks the ground

		window.create(mode, "City Builder");

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