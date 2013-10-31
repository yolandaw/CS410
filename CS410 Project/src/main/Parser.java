package main;

import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;


public class Parser {
	
	private LogGatherer parsedClass;
	private LinkedList<Class> createdClassObjects; 
	private int currentLineNum; 
	private Method currentMethod; 
	private int blockCommentHandler; 
	private int methodHandler; 
	private int classHandler;
	private boolean lineInMethod;
	private String currentLine;
	

	public Parser(){
		parsedClass = new LogGatherer();
	}

	
	public void startParsingClass(String localRepoUrl, String parsingClass) throws IOException, GitAPIException {
		parsedClass.startGatheringLog(localRepoUrl, parsingClass);
		currentLineNum = 0;
		createdClassObjects = new LinkedList<Class>();
		blockCommentHandler = -1;
		methodHandler = -1;
		classHandler = -1;
		lineInMethod = false;
		

		int codeLineNums = parsedClass.numLinesOfCode();

		for(int i=0; i<codeLineNums; i++) {
			currentLineNum = i;
			currentLine = parsedClass.rawCode(i);
			parsingCodeLine(currentLine);
			System.out.println(currentLineNum);
		//	System.out.println(classHandler);
		}
		
		// test
		int check = createdClassObjects.size();
		System.out.println(check);
		int a = 0;
		while(a < check) {
			Class theClass = createdClassObjects.get(a);
			System.out.println("Class Name: " + theClass.getClassName());
		
		    int methodSize = theClass.getListOfMethods().size();
		    LinkedList<Method> methods = theClass.getListOfMethods();
			for (int b=0; b < methodSize; b++) {
				
				Method theMethod = methods.get(b);
				System.out.println("Method Name: " + theMethod.getMethodName());	
				theMethod.printOwnerships();
			}
		    a++;
		} // test end
		
		
		// sends the createdClassObjects and createdMethodObjects to the visualization class
		//...
	}
	
	public void parsingCodeLine(String codeLine) throws GitAPIException, IOException {
		StringTokenizer tokenizer = new StringTokenizer(codeLine);
		
		// checks whether the code line outside a method is empty
		if(!tokenizer.hasMoreTokens()) {
			return;
		}else {
			String token = tokenizer.nextToken();
			
			if(token.contains("package")) {
				return;
			}else if (token.contains("import")) {
				return;
			}
			// filter the code line that is outside a method and contains no actual codes such as comments: line comment, block comment, and documentation comment
			else if(token.contains("//")) {
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
				if(lineInMethod) {
						
					// filter the code line that is inside a method and contains no actual codes such as comment: line comment and block comment
				//	if(token.contains("//")) {
				//		return;
				//	}else if(token.contains("/*")) {
				//		blockCommentHandler = 1;
				//		return;
				//	}else if(token.contains("*/")) {
				//		blockCommentHandler = -1;
				//		return;
				//	}else if(blockCommentHandler == 1) {
				//		return;
				//	}else { 
				//		String lineCode = parsedClass.rawCode(currentLineNum);
						
						if(currentLine.contains("{") && currentLine.contains("}")) {
						
						}else if(currentLine.contains("{")) {
							methodHandler++;
						}else if (currentLine.contains("}")) {
							methodHandler--;
							if(methodHandler == -1) {
								lineInMethod = false;	
								PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
								currentMethod.increOwnershipSize(ownership);
								createdClassObjects.get(classHandler).addMethod(currentMethod);
								return;
							}
						}

						PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
						currentMethod.increOwnershipSize(ownership);
					//	System.out.println(ownership.getName() + currentLineNum);
				//	}	
				}else if(classHandler > -1 && currentLine.contains("}")) {
					classHandler--;
			
				}else {
					
					if((token.equals("public") || token.equals("private") || token.equals("protected"))) {
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
						// could be the constructor
						if(token.contains("(")) {
							StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
							String methodName = tokenizer2.nextToken();
								
							methodCreator(methodName);
						}else {
							token = tokenizer.nextToken();
							if(token.contains("(")) {
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
		Class currentClass = new Class(className);
		createdClassObjects.add(currentClass);
		classHandler++;
	}
	
	// creates the method object
	public void methodCreator(String methodName) {
		
		if(currentLine.contains("{")) {
			methodHandler++;	
		}
		System.out.println("Method is created");
		currentMethod= new Method(methodName);
		PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
		currentMethod.adjustOwnership(ownership, 1);
		lineInMethod = true;
	}
}
