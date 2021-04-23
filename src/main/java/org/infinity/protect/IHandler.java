package org.infinity.protect;

import java.util.List;

public interface IHandler {
	
	List<Handler> getHandlerList();
	
	Handler getHandler(Class<?> clas);

}
