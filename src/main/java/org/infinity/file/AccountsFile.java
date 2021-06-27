package org.infinity.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.infinity.main.InfMain;
import org.infinity.ui.account.main.AddThread;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.crypt.AES;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class AccountsFile {

	public static File dir = new File(InfMain.getDirection() + File.separator);
	public static File accountFile = FileUtil.createJsonFile(dir, "accounts");

	public void loadAccounts() {
		try {
			String text = FileUtil.readFile(accountFile.getAbsolutePath(), StandardCharsets.UTF_8);
			if (text.isEmpty())
				return;

			JsonObject jsonReader = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (jsonReader == null)
				return;

			for (Map.Entry<String, JsonElement> entry : jsonReader.entrySet()) {
				String password = "";
				JsonArray jsonArray = entry.getValue().getAsJsonArray();

				if (jsonArray != null) {
					if (jsonArray.size() > 0) {
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
				// System.out.println("do syuda dowel");
				String username = entry.getKey();
				AddThread addThread = new AddThread(username, password);
				addThread.start();
			}
		} catch (IOException | JsonSyntaxException e) {
		}
	}

	public void saveAccounts() {
		if (!dir.exists())
			dir.mkdirs();

		JsonObject json = new JsonObject();
		InfMain.getAccountManager().getRegistry().forEach(account -> {
			JsonArray array = new JsonArray();
			array.add(account.getMask());
			String password = "";
			if (account.getPassword() != null) {
				String encryptedPassword = AES.encrypt(account.getPassword(), AES.getKey());
				if (encryptedPassword != null)
					password = encryptedPassword;
			}
			array.add(password);
			json.add(account.getUsername(), array);
		});
		FileUtil.saveJsonObjectToFile(json, accountFile);
	}

}
