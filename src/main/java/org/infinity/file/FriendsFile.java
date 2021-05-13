package org.infinity.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.infinity.InfMain;
import org.infinity.utils.system.FileUtil;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class FriendsFile {

	public static File dir = new File(InfMain.getInfDirection() + File.separator);
	public static File friendFile = FileUtil.createJsonFile(dir, "friend");

	public void loadFriends() {
		try {
			String text = FileUtils.readFileToString(friendFile);

			if (text.isEmpty())
				return;

			JsonObject configurationObject = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (configurationObject == null)
				return;

			for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
				if (entry.getValue() instanceof JsonObject) {
					InfMain.getFriend().add(entry.getKey());
				}
			}

		} catch (IOException | JsonSyntaxException e) {
		}
	}

	public void saveFriends() {
		if (!dir.exists())
			dir.mkdirs();

		JsonObject json = new JsonObject();
		for (String friends : InfMain.getFriend().getFriendList()) {
			JsonArray array = new JsonArray();
			json.add(friends, array);
		}

		FileUtil.saveJsonObjectToFile(json, friendFile);
	}

}
