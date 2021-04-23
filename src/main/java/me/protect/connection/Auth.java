package me.protect.connection;

public class Auth {

	private AuthType type;

	public Auth(AuthType type) {
		this.type = type;
	}

	public enum AuthType {
		ERROR, NOLICENSE, SUCCESS;
	}

	public AuthType getType() {
		return type;
	}

}
