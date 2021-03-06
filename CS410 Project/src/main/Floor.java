package main;


import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

public class Floor implements org.jsfml.graphics.Drawable {

        private String floorName;
        private VertexArray floorVertices;
        private VertexArray highlightedFloorVertices;
		private boolean highlighted;
        private int numOfLines;
        private Map<Author, Integer> ownerships = new HashMap<Author, Integer>(); 
        private IntRect floorBoundaries;
        private int depth;
        private Texture texture;
        private IntRect tilePosition;
        // 0 = public, 1 = private, 2 = protected
        private int accessModifierType;
        private Author floorAuthor;
        
        /**
    	 * 
    	 * Sets default values upon construction
    	 * 
    	 * @param functionName: The name of the floor
    	 * 
    	 */
        public Floor(String functionName) {
                setFloorName(functionName);
                floorBoundaries = new IntRect(0, 0, 0, 0);
                floorVertices = new VertexArray(PrimitiveType.QUADS);
                highlightedFloorVertices = new VertexArray(PrimitiveType.QUADS);
                setDepth(0);
                texture = new Texture();
                texture.setRepeated(true);
                tilePosition = new IntRect(0,0,0,0);
        }
        
        /**
    	 * 
    	 * @param left, top, width, height: The rectangular dimensions of the floor
    	 * 
    	 */
        public void setFloorBoundaries(int left, int top, int width, int height) {
                floorBoundaries = new IntRect(left, top, width, height);
        }
        
        /**
    	 * 
    	 * @param left, top: The new position of the floor
    	 * 
    	 */
        public void setFloorPosition(int left, int top) {
                floorBoundaries = new IntRect(left, top, floorBoundaries.width, floorBoundaries.height);
        }
        
        /**
    	 * 
    	 * @param width, height: The new width and height of the floor
    	 * 
    	 */
        public void setFloorDimensions(int width, int height) {
                floorBoundaries = new IntRect(floorBoundaries.left, floorBoundaries.top, width, height);
        }
        
        /**
    	 * 
    	 * @return the floor's boundaries
    	 * 
    	 */
        public IntRect getFloorBoundaries() {
                return floorBoundaries;
        }
        
        /**
    	 * 
    	 * @return the number of owners of the floor
    	 * 
    	 */
        public void getNumOfOwners() {
                ownerships.size();
        }
        
        /**
    	 * 
    	 * @return the total number of lines of the floor (code wise)
    	 * 
    	 */
        public int getNumOfLines() {
                return numOfLines;
        }
        
