package main;

import java.util.Map;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseEvent;

public class CityController {

	RenderWindow window;
	CityModel model;
	Boolean scrollMode;
	double scrollXVelocity;
	double scrollYVelocity;
	Vector2i leftClickedMousePos;
	
	boolean scrollLeft;
	boolean scrollRight;
	boolean scrollUp;
	boolean scrollDown;
	
	CityController(CityModel city) {
		model = city;
		window = city.getWindow();
		scrollMode = false;
	}
	
	public void updateModel() {
		
		for (Event event : window.pollEvents()) {
			
			if (event.type == Event.Type.CLOSED) {
				window.close();
			}
			
			if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
				if (event.asMouseButtonEvent().button == Button.LEFT) {
					scrollMode = true;
					window.setMouseCursorVisible(false);
					leftClickedMousePos = Mouse.getPosition(window);
					Mouse.setPosition(new Vector2i(window.getSize().x/2, window.getSize().y/2), window);
				}
			}
			
			if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
				if (event.asMouseButtonEvent().button == Button.LEFT) {
					scrollMode = false;
					window.setMouseCursorVisible(true);
					if (leftClickedMousePos != null) {
						Mouse.setPosition(leftClickedMousePos, window);
					}
				}
			}
			
			if (event.type == Event.Type.MOUSE_MOVED && scrollMode) {
				MouseEvent mEvent = event.asMouseEvent();
				scrollXVelocity = mEvent.position.x - window.getSize().x/2;
				scrollYVelocity = mEvent.position.y - window.getSize().y/2;
			}
			
			if (event.type == Event.Type.MOUSE_MOVED) {
				MouseEvent mEvent = event.asMouseEvent();
				updateHoveredOverFloor(mEvent);
			}
			
			if (event.type == Event.Type.KEY_PRESSED) {
				if (event.asKeyEvent().key == Key.LEFT) {
					scrollLeft = true;
					scrollMode = false;
					//moveModelView((float) -5, (float) 0);
				}
				
				if (event.asKeyEvent().key == Key.RIGHT) {
					scrollRight = true;
					scrollMode = false;
					//window.setMouseCursorVisible(true);
					//scrollXVelocity = 5;
					//moveModelView((float) 5, (float) 0);
				}
				
				if (event.asKeyEvent().key == Key.UP) {
					scrollUp = true;
					scrollMode = false;
					//window.setMouseCursorVisible(true);
					//scrollYVelocity = -5;
					//moveModelView((float) 0, (float) -5);
				}
				
				if (event.asKeyEvent().key == Key.DOWN) {
					scrollDown = true;
					scrollMode = false;
					//window.setMouseCursorVisible(true);
					//scrollYVelocity = 5;
					//moveModelView((float) 0, (float) 5);
				}
				
				if (event.asKeyEvent().key == Key.I) {
					scrollMode = false;
					model.getCurrentView().zoom((float)0.98);
				}
				
				if (event.asKeyEvent().key == Key.O) {
					scrollMode = false;
					model.getCurrentView().zoom((float)1.02);
				}
			}
			
