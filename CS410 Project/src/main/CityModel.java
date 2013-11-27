package main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class CityModel {
	
	LinkedList<Tower> towers;
	RenderWindow window;
	View currentView;
	View legendView;
	RectangleShape legendBar;
	Floor currentFloorDetails;
	RectangleShape floorDetailsMenu;
	IntRect worldDimensions;
	VertexArray ground;
	VertexArray grassTop;
	VertexArray grassMid;
	VertexArray sky;
	Font defaultFont;
	int grassMidHeight;
	int cityDistance;
	LinkedList<Sprite> packageSigns = new LinkedList<Sprite>();
	LinkedList<Text> packageTexts;
	LinkedList<Text> ownerTexts;
	LinkedList<Author> packageOwners;
	LinkedList<RectangleShape> towerSigns = new LinkedList<RectangleShape>();
	LinkedList<Text> signNames = new LinkedList<Text>();
	Map<String, Author> allAuthors;

	/**
	 * 
	 * Sets default values upon construction
	 * 
	 * @param newWindow: the window that this city model will be associated with
	 * 
	 */
	CityModel(RenderWindow newWindow) {
		setWindow(newWindow);
		setCurrentView(newWindow.getDefaultView());
		towers = new LinkedList<Tower>();
		packageTexts = new LinkedList<Text>();
		ownerTexts = new LinkedList<Text>();
		packageOwners = new LinkedList<Author>();
		allAuthors = new HashMap<String, Author>();
		setWorldDimensions(0, 0, 0, 0);
		grassMidHeight = 30;
		currentFloorDetails = null;
		setUpFloorDetailsMenu();
		cityDistance = 400;
		currentView = new View();
	}
	
	/**
	 * 
	 * Initializes the author color legend view
	 * 
	 */
	private void setupLegendView(float maxLength) {
		View newView = new View(window.getDefaultView().getCenter(), window.getDefaultView().getSize());
		legendView = newView;
		
		legendBar = new RectangleShape();
		legendBar.setSize(new Vector2f(maxLength, legendView.getSize().y));
		legendBar.setPosition(0,0);
		legendBar.setOutlineThickness(1);
		legendBar.setFillColor(new Color(240, 240, 240, 100));
	}
	
	/**
	 * 
	 * @return the view for the author color legend
	 * 
	 */
	public View getLegendView() {
		return legendView;
	}
	
	/**
	 * 
	 * Sets the towers of the city model and sets up any other static drawables for the city model
	 * @param newTowers: The towers of the city model
	 * 
	 */
	public void setTowers(LinkedList<Tower> newTowers) {
		int x = 400;
		int x2;
		towers = newTowers;
		String currentPackage = newTowers.getFirst().getCityName();
		if(currentPackage.contains(".")){
			currentPackage = currentPackage.substring(currentPackage.lastIndexOf(".")+1, currentPackage.length());
		}
		
		calculateWorldDimensions();
		
		//quinnTest
		Texture image = new Texture();
        try {
			image.loadFromFile(Paths.get("resources","billboard.png"));
		} catch (IOException e) {
			System.out.println("Error in package sign sprite load");
		}
		Sprite firstSign = new Sprite(image);
		firstSign.setPosition(x - 3, worldDimensions.height/2 - firstSign.getGlobalBounds().height - 3);
		packageSigns.add(firstSign);
		//quinnTest end
		
		Text firstPackageText = new Text(currentPackage, defaultFont, 16);
		firstPackageText.setStyle(Text.BOLD|Text.UNDERLINED);
		firstPackageText.setColor(new Color(0,0,0));
		firstPackageText.setPosition(firstSign.getPosition().x + firstSign.getGlobalBounds().width/2 - firstPackageText.getLocalBounds().width/2, firstSign.getPosition().y + 30);
		packageTexts.add(firstPackageText);
		
		LinkedList<Tower> packageTowers = new LinkedList<Tower>();
		
		x = x + (int) firstSign.getGlobalBounds().width;
		
        
        Font defaultFont = new Font();
        
		try {
			defaultFont.loadFromFile(FileSystems.getDefault().getPath("resources","arialbd.ttf"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		Texture metalBG = new Texture();
        try {
			metalBG.loadFromFile(Paths.get("resources","metal.png"));
		} catch (IOException e) {
			System.out.println("Error in package sign sprite load");
		}		
        
        RectangleShape towerSign;
        Text signName;
		
		for (Tower t: towers) {
			String compareName = t.getCityName();
			if(compareName.contains(".")){
				compareName = compareName.substring(compareName.lastIndexOf(".")+1, compareName.length());
			}
			
			if (!compareName.equals(currentPackage)) {
				currentPackage = t.getCityName();
				if(currentPackage.contains(".")){
					currentPackage = currentPackage.substring(currentPackage.lastIndexOf(".")+1, currentPackage.length());
				}
				
				//quinnTest
				Sprite newSign = new Sprite(image);
				newSign.setPosition(x+cityDistance-newSign.getGlobalBounds().width - 3, worldDimensions.height/2 - newSign.getGlobalBounds().height - 3);
				packageSigns.add(newSign);
				//quinnTest end
				
				Text newPackageText = new Text(currentPackage, defaultFont, 16);
				newPackageText.setColor(new Color(0,0,0));
				newPackageText.setStyle(Text.BOLD|Text.UNDERLINED);
				newPackageText.setPosition(newSign.getPosition().x + newSign.getGlobalBounds().width/2 - newPackageText.getLocalBounds().width/2, newSign.getPosition().y + 35);
				packageTexts.add(newPackageText);
				
				x = x + cityDistance;
				Author owner = findPackageOwner(packageTowers);
				
				packageOwners.add(owner);
		        
				packageTowers.clear();

			}
			
			packageTowers.push(t);
			
			//hack to see all towers
			t.setTowerPosition(x, worldDimensions.height/2);
			x2 = x;
			x=x+t.getTowerWidth()+t.getTowerDepth();
			//end hack to see all towers
			
			
			t.updateFloors(grassMidHeight);
			t.setTowerOwner();
			
			signName  = new Text(t.getTowerName(), defaultFont , 18);
	        signName.setColor(new Color(255,255,255));
	        signName.setPosition(x2 + t.getTowerWidth()/2 - signName.getLocalBounds().width/2, worldDimensions.height/2 -  (t.getTowerHeightAbove() + 30 + 5));   
	        
	        signNames.add(signName);

			towerSign = new RectangleShape(new Vector2f(signName.getLocalBounds().width,30));
	        towerSign.setPosition(signName.getPosition().x,worldDimensions.height/2 -  (t.getTowerHeightAbove() + towerSign.getSize().y + 5));
	        towerSign.setFillColor(t.getTowerOwner().getAuthorColor());
	        towerSign.setOutlineThickness(5);
	        towerSign.setOutlineColor(new Color(52,40,44));
	        towerSign.setTexture(metalBG);
	        
	        towerSigns.add(towerSign);
		}
		
		Author lastOwner = findPackageOwner(packageTowers);
		packageOwners.add(lastOwner);
		
		addPackageSignText();
		
		setUpLegend();
		
		createGround();
		createGrassTop();
		createGrassMid();
		createSky();
	}
	
	/**
	 * 
	 * initializes the author color legend
	 * 
	 */
	private void setUpLegend() {
		Texture authorImage = new Texture();
        try {
        	authorImage.loadFromFile(Paths.get("resources","author.png"));
		} catch (IOException e) {
			System.out.println("Error in package sign sprite load");
		}
        
        float maxLength = 0;
		int authorYPosition = 10;
        for (Map.Entry<String, Author> entry: allAuthors.entrySet()) {
        	Author author = entry.getValue();
        	Text authorNameText = new Text(author.getAuthorName(), defaultFont, 12);
        	Sprite authorSprite = author.getAuthorSprite();
        	authorSprite.setPosition(10, authorYPosition);
        	authorSprite.setColor(author.getAuthorColor());
        	authorSprite.setTexture(authorImage);
        	authorNameText.setPosition(authorSprite.getPosition().x, authorSprite.getPosition().y + authorNameText.getLocalBounds().height + 3);
        	authorNameText.setColor(author.getAuthorColor());
        	author.setAuthorNameText(authorNameText);
        	authorYPosition=(int) (authorYPosition+authorSprite.getLocalBounds().height + authorNameText.getLocalBounds().height + 5);
        	
        	if (author.getAuthorNameText().getLocalBounds().width > maxLength) {
        		maxLength = author.getAuthorNameText().getLocalBounds().width;
        	}
        }   

        
     	
        
        setupLegendView(maxLength + 15);
	}
	
	public void setAllAuthors(Map<String,Author> newAuthors) {
		allAuthors = newAuthors;
	}
	
	/**
	 * 
	 * Stores text drawables for package signs for every package in the city model
	 * 
	 */
	private void addPackageSignText() {
		int i = 0;
		for (Author a: packageOwners) {
			Sprite packageSign = packageSigns.get(i);
			Text mayorText;
			if(a==null){
				mayorText = new Text("Mayor Unknown", defaultFont, 20);
				mayorText.setColor(new Color(220,220,220));
			}
			else{
				mayorText = new Text("Mayor " + a.getAuthorName(), defaultFont, 20);
				mayorText.setColor(a.getAuthorColor());
			}
			mayorText.setPosition(packageSign.getPosition().x + packageSign.getGlobalBounds().width/2 - mayorText.getLocalBounds().width/2, packageSign.getPosition().y + 85);
			ownerTexts.add(mayorText);
			i++;
		}
	}
	
	/**
	 * 
	 * Finds the author with the most owned towers in a package
	 * 
	 * @param towers: the towers of a package
	 * 
	 */
	private Author findPackageOwner(LinkedList<Tower> towers) {
		
		Author owner = new Author("Ducky", "Ducky");
		Map<Author, Integer> ownerships = new HashMap<Author, Integer>();
		int mostTowers = 0;
		
		for (Tower t: towers) {
			owner = t.getTowerOwner();
			if (ownerships.containsKey(owner)) {
				int value = ownerships.get(owner) + 1;
				ownerships.put(owner, value);
			} else {
				ownerships.put(owner, 1);
			}
		}
		
		 for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			System.out.println(entry);
			if (entry.getValue() > mostTowers) {
				mostTowers = entry.getValue();
				owner = entry.getKey();
			}
		 }
		
		//System.out.println( owner.getAuthorName() + " is mayor of " + towers.peek().getCityName());
		return owner;
	}
	
	/**
	 * 
	 * determine and set the world dimensions based on tower sizes, city distance and package sign sizes
	 * 
	 */
	private void calculateWorldDimensions() {
		int tallestTower = window.getSize().y*2;
		int totalWidth = 400;
		String currentPackage = towers.getFirst().getCityName();
		
		for (Tower t: towers) {
			
			setRandomTowerSize(t);
			
			if (t.getTowerHeight() * 3 > tallestTower) {
				tallestTower = t.getTowerHeight() * 3;
			}
			
			totalWidth = totalWidth + t.getTowerWidth();
			
			if (!t.getCityName().equals(currentPackage)) {				
				totalWidth = totalWidth + cityDistance + 500;
				currentPackage = t.getCityName();
			}
		}
		
		if (totalWidth < window.getSize().x*2) {
			totalWidth = window.getSize().x*2;
		} else {
			totalWidth = totalWidth + window.getSize().x;
		}
		
		setWorldDimensions(0, 0, totalWidth, tallestTower);
		currentView.setCenter(worldDimensions.width/2, worldDimensions.height/2);
		currentView.setSize(window.getSize().x*2, window.getSize().y*2);
		
	}
	
	/**
	 * 
	 * set a tower's size to random values
	 * 
	 */
	private void setRandomTowerSize(Tower t) {
		Random randInt = new Random();
		int randomWidth;
		int randomDepth;
		int randomHeight;
		
		randomWidth = randInt.nextInt(100) + 50;
		t.setTowerWidth(randomWidth);
		randomDepth = randInt.nextInt(5) + 15;
		t.setTowerDepth(randomDepth);
		randomHeight = randInt.nextInt(20) + 20;
		t.setFloorHeight(randomHeight);
		
		if(StaticControls.floorHeightRandom == false){
            t.setFloorHeight(30);        //unrandomize height. all height = 30
	    }
	    if(StaticControls.towerWidthRandom == false){
	            t.setTowerWidth(125);        //unrandomize width. all width = 125
	    }
	    if(StaticControls.towerDepthRandom == false){
	            t.setTowerDepth(15);        //unrandomize depth. all depth = 15
	    }
	}
	
	/**
	 * 
	 * return the towers of this city model
	 * 
	 */
	public LinkedList<Tower> getTowers() {
		return towers;
	}
	
	/**
	 * 
	 * initialize the floor details menu
	 * 
	 */
	private void setUpFloorDetailsMenu() {
		floorDetailsMenu = new RectangleShape();
		floorDetailsMenu.setFillColor(new Color(255,255,255,200));
		floorDetailsMenu.setOutlineThickness(5);
		defaultFont = new Font();
		try {
			defaultFont.loadFromFile(FileSystems.getDefault().getPath("resources","arial.ttf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param newWindow: the new window that this city model is associated with
	 * 
	 */
	private void setWindow(RenderWindow newWindow) {
		window = newWindow;
	}
	
	/**
	 * 
	 * @return the current window this city model is associated with
	 * 
	 */
	public RenderWindow getWindow() {
		return window;
	}
	
	/**
	 * 
	 * @return the current world dimensions
	 * 
	 */
	public IntRect getWorldDimensions() {
		return worldDimensions;
	}
	
	/**
	 * 
	 * @param left,top,width,height: the new world dimensions
	 * 
	 */
	public void setWorldDimensions(int left, int top, int width, int height) {
		worldDimensions = new IntRect(left, top, width, height);
	}
	
	/**
	 * 
	 * @param constView: The new view for the model
	 * 
	 */
	public void setCurrentView(ConstView constView) {
		currentView = (View) constView;
	}
	
	/**
	 * 
	 * Sets the window's view to the models current view
	 * 
	 */
	public void updateDisplayedView() {
		window.setView(currentView);
	}
	
	/**
	 * 
	 * @return the current view for the model
	 * 
	 */
	public View getCurrentView() {
		return currentView;
	}
	
	/**
	 * 
	 * @param newFloor: the new floor that is hovered over
	 * 
	 */
	public void setCurrentFloorDetails(Floor newFloor) {
		currentFloorDetails = newFloor;
	}
	
	/**
	 * 
	 * @return currentFloorDetails, the current floor that is being hovered over
	 * 
	 */
	public Floor getCurrentFloorDetails() {
		return currentFloorDetails;
	}
	
	/**
	 * 
	 * Set the floor details menu position
	 * @param x: The new x position
	 * @param y: The new y position
	 * 
	 */
	public void setPosFloorDetailsMenu(float x, float y) {
		floorDetailsMenu.setPosition(x, y);
	}
	
	/**
	 * 
	 * Determines the author with the longest name, or the floor name if it is the longest
	 * 
	 * @return the length of the longest name
	 * 
	 */
	private int findMaxStringLength() {
		Map<Author, Integer> ownerships = currentFloorDetails.getOwnerships();
		
		int maxLength = currentFloorDetails.getFloorName().length();
		int authorNameLength = 0;
		for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			authorNameLength = entry.getKey().getAuthorName().length();
			if (maxLength < authorNameLength) {
				maxLength = authorNameLength;
			}
		}
		
		return maxLength;
	}
	
	/**
	 * 
	 * Draw the floor details menu for currentFloorDetails (currently set floor)
	 * 
	 */
	private void drawFloorDetailsMenu() {
		//draw to window's default view
		window.setView(window.getDefaultView());
		
		Map<Author, Integer> ownerships = currentFloorDetails.getOwnerships();
		int numOfFloorAuthors = ownerships.size();
		Vector2f menuPos = floorDetailsMenu.getPosition();
		float menuXSize = 12;
		float menuYRowSize = 20;
		
		int maxLength = findMaxStringLength();
		
		floorDetailsMenu.setSize(new Vector2f(menuXSize * maxLength, menuYRowSize*(numOfFloorAuthors+1)));
		window.draw(floorDetailsMenu);
		
		VertexArray separator = new VertexArray(PrimitiveType.LINES);
		separator.add(new Vertex(new Vector2f(menuPos.x, menuPos.y + menuYRowSize)));
		separator.add(new Vertex(new Vector2f(menuPos.x + floorDetailsMenu.getSize().x, menuPos.y + menuYRowSize)));
		
		Text text = new Text();
		text.setFont(defaultFont);
		text.setCharacterSize(15);
		text.setColor(new Color(0,0,0));
		
		text.setStyle(2);
		text.setString(currentFloorDetails.getFloorName());
		text.setPosition(menuPos.x, menuPos.y);
		window.draw(text);
		
		text.setStyle(0);
		text.setCharacterSize(10);
		int i = 0;
		for (Map.Entry<Author, Integer> entry: ownerships.entrySet()) {
			separator.add(new Vertex(new Vector2f(menuPos.x, menuPos.y + (menuYRowSize * (i+1)))));
			separator.add(new Vertex(new Vector2f(menuPos.x + floorDetailsMenu.getSize().x, menuPos.y + (menuYRowSize * (i+1)))));
			text.setString(entry.getKey().getAuthorName());
			text.setPosition(menuPos.x, menuPos.y + (menuYRowSize * (i+1)));
			window.draw(text);
			text.setString(entry.getValue().toString());
			text.setPosition(menuPos.x + ((menuXSize * maxLength)/2), menuPos.y + (menuYRowSize * (i+1)));
			window.draw(text);
			i++;
			}
		
		window.draw(separator);
		window.setView(currentView);
	}
	
	/**
	 * 
	 * Initializes the ground based on world dimensions
	 * 
	 */
	private void createGround() {
		ground = new VertexArray(PrimitiveType.QUADS);
		Color lightGroundColor = new Color(166, 152, 110, 255);
		Color darkGroundColor = new Color(50, 39, 6, 255);
		
		ground.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top + worldDimensions.height), darkGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top + worldDimensions.height), darkGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), lightGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), lightGroundColor));
	}
	
	/**
	 * 
	 * Initializes the top of the grass based on world dimensions
	 * 
	 */
	private void createGrassTop() {
		grassTop = new VertexArray(PrimitiveType.QUADS);
		Color grassTopColor = new Color(106, 190, 137, 255);
		
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 - grassMidHeight), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 - grassMidHeight), grassTopColor));
	}
	
	/**
	 * 
	 * Initializes the front of the grass based on world dimensions
	 * 
	 */
	private void createGrassMid() {
		grassMid = new VertexArray(PrimitiveType.QUADS);
		Color grassMidColor = new Color(81, 158, 110, 255);
		
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 + grassMidHeight), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 + grassMidHeight), grassMidColor));
	}
	
	/**
	 * 
	 * Initializes the sky drawable based on world dimensions
	 * 
	 */
	private void createSky() {
		sky = new VertexArray(PrimitiveType.QUADS);
		Color lightSkyColor = Color.WHITE;
		Color darkSkyColor = new Color(87,188,244);
		
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top + worldDimensions.height/2), lightSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top + worldDimensions.height/2), lightSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top), darkSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top), darkSkyColor));
	}

	/**
	 * 
	 * Draw the author color legend
	 * 
	 */
	private void drawLegend() {
		window.setView(window.getDefaultView());
		window.draw(legendBar);
		
		int lastAuthor = allAuthors.size() - 1;
		int i = 0;
		window.setView(legendView);
		for (Map.Entry<String, Author> entry: allAuthors.entrySet()) {
			Author author = entry.getValue();
			float y = author.getAuthorSprite().getPosition().y;
			
			if (i == lastAuthor && y < legendView.getSize().y) {
				legendView.setCenter(window.getDefaultView().getCenter());
			} else {
			
				if (i == 0 && y > legendView.getCenter().y - legendView.getSize().y/2 + 10) {
					legendView.setCenter(window.getDefaultView().getCenter());
				}
				
				float spriteHeight = author.getAuthorSprite().getLocalBounds().height;
				float textHeight = author.getAuthorNameText().getLocalBounds().height;
				
				if (i == lastAuthor && y + spriteHeight + textHeight + 10 < legendView.getCenter().y + legendView.getSize().y/2) {
					legendView.setCenter(legendView.getCenter().x, y + spriteHeight + textHeight + 10 - legendView.getSize().y/2);
				}
			}
			
			window.draw(entry.getValue());
			i++;
		}
		
		window.setView(currentView);
	}
	
	/**
	 * 
	 * Draw the entirety of the city
	 * 
	 */
	public void drawCity() {
		window.clear(new Color(0,0,0));
		updateDisplayedView();
		window.draw(sky);
		window.draw(ground);
		window.draw(grassTop);
		for (Tower t: towers) {
			window.draw(t);
		}
		//quinnTest
		for (Sprite sign:packageSigns) {
			window.draw(sign);
		}
				//quinnTest end
		for (Text t: packageTexts) {
			window.draw(t);
		}
		for (Text o: ownerTexts) {
			window.draw(o);
		}
		for(RectangleShape r: towerSigns){
	        window.draw(r);
		}
		for(Text t: signNames){
	        window.draw(t);

		}
		window.draw(grassMid);
		
		drawLegend();

		if (currentFloorDetails != null) {
			drawFloorDetailsMenu();
		}
	}
}
