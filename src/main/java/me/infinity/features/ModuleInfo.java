package me.infinity.features;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.infinity.features.Module.Category;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

	public String name();

	public int key();

	public boolean visible();

	public Category category();

	public String desc();

}