			if (event.type == Event.Type.KEY_RELEASED) {
				if (event.asKeyEvent().key == Key.LEFT) {
					scrollMode = false;
					scrollLeft = false;
					//window.setMouseCursorVisible(true);
					//moveModelView((float) -10, (float) 0);
					scrollXVelocity = -5;
				}
				
				if (event.asKeyEvent().key == Key.RIGHT) {
					scrollMode = false;
					scrollRight = false;
					//window.setMouseCursorVisible(true);
					scrollXVelocity = 5;
				}
				
				if (event.asKeyEvent().key == Key.UP) {
					scrollMode = false;
					scrollUp = false;
					//window.setMouseCursorVisible(true);
					scrollYVelocity = -5;
				}
				
				if (event.asKeyEvent().key == Key.DOWN) {
					scrollMode = false;
					scrollDown = false;
					//window.setMouseCursorVisible(true);
					scrollYVelocity = 5;
				}
			}
		}
		
		scrollModelView();
		
		if(scrollUp == true){
			moveModelView((float) 0, (float) -5);
		}
		
		if(scrollDown == true){
			moveModelView((float) 0, (float) 5);
		}
		
		if(scrollRight == true){
			moveModelView((float) 5, (float) 0);
		}
		
		if(scrollLeft == true){
			moveModelView((float) -5, (float) 0);
		}
		
		constrainModelViewToWorld();
	}
	
	private void scrollModelView() {
		if (scrollXVelocity > 0) {
			scrollXVelocity = scrollXVelocity - (scrollXVelocity * 0.1);
		} else {
			if (scrollXVelocity < 0) {
				scrollXVelocity = scrollXVelocity + (scrollXVelocity * -0.1);
			}
		}
		
		if (scrollYVelocity > 0) {
			scrollYVelocity = scrollYVelocity - (scrollYVelocity * 0.1);
		} else {
			if (scrollYVelocity < 0) {
				scrollYVelocity = scrollYVelocity + (scrollYVelocity * -0.1);
			}
		}
		
		moveModelView((float) scrollXVelocity, (float) scrollYVelocity);
		constrainModelViewToWorld();
		resetMousePosition();
	}
	
	private void updateHoveredOverFloor(MouseEvent mEvent) {
		boolean hoveredOver = false;
		Vector2f worldCoord = window.mapPixelToCoords(mEvent.position);
		
		if (!scrollMode) {
			for (Tower t: model.getTowers()) {
				Vector2i towerPos = t.getTowerPosition();
				//check if mouse is in the x range of the tower
				if (worldCoord.x > towerPos.x && worldCoord.x < (towerPos.x + t.getTowerWidth())) {
					
					//check which floor the mouse is over
					for (Floor f: t.getListOfFloor()) {
						IntRect floorBoundaries = f.getFloorBoundaries();
						
						if (worldCoord.y <= floorBoundaries.top && worldCoord.y > floorBoundaries.top - floorBoundaries.height) {
							hoveredOver = true;
							if (model.getCurrentFloorDetails() != null) {
								model.getCurrentFloorDetails().setHighlighted(false);
							}
							f.setHighlighted(true);
							model.setCurrentFloorDetails(f);
							model.setPosFloorDetailsMenu(worldCoord.x + 15, worldCoord.y);
						}
					}
				}
			}
			
			//wasn't hovered over anything, so tear down
			if (!hoveredOver) {
				if (model.getCurrentFloorDetails() != null) {
					model.getCurrentFloorDetails().setHighlighted(false);
				}
				model.setCurrentFloorDetails(null);
			}
		} else {
			//in scrollmode, so tear down
			if (model.getCurrentFloorDetails() != null) {
				model.getCurrentFloorDetails().setHighlighted(false);
			}
			model.setCurrentFloorDetails(null);
		}
	}
	
	private void resetMousePosition() {
		if (Mouse.isButtonPressed(Button.LEFT) && scrollMode) {
			Mouse.setPosition(new Vector2i(window.getSize().x/2, window.getSize().y/2), window);
		}
	}
	
	private void moveModelView(float x, float y) {
		model.getCurrentView().move(x, y);
	}
	
	private void constrainModelViewToWorld() {
		View resetView = model.getCurrentView();
		Vector2f resetCenter = resetView.getCenter();
		float currentLeftX = resetView.getCenter().x - (resetView.getSize().x/2);
		float currentTopY = resetView.getCenter().y - (resetView.getSize().y/2);
		IntRect worldDimensions = model.getWorldDimensions();
		
		if (currentLeftX < model.getWorldDimensions().left) {
			resetCenter = new Vector2f(worldDimensions.left + (resetView.getSize().x/2), resetCenter.y);
		} else {
			if (currentLeftX + resetView.getSize().x > worldDimensions.left + worldDimensions.width) {
				resetCenter = new Vector2f(worldDimensions.left + worldDimensions.width - (resetView.getSize().x/2), resetCenter.y);
			}
		}
		
		if (currentTopY < model.getWorldDimensions().top) {
			resetCenter = new Vector2f(resetCenter.x, worldDimensions.top + (resetView.getSize().y/2));
		} else {
			if (currentTopY + resetView.getSize().y > worldDimensions.top + worldDimensions.height) {
				resetCenter = new Vector2f(resetCenter.x, worldDimensions.top + worldDimensions.height-(resetView.getSize().y/2));
			}
		}
		
		resetView = new View(resetCenter, resetView.getSize());
		model.setCurrentView(resetView);
	}
}
