package org.infinity.protect;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.infinity.protect.impl.ConnectLocker;
import org.infinity.protect.impl.InitProcess;
import org.infinity.protect.impl.OpenScreenLocker;
import org.infinity.protect.impl.ProtectHandler;
import org.infinity.protect.impl.ReLoginProcess;
import org.infinity.protect.impl.AuthHandler;
import org.infinity.protect.impl.TickLogger;

public class HandlerManager implements IHandler {

	private List<Handler> handlerList = Arrays.asList(new ProtectHandler(), new AuthHandler(), new TickLogger(),
			new InitProcess(), new ReLoginProcess(), new ConnectLocker(), new OpenScreenLocker());

	@Override
	public List<Handler> getHandlerList() {
		return handlerList;
	}

	@Override
	public Handler getHandler(Class<?> clas) {
		Iterator<Handler> iterator = handlerList.iterator();
		Handler handler;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			handler = (Handler) iterator.next();
		} while (handler.getClass() != clas);
		return handler;
	}

}
