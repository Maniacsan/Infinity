package me.protect.utils;

import java.util.function.Supplier;

import org.infinity.event.protect.ButtonPressEvent;
import org.infinity.main.InfMain;
import org.infinity.utils.user.User;

import com.darkmagician6.eventapi.EventManager;

import me.protect.imain.ICheck;

public class Check implements ICheck {

	private Supplier<String> result;

	public Check() {
		this.result = null;
	}

	@Override
	public Supplier<String> getResult() {
		return result;
	}

	@Override
	public void setResult(Supplier<String> result, String username, String role, String photo) {
		InfMain.setUser(new User(username, role, photo));
		this.result = result;
		ButtonPressEvent event = new ButtonPressEvent();
		EventManager.call(event);
	}

}
