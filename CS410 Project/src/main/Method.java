package main;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Method {
	
	private String METHOD_NAME;
	private Map<Author, Integer> ownerships = new HashMap<Author, Integer>(); 

	
	public Method(String methodName) {
		METHOD_NAME = methodName;
	}

	public String getMethodName() {
		return METHOD_NAME;
	}
	
	public void adjustOwnership(Author author, int size) {
		ownerships.put(author, size);
	}
	
	public void increOwnershipSize(Author author) {	
		Iterator<Author> itr = ownerships.keySet().iterator();
		Author existAuthor;
	
		
		while(itr.hasNext()) {
			existAuthor = (Author) itr.next();
			if(existAuthor.getEmailAddr().equals(author.getEmailAddr())) {
				ownerships.put(existAuthor, ownerships.get(existAuthor) + 1);
				return;
			}
		}
		adjustOwnership(author, 1);
	}
	

	// for parsing test
	public void printOwnerships() {
		Set<Author> authorSet = ownerships.keySet();
		Iterator<Author> authorIter = authorSet.iterator();
		while(authorIter.hasNext()) {
			Author author = authorIter.next();
			System.out.println("Author Name: " + author.getAuthorName() + ", Ownership size: " + ownerships.get(author) + ".");
		}
	}
}
