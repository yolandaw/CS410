package main;

import java.util.LinkedList;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;

public class CityModel {
	
	LinkedList<Tower> towers;
	RenderWindow window;
	View currentView;
	IntRect worldDimensions;

	CityModel(RenderWindow newWindow) {
		setWindow(newWindow);
		setCurrentView(newWindow.getDefaultView());
		towers = new LinkedList<Tower>();
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
	
	public void setCurrentView(ConstView constView) {
		currentView = (View) constView;
		window.setView(currentView);
	}
	
	public void moveCurrentView(float x, float y) {
		currentView.move(x, y);
		window.setView(currentView);
	}
	
	public View getCurrentView() {
		return currentView;
	}
	
	public void drawCity() {
		for (Tower t: towers) {
			window.draw(t);
		}
	}
}
