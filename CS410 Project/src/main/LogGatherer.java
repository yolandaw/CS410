package main;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class LogGatherer{

	private Repository repository;
	private BlameResult rawBlameResult;

	public LogGatherer(String localGitFolderPath, String filePath) throws GitAPIException, IOException{
		repository = openDirectory(localGitFolderPath);
		rawBlameResult = rawBlameResult(repository, filePath);
		closeRepository(repository);
	}

	private Repository openDirectory(String localGitFolderPath) throws IOException, NoHeadException, GitAPIException{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(localGitFolderPath))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		System.out.println("Opening directory: " + repository.getDirectory());
		return repository;
	}

	private void closeRepository(Repository repository){
		this.repository.close();
	}

	private BlameResult rawBlameResult(Repository repository, String filePath) throws GitAPIException, IOException{
		BlameCommand blamer = new BlameCommand(repository);
		blamer.setFilePath(filePath);
		BlameResult result = blamer.call();
		result.computeAll();
		return result;
	}

	public PersonIdent getAuthor(int codeLineNumber){
		return rawBlameResult.getSourceAuthor(codeLineNumber);	
	}


	public int getCommitTime(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getCommitTime();
	}

	public String getCommitMessage(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getFullMessage();
	}

	public String getCommitID(int codeLineNumber){
		return rawBlameResult.getSourceCommit(codeLineNumber).getName().substring(0, 7);	
	}


	public int numLinesOfCode(){
		return rawBlameResult.getResultContents().size();
	}

	public String[] rawCode() throws GitAPIException, IOException{
		RawText rawCode = rawBlameResult.getResultContents();
		String[] stringArray = new String[rawCode.size()];
		for(int i=0; i<rawCode.size(); i++){
			stringArray[i] = rawCode.getString(i);	
		}
		return stringArray;
	}

	public String rawCode(int codeLineNumber) throws GitAPIException, IOException{
		RawText rawCode = rawBlameResult.getResultContents();
		return rawCode.getString(codeLineNumber);	
	}

}

