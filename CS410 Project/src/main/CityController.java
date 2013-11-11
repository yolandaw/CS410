package main;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2i;
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
					Mouse.setPosition(leftClickedMousePos, window);
				}
			}
			
			if (event.type == Event.Type.MOUSE_MOVED && scrollMode) {
				MouseEvent mEvent = event.asMouseEvent();
				scrollXVelocity = mEvent.position.x - window.getSize().x/2;
				scrollYVelocity = mEvent.position.y - window.getSize().y/2;
			}
			
		}
		
		scrollModelView();
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
		
		model.moveCurrentView((float) scrollXVelocity, (float) scrollYVelocity);
		
		resetMousePosition();
	}
	
	private void resetMousePosition() {
		if (Mouse.isButtonPressed(Button.LEFT) && scrollMode) {
			Mouse.setPosition(new Vector2i(window.getSize().x/2, window.getSize().y/2), window);
		}
	}
}
