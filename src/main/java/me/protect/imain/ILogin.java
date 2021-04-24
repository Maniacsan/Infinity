package me.protect.imain;

import me.protect.connection.Auth;

public interface ILogin {
	
	Auth getAuth();
	
	void setAuth(Auth auth);
	
	Auth login(String username, String password);

}
