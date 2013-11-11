package main;

import java.util.LinkedList;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class CityModel {
	
	LinkedList<Tower> towers;
	RenderWindow window;
	View currentView;
	IntRect worldDimensions;
	Color backGroundColor;

	CityModel(RenderWindow newWindow) {
		setWindow(newWindow);
		setCurrentView(newWindow.getDefaultView());
		towers = new LinkedList<Tower>();
		backGroundColor = new Color (178,224,250);
		setWorldDimensions(0, 0, window.getSize().x*2, window.getSize().y*2);
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
	
	public void drawCity() {
		window.clear(backGroundColor);
		updateDisplayedView();
		RectangleShape border = new RectangleShape();
		border.setSize(new Vector2f(worldDimensions.width, worldDimensions.height));
		border.setPosition(worldDimensions.left, worldDimensions.top);
		border.setOutlineColor(new Color(0,0,0));
		border.setFillColor(new Color(255, 255, 255, 0));
		border.setOutlineThickness(1);
		window.draw(border);
		for (Tower t: towers) {
			window.draw(t);
		}
	}
}
