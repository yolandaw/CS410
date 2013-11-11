package main;

import java.util.LinkedList;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class CityModel {
	
	LinkedList<Tower> towers;
	RenderWindow window;
	View currentView;
	IntRect worldDimensions;
	VertexArray ground;
	VertexArray grassTop;
	VertexArray grassMid;
	VertexArray sky;

	CityModel(RenderWindow newWindow) {
		setWindow(newWindow);
		setCurrentView(newWindow.getDefaultView());
		towers = new LinkedList<Tower>();
		setWorldDimensions(0, 0, window.getSize().x*2, window.getSize().y*2);
		createGround();
		createGrassTop();
		createGrassMid();
		createSky();
	}
	
	public void setTowers(LinkedList<Tower> newTowers) {
		towers = newTowers;
		for (Tower t: towers) {
			t.updateFloors();
		}
	}
	
	public LinkedList<Tower> getTowers() {
		return towers;
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
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 - 30), grassTopColor));
		grassTop.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 - 30), grassTopColor));
	}
	
	private void createGrassMid() {
		grassMid = new VertexArray(PrimitiveType.QUADS);
		Color grassMidColor = new Color(67, 152, 120, 255);
		
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.height/2 + 30), grassMidColor));
		grassMid.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.height/2 + 30), grassMidColor));
	}
	
	private void createSky() {
		sky = new VertexArray(PrimitiveType.QUADS);
		Color lightSkyColor = new Color(178,224,250);
		Color darkSkyColor = new Color(87,188,244);
		
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top + worldDimensions.height), darkSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top + worldDimensions.height), darkSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left + worldDimensions.width, worldDimensions.top), lightSkyColor));
		sky.add(new Vertex(new Vector2f(worldDimensions.left, worldDimensions.top), lightSkyColor));
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
		window.draw(grassMid);
	}
}
