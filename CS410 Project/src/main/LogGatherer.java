package main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class LogGatherer {

	public void openDirectory() throws IOException, NoHeadException, GitAPIException{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("C:/Users/Quinn/Documents/Homework/CPSC 410/CS410/test/.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();

		System.out.println("Having repository: " + repository.getDirectory());

		showLog(repository);

		repository.close();
	}

	public void showLog(Repository repo) throws NoHeadException, GitAPIException, IOException{
		Repository repository = repo;

		Iterable<RevCommit> logs = new Git(repository).log()
				.all()
				.call();
		int count = 0;
		for(RevCommit rev : logs) {
			System.out.println("Commit: " + rev /*+ ", name: " + rev.getName() + ", id: " + rev.getId().getName()*/);
			count++;
		}
		System.out.println("Had " + count + " commits overall on current branch");

		logs = new Git(repository).log()
				// for all log.all()
				.addPath("README.md")
				.call();
		count = 0;
		for(RevCommit rev : logs) {
			System.out.println("Commit: " + rev /*+ ", name: " + rev.getName() + ", id: " + rev.getId().getName()*/);
			count++;
		}
		System.out.println("Had " + count + " commits on README.md");

		logs = new Git(repository).log()
				// for all log.all()
				.addPath("pom.xml")
				.call();
		count = 0;
		for(RevCommit rev : logs) {
			System.out.println("Commit: " + rev /*+ ", name: " + rev.getName() + ", id: " + rev.getId().getName()*/);
			count++;
		}
		System.out.println("Had " + count + " commits on pom.xml");

		//blame start
		Git git = new Git(repository);

		BlameCommand blamer = new BlameCommand(repository);
		blamer.setFilePath("CS410 Project/src/main/Main.java");
		BlameResult result = blamer.call();	//Why does this return null??
		result.computeAll();
		RawText rawCode = result.getResultContents();
		for(int i=0; i<rawCode.size(); i++){
			System.out.println(rawCode.getString(i) + " // author: " + result.getSourceAuthor(i).getName());	
		}

		//blame end

		repository.close();
	}
}

