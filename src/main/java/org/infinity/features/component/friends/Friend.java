package org.infinity.features.component.friends;

import java.util.ArrayList;
import java.util.List;

import org.infinity.file.Friends;
import org.infinity.utils.Helper;

import net.minecraft.util.Formatting;

public class Friend {

	private Friends friendFile = new Friends();
	private List<String> friendList = new ArrayList<String>();

	public boolean contains(String name) {
		if (friendList.contains(name)) {
			return true;
		}
		return false;
	}

	public void switchFriend(String name) {
		if (friendList.contains(name)) {
			remove(name);
		} else {
			friendList.add(name);
			Helper.infoMessage(Formatting.WHITE + name + Formatting.GRAY + " added to friend list");
		}
		save();
	}

	public void add(String name) {
		if (friendList.contains(name)) {
			error(name);
		} else {
			friendList.add(name);
			Helper.infoMessage(Formatting.WHITE + name + Formatting.GRAY + " added to friend list");
		}
		save();
	}

	public void remove(String name) {
		if (friendList.contains(name)) {
			friendList.remove(name);
			Helper.infoMessage(name + Formatting.GRAY + " removed from list");
		} else {
			Helper.infoMessage(name + Formatting.GRAY + " missing in the list");
		}
		save();
	}

	private void error(String name) {
		Helper.infoMessage(
				Formatting.GRAY + "Friend " + Formatting.BLUE + name + Formatting.GRAY + " is already on the list");
	}

	public void save() {
		friendFile.saveFriends();
	}

	public void load() {
		friendFile.loadFriends();
	}

	public Friends getFriendsFile() {
		return friendFile;
	}

	public List<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<String> friendList) {
		this.friendList = friendList;
	}

}