        /**
    	 * 
    	 * Updates the total number of lines of the floor based on how many lines each author owns
    	 * 
    	 */
        public void updateNumOfLines() {
                for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
                        numOfLines = numOfLines + entry.getValue();
                }
        }
        
        /**
    	 * 
    	 * @return the floor's name
    	 * 
    	 */
        public String getFloorName() {
                return floorName;
        }
        
        /**
    	 * 
    	 * @return the ownerships map
    	 * 
    	 */
        public Map<Author,Integer> getOwnerships() {
        	return ownerships;
        }
        
        /**
    	 * 
    	 * @param functionName: the floors new name
    	 * 
    	 */
        private void setFloorName(String functionName) {
                floorName = functionName;
        }
        
        /**
    	 * 
    	 * @param newDepth: The new depth of the floor
    	 * 
    	 */
        public void setDepth(int newDepth) {
                depth = newDepth;
        }
        
        /**
    	 * 
    	 * @param author: new author to be added
    	 * @param size: new number of lines for the author
    	 * 
    	 */
        public void adjustOwnership(Author author, int size) {
                ownerships.put(author, size);
        }
        
        /**
    	 * 
    	 * @param path: the file path of the texture
    	 * @param position: the position of the tile in the texture
    	 * 
    	 */
        public void setTexture(Path path, IntRect position) {
                try {
                        texture.loadFromFile(path,position);
                        setTilePosition(position);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        
        /**
    	 * 
    	 * @param position: the new position of the tile in the texture
    	 * 
    	 */
        private void setTilePosition(IntRect position) {
                tilePosition = position;
        }
        
        /**
    	 * 
    	 * Increment ownership for an author by 1
    	 * 
    	 * @param author: the author which is getting an increase in ownership
    	 * 
    	 */
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
        
        /**
    	 * 
    	 * Set's whether a floor is drawn as highlighted or not
    	 * @param isHighlighted: Set's whether the floor is highlighted or not
    	 * 
    	 */
        public void setHighlighted(boolean isHighlighted) {
			highlighted = isHighlighted;
		}
	
        /**
    	 * 
    	 * @return whether a floor is highlighted or not
    	 * 
    	 */
		public boolean getHighlighted() {
			return highlighted;
		}
	
		/**
    	 * 
    	 * Create the highlighted version of the floor
    	 * 
    	 */
		public void highlightFloor() {
			for (int i = 0; i<floorVertices.size(); i++) {
				Vertex vertex = floorVertices.get(i);
				Color brighter = vertex.color;
				brighter = new Color((int) (brighter.r*1.3),(int) (brighter.g*1.3),(int) (brighter.b*1.3));
				vertex = new Vertex(vertex.position, brighter, vertex.texCoords);
				highlightedFloorVertices.add(vertex);
			}
		}
        
		/**
    	 * 
    	 * Creates the floor drawable and splits it up baed on authors and ownership sizes
    	 * 
    	 */
        public void splitFloorOwnership() {
                floorVertices.clear();
                
                float tempX = floorBoundaries.left;
                Color authorColor = new Color(0,0,0);
                Vector2f topLeft = new Vector2f(0, 0);
                Vector2f topRight = new Vector2f(0, 0);
                Vector2f bottomRight = new Vector2f(0, 0);
                Vector2f bottomLeft = new Vector2f(0, 0);
                Vector2f textCoord = calculateTextureCoords();
                
                for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
                        float percentOwnership = (float) entry.getValue()/numOfLines;
                        authorColor = entry.getKey().getAuthorColor();
                        
                        if ((int) tempX+(floorBoundaries.width*percentOwnership) > floorBoundaries.left+floorBoundaries.width) {
                                break;
                        }
                        
                        // texture coordinates
                        topLeft = new Vector2f(0, 0);
                        topRight = new Vector2f(textCoord.x*percentOwnership, 0);
                        bottomRight = new Vector2f(textCoord.x*percentOwnership, textCoord.y);
                        bottomLeft = new Vector2f(0, textCoord.y);

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
                
                // create highlighted version of floor, move to Tower updateFloors method
				highlightFloor();
        }
        
        //sets the ownership of the floor  
        public void setFloorOwnership(){

        	Author newOwner = null;
        	int newMax=0;
        	
        	for(Map.Entry<Author, Integer> entry: ownerships.entrySet()){
        		if(entry.getValue()>newMax){
        			newOwner = entry.getKey();
        			newMax = entry.getValue();
        		}
        	}
        
        	floorAuthor = newOwner;
        }
        
        //returns owner of the floor
        public Author getFloorOwner(){
        	return floorAuthor;
        }
        
        /**
    	 * 
    	 * @param authorColor: The author's color that is associated with this section of the floor
    	 * @param leftSideX: The left side of the front portion of the floor
    	 * @param widthPercent: The percentage of the total width of the floor that this section takes up
    	 * 
    	 */
        private void addTopOfFloor(Color authorColor, float leftSideX, float widthPercent) {
                float lighten = (float) 1.3;
                Color lighterColor = new Color((int) (authorColor.r * lighten),(int) (authorColor.g * lighten),(int) (authorColor.b * lighten), 255);
                
                Vector2f textCoord = calculateTextureCoords();
                
                Vector2f topLeft = new Vector2f(0, 0);
                Vector2f topRight = new Vector2f(textCoord.x*widthPercent, 0);
                Vector2f bottomRight = new Vector2f(textCoord.x*widthPercent, depth);
                Vector2f bottomLeft = new Vector2f(0, depth);
                
                floorVertices.add(new Vertex(new Vector2f(leftSideX, floorBoundaries.top - floorBoundaries.height),lighterColor,bottomLeft));
                floorVertices.add(new Vertex(new Vector2f(leftSideX + depth, floorBoundaries.top - floorBoundaries.height - depth),lighterColor,topLeft));
                floorVertices.add(new Vertex(new Vector2f((float) (leftSideX+(floorBoundaries.width*widthPercent)) + depth, floorBoundaries.top-floorBoundaries.height - depth),lighterColor,topRight));
                floorVertices.add(new Vertex(new Vector2f((float) (leftSideX+(floorBoundaries.width*widthPercent)), floorBoundaries.top - floorBoundaries.height),lighterColor,bottomRight));
        }
        
        /**
    	 * 
    	 * @param authorColor: The author's color that is associated with this section of the floor
    	 * @param rightSideX: The right side of the front portion of the floor
    	 * 
    	 */
        private void addSideOfFloor(Color authorColor, float rightSideX) {
                float darken = (float) 0.8;
                Color darkerColor = new Color((int) (authorColor.r * darken),(int) (authorColor.g * darken),(int) (authorColor.b * darken), 255);
                
                Vector2f textCoord = calculateTextureCoords();
                
                Vector2f topLeft = new Vector2f(0, 0);
                Vector2f topRight = new Vector2f(depth, 0);
                Vector2f bottomRight = new Vector2f(depth, textCoord.y);
                Vector2f bottomLeft = new Vector2f(0, textCoord.y);
                
                floorVertices.add(new Vertex(new Vector2f(rightSideX, floorBoundaries.top),darkerColor,bottomLeft));
                floorVertices.add(new Vertex(new Vector2f(rightSideX, floorBoundaries.top - floorBoundaries.height),darkerColor,topLeft));
                floorVertices.add(new Vertex(new Vector2f(rightSideX + depth, floorBoundaries.top - floorBoundaries.height - depth),darkerColor,topRight));
                floorVertices.add(new Vertex(new Vector2f(rightSideX + depth, floorBoundaries.top - depth),darkerColor,bottomRight));
        }
        
        /**
    	 * 
    	 * @return the texture coordinates based on the floor's overall size
    	 * 
    	 */
        private Vector2f calculateTextureCoords() {
                float xMapping = 0;
                float yMapping = 0;
                
                if (tilePosition.width != 0 && tilePosition.height != 0) {
                        xMapping = Math.round(floorBoundaries.width/tilePosition.width) * tilePosition.width;
                        yMapping = Math.round(floorBoundaries.height/tilePosition.height) * tilePosition.height;
                        
                        if (xMapping == 0) {
							xMapping = tilePosition.width;
						}
			
						if (yMapping == 0) {
							yMapping = tilePosition.height;
						}
                }
                
                return new Vector2f(xMapping, yMapping);
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
        
        /**
    	 * 
    	 * Draw floor to the window based on whether it is highlighted or not
    	 * @param window: The window to be drawn to
    	 * @param renderStates: The renderstates to be added upon drawing
    	 * 
    	 */
        public void draw(RenderTarget target, RenderStates states) {
                RenderStates state = new RenderStates(states, texture);
				if (highlighted) {
					target.draw(highlightedFloorVertices,state);
				} else {
					target.draw(floorVertices,state);
				}
        }
        
        /**
    	 * 
    	 * @param accessType: set floors access type, private(1) or public(0)
    	 * 
    	 */
        public void setAccessType(int accessType) {
                accessModifierType = accessType;
        }
        
        /**
    	 * 
    	 * @return the floors access type
    	 * 
    	 */
        public int getAccessType() {
                return accessModifierType;
        }
        

}