package main;

public class Author {
	
	private String authorName;
	private String emailAddress;
	
	
	public Author(String name, String emailaddr) {
		authorName = name;
		emailAddress = emailaddr;
	}
	
	public String getAuthorName() {
		return authorName; 
	}
	
	public String getEmailAddr() {
		return emailAddress;
	}
}

