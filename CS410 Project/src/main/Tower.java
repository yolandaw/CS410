package main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;


//Builds the class tower with name and information of number of floors and number of contributors
//calls the Floor class functions to build internal floors?


public class Tower implements org.jsfml.graphics.Drawable {
        
        private String towerName;
        private int towerHeight;
        private int towerWidth;
        private int towerXPos;
        private int numberOfFloors;
        private int floorHeight;
        private int towerDepth;
        private Vector2i pos;
        String[] arrayOfContributors;
        private LinkedList<Floor> towerFloors = new LinkedList<Floor>();
        private String cityName;
        private Author towerOwner;
        private Map<Author, Integer> ownerList = new HashMap<Author, Integer>(); 
//        private RectangleShape towerSign;
//        private Text towerSignNames;


        
        //constructing a new Tower/Class
        public Tower(String className) {
                setTowerName(className);
                arrayOfContributors = new String[20];
                pos = new Vector2i(0,0);
                towerWidth = 0;
                towerHeight = 0;
                floorHeight = 0;
                towerDepth = 0;
        }
        
        //goes through the lists of existing contributors and adds if is a new contributor 
        public void contributorMovingIn(String authorName){
                int i = 0;
                
                for(i = 0; i <= arrayOfContributors.length;){
                        if(!arrayOfContributors[i].equals(authorName))
                        {
                                i++;
                        }
                        else {
                                break;
                        }
                }
                arrayOfContributors[i] = authorName;
                
        }
        
        /**
    	 * 
    	 * @param newDepth: The new depth of the tower
    	 * 
    	 */
        public void setTowerDepth(int newDepth) {
        	towerDepth = newDepth;
        }
        
        /**
    	 * 
    	 * @param newWidth: The new width of the tower
    	 * 
    	 */
        public void setTowerWidth(int newWidth) {
        	towerWidth = newWidth;
        }
        
        /**
    	 * 
    	 * @param newHeight: The new height of the tower
    	 * 
    	 */
        public void setFloorHeight(int newHeight) {
        	floorHeight = newHeight;
        }
        
        /**
    	 * 
    	 * @param x: The new x position of the tower
    	 * @param y: the new y position of the tower
    	 * 
    	 */
        public void setTowerPosition(int x, int y) {
        	pos = new Vector2i(x, y);
        }
        
        /**
    	 * 
    	 * @return the tower's position
    	 * 
    	 */
        public Vector2i getTowerPosition() {
        	return pos;
        }
        
        /**
    	 * 
    	 * @return the tower's width
    	 * 
    	 */
        public int getTowerWidth() {
        	return towerWidth;
        }
        
        /**
    	 * 
    	 * @return the tower's height
    	 * 
    	 */
        public int getFloorHeight() {
        	return floorHeight;
        }
        
        /**
    	 * 
    	 * @return the tower's depth
    	 * 
    	 */
        public int getTowerDepth() {
        	return towerDepth;
        }

        
        //sets the Tower Name to be drawn
        public void setTowerName(String className){
                towerName = className;                 
        }
        
        //sets the standard floor height of each floor in tower
        public int generateFloorHeight(){
                Random rn = new Random();
                floorHeight = rn.nextInt(5);
                return floorHeight;
        }
        
        //increases the number of floors in the tower
        public void floorCounter(){
                numberOfFloors++;
        }
        
        /**
    	 * 
    	 * @param methodName: new floor to be added to the tower
    	 * 
    	 */    
	    public void addFloor(Floor methodName) {
	            towerFloors.add(methodName);
	    }
	    
	    /**
    	 * 
    	 * @return the list of the tower's floors
    	 * 
    	 */
	    public LinkedList<Floor> getListOfFloor() {
	            return towerFloors;
	    }
	    
	    /**
    	 * 
    	 * @return the tower's name
    	 * 
    	 */
	    public String getTowerName() {
	            return towerName;
	    }
	    
	    /**
    	 * 
    	 * @return the tower's total height, including underground and above ground floors
    	 * 
    	 */
	    public int getTowerHeight() {
	        return towerFloors.size()*floorHeight;
	    }
	    
	    /**
    	 * 
    	 * @return the tower's height only including above ground floors
    	 * 
    	 */
	    public int getTowerHeightAbove(){
	    	return towerHeight;
	    }
    
