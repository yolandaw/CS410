package main;

import java.util.LinkedList;

public class Class {
	
    private String CLASSNAME;
    private LinkedList<Method> classMethods;
    
    public Class(String className) {
    	CLASSNAME = className;
    	classMethods = new LinkedList<Method>();
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
