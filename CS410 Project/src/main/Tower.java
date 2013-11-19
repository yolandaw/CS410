package main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import org.eclipse.jgit.revwalk.DepthWalk;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;


//Builds the class tower with name and information of number of floors and number of contributors
//calls the Floor class functions to build internal floors?


public class Tower implements org.jsfml.graphics.Drawable {
        
        private String towerName;
        private int towerHeight;
        private int towerUpperHeight;
        private int towerWidth;
        private int towerXPos;
        private int numberOfFloors;
        private int floorHeight;
        private int towerDepth;
        private Vector2i pos;
        String[] arrayOfContributors;
        private LinkedList<Floor> towerFloors = new LinkedList<Floor>();
        private String cityName;

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
        
        public void setTowerDepth(int newDepth) {
        	towerDepth = newDepth;
        }
        
        public void setTowerWidth(int newWidth) {
        	towerWidth = newWidth;
        }
        
        public void setFloorHeight(int newHeight) {
        	floorHeight = newHeight;
        }
        
        public void setTowerPosition(int x, int y) {
        	pos = new Vector2i(x, y);
        }
        
        public Vector2i getTowerPosition() {
        	return pos;
        }
        
        public int getTowerWidth() {
        	return towerWidth;
        }
        
        public int getFloorHeight() {
        	return floorHeight;
        }
        
        public int getTowerDepth() {
        	return towerDepth;
        }

        //function that takes location x and y to build from city builder
        
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
        
    public void addFloor(Floor methodName) {
            towerFloors.add(methodName);
    }
    
    public LinkedList<Floor> getListOfFloor() {
            return towerFloors;
    }
    
    public String getTowerName() {
            return towerName;
    }
    
    public int getTowerHeight() {
        return towerFloors.size()*floorHeight;
    }
    
  //adding tower signs
    private void addSigns(RenderTarget window){
        Font defaultFont = new Font();
  
		try {
			defaultFont.loadFromFile(FileSystems.getDefault().getPath("resources","arial.ttf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        //adding tower name
        RectangleShape towerSign = new RectangleShape(new Vector2f(towerWidth,30));
        towerSign.setPosition(towerXPos, towerUpperHeight);

        Text towerSignName = new Text(towerName, defaultFont , 16);
		towerSignName.setColor(new Color(0,0,0));
        towerSignName.setPosition(towerXPos, towerUpperHeight);
        
        window.draw(towerSign);
        window.draw(towerSignName);
    }
    
    
 // can add other floor updates in here
    public void updateFloors(int split, int xPos) {
            int below = pos.y + split + floorHeight;
            int above = pos.y;
            int numAbove = 0;
            towerXPos = xPos;
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
            }
            
            towerHeight = numAbove*floorHeight;
            towerUpperHeight = towerHeight;
    }

        
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
        }
    
    public void setCityName(String packageName) {
            cityName = packageName;
    }
    
    public String getCityName() {
            return cityName;
    }
}