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
	
	/**
	 * 
	 * Sets all default values on creation
	 * @param name: the name of the author
	 * @param emailaddr: the email address of the author
	 * 
	 */
	public Author(String name, String emailaddr) {
		authorName = name;
		emailAddress = emailaddr;
		generateAuthorColor();
		authorSprite = new Sprite();
		authorNameText = new Text();
	}
	
	/**
	 * 
	 * @return: The author's name
	 * 
	 */
	public String getAuthorName() {
		return authorName; 
	}
	
	/**
	 * 
	 * @return: The author's email address
	 * 
	 */
	public String getEmailAddr() {
		return emailAddress;
	}
	
	/**
	 * 
	 * @return: The author's color
	 * 
	 */
	public Color getAuthorColor() {
		return authorColor;
	}
	
	/**
	 * 
	 * Set author color to the default unknown color of r:220, g:220, b:220
	 * 
	 */
	public void setUnknownAuthorColor() {
		authorColor = new Color(220,220,220);
	}
	
	/**
	 * 
	 * Sets the author sprite's position
	 * 
	 * @param x: x position of author sprite
	 * @param y: y position of author sprite
	 * 
	 */
	public void setPosition(float x, float y) {
		authorSprite.setPosition(x, y);
	}
	
	/**
	 * 
	 * Set author color to a random r,g,b values between 0 and 255 
	 */
	private void generateAuthorColor() {
		Random randInt = new Random();
		authorColor = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
	}
	
	/**
	 * 
	 * @return: The author sprite
	 * 
	 */
	public Sprite getAuthorSprite() {
		return authorSprite;
	}
	
	/**
	 * 
	 * @return: The author's name text
	 * 
	 * 
	 */
	public Text getAuthorNameText() {
		return authorNameText;
	}
	
	/**
	 * 
	 * @param newText: set the author name text to newText
	 * 
	 */
	public void setAuthorNameText(Text newText) {
		authorNameText = newText;
	}

	/**
	 * 
	 * Draw authornametext and author sprite to window
	 * @param window: The window to be drawn to
	 * @param renderStates: The renderstates to be added upon drawing
	 * 
	 */
	public void draw(RenderTarget window, RenderStates renderStates) {
		// TODO Auto-generated method stub
		window.draw(authorNameText);
		window.draw(authorSprite);
		
	}
}

