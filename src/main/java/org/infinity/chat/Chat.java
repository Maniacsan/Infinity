package org.infinity.chat;

import java.util.List;

import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public class Chat {

	public List<ChatHudLine<OrderedText>> lines = Lists.newArrayList();

	public String name;

	public Chat(String name) {
		this.name = name;
	}
}