package main;

/**
 * An all in one place for static variables
 *
 */
public class StaticControls {
	
	StaticControls(){
		//so it builds a one floor building and not a empty parcel of land. real estate ain't cheap.
	}
	
	//if true, floor heights are randomized between 20 to 40
	//else, floor heights are all set to 30
	static boolean floorHeightRandom = false;
	
	//if true, tower widths are randomized
	//else, tower widths are all the same
	static boolean towerWidthRandom = true;
	
	//if true, tower depths are randomized
	//else, tower depths are all the same
	static boolean towerDepthRandom = true;
	
}
