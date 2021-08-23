package org.infinity.file;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.crypt.AES;
import org.infinity.via.ViaFabric;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ClientSettings {

	private File file = FileUtil.createJsonFile(Helper.MC.runDirectory, "settings");

	public void save() {
		JsonObject json = new JsonObject();
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("CURRENT_CAPE", AES.encrypt(InfMain.getCape().CURRENT_CAPE, AES.getKey()));
		jsonObject.addProperty("CAPE_NAME", InfMain.getCape().CURRENT_NAME);
		jsonObject.addProperty("VIA", String.valueOf((int) ViaFabric.stateValue));
		json.add("Settings", jsonObject);

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

					InfMain.getCape().setCurrent(jsonObject.get("CAPE_NAME").getAsString(),
							AES.decrypt(jsonObject.get("CURRENT_CAPE").getAsString(), AES.getKey()));
					ViaFabric.stateValue = jsonObject.get("VIA").getAsInt();
				}
			}

		} catch (Exception e) {

		}
	}

}
