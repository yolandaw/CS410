package main;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
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
	double scrollLegendYVelocity;
	Vector2i leftClickedMousePos;
	
	boolean scrollLeft;
	boolean scrollRight;
	boolean scrollUp;
	boolean scrollDown;
	boolean legendScroll;
	double variableSpeed = StaticControls.directionScrollStartSpeed;
	
	CityController(CityModel city) {
		model = city;
		window = city.getWindow();
		scrollMode = false;
		legendScroll = false;
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
				
				if (!Keyboard.isKeyPressed(Key.I) && !Keyboard.isKeyPressed(Key.O)) {
					if (!scrollLeft && !scrollRight && !scrollUp && !scrollDown) {
						updateHoveredOverFloor(mEvent);
					}
				}
			}
			
			if (event.type == Event.Type.KEY_PRESSED) {
				if (event.asKeyEvent().key == Key.LEFT) {
					scrollLeft = true;
					scrollMode = false;
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.RIGHT) {
					scrollRight = true;
					scrollMode = false;
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.UP) {
					scrollUp = true;
					scrollMode = false;
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.DOWN) {
					scrollDown = true;
					scrollMode = false;
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.I) {
					scrollMode = false;
					model.getCurrentView().zoom((float)0.98);
					if (model.getCurrentFloorDetails() != null) {
						model.getCurrentFloorDetails().setHighlighted(false);
					}
					
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.O) {
					scrollMode = false;
					View oldView = model.getCurrentView();
					float oldX = oldView.getSize().x;
					float oldY = oldView.getSize().y;
					
					model.getCurrentView().zoom((float)1.02);
					if (model.getCurrentView().getSize().x > model.getWorldDimensions().width) {
						model.getCurrentView().setSize(model.getWorldDimensions().width, oldY);
					} else {
						if (model.getCurrentView().getSize().y > model.getWorldDimensions().height) {
							model.getCurrentView().setSize(oldX, model.getWorldDimensions().height);
						}
					}
					
					tearDownFloorMenu();
				}
				
				if (event.asKeyEvent().key == Key.W) {
					legendScroll = true;
					scrollLegendYVelocity = 5;
				}
				
				if (event.asKeyEvent().key == Key.S) {
					legendScroll = true;
					scrollLegendYVelocity = -5;
				}
			}
			
			if (event.type == Event.Type.KEY_RELEASED) {
				if (event.asKeyEvent().key == Key.LEFT) {
					scrollMode = false;
					scrollLeft = false;
					scrollXVelocity = -5;
					variableSpeed = StaticControls.directionScrollStartSpeed;
				}
				
				if (event.asKeyEvent().key == Key.RIGHT) {
					scrollMode = false;
					scrollRight = false;
					scrollXVelocity = 5;
					variableSpeed = StaticControls.directionScrollStartSpeed;				
				}
				
				if (event.asKeyEvent().key == Key.UP) {
					scrollMode = false;
					scrollUp = false;
					scrollYVelocity = -5;
					variableSpeed = StaticControls.directionScrollStartSpeed;
				}
				
				if (event.asKeyEvent().key == Key.DOWN) {
					scrollMode = false;
					scrollDown = false;
					scrollYVelocity = 5;
					variableSpeed = StaticControls.directionScrollStartSpeed;
				}
				
				if (event.asKeyEvent().key == Key.W) {
					legendScroll = false;
				}
				
				if (event.asKeyEvent().key == Key.S) {
					legendScroll = false;
				}
			}
		}
		
		scrollModelView();
		scrollLegendView();
		
		if(scrollUp == true){
			variableSpeed = variableSpeed * 1.01;
			moveModelView((float) 0, (float) -variableSpeed);
		}
		
		if(scrollDown == true){
			variableSpeed = variableSpeed * 1.01;
			moveModelView((float) 0, (float) variableSpeed);
		}
		
		if(scrollRight == true){
			variableSpeed = variableSpeed * 1.01;
			moveModelView((float) variableSpeed, (float) 0);
		}
		
		if(scrollLeft == true){
			variableSpeed = variableSpeed * 1.01;
			moveModelView((float) -variableSpeed, (float) 0);
		}
		
		constrainModelViewToWorld();
	}
	
	private void scrollLegendView() {
		
		if (!legendScroll) {
			if (scrollLegendYVelocity > 0) {
				scrollLegendYVelocity = scrollLegendYVelocity - (scrollLegendYVelocity * 0.1);
			} else {
				if (scrollLegendYVelocity < 0) {
					scrollLegendYVelocity = scrollLegendYVelocity + (scrollLegendYVelocity * -0.1);
				}
			}
		}
		
		moveModelLegendViewY((float) scrollLegendYVelocity);
		
	}

	private void tearDownFloorMenu() {
		if (model.getCurrentFloorDetails() != null) {
			model.getCurrentFloorDetails().setHighlighted(false);
		}
		model.setCurrentFloorDetails(null);
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
							model.setPosFloorDetailsMenu(mEvent.position.x + 15, mEvent.position.y);
						}
					}
				}
			}
			
			//wasn't hovered over anything, so tear down
			if (!hoveredOver) {
				tearDownFloorMenu();
			}
		} else {
			//in scrollmode, so tear down
			tearDownFloorMenu();
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
	
	private void moveModelLegendViewY(float y) {
		model.getLegendView().move(0, y);
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
