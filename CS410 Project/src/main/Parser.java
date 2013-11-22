package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;


public class Parser {
	
    // The gathered log information that is going to be parsed
    private LogGatherer parsedClass;
    // The created list of class objects through parsing the log
    private LinkedList<Tower> createdClassObjects;
    // Stores all the created authors
    private Map<String,Author> allAuthors;
    // The current line number that is being parsed
    private int currentLineNum; 
    // The current line information that is being parsed
    private String currentLine;
    // The block comment handler that excludes block comments when parsing
    private int blockCommentHandler; 
    // The class handler is used for checking the nested class
    private int classHandler;
    // The class finish is used for checking the nested class
    private int classFinishes;
    // The current method that is created and is being parsed
    private Floor currentMethod;
    // The method handler is used for checking whether currently parsing inside a method
    private int methodHandler; 
    // The lineInMethod is used for checking whether the currently parsing line is inside the method or not
    private boolean lineInMethod;
    // The isConstructor is used for checking whether the currently created method is basic constructor 
    private boolean isConstructor;
    // The name of the package that parsed class belongs to 
    private String cityName;
    // The classCreated is Used to check when the class is ended
    private boolean classCreated;
	
    
    /**
     * Basic constructor
     */
    public Parser(){}
    
    
    public Map<String, Author> getAllAuthors() {
    	
    	return allAuthors;
    
    }
    
    
    
    // testing for the nested class for parsing 
    // The first nested class
    public class NestedClass1 {	 
    	
    	/**
    	 * test for nested class for parsing
    	 */
    	private boolean nestedClass1_Vari;
    		
    	public void nestedClass1_Method1() {
    		int t1 = 123;
    		int t2 = 1245;
    	
    	}
    	
    	private int nestedClass1_Method2() {
    		return 2;
    	}
    	
    	// The second nestedClass
    	private class NestedClass2 {        
    		
    		int nestedClass2_Method1() {
    			int a = 1;
    			return a;
    		}
    	}
    
    	public boolean nestedClass1_Method3() {
    		return true;
    	}
    	
    }
    
    // testing for the nested class for parsing 
    // The third nestedClass
   public class NestedClass3 {
    
	   public void nestedClass3_Method1() {
    		int t1 = 5;
    	}
   
   }
   
   
   
   /**
    * Start parsing the gathered Log
    * 
    * @param gatheredLog: The gathered log information
    * @throws IOException
    * @throws GitAPIException
    */
   public void startParsingClass(LogGatherer gatheredLog) throws IOException, GitAPIException {
      
	   
	   parsedClass = gatheredLog;
       currentLineNum = 0;
       createdClassObjects = new LinkedList<Tower>();
       blockCommentHandler = -1;
       methodHandler = -1;
       classHandler = -1;
       classFinishes = 0;
       classCreated = false;
       lineInMethod = false;
       isConstructor = false;

       if(allAuthors == null) {
    	   allAuthors = new HashMap<String, Author>();
       }


       int codeLineNums = parsedClass.numLinesOfCode();
       for(int i=0; i<codeLineNums; i++) {
           currentLineNum = i;
           currentLine = parsedClass.rawCode(i);
           parsingCodeLine(currentLine);
           
           // used to solve the exception problems
      /*   System.out.println(currentLine);
           System.out.println("LineInMethod: " + lineInMethod);
           System.out.println("Method Handler: " + methodHandler);
           System.out.println("Class Handler: " + classHandler);
           System.out.println("Class Created: " + classCreated);
           System.out.println("blockCommentHandler: " + blockCommentHandler);
           System.out.println(" "); */
       }
       
       // parsing test
       int check = createdClassObjects.size();
       System.out.println(check);
       int a = 0;
       while(a < check) {
           Tower theClass = createdClassObjects.get(a);
           System.out.println("Class Name: " + theClass.getTowerName());
           System.out.println("Package Name: " + theClass.getCityName());
           System.out.println(" ");

           LinkedList<Floor> methods = theClass.getListOfFloor();
           int methodSize = methods.size();
           
           for (int b=0; b < methodSize; b++) {
               Floor theMethod = methods.get(b);
               System.out.println("Method Name: " + theMethod.getFloorName());        
               theMethod.printOwnerships();
               System.out.println("Method access type: " + theMethod.getAccessType());
               System.out.println(" ");

           }
           a++;
       } // parsing test end 
   }
   
