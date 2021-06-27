package org.infinity.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.infinity.main.InfMain;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.crypt.AES;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class AuthInfo {
	
	public static AuthInfo INSTANCE = new AuthInfo();

	public static File dir = new File(InfMain.getDirection() + File.separator);
	public static File authdata = FileUtil.createJsonFile(dir, "auth");

	public String[] loadAccount() {
		String username = "";
		String password = "";
		try {
			String text = FileUtil.readFile(authdata.getAbsolutePath(), StandardCharsets.UTF_8);
			if (text.isEmpty())
				return null;

			JsonObject jsonReader = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (jsonReader == null)
				return null;

			for (Map.Entry<String, JsonElement> entry : jsonReader.entrySet()) {
				JsonArray jsonArray = entry.getValue().getAsJsonArray();

				if (jsonArray != null) {
					if (jsonArray.size() > 0) {
						username = jsonArray.get(0).getAsString();
						if (jsonArray.get(1).isJsonNull()) {
							password = "";
						} else {
							String decryptedPassword = AES.decrypt(jsonArray.get(1).getAsString(), AES.getKey());
							if (decryptedPassword == null)
								password = "";
							password = decryptedPassword;
						}
					}
				}
			}
		} catch (IOException | JsonSyntaxException e) {
		}
		return new String[] { username, password };
	}

	public void saveAccount(String username, String password) {
		if (!dir.exists())
			dir.mkdirs();

		JsonObject json = new JsonObject();
		JsonArray array = new JsonArray();
		array.add(username);
		if (password != null) {
			String encryptedPassword = AES.encrypt(password, AES.getKey());
			if (encryptedPassword != null)
				password = encryptedPassword;
		}
		array.add(password);
		json.add("AuthData", array);

		FileUtil.saveJsonObjectToFile(json, authdata);
	}

}
