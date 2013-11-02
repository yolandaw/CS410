package main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

public class Floor implements org.jsfml.graphics.Drawable {

	private String floorName;
	private VertexArray floorVertices;
	private int numOfLines;
	private Map<Author, Integer> ownerships = new HashMap<Author, Integer>(); 
	private IntRect floorBoundaries;
	
	
	public Floor(String functionName) {
		setFloorName(functionName);
		floorBoundaries = new IntRect(0, 0, 0, 0);
		setFloorDimensions(100, 30);
		floorVertices = new VertexArray(PrimitiveType.TRIANGLES);
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
	
	public void splitFloorOwnership() {
		floorVertices.clear();
		
		float tempX = floorBoundaries.left;

		for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			float percentOwnership = (float) entry.getValue()/numOfLines;
			Color authorColor = entry.getKey().getAuthorColor();
			
			if (tempX+(floorBoundaries.width*percentOwnership) > floorBoundaries.left+floorBoundaries.width) {
				break;
			}

			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top),authorColor));
			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top-floorBoundaries.height),authorColor));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*percentOwnership)), floorBoundaries.top),authorColor));
			
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*percentOwnership)), floorBoundaries.top),authorColor));
			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top-floorBoundaries.height),authorColor));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*percentOwnership)), floorBoundaries.top-floorBoundaries.height),authorColor));
			
			tempX = (float) (tempX+(floorBoundaries.width*percentOwnership));
		}
		
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
		target.draw(floorVertices);
	}
	

}
