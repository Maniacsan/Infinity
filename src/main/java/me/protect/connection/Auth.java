package me.protect.connection;

public class Auth {

	private AuthType type;
	private String username;
	private String password;

	public Auth(AuthType type, String username, String password) {
		this.type = type;
		this.username = username;
		this.password = password;
	}

	public enum AuthType {
		ERROR, NOLICENSE, SUCCESS;
	}

	public AuthType getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
