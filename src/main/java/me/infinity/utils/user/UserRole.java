package me.infinity.utils.user;

public enum UserRole {
	
	ADMIN("Admin"), MODERATOR("Moderator"), PREMIUM("Premium"), USER("User");

    public String name;
    
	UserRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
