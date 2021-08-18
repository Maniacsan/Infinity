package org.infinity.file;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.infinity.main.InfMain;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.crypt.AES;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ClientSettings {

	public static File file = FileUtil.createJsonFile(InfMain.getDirection(), "settings");

	public void save() {
		JsonObject json = new JsonObject();

		json.addProperty("CURRENT_CAPE", AES.encrypt(InfMain.getCape().CURRENT_CAPE, AES.getKey()));

		FileUtil.saveJsonObjectToFile(json, file);
	}

	public void load() {
		try {
			String text = FileUtil.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
			if (text.isEmpty())
				return;

			JsonObject configurationObject = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (configurationObject == null)
				return;

			for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
				if (entry.getValue() instanceof JsonObject) {
					JsonObject jsonObject = (JsonObject) entry.getValue();

					InfMain.getCape().CURRENT_CAPE = AES.decrypt(jsonObject.get("CURRENT_CAPE").getAsString(),
							AES.getKey());
				}
			}

		} catch (Exception e) {

		}
	}

}
