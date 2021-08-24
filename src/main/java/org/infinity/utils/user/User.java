package org.infinity.utils.user;

public class User {

	private String name;
	private String photo;
	private String username;
	private UserRole role;

	public User(String name, String role, String photo) {
		this.name = name;
		this.role = UserRole.valueOf(role.toUpperCase());
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public UserRole getRole() {
		return role;
	}

	public String getPhoto() {
		return photo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}
}
