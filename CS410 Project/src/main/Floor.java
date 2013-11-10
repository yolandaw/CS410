package main;


import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

public class Floor implements org.jsfml.graphics.Drawable {

	private String floorName;
	private VertexArray floorVertices;
	private int numOfLines;
	private Map<Author, Integer> ownerships = new HashMap<Author, Integer>(); 
	private IntRect floorBoundaries;
	private int depth;
	private Texture texture;
	private IntRect tilePosition;
	
	public Floor(String functionName) {
		setFloorName(functionName);
		floorBoundaries = new IntRect(0, 0, 0, 0);
		setFloorDimensions(100, 30);
		floorVertices = new VertexArray(PrimitiveType.QUADS);
		setDepth(15);
		texture = new Texture();
		texture.setRepeated(true);
		tilePosition = new IntRect(0,0,0,0);
	}
	
	public void setFloorBoundaries(int left, int top, int width, int height) {
		floorBoundaries = new IntRect(left, top, width, height);
	}
	
	public void setFloorPosition(int left, int top) {
		floorBoundaries = new IntRect(left, top, floorBoundaries.width, floorBoundaries.height);
	}
	
	public void setFloorDimensions(int width, int height) {
		floorBoundaries = new IntRect(floorBoundaries.left, floorBoundaries.top, width, height);
	}
	
	public IntRect getFloorBoundaries() {
		return floorBoundaries;
	}
	
	public void getNumOfOwners() {
		ownerships.size();
	}
	
	public int getNumOfLines() {
		return numOfLines;
	}
	
	public void updateNumOfLines() {
		for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			numOfLines = numOfLines + entry.getValue();
		}
	}
	
	public String getFloorName() {
		return floorName;
	}
	
	private void setFloorName(String functionName) {
		floorName = functionName;
	}
	
	private void setDepth(int newDepth) {
		depth = newDepth;
	}
	
	public void adjustOwnership(Author author, int size) {
		ownerships.put(author, size);
	}
	
	public void setTexture(Path path, IntRect position) {
		try {
			texture.loadFromFile(path,position);
			setTilePosition(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setTilePosition(IntRect position) {
		tilePosition = position;
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
	
	public void splitFloorOwnership() {
		floorVertices.clear();
		
		float tempX = floorBoundaries.left;
		Color authorColor = new Color(0,0,0);
		Vector2f topLeft = new Vector2f(0, 0);
		Vector2f topRight = new Vector2f(0, 0);
		Vector2f bottomRight = new Vector2f(0, 0);
		Vector2f bottomLeft = new Vector2f(0, 0);
		float xMapping = 0;
		float yMapping = 0;
		
		if (tilePosition.width != 0 && tilePosition.height != 0) {
			xMapping = Math.round(floorBoundaries.width/tilePosition.width) * tilePosition.width;
			yMapping = Math.round(floorBoundaries.height/tilePosition.height) * tilePosition.height;
		}
		
		for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			float percentOwnership = (float) entry.getValue()/numOfLines;
			authorColor = entry.getKey().getAuthorColor();
			
			if ((int) tempX+(floorBoundaries.width*percentOwnership) > floorBoundaries.left+floorBoundaries.width) {
				break;
			}
			
			// texture coordinates
			topLeft = new Vector2f(0, 0);
			topRight = new Vector2f(xMapping*percentOwnership, 0);
			bottomRight = new Vector2f(xMapping*percentOwnership, yMapping);
			bottomLeft = new Vector2f(0, yMapping);

			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top),authorColor,bottomLeft));
			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top-floorBoundaries.height),authorColor,topLeft));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*percentOwnership)), floorBoundaries.top-floorBoundaries.height),authorColor,topRight));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*percentOwnership)), floorBoundaries.top),authorColor,bottomRight));
			
			// top of floor
			addTopOfFloor(authorColor, tempX, percentOwnership);
			
			tempX = (float) (tempX+(floorBoundaries.width*percentOwnership));
		}
		
		// side of floor
		addSideOfFloor(authorColor, tempX);
	}
	
	private void addTopOfFloor(Color authorColor, float leftSideX, float widthPercent) {
		float lighten = (float) 1.2;
		Color lighterColor = new Color((int) (authorColor.r * lighten),(int) (authorColor.g * lighten),(int) (authorColor.b * lighten), 255);
		
		floorVertices.add(new Vertex(new Vector2f(leftSideX, floorBoundaries.top - floorBoundaries.height),lighterColor));
		floorVertices.add(new Vertex(new Vector2f(leftSideX + depth, floorBoundaries.top - floorBoundaries.height - depth),lighterColor));
		floorVertices.add(new Vertex(new Vector2f((float) (leftSideX+(floorBoundaries.width*widthPercent)) + depth, floorBoundaries.top-floorBoundaries.height - depth),lighterColor));
		floorVertices.add(new Vertex(new Vector2f((float) (leftSideX+(floorBoundaries.width*widthPercent)), floorBoundaries.top - floorBoundaries.height),lighterColor));
	}
	
	private void addSideOfFloor(Color authorColor, float rightSideX) {
		float darken = (float) 0.8;
		Color darkerColor = new Color((int) (authorColor.r * darken),(int) (authorColor.g * darken),(int) (authorColor.b * darken), 255);
		
		floorVertices.add(new Vertex(new Vector2f(rightSideX, floorBoundaries.top),darkerColor));
		floorVertices.add(new Vertex(new Vector2f(rightSideX, floorBoundaries.top - floorBoundaries.height),darkerColor));
		floorVertices.add(new Vertex(new Vector2f(rightSideX + depth, floorBoundaries.top - floorBoundaries.height - depth),darkerColor));
		floorVertices.add(new Vertex(new Vector2f(rightSideX + depth, floorBoundaries.top - depth),darkerColor));
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
	
	
	public void draw(RenderTarget target, RenderStates states) {
		RenderStates state = new RenderStates(states, texture);
		target.draw(floorVertices,state);
	}
	

}