  //adding tower signs - need to add text alignment
//    public void addSigns(RenderTarget window){
//        Font defaultFont = new Font();
//  
//		try {
//			defaultFont.loadFromFile(FileSystems.getDefault().getPath("resources","arialbd.ttf"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		
//		Texture metalBG = new Texture();
//        try {
//			metalBG.loadFromFile(Paths.get("resources","metal.png"));
//		} catch (IOException e) {
//			System.out.println("Error in package sign sprite load");
//		}		
//		towerSign = new RectangleShape(new Vector2f(towerName.length()*10,30));
//        towerSign.setPosition(towerXPos, pos.y - towerHeight - towerSign.getSize().y - 5);
//        towerSign.setFillColor(towerOwner.getAuthorColor());
//        towerSign.setOutlineThickness(5);
//        towerSign.setOutlineColor(new Color(52,40,44));
//        towerSign.setTexture(metalBG);
//        
//        
//
//        towerSignName = new Text(towerName, defaultFont , 18);
//		towerSignName.setColor(new Color(255,255,255));
//        towerSignName.setPosition(towerXPos, pos.y - towerHeight - towerSign.getSize().y - 5);
//        
//        window.draw(towerSign);
//        window.draw(towerSignName);
//    }
    
    
    //find tower owner
    public void setTowerOwner(){
    	
    	int floorCnt;
    	int maxNumFloor = 0;
  	 	Author tempOwner = new Author("UnknownName","UnknownEmailAddr");
    	
    	for(Floor f: towerFloors){
            	if(ownerList.containsKey(f.getFloorOwner())){
            		floorCnt = ownerList.get(f.getFloorOwner());
            		ownerList.put(f.getFloorOwner(), floorCnt +1);
        		}else{
        			ownerList.put(f.getFloorOwner(), 1);
        		}
          
    	}
    	
    	for(Map.Entry<Author, Integer> entry: ownerList.entrySet()){

    		if(entry.getValue()>maxNumFloor){
    			maxNumFloor = entry.getValue();
    			tempOwner = entry.getKey();
    		}
    	
    	}
    	towerOwner = tempOwner;    	    	
    	System.out.println("Tower Class - Tower Owner: " + towerOwner);

    
    }
    
    //get towner owner
    public Author getTowerOwner(){
    	return towerOwner;
    }
    
    
 // can add other floor updates in here
    public void updateFloors(int split) {
            int below = pos.y + split + floorHeight;
            int above = pos.y;
            int numAbove = 0;
            for (Floor f: towerFloors) {
                    f.updateNumOfLines();
                    f.setFloorDimensions(towerWidth, floorHeight);
                    f.setDepth(towerDepth);
                    
                    switch (f.getAccessType()) {
                    case 0:
                    	f.setFloorPosition(pos.x, above);
                        above = above - floorHeight;
                        numAbove++;
                        break;
                    case 1:
                    	f.setFloorPosition(pos.x, below);
                        below = below + floorHeight;
                        break;
                    }
                    
                    f.setTexture(FileSystems.getDefault().getPath("resources","texture.png"),new IntRect(0, 0, 32, 24));
                    f.splitFloorOwnership();
                    f.setFloorOwnership();
            }
            
            towerHeight = numAbove*floorHeight;
    }

	    /**
		 * 
		 * Draws the tower which subsequently means drawing all of the tower's floors
		 * 
		 * @param window: The window to be drawn to
		 * @param renderStates: The renderstates to be applied when drawn
		 * 
		 */
        public void draw(RenderTarget window, RenderStates renderStates) {
        	LinkedList<Floor> belowFloors = new LinkedList<Floor>();
        		//draw above ground Floors
                for (Floor f: towerFloors) {
                	switch (f.getAccessType()) {
                	case 0:
                        window.draw(f);
                        break;
                	case 1:
                		belowFloors.push(f);
                	}
                }
                                 
                //draw underground Floors
                for (Floor f: belowFloors) {
                	window.draw(f);
                }
                
//                addSigns(window, towerOwner);

        }
    
        /**
    	 * 
    	 * @param packageName: The name of the city that the tower belongs to
    	 * 
    	 */
	    public void setCityName(String packageName) {
	            cityName = packageName;
	    }
	    
	    /**
    	 * 
    	 * @return the name of the city that the tower belongs to
    	 * 
    	 */
	    public String getCityName() {
	            return cityName;
	    }
}
