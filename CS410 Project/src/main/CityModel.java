package main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class CityModel {
	
	LinkedList<Tower> towers;
	RenderWindow window;
	View currentView;
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
	LinkedList<RectangleShape> packageSigns;

	CityModel(RenderWindow newWindow) {
		setWindow(newWindow);
		setCurrentView(newWindow.getDefaultView());
		towers = new LinkedList<Tower>();
		packageSigns = new LinkedList<RectangleShape>();
		setWorldDimensions(0, 0, 0, 0);
		grassMidHeight = 30;
		currentFloorDetails = null;
		setUpFloorDetailsMenu();
		cityDistance = 300;
		currentView = new View();
	}
	
	//need to place towers based on package
	public void setTowers(LinkedList<Tower> newTowers) {
		
		
		int x = 400;
		towers = newTowers;
		String currentPackage = newTowers.getFirst().getCityName();
		
		calculateWorldDimensions();
		
		RectangleShape firstSign = new RectangleShape();
		firstSign.setFillColor(new Color(154,136,81));
		firstSign.setOutlineColor(new Color(114,100,60));
		firstSign.setOutlineThickness(3);
		firstSign.setSize(new Vector2f(100 ,100));
		firstSign.setPosition(x - 3, worldDimensions.height/2 - firstSign.getSize().y - 3);
		packageSigns.push(firstSign);
		x = x + (int) firstSign.getSize().x;
		
		for (Tower t: towers) {
			if (!t.getCityName().equals(currentPackage)) {
				RectangleShape newSign = new RectangleShape();
				newSign.setFillColor(new Color(154,136,81));
				newSign.setSize(new Vector2f(100 ,100));
				newSign.setOutlineColor(new Color(114,100,60));
				newSign.setOutlineThickness(3);
				newSign.setPosition(x+cityDistance-newSign.getSize().x - 3, worldDimensions.height/2 - newSign.getSize().y - 3);
				packageSigns.push(newSign);
				
				x = x + cityDistance;
				currentPackage = t.getCityName();
			}
			
			//hack to see all towers
			t.setTowerPosition(x, worldDimensions.height/2);	
			x=x+t.getTowerWidth()+t.getTowerDepth();
			//end hack to see all towers
			
			t.updateFloors(grassMidHeight);
		}
		
		createGround();
		createGrassTop();
		createGrassMid();
		createSky();
	}
	
	private void calculateWorldDimensions() {
		int tallestTower = window.getSize().y*2;
		int totalWidth = window.getSize().x;
		String currentPackage = towers.getFirst().getCityName();
		
		for (Tower t: towers) {
			
			setRandomTowerSize(t);
			
			if (t.getTowerHeight() * 3 > tallestTower) {
				tallestTower = t.getTowerHeight() * 3;
			}
			
			totalWidth = totalWidth + t.getTowerWidth();
			
			if (!t.getCityName().equals(currentPackage)) {				
				totalWidth = totalWidth + cityDistance;
				currentPackage = t.getCityName();
			}
		}
		
		if (totalWidth < window.getSize().x*2) {
			totalWidth = window.getSize().x*2;
		}
		
		setWorldDimensions(0, 0, totalWidth, tallestTower);
		currentView.setCenter(worldDimensions.width/2, worldDimensions.height/2);
		currentView.setSize(window.getSize().x*2, window.getSize().y*2);
		
	}
	
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
	}
	
	public LinkedList<Tower> getTowers() {
		return towers;
	}
	
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
	
	private void setWindow(RenderWindow newWindow) {
		window = newWindow;
	}
	
	public RenderWindow getWindow() {
		return window;
	}
	
	public IntRect getWorldDimensions() {
		return worldDimensions;
	}
	
	public void setWorldDimensions(int left, int top, int width, int height) {
		worldDimensions = new IntRect(left, top, width, height);
	}
	
	public void setCurrentView(ConstView constView) {
		currentView = (View) constView;
	}
	
	public void updateDisplayedView() {
		window.setView(currentView);
	}
	
	public View getCurrentView() {
		return currentView;
	}
	
	public void setCurrentFloorDetails(Floor newFloor) {
		currentFloorDetails = newFloor;
	}
	
	public Floor getCurrentFloorDetails() {
		return currentFloorDetails;
	}
	
	public void setPosFloorDetailsMenu(float x, float y) {
		floorDetailsMenu.setPosition(x, y);
	}
	
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
	
	private void createGround() {
		ground = new VertexArray(PrimitiveType.QUADS);
		Color lightGroundColor = new Color(166, 152, 110, 255);
		Color darkGroundColor = new Color(50, 39, 6, 255);
		
		ground.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top + worldDimensions.height), darkGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top + worldDimensions.height), darkGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), lightGroundColor));
		ground.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), lightGroundColor));
	}
	
	private void createGrassTop() {
		grassTop = new VertexArray(PrimitiveType.QUADS);
		Color grassTopColor = new Color(106, 190, 137, 255);
		
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 - grassMidHeight), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 - grassMidHeight), grassTopColor));
	}
	
	private void createGrassMid() {
		grassMid = new VertexArray(PrimitiveType.QUADS);
		Color grassMidColor = new Color(81, 158, 110, 255);
		
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 + grassMidHeight), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 + grassMidHeight), grassMidColor));
	}
	
	private void createSky() {
		sky = new VertexArray(PrimitiveType.QUADS);
		Color lightSkyColor = Color.WHITE;
		Color darkSkyColor = new Color(87,188,244);
		
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top + worldDimensions.height/2), lightSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top + worldDimensions.height/2), lightSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top), darkSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top), darkSkyColor));
	}
	
	public void drawCity() {
		window.clear(new Color(0,0,0));
		updateDisplayedView();
		window.draw(sky);
		window.draw(ground);
		window.draw(grassTop);
		for (Tower t: towers) {
			window.draw(t);
		}
		for (RectangleShape p: packageSigns) {
			window.draw(p);
		}
		window.draw(grassMid);

		if (currentFloorDetails != null) {
			drawFloorDetailsMenu();
		}
	}
}
