package me.protect.imain;

import me.protect.connection.Auth;

public interface ILogin {
	
	Auth getAuth();
	
	Auth login(String username, String password);

}
