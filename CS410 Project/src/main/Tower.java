package main;

import java.util.LinkedList;
import java.util.Random;



//Builds the class tower with name and information of number of floors and number of contributors
//calls the Floor class functions to build internal floors?


public class Tower {
	
	private String towerName;
	private int towerHeight;
	private int towerWidth;
	private int numberOfFloors;
	private int floorHeight;
	String[] arrayOfContributors;
	private LinkedList<Floor> towerFloors = new LinkedList<Floor>();
	

	//constructing a new Tower/Class
	public Tower(String className) {
		setTowerName(className);
		arrayOfContributors = new String[20];
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
}
