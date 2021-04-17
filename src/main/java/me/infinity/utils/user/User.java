package me.infinity.utils.user;

public class User {

	private String name;
	private UserRole role;

	public User(String name, String role) {
		this.name = name;
		this.role = UserRole.valueOf(role);
	}

	public String getName() {
		return name;
	}

	public UserRole getRole() {
		return role;
	}
}
