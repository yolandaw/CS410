package main;

import java.util.LinkedList;

public class Class {
	
//	private LinkedList<String> classMethods = new LinkedList<String>();
    private String CLASSNAME;
    private LinkedList<Method> classMethods = new LinkedList<Method>();
    
    public Class(String className) {
    	CLASSNAME = className;
    }
    
    public void addMethod(Method methodName) {
    	classMethods.add(methodName);
    }
    
    public LinkedList<Method> getListOfMethods() {
    	return classMethods;
    }
    
    public String getClassName() {
    	return CLASSNAME;
    }
    
    
    

}
