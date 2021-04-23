package org.infinity.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.infinity.InfMain;
import org.infinity.ui.account.main.AddThread;
import org.infinity.utils.AES;
import org.infinity.utils.FileUtil;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class AccountsFile {

	public static File dir = new File(InfMain.getInfDirection() + File.separator);
	public static File accountFile = FileUtil.createJsonFile(dir, "accounts");

	public void loadAccounts() {
		try {
			String text = FileUtils.readFileToString(accountFile);
			if (text.isEmpty())
				return;

			JsonObject jsonReader = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (jsonReader == null)
				return;

			for (Map.Entry<String, JsonElement> entry : jsonReader.entrySet()) {
					String mask = "";
					String password = "";
					JsonArray jsonArray = entry.getValue().getAsJsonArray();

					if (jsonArray != null) {
						if (jsonArray.size() > 0) {
							if (jsonArray.get(0).isJsonNull()) {
								mask = "";
							} else {
								mask = jsonArray.get(0).getAsString();
							}
							if (jsonArray.get(1).isJsonNull()) {
								password = "";
							} else {
								String decryptedPassword = AES.decrypt(jsonArray.get(1).getAsString(), AES.getKey());
								if(decryptedPassword == null)
									password = "";
								password = decryptedPassword;
							}
						}
					}
					//System.out.println("do syuda dowel");
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
			if(account.getPassword() != null) {
				String encryptedPassword = AES.encrypt(account.getPassword(), AES.getKey());
				if(encryptedPassword != null)
					password = encryptedPassword;
			}
			array.add(password);
			json.add(account.getUsername(), array);
		});
		FileUtil.saveJsonObjectToFile(json, accountFile);
	}

}
