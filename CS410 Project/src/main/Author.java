package main;

import java.util.Random;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

public class Author implements org.jsfml.graphics.Drawable {
	
	private String authorName;
	private String emailAddress;
	private Color authorColor;
	
	
	public Author(String name, String emailaddr) {
		authorName = name;
		emailAddress = emailaddr;
		generateAuthorColor();
	}
	
	public String getAuthorName() {
		return authorName; 
	}
	
	public String getEmailAddr() {
		return emailAddress;
	}
	
	public Color getAuthorColor() {
		return authorColor;
	}
	
	public void setUnknownAuthorColor() {
		authorColor = new Color(255,255,255);
	}
	
	private void generateAuthorColor() {
		Random randInt = new Random();
		authorColor = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
	}

	
	public void draw(RenderTarget window, RenderStates renderStates) {
		// TODO Auto-generated method stub
		
	}
}

