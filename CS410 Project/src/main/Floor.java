package main;


import java.util.Random;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

public class Floor implements org.jsfml.graphics.Drawable {

	private String floorName;
	private VertexArray floorVertices;
	private int numOfLines;
	private int committers; //should be some list of committers?
	private IntRect floorBoundaries;
	
	
	public Floor(String functionName) {
		setFloorName(functionName);
		committers = 5;
		floorBoundaries = new IntRect(0, 0, 0, 0);
		setFloorDimensions(100, 30);
		floorVertices = new VertexArray(PrimitiveType.TRIANGLES);
		setNumOfLines(1000);
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
	
	public void incrementNumOfLines() {
		numOfLines++;
	}
	
	public int getNumOfLines() {
		return numOfLines;
	}
	
	public void setNumOfLines(int newNumOfLines) {
		numOfLines = newNumOfLines;
	}
	
	public String getFloorName() {
		return floorName;
	}
	
	private void setFloorName(String functionName) {
		floorName = functionName;
	}
	
	public void splitFloorOwnership() {
		floorVertices.clear();
		
		while (floorVertices.size()/6 < committers) {
			floorVertices.add(new Vertex(new Vector2f(0, 0),new Color(0,0,0)));
		}
		
		float tempX = floorBoundaries.left;
		Random randInt = new Random();

		float[] test = new float[committers]; // expecting some knowledge of a committers total number of lines contributed or percent?
		test[0] = (float) 250/numOfLines;
		test[1] = (float) 150/numOfLines;
		test[2] = (float) 200/numOfLines;
		test[3] = (float) 100/numOfLines;
		test[4] = (float) 300/numOfLines;
		
		Color[] test2 = new Color[committers]; // expecting each committer to a have a unique color(or texture?) associated with them?
		test2[0] = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
		test2[1] = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
		test2[2] = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
		test2[3] = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
		test2[4] = new Color(randInt.nextInt(255),randInt.nextInt(255),randInt.nextInt(255));
		
		for (int i = 0; i < committers; i++) {
			
			if (tempX+(floorBoundaries.width*test[i]) > floorBoundaries.left+floorBoundaries.width) {
				break;
			}

			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top),test2[i]));
			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top-floorBoundaries.height),test2[i]));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*test[i])), floorBoundaries.top),test2[i]));
			
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*test[i])), floorBoundaries.top),test2[i]));
			floorVertices.add(new Vertex(new Vector2f(tempX, floorBoundaries.top-floorBoundaries.height),test2[i]));
			floorVertices.add(new Vertex(new Vector2f((float) (tempX+(floorBoundaries.width*test[i])), floorBoundaries.top-floorBoundaries.height),test2[i]));
			
			tempX = (float) (tempX+(floorBoundaries.width*test[i]));
		}
		
	}
	
	public void draw(RenderTarget target, RenderStates states) {
		target.draw(floorVertices);
	}
	

}
