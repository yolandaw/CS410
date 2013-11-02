package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;


public class Parser {
	 
	private LogGatherer parsedClass;
	private LinkedList<Tower> createdClassObjects;
	private Map<String,Author> allAuthors;
	private int currentLineNum; 
	private String currentLine;
	private int blockCommentHandler; 
	private int classHandler;
	private Floor currentMethod; 
	private int methodHandler; 
	private boolean lineInMethod;
	private boolean isConstructor;
	

	public Parser(){}

	public void startParsingClass(LogGatherer gatheredLog) throws IOException, GitAPIException {
		parsedClass = gatheredLog;
		currentLineNum = 0;
		createdClassObjects = new LinkedList<Tower>();
		allAuthors = new HashMap<String, Author>();
		blockCommentHandler = -1;
		methodHandler = -1;
		classHandler = -1;
		lineInMethod = false;
		isConstructor = false;
		
		int codeLineNums = parsedClass.numLinesOfCode();

		for(int i=0; i<codeLineNums; i++) {
			currentLineNum = i;
			currentLine = parsedClass.rawCode(i);
			parsingCodeLine(currentLine);
		}
		
		// parsing test
		int check = createdClassObjects.size();
		System.out.println(check);
		int a = 0;
		while(a < check) {
			Tower theClass = createdClassObjects.get(a);
			System.out.println("Class Name: " + theClass.getTowerName());
		
		    LinkedList<Floor> methods = theClass.getListOfFloor();
		    int methodSize = methods.size();
		    
			for (int b=0; b < methodSize; b++) {
				Floor theMethod = methods.get(b);
				System.out.println("Method Name: " + theMethod.getFloorName());	
				theMethod.printOwnerships();
				System.out.println(" ");
			}
		    a++;
		} // parsing test end
	}
	
