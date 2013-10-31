package main;

import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;


public class Parser {
	
	private LogGatherer parsedClass; //
	private int currentLineNum; //
	private LinkedList<String> trackingClass; 
	private LinkedList<Tower> createdClassObjects; //
	private LinkedList<Floor> createdMethodObjects; //
	private Floor currentMethod; //
	private Tower currentClass;
	private int blockCommentHandler; //
	private int methodHandler; //
	
	
	
	public Parser(){
		parsedClass = new LogGatherer();
	}
	
	public void startParsingClass(String localRepoUrl, String parsingClass) throws IOException, GitAPIException {
		parsedClass.startGatheringLog(localRepoUrl, parsingClass);
		currentLineNum = 0;
		trackingClass = new LinkedList<String>();
		createdClassObjects = new LinkedList<Tower>();
		createdMethodObjects = new LinkedList<Floor>();
		blockCommentHandler = -1;
		methodHandler = -1;
		
		
		
		int codeLineNums = parsedClass.numLinesOfCode();
		for(int i=0; i<codeLineNums; i++) {
			currentLineNum = i;
			parsingCodeLine(parsedClass.rawCode(i));
		}
		
		// sends the createdClassObjects and createdMethodObjects to the visualization class
		//...
	}
	
	public void parsingCodeLine(String codeLine) throws GitAPIException, IOException {
		StringTokenizer tokenizer = new StringTokenizer(codeLine);
		
		// checks whether the code line is empty
		if(!tokenizer.hasMoreTokens()) {
			return;
		}else {
			String token = tokenizer.nextToken();
			
			if(token.contains("package")) {
				return;
			}else if (token.contains("import")) {
				return;
			}
			// the code line contains no actual codes such as comments: comment line, block comment, and documentation comment
			else if(token.contains("//") || token.contains("*")) {
				return;
			}else if(token.contains("/*")) {
				blockCommentHandler = 1;
				return;
			}else if(token.contains("*/")) {
				blockCommentHandler = -1;
				return;
			}else if(blockCommentHandler == 1) {
				return;
			}else {
				// the line will contain one of these: method contents, class name, variable name.
				
				// checks whether the code line is related to a method
				if(methodHandler == 1) {
					if(parsedClass.rawCode(currentLineNum).contains("}")) {
						methodHandler = -1;
						createdMethodObjects.add(currentMethod);
					}
					PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
					currentMethod.increOwnershipSize(ownership);
					
				
				}else if(token.equals("public") || token.equals("private")) {
					token = tokenizer.nextToken();
					if(token.equals("static")) {
						//
					}else {
						if(token.equals("class")) {
							token = tokenizer.nextToken();
							StringTokenizer tokenizer2 = new StringTokenizer(token, "{");
							String className = tokenizer2.nextToken();
							
							classCreator(className);
							
						}else if(token.equals("abstract") || token.equals("interface")) {
							tokenizer.nextToken();
							token = tokenizer.nextToken();
							StringTokenizer tokenizer2 = new StringTokenizer(token, "{");
							String className = tokenizer2.nextToken();
							
							classCreator(className);
								
						}else {
							// the line will contain one of these: method contents, variable name
							token = tokenizer.nextToken();
							if(token.contains("(")) {
								StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
								String methodName = tokenizer2.nextToken();
								
								methodCreator(methodName);
								
							}else {
								String methodName = token;
								token = tokenizer.nextToken();
								if(token.contains("(")) {
									methodCreator(methodName);
								}else {
									return;
								}
							}	
						}
					}
				}else {
					
					//
				}
			}
		}
	}
	// creates the class object 
	public void classCreator(String className) {
		trackingClass.add(className);
		currentClass = new Tower(className);
		createdClassObjects.add(currentClass);
	}
	
	// creates the method object
	public void methodCreator(String methodName) {
		methodHandler = 1;
		currentMethod= new Floor(methodName);
		PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
		currentMethod.adjustOwnership(ownership, 1);
	}
}
