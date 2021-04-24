package me.protect.imain;

import java.util.function.Supplier;

public interface ICheck {

	Supplier<String> getResult();

	void setResult(Supplier<String> result, String username, String role);

}
