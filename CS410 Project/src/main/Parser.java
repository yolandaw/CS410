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
        
        private boolean classCreated;
        
        private boolean checkTest;
        
        /**
         * Basic constructor
         */
        public Parser(){}
        
     
        
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
                if (allAuthors == null) {
                	allAuthors = new HashMap<String, Author>();
                }
                blockCommentHandler = -1;
                methodHandler = -1;
                classHandler = -1;
                classFinishes = 0;
                lineInMethod = false;
                isConstructor = false;
                classCreated = false;
                
                int codeLineNums = parsedClass.numLinesOfCode();

                for(int i=0; i<codeLineNums; i++) {
                        currentLineNum = i;
                        currentLine = parsedClass.rawCode(i);
                        parsingCodeLine(currentLine);
                        System.out.println(currentLine + " lineinmethod: "+ lineInMethod + " methhand: "+ methodHandler + " classHandler: "+ classHandler);
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
                        }else if(currentLine.contains("@")) {
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
                                                		StringTokenizer tokenizeri = null;
                                                		boolean openParenthesis = false;
                                                        if(token.charAt(0) == '}') {
                                                        		result--;
                                                        }else if(token.charAt(0) == '{') {
                                                        		openParenthesis = true;
                                                        		tokenizeri = new StringTokenizer(currentLine, ")");
                                                                result++;
                                                        }
                                                        
                                                        while(tokenizer.hasMoreTokens()) {
                                                                token = tokenizer.nextToken();
                                                        }
                                                        
                                                        int lastIndex = token.length() - 1;
                                                        if (token.charAt(lastIndex) == '}') {
                                                        		if(!openParenthesis) {
                                                        			result = 0;
                                                        		}else {
                                                        			tokenizeri.nextToken();
                                                        			String theToken = tokenizeri.nextToken();
                                                        			if(theToken.contains("{") && theToken.contains("}")) {
                                                        				// do not change the 'result'
                                                        			}else {
                                                        				result--;
                                                        			}
                           
                                                        		}
                                                               
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
                                                                
                                }else if(classHandler > -1 && currentLine.contains("}") && !currentLine.contains("{") && classCreated) {
                                	
                                	classFinishes++;
                                	
                                	if(classHandler == 1) {
                                		classHandler = 0;
                                	}else {
                                    	classHandler -= classFinishes;
                                	}
                                	
                        
                                }else {
                                                                        
                                        if(token.equals("public") || token.equals("private") || token.equals("protected")) {
                                                token = tokenizer.nextToken();
                                        }
                                        
                                        if(token.equals("final")) {
                                                token = tokenizer.nextToken();
                                        }
                                        
                                        if(token.equals("static")) {
                                        	classCreated = false;
                                        	if(tokenizer.hasMoreTokens()){
                                        		token = tokenizer.nextToken();
                                        	}
                                        	else return;
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
                                                        if(currentLine.contains(";") && !currentLine.contains("}") && !currentLine.contains("{")){
                                                        	return;
                                                        }
                                                        StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
                                                        String methodName = tokenizer2.nextToken();
                                                        
                                                        methodCreator(methodName);
                                                        
                                                    
                                                }else {
                                                		if(!tokenizer.hasMoreTokens()){
                                                			return;
                                                		}
                                                        token = tokenizer.nextToken();
                                                        if(token.contains("(")) {
                                                                if(currentLine.contains("{") && currentLine.contains("}")) {
                                                                        isConstructor = true;
                                                                }
                                                                if(currentLine.contains(";") && !currentLine.contains("}") && !currentLine.contains("{")){
                                                                	return;
                                                                }
                                                                StringTokenizer tokenizer2 = new StringTokenizer(token, "(");
                                                               
                                                                // problem:
                                                                String methodName = tokenizer2.nextToken();
                                                                
                                                                methodCreator(methodName);                                                                        
                                                        }else {
                                                                String methodName = token;
                                                                if(tokenizer.hasMoreTokens()) {
                                                                        token = tokenizer.nextToken();
                                                                        if(        token.contains("(")) {
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
        private void classCreator(String className) {
                Tower currentClass = new Tower(className);
                currentClass.setCityName(cityName);
                createdClassObjects.add(currentClass);
                classHandler++;
                classHandler += classFinishes;
                System.out.println(currentLine + "classes created");
                classCreated = true;
        }
        
        /**
         * Creates a new method object
         * 
         * @param methodName: The name of the new method/floor object
         */
        private void methodCreator(String methodName) {
        	System.out.println(currentLine +" methodcreated");
        		methodHandler = -1;
                if(currentLine.contains("{")) {
                        methodHandler++;        
                }
                currentMethod= new Floor(methodName);
                lineInMethod = true;
                currentMethod.setAccessType(3);
                
                PersonIdent ownership = parsedClass.getAuthor(currentLineNum);
                Author author = getUniqueAuthor(ownership);
                
                currentMethod.adjustOwnership(author, 1);
                
                if(isConstructor) {
                        createdClassObjects.get(classHandler).addFloor(currentMethod);
                        isConstructor = false;
                        lineInMethod = false;
                        methodHandler = -1;
                }
                
                // adding the feature for each method: public/private/protected
                if(currentLine.contains("protected")) {
                        currentMethod.setAccessType(2);
                        
                }else if(currentLine.contains("private")) {
                        currentMethod.setAccessType(1);
                        
                }else {
                        // method type in this case is public
                        currentMethod.setAccessType(0);        
                }
        }
        
        /**
         * 
         * @return: The created Class Objects through parsing the Log
         */
        public LinkedList<Tower> getParsedLog() {
                return createdClassObjects;        
        }
        
        private Author getUniqueAuthor(PersonIdent ownership) {
            Author author = new Author("Empty", "Empty");
            String uniqueEmail = "UnknownEmailAddr";
            //System.out.println(ownership);
            if(ownership != null){
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
            } else {
                    author = allAuthors.get(uniqueEmail);
            }
            
            return author;
    }
}