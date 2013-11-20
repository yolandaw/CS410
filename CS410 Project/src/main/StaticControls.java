package main;

/**
 * An all in one place for static variables
 *
 */
public class StaticControls {
	private StaticControls(){
		//so it builds a one floor building and not a empty parcel of land. real estate ain't cheap.
		//it's now an underground bunker, you're all welcome to stay over
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
	
	//if true, program will continue to run after encountering errors
	//else, program will break if exception encountered
	static boolean continueFromExceptions = false;
	
	//if true, background music will load and play
	//else, no music
	static boolean music = true;
	
	//starting scroll speed when using the directional pad
	static double directionScrollStartSpeed = 5;
}
