package main;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.PersonIdent;

public class Method {
	
	private String METHOD_NAME;
	private Map<PersonIdent, Integer> ownerships = new HashMap<PersonIdent, Integer>(); 

	
	public Method(String methodName) {
		METHOD_NAME = methodName;
	}

	public String getMethodName() {
		return METHOD_NAME;
	}
	
	public void adjustOwnership(PersonIdent ident, int size) {
		ownerships.put(ident, size);
	}
	
	public void increOwnershipSize(PersonIdent ident) {
		if(ownerships.containsKey(ident)) {
			ownerships.put(ident, ownerships.get(ident) + 1);
		}else {
			ownerships.put(ident, 1);
		}
	}
	/*
	 * public void test2(){
	 * 	test comment
	 * }
	 */
	
	/*
	public void test() {
		test comment
	}
	*/
	// for test
	public void printOwnerships() {
		int committers = ownerships.size();
		//for(int z=0; z < committers; z++) { test comment
		/*
		 * test comment
		 */
		
		/*
		 test comment
		 
		 */
		 
		System.out.println(ownerships.entrySet().toString());
	}

	

}
