package main;

import java.util.Random;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;

public class Author implements org.jsfml.graphics.Drawable {
	
	private String authorName;
	private String emailAddress;
	private Color authorColor;
	private Sprite authorSprite;
	private Text authorNameText;
	
	
	public Author(String name, String emailaddr) {
		authorName = name;
		emailAddress = emailaddr;
		generateAuthorColor();
		authorSprite = new Sprite();
		authorNameText = new Text();
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
		authorColor = new Color(220,220,220);
	}
	
	public void setPosition(float x, float y) {
		authorSprite.setPosition(x, y);
	}
	
	private void generateAuthorColor() {
		Random randInt = new Random();
		authorColor = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
	}
	
	public Sprite getAuthorSprite() {
		return authorSprite;
	}
	
	public Text getAuthorNameText() {
		return authorNameText;
	}
	
	public void setAuthorNameText(Text newText) {
		authorNameText = newText;
	}

	
	public void draw(RenderTarget window, RenderStates renderStates) {
		// TODO Auto-generated method stub
		window.draw(authorNameText);
		window.draw(authorSprite);
		
	}
}

