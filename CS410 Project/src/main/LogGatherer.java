package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class LogGatherer{

	private Repository repository;
	private BlameResult rawBlameResult;

	
	/**
	 * Basic constructor
	 */
	public LogGatherer() {}
	
	
	/**
	 * 
	 * @param localGitFolderPath the path to the local .git repository ex: "C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git"
	 * @param filePath the file path to the file we want to get the log for ex: "CS410 Project/src/main/Main.java"
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public void startGatheringLog(String localGitFolderPath, String filePath) throws NoHeadException, IOException, GitAPIException {
		repository = openDirectory(localGitFolderPath);
		rawBlameResult = rawBlameResult(repository, filePath);
		closeRepository(repository);
	}
	

	/**
	 * 
	 * @param localGitFolderPath the path to the local .git repository ex: "C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git"
	 * @return the repository file
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	private Repository openDirectory(String localGitFolderPath) throws IOException, NoHeadException, GitAPIException{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(localGitFolderPath))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		System.out.println("Opening directory: " + repository.getDirectory());
		return repository;
	}

	/**
	 * 
	 * @param repository the repository file to close
	 */
	private void closeRepository(Repository repository){
		this.repository.close();
	}

	/**
	 * 
	 * @param repository the repository file to get BlameResult for
	 * @param filePath the file path to the file (ex: "CS410 Project/src/main/Main.java") to get blame for
	 * @return a BlameResult object for the filePath file
	 * @throws GitAPIException
	 * @throws IOException
	 */
	private BlameResult rawBlameResult(Repository repository, String filePath) throws GitAPIException, IOException{
		BlameCommand blamer = new BlameCommand(repository);
		blamer.setFilePath(filePath);
		BlameResult result = blamer.call();
		result.computeAll();
		return result;
	}

	/**
	 * 
	 * @param codeLineNumber the line of code to get author for
	 * @return a PersonIdent object of the author of that line of code
	 */
	public PersonIdent getAuthor(int codeLineNumber){
		return rawBlameResult.getSourceAuthor(codeLineNumber);	
	}


	/**
	 * 
	 * @param codeLineNumber the line of code to get the commit time for
	 * @return number of seconds since the epoch when the line of code was committed
	 */
	public int getCommitTime(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getCommitTime();
	}

	/**
	 * 
	 * @param codeLineNumber
	 * @return the message that accompanied the commit
	 */
	public String getCommitMessage(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getFullMessage();
	}

	/**
	 * 
	 * @param codeLineNumber
	 * @return the commit id for the commit that last altered that line of code
	 */
	public String getCommitID(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getName().substring(0, 7);	//short hash?
		
	}

	/**
	 * 
	 * @return number of lines of code in the file being logged
	 */
	public int numLinesOfCode(){
		return rawBlameResult.getResultContents().size();
	}

	/**
	 * 
	 * @return a String array containing the lines of code of file being logged
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public String[] rawCode() throws GitAPIException, IOException{
		RawText rawCode = rawBlameResult.getResultContents();
		String[] stringArray = new String[rawCode.size()];
		for(int i=0; i<rawCode.size(); i++){
			stringArray[i] = rawCode.getString(i);	
		}
		return stringArray;
	}

	/**
	 * 
	 * @param codeLineNumber which line of code to return
	 * @return the line of code at that particular line
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public String rawCode(int codeLineNumber) throws GitAPIException, IOException{
		RawText rawCode = rawBlameResult.getResultContents();
		return rawCode.getString(codeLineNumber);	
	}
	
	/**
	 * 
	 * @param localGitFolder the path to the local .git repository ex: "C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git"
	 * @return a string array containing all paths in the git index
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public String[] getIndexedFilePaths(String localGitFolder) throws NoHeadException, IOException, GitAPIException{
		String[] entries;
		repository = openDirectory(localGitFolder);
		int indexEntryCount = DirCache.read(repository).getEntryCount();
		entries = new String[indexEntryCount];
		//System.out.println("index path entry count: " + indexEntryCount);
		for(int i=0; i<indexEntryCount; i++){
			//System.out.println("path " + i + ": " + DirCache.read(repository).getEntry(i).getPathString());
			entries[i] = DirCache.read(repository).getEntry(i).getPathString();
		}
		repository.close();
		return entries;
	}
	
	/**
	 * 
	 * @param localGitFolder the path to the local .git repository ex: "C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git"
	 * @return a String ArrayList of java file paths from the git index
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public List<String> getJavaFilePaths(String localGitFolder) throws NoHeadException, IOException, GitAPIException{
		List<String> paths = new ArrayList<String>();
		String[] entries = getIndexedFilePaths(localGitFolder);
		for(String entry:entries){
			if(entry.endsWith(".java")){
				paths.add(entry);
				System.out.println(entry);
			}
		}
		return paths;
	}
	
	/**
	 * 
	 * @param localGitFolder path to the local .git repository
	 * @param firstXNumberOfFiles the limit for max number of files to return
	 * @return a String ArrayList of the first X java file paths from the git index
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public List<String> getJavaFilePaths(String localGitFolder, int firstXFiles) throws NoHeadException, IOException, GitAPIException{
		List<String> paths = new ArrayList<String>();
		String[] entries = getIndexedFilePaths(localGitFolder, firstXFiles);
		for(String entry:entries){
			if(entry.endsWith(".java")){
				paths.add(entry);
				System.out.println(entry);
			}
		}
		return paths;
	}
	
	public String[] getIndexedFilePaths(String localGitFolder, int firstXFiles) throws NoHeadException, IOException, GitAPIException{
		String[] entries;
		repository = openDirectory(localGitFolder);
		int indexEntryCount = DirCache.read(repository).getEntryCount();
		if(firstXFiles<indexEntryCount){
			indexEntryCount = firstXFiles;
		}
		entries = new String[indexEntryCount];
		//System.out.println("index path entry count: " + indexEntryCount);
		for(int i=0; i<indexEntryCount; i++){
			//System.out.println("path " + i + ": " + DirCache.read(repository).getEntry(i).getPathString());
			entries[i] = DirCache.read(repository).getEntry(i).getPathString();
		}
		repository.close();
		return entries;
	}
}

