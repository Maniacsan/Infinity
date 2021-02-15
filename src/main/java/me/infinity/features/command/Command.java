package me.infinity.features.command;

import me.infinity.utils.Helper;

public abstract class Command {

	public static String prefix = "#";
	private String name;
	private String desc;

	public Command() {
		this.setName(getClass().getAnnotation(CommandInfo.class).name());
		this.setDesc(getClass().getAnnotation(CommandInfo.class).desc());
	}

	// methods
	public abstract void command(String[] args, String msg);
	public abstract void error();
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void set(String message) {
		Helper.infoMessage(message);
	}

}
