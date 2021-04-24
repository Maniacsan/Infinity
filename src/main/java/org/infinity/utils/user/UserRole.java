package org.infinity.utils.user;

public enum UserRole {
	
	Admin("Admin"), Moderator("Moderator"), Premium("Premium"), User("User");

    public String name;
    
	UserRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