	public void parsingCodeLine(String codeLine) throws GitAPIException, IOException {
		StringTokenizer tokenizer = new StringTokenizer(codeLine);
		
		// checks whether the code line outside a method is empty
		if(!tokenizer.hasMoreTokens()) {
			return;
		}else {
			String token = tokenizer.nextToken();
			
			if(token.contains("package") && !lineInMethod) {
				return;
			}else if (token.contains("import") && !lineInMethod) {
				return;
			}
			// filter the code line that is outside a method and contains no actual codes such as comments: line comment, block comment, and documentation comment
			else if(currentLine.contains("*/") && blockCommentHandler == 1) {
				blockCommentHandler = -1;
				return;
			}else if(token.contains("/*")) {
				blockCommentHandler = 1;
				return;
			}else if(token.contains("//")) {  
				return;
			}else if(blockCommentHandler == 1) {
				return;
			}else {
				// the line will contain one of these: method contents, class name, variable name.
				
				// checks whether the code line is related to a method
				if(lineInMethod) {
						
					// filter the code line that is inside a method and contains no actual codes such as comment: line comment and block comment
					if(currentLine.contains("{") || currentLine.contains("}")) {
						int result = 0;
							
						// only check first most character and the last most character 
						// case 1: both '{' and '}' can be in the same line
						if(currentLine.contains("{") && currentLine.contains("}")) {
							
							if(token.charAt(0) == '}') {
								result--;
							}else if(token.charAt(0) == '{') {
								result++;
							}
							
							while(tokenizer.hasMoreTokens()) {
								token = tokenizer.nextToken();
							}
							
							int lastIndex = token.length() - 1;
							if (token.charAt(lastIndex) == '}') {
								result--;
							}else if(token.charAt(lastIndex) == '{') {
								result++;
							}
								
						// case2: only one of '{' and '}' is in the line  
						}else {
							if(token.charAt(0) == '}') {
								result--;
							}else if (token.charAt(0) == '{') {
								result++;
							}else {
								while(tokenizer.hasMoreTokens()) {
									token = tokenizer.nextToken();
								}
								int lastIndex = token.length() - 1;
								if(token.charAt(lastIndex) == '}') {
									result--;
								}else if(token.charAt(lastIndex) == '{') {
									result++;
								}
							}
						}
						
						methodHandler += result;
								
						if(methodHandler == -1) {
							lineInMethod = false;	
							
							PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
							Author author = getUniqueAuthor(ownership);
							currentMethod.increOwnershipSize(author);
							
							createdClassObjects.get(classHandler).addFloor(currentMethod);
							return;
						}
					}
					PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
					Author author = getUniqueAuthor(ownership);
					currentMethod.increOwnershipSize(author);	
								
				}else if(classHandler > -1 && currentLine.contains("}") && !currentLine.contains("{")) {
					classHandler--;
			
				}else {
									
					if(token.equals("public") || token.equals("private") || token.equals("protected")) {
						token = tokenizer.nextToken();
					}
					
					if(token.equals("final")) {
						token = tokenizer.nextToken();
					}
					
					if(token.equals("static")) {
						token = tokenizer.nextToken();
					}
					
					if(token.equals("enum")) {
						token = tokenizer.nextToken();
						if(!token.equals("class")) {
							StringTokenizer tokenizer2 = new StringTokenizer(token, "{");
							String className = tokenizer2.nextToken();
								
							classCreator(className);
							return;
						}	
					}
					
					if(token.equals("class") || token.equals("interface")) {
						token = tokenizer.nextToken();
						StringTokenizer tokenizer2 = new StringTokenizer(token, "{");
						String className = tokenizer2.nextToken();
							
						classCreator(className);
							
					}else if(token.equals("abstract")) {
						tokenizer.nextToken();
						token = tokenizer.nextToken();
						StringTokenizer tokenizer2 = new StringTokenizer(token, "{");
						String className = tokenizer2.nextToken();
							
						classCreator(className);
					}else {
						// the line will contain one of these: method contents, variable name
						// the method could be the constructor
						if(token.contains("(")) {
							if(currentLine.contains("{") && currentLine.contains("}")) {
								isConstructor = true;
							}
							StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
							String methodName = tokenizer2.nextToken();
								
							methodCreator(methodName);
						}else {
							token = tokenizer.nextToken();
							if(token.contains("(")) {
								if(currentLine.contains("{") && currentLine.contains("}")) {
									isConstructor = true;
								}
								StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
								String methodName = tokenizer2.nextToken();
								
								methodCreator(methodName);									
							}else {
								String methodName = token;
								if(tokenizer.hasMoreTokens()) {
									token = tokenizer.nextToken();
									if(	token.contains("(")) {
										methodCreator(methodName);
									}else {
										return;
									}
								}
							}	
						}
					}
				}
			}
		}
	}
	
	// creates the class object 
	public void classCreator(String className) {
		Tower currentClass = new Tower(className);
		createdClassObjects.add(currentClass);
		classHandler++;
	}
	
	// creates the method object
	public void methodCreator(String methodName) {
		
		if(currentLine.contains("{")) {
			methodHandler++;	
		}
		currentMethod= new Floor(methodName);
		lineInMethod = true;
		
		PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
		Author author = getUniqueAuthor(ownership);
		
		currentMethod.adjustOwnership(author, 1);
		
		if(isConstructor) {
			createdClassObjects.get(classHandler).addFloor(currentMethod);
			isConstructor = false;
			lineInMethod = false;
			methodHandler = -1;
		}
	}
	
	public LinkedList<Tower> getParsedLog() {
		return createdClassObjects;	
	}
	
	private Author getUniqueAuthor(PersonIdent ownership) {
		Author author = new Author("Empty", "Empty");
		String uniqueEmail = ownership.getEmailAddress();
		
		if (!allAuthors.containsKey(uniqueEmail)) {
			author = new Author(ownership.getName(), uniqueEmail);
			if (uniqueEmail == "") {
				author.setUnknownAuthorColor();
			}
			allAuthors.put(uniqueEmail, author);
		} else {
			author = allAuthors.get(uniqueEmail);
		}
		
		return author;
	}
}