   /**
    * Parse the line to create matching class, method objects, and to compute the ownerships of each method 
    * 
    * @param codeLine: one line of the code lines gathered from the log 
    * @throws IOException
    * @throws GitAPIException
    */
   private void parsingCodeLine(String codeLine) throws GitAPIException, IOException {
	   
       StringTokenizer tokenizer = new StringTokenizer(codeLine);
       
       // checks whether the code line outside a method is empty
       if(!tokenizer.hasMoreTokens()) {
    	   return;
       }else {
    	   String token = tokenizer.nextToken();
    	   
    	   
    	   if(token.contains("package") && !lineInMethod) {  
    		   token = tokenizer.nextToken();
                
    		   StringTokenizer tempTokenizer = new StringTokenizer(token, ";");
    		   cityName = tempTokenizer.nextToken();
            
    		   return;    
    	   }else if (token.contains("import") && !lineInMethod) {
    		   return;
    	   }
    	   
    	   // filter the code line that is outside a method and contains no actual codes such as comments
           if (currentLine.contains("/*") && currentLine.contains("*/")){
        	   
        	   if(token.charAt(0) == '/' && token.charAt(1) == '*') {
        		   
        		   StringTokenizer tempTokenizer = tokenizer;
        		   String tempToken = null;
        		   while(tempTokenizer.hasMoreTokens()) {
          				tempToken = tempTokenizer.nextToken();
        		   }
        		   
        		   int lastIndex = tempToken.length() - 1;
        		   if(tempToken.charAt(lastIndex - 1) == '*' && tempToken.charAt(lastIndex) == '/') {
        			   return;
        		   }
        	   }
           }
           
           // filter the code line that is outside a method and contains no actual codes such as comments
           if(currentLine.contains("*/") && blockCommentHandler == 1 && !currentLine.contains("\"/*\"")) {
        	   blockCommentHandler = -1;
               return;
           }else if(blockCommentHandler == 1) {
        	   return;
           }else if(currentLine.contains("/*") && !currentLine.contains("*/") && !currentLine.contains("\"/*\"")) {
        	   if(token.charAt(0) == '/'  && token.charAt(1) == '*') {
          			blockCommentHandler = 1;
          			return;
          		}
        	   blockCommentHandler = 1;
           }
           
           if(token.contains("//") && !lineInMethod) {  
        	   return;
           }else if(currentLine.contains("@") && !tokenizer.hasMoreTokens()) {
        	   return;
           }else {
        	   // the line will contain one of these: method contents, class name, variable name.
        	   
        	   if(token.charAt(0) == '/') {
        		   return;
        	   }
           
        	   // checks whether the code line is related to a method
               if(lineInMethod) {
            	   // filter the code line that is inside a method
            	   if(currentLine.contains("{") || currentLine.contains("}")) {
            		   int result = 0;
            		   
                       // only check first most character and the last most character 
                       // case 1: both '{' and '}' can be in the same line
            		   if(currentLine.contains("{") && currentLine.contains("}")) {
            			   
            			   StringTokenizer tempTokenizer = null;
                    	   boolean openParenthesis = false;
                    	   
                    	   if(token.charAt(0) == '}') {
                    		   result--;
                    	   }else if(token.charAt(0) == '{') {
                    		   openParenthesis = true;
                    		   tempTokenizer = new StringTokenizer(currentLine, ")");
                               result++;
                    	   }
                    	   
                           if(currentLine.contains("//") && !currentLine.contains("\"//\"")) {
                        	   StringTokenizer tokenizera = new StringTokenizer(token, "//");
                               tokenizer = tokenizera;
                           }   	
                           
                           while(tokenizer.hasMoreTokens()) {
                               token = tokenizer.nextToken();
                           }
                           
                           
                           boolean commentedOut = false;
                       
                           if(blockCommentHandler == 1) {
                        	  	if(openParenthesis) {
                               		StringTokenizer tempTokenizer2 = new StringTokenizer(currentLine, "/*");
                               		StringTokenizer tempTokenizer3 = new StringTokenizer(currentLine, "}");
                               		
                               		String tempToken2 = tempTokenizer2.nextToken();
                               		String tempToken3 = tempTokenizer3.nextToken();
                               		
                               		if(tempToken2.length() < tempToken3.length()) {
                               			commentedOut = true;
                               		}
                               		
                        	  	}else {
                               		StringTokenizer tempTokenizer2 = new StringTokenizer(currentLine, "/*");
                               		StringTokenizer tempTokenizer3 = new StringTokenizer(currentLine, "{");
                               		
                               		String tempToken2 = tempTokenizer2.nextToken();
                               		String tempToken3 = tempTokenizer3.nextToken();
                               		
                               		if(tempToken2.length() < tempToken3.length()) {
                               			commentedOut = true;
                               		}
                        	  	}
                           }
                                               
                           int lastIndex = token.length() - 1;
                           if (token.charAt(lastIndex) == '}' && !commentedOut) {
                        		if(!openParenthesis) {
                        			result = 0;
                        		}else {
                        			tempTokenizer.nextToken();
                           			String theToken = tempTokenizer.nextToken();
                           			if(theToken.contains("{") && theToken.contains("}")) {
                           				// do not change the 'result'
                           			}else {
                           				result--;
                           			}
                  
                        		}                       
                           }else if(token.charAt(lastIndex) == '{' && !commentedOut) {
                               result++;
                           }
                                                       
                       // case2: only one of '{' and '}' is in the line  
            		   }else {
            			   
            			   if(token.charAt(0) == '}') { 
            				   result--;
            		       }else if (token.charAt(0) == '{') {             
            		    	   result++;
            		       }else {           
            		    		if(currentLine.contains("//") && !currentLine.contains("\"//\"")) {
            		    			String token3 = null;
                           			StringTokenizer tempTokenizer = new StringTokenizer(currentLine, "//");
                           			
                           			token3 = tempTokenizer.nextToken();
                           			StringTokenizer tempTokenizer2 = new StringTokenizer(token3);        		
                           			while(tempTokenizer2.hasMoreTokens()) {
                           				token3 = tempTokenizer2.nextToken();
                           			}
                          			int lastIndex = token3.length() - 1;
                                               			
                           			if(token3.charAt(lastIndex) == '}') {
                           				result--;
                           			}else if(token3.charAt(lastIndex) == '{') {
                           	      		result++; 		
                           			}else if(currentLine.contains("{")) {
                          				if(!currentLine.contains("\"{\"") && !currentLine.contains("'{'")) {
                           					result++;
                           				}        		
                           			}else if(currentLine.contains("}")) {
                           				if(!currentLine.contains("\"}\"") && !currentLine.contains("'}'")) {
                           					result--;
                           				}
                           			}
            		    		}else {
                                               		
                           			while(tokenizer.hasMoreTokens()) {
                           				token = tokenizer.nextToken();
                           			}
                           		    
                           			int lastIndex = token.length() - 1; 
                           			if(token.charAt(lastIndex) == '}') {
                           				result--;
                           			}else if(token.charAt(lastIndex) == '{') {
                           				result++;
                           			}else if(currentLine.contains("{")) {
                           				if(!currentLine.contains("\"{\"") && !currentLine.contains("'{'")) {
                           					result++;
                                               				
                           				}
                           			}else if(currentLine.contains("}")) {
                           				if(!currentLine.contains("\"}\"") && !currentLine.contains("'}'")) {
                           					result--;
                                               		
                           				}
                           			}
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
                                                       
               }else if(classHandler > -1 && currentLine.contains("}") && !currentLine.contains("{") && classCreated && !currentLine.contains("},") && !currentLine.contains("};")&& !currentLine.contains(");")) {
                       	
            		classFinishes++;
                       	
            		if(classHandler == 1) {
                   		classHandler = 0;
                   	}else if(classHandler == 0) {
                   		classHandler = -1;
                   	}else {
                       	classHandler -= classFinishes;
                   	}   	
 
               }else {
                       	
            		if(token.contains("@")) {
            			if(token.contains("Override") || token.contains("SuppressWarnings") || token.contains("ExportAttribute")) {
            				token = tokenizer.nextToken();
            			}else {
            				tokenizer.nextToken();
               				token = tokenizer.nextToken();
               			}
               		}
                                                               
            		if(token.equals("public") || token.equals("private") || token.equals("protected")) {
                        token = tokenizer.nextToken();
            		}
            		
            		if(token.equals("final")) {
            			token = tokenizer.nextToken();
                                       
                        if(token.contains("public")) {
                        	token = tokenizer.nextToken();
                         }
            		}
                               
            		if(token.equals("static")) {
            			if(!currentLine.contains("(")) {
            				classCreated = false;
            			}
                               		
            			if(tokenizer.hasMoreTokens()) {
            				token = tokenizer.nextToken();
                               			
            				if(token.equals("public") || token.equals("private")) {
            					token = tokenizer.nextToken();
            				}
            				if(token.equals("final")) {
            					token = tokenizer.nextToken();
            				}
            			}else {
            				return;
            			}
            		}
                               
            		if(token.equals("native")) {
            			token = tokenizer.nextToken();
            		 }
                               
            		if(token.equals("synchronized")) {
            			token = tokenizer.nextToken();
            		}
                               
            		if(token.equals("enum")) {
            			token = tokenizer.nextToken();
            			
            			if(!token.equals("class")) {
            				StringTokenizer tempTokenizer = new StringTokenizer(token, "{");
            				String className = tempTokenizer.nextToken();
                                                       
            				classCreator(className);
            				return;
            			}      
            		}                
   
            		if(token.equals("class") || token.equals("interface")) {
            			
            			token = tokenizer.nextToken();
            			String className = null;
                          
            			StringTokenizer tempTokenizer = new StringTokenizer(token, "{");
                       	className = tempTokenizer.nextToken();
     
                        classCreator(className);
                        return;     

            		}else if(token.equals("abstract") && !currentLine.contains("(")) {
            			tokenizer.nextToken();
                        token = tokenizer.nextToken();
                       
                        if(token.equals("public")) {
                        	token = tokenizer.nextToken();
                        }
                                       
                        if(token.equals("class")) {
                           	token = tokenizer.nextToken();
                        }
                                       
                        StringTokenizer tempTokenizer = new StringTokenizer(token, "{");
                        String className = tempTokenizer.nextToken();
                                               
                        classCreator(className);
                        return;       
                        
            		}else {
            			// the line contains only variable
            			if(!currentLine.contains("(") || currentLine.contains("),")) {
                   			return;
            			}
                  
            			// the line will contain method contents    	
                        
                        // the method could be the constructor
            			String currentTowerName = createdClassObjects.get(classHandler).getTowerName();
                   		String methodType = null;
                   		if(token.contains("(")) {
                   			StringTokenizer tempTokenizer = new StringTokenizer(token, "(");
                   			methodType = tempTokenizer.nextToken();  
                   		}else {
                   			methodType = token;
                   		}	 
                   		
                   		if(currentTowerName.contains("<")) {	
                 			StringTokenizer tempTokenizer = new StringTokenizer(currentTowerName, "<");
                 			currentTowerName = tempTokenizer.nextToken();
                   		}
                   		
                   		if(methodType.equals(currentTowerName)) {
                   			
                   			if(currentLine.contains("{") && currentLine.contains("}")) {
                   				isConstructor = true;
                   			}
                               		 
                   			if(currentLine.contains(";") && !currentLine.contains("{") && !currentLine.contains("}")) {
                               	return;
                   			}
                               			
                   			String methodName = methodType;   		
                   			methodCreator(methodName);
           
                   		}else {        
        
                       		if(!tokenizer.hasMoreTokens()) {
                       			return;
                       		}
                                       		
                       		String tempToken = null;
                       		StringTokenizer tempTokenizer = new StringTokenizer(currentLine);
                       		tempToken = tempTokenizer.nextToken();
                       		if(tempToken.equals("public") || tempToken.equals("private")) {
                       			tempToken = tempTokenizer.nextToken();
                       		}
                                       		
                                       		
                       		if(token.contains("<")) {
                   				if(token.charAt(0) == '<') {
                   					
                   					if(!token.contains(">")) {
                   						token = tokenizer.nextToken();
                   						tempToken = tempTokenizer.nextToken();
                   						while(!token.contains(">")) {
                   							token = tokenizer.nextToken();
                   							tempToken = tempTokenizer.nextToken();
                   						}
                   					}
        				
                   					tempToken = tempTokenizer.nextToken();
                   					if(!tempToken.contains("(")) {
                   						token = tokenizer.nextToken();
                   						
                   					}
                                       					
                                       					
                   					if(token.contains("<")) {
                   						if(!token.contains(">")) {
                   							while(!token.contains(">")) {
                   								token = tokenizer.nextToken();
                   							}
                   						}
                   					}
                                       					
                   				}else {
                   					int lastIndex = token.length() - 1;
                   					while(token.charAt(lastIndex) != '>') {
                   						token = tokenizer.nextToken();
                   						lastIndex = token.length() - 1;
                   					}
                   				}
                       		}

                   			token = tokenizer.nextToken();
                   			if(token.contains("(")) {         
                   				                       
                   				if(currentLine.contains("{") && currentLine.contains("}")) {
                                    isConstructor = true;
                   				}
                   				if(currentLine.contains(";") && !currentLine.contains("}") && !currentLine.contains("}")) {
                                   	return;
                   				}
                   				
                                StringTokenizer tempTokenizer2 = new StringTokenizer(token, "(");
                                String methodName = tempTokenizer2.nextToken();
                                methodCreator(methodName);                                                                        
                   			}else {
                   				String methodName = token;
                   				if(tokenizer.hasMoreTokens()) {
                   					token = tokenizer.nextToken();
                   				    if(token.contains("(")) {
                   				    	if(currentLine.contains("{") && currentLine.contains("}")) {
                                   	 		isConstructor = true;
                   				    	}
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
   
        
   /**
    * Creates a new class object
    * 
    * @param className: The name of the new class/tower object
    */
   private void classCreator(String tempClassName) {
	   String className = tempClassName;
   	
	   if(className.contains("<")) { 	
		   StringTokenizer tokenizer2 = new StringTokenizer(className, "<");
	       className = tokenizer2.nextToken();
	   }
   	
	   Tower currentClass = new Tower(className);
	   currentClass.setCityName(cityName);
	   createdClassObjects.add(currentClass);
          
	   if(classHandler == -1 ) {
		   classHandler++;
	   }else  {
		   classHandler = createdClassObjects.size() - 1;
	   }
	   classCreated = true;
   }
   

   /**
    * Creates a new method object
    * 
    * @param methodName: The name of the new method/floor object
    */
   private void methodCreator(String methodName) {
   	
   		methodHandler = -1;
      
   		currentMethod= new Floor(methodName);
        lineInMethod = true;
        currentMethod.setAccessType(3);
           
        PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
        Author author = getUniqueAuthor(ownership);
        
        currentMethod.adjustOwnership(author, 1);
          
        // adding the feature for each method: public/private/protected
        if(currentLine.contains("protected")) {
        	currentMethod.setAccessType(2);       
        }else if(currentLine.contains("private")) {
        	currentMethod.setAccessType(1);
        }else {
        	 // method type in this case is public
            currentMethod.setAccessType(0);        
        }
           
        if(isConstructor) {
        	createdClassObjects.get(classHandler).addFloor(currentMethod);
            isConstructor = false;
            lineInMethod = false;
            methodHandler = -1;
            return;
        }
           
        if(currentLine.contains("{")) {
        	methodHandler++;        
        }else if (currentLine.contains(";")) {
        	
        	// method is ended
           	lineInMethod = false;
           	createdClassObjects.get(classHandler).addFloor(currentMethod);
        }  
   }
   
   
   /**
    * 
    * @return: The created Class Objects through parsing the Log
    */
   public LinkedList<Tower> getParsedLog() {
	   return createdClassObjects;  
   }

        
           
   private Author getUniqueAuthor(PersonIdent tempOwnership) {
	   PersonIdent ownership = tempOwnership;
       Author author = new Author("Empty", "Empty");
       String uniqueEmail = "UnknownEmailAddr";
       if(ownership != null) {
    	   uniqueEmail = ownership.getEmailAddress();
       }

       if (!allAuthors.containsKey(uniqueEmail)) {
    	   String uniqueName = "UnknownName";
    	   if(ownership != null){
    		   uniqueName = ownership.getName();
    	   }
    	   author = new Author(uniqueName, uniqueEmail);
              
    	   if (uniqueEmail == "") {
    		   author.setUnknownAuthorColor();
               allAuthors.put("UnknownEmailAddr", author);               
    	   }else {
    		   allAuthors.put(uniqueEmail, author);
    	   }
       }else {
    	   author = allAuthors.get(uniqueEmail);
       }     
       return author;
   }
}