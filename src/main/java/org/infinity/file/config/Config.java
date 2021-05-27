package org.infinity.file.config;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.system.FileUtil;
import org.infinity.utils.system.crypt.AES;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;

public class Config {

	private String name;
	private String author;
	private String date;
	private File configFile;

	public Config(File configFile, Module module, boolean refresh) {
		this.name = "";
		this.configFile = configFile;
		loadConfig(refresh);
	}

	public Config(String name, String author, String date) {
		this.name = name;
		this.author = author;
		this.date = date;
		this.configFile = new File(ConfigManager.dir + File.separator + name + ".json");
	}

	public void save() {
		saveConfig();
	}

	public void add() {
		saveConfig();
	}

	public void load() {
		try {
			String text = FileUtils.readFileToString(configFile);

			if (text.isEmpty())
				return;

			JsonObject configurationObject = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (configurationObject == null)
				return;

			for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
				if (entry.getValue() instanceof JsonObject) {

					JsonObject jsonObject = (JsonObject) entry.getValue();

					for (Module module : InfMain.getModuleManager().getList()) {

						if (module.getCategory().equals(Category.HIDDEN))
							continue;

						if (!module.getName().equalsIgnoreCase(entry.getKey()))
							continue;

						if (Boolean.valueOf(jsonObject.get("Enabled").getAsBoolean())) {
							module.setEnabled(true);
						}

						module.setVisible(jsonObject.get("Visible").getAsBoolean());
						module.setKey(jsonObject.get("Key").getAsInt());

						setSettings(jsonObject, module);
					}
				}
			}

		} catch (IOException | JsonSyntaxException e) {
		}
	}

	public void saveConfig() {
		try {
			if (!configFile.exists())
				configFile.createNewFile();
			JsonObject json = new JsonObject();
			JsonObject info = new JsonObject();

			String author = AES.encrypt(InfMain.getUser().getName(), AES.getKey());
			String date = AES.encrypt(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()),
					AES.getKey());

			info.addProperty("Author", author);
			info.addProperty("Date", date);

			for (Module m : InfMain.getModuleManager().getList()) {

				if (m.getCategory().equals(Category.HIDDEN))
					continue;

				JsonObject dataJson = new JsonObject();
				JsonArray jsonArray = new JsonArray();
				dataJson.addProperty("Enabled", Boolean.valueOf(m.isEnabled()));
				dataJson.addProperty("Visible", Boolean.valueOf(m.isVisible()));
				dataJson.addProperty("Key", Integer.valueOf(m.getKey()));
				saveSettings(m, dataJson, jsonArray);

				json.add("Config", info);
				json.add(m.getName(), dataJson);
			}
			FileUtil.saveJsonObjectToFile(json, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadConfig(boolean refresh) {
		if (!configFile.getName().endsWith(".json"))
			return;
		name = configFile.getName().replace(".json", "");
		try {
			String text = FileUtils.readFileToString(configFile);

			if (text.isEmpty())
				return;

			JsonObject configurationObject = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (configurationObject == null)
				return;

			for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
				if (entry.getValue() instanceof JsonObject) {

					JsonObject jsonObject = (JsonObject) entry.getValue();

					if (entry.getKey().equalsIgnoreCase("Config")) {
						setAuthor(jsonObject.get("Author").isJsonNull() ? ""
								: AES.decrypt(jsonObject.get("Author").getAsString(), AES.getKey()));
						setDate(jsonObject.get("Date").isJsonNull() ? ""
								: AES.decrypt(jsonObject.get("Date").getAsString(), AES.getKey()));
					}
				}
			}
		} catch (IOException | JsonSyntaxException e) {
		}
	}

	private void setSettings(JsonObject jsonObject, Module module) {
		for (Setting setting : module.getSettings()) {
			if (!setting.getModule().getName().equalsIgnoreCase(module.getName()))
				continue;

			// fix crash with new settings
			if (jsonObject.get(setting.getName()) == null || jsonObject.get(setting.getName()).isJsonNull())
				continue;

			/* The best solution, polymorphism in this case is pointless to use */
			switch (setting.getCategory()) {
			case "String":
				setting.setCurrentMode(jsonObject.get(setting.getName()).getAsString());
				break;
			case "Boolean":
				setting.setToggle(jsonObject.get(setting.getName()).getAsBoolean());
				break;
			case "Double":
				setting.setCurrentValueDouble(jsonObject.get(setting.getName()).getAsDouble());
				break;
			case "Float":
				setting.setCurrentValueFloat(jsonObject.get(setting.getName()).getAsFloat());
				break;
			case "Int":
				setting.setCurrentValueInt(jsonObject.get(setting.getName()).getAsInt());
				break;
			case "Color":
				setting.setColor(jsonObject.get(setting.getName()).getAsInt());
				break;
			case "Blocks":
				final JsonElement blockIds = jsonObject.get(setting.getName());
				JsonArray jsonArray = blockIds.getAsJsonArray();
				if (jsonArray == null)
					return;

				for (JsonElement jsonElement : jsonArray)
					setting.addBlockFromId(jsonElement.getAsInt());

				break;
			}
		}
	}

	private void saveSettings(Module m, JsonObject dataJson, JsonArray jsonArray) {
		List<Setting> settings = m.getSettings();

		if (settings == null)
			return;

		for (Setting setting : settings) {
			switch (setting.getCategory()) {
			case "Boolean":
				dataJson.addProperty(setting.getName(), setting.isToggle());
				break;
			case "Double":
				dataJson.addProperty(setting.getName(), setting.getCurrentValueDouble());
				break;
			case "Float":
				dataJson.addProperty(setting.getName(), setting.getCurrentValueFloat());
				break;
			case "Int":
				dataJson.addProperty(setting.getName(), setting.getCurrentValueInt());
				break;
			case "String":
				dataJson.addProperty(setting.getName(), setting.getCurrentMode());
				break;
			case "Color":
				dataJson.addProperty(setting.getName(), setting.getColor().getRGB());
				break;
			case "Blocks":
				for (Block blocks : setting.getBlocks()) {
					jsonArray.add(Block.getRawIdFromState(blocks.getDefaultState()));
				}
				dataJson.add(setting.getName(), jsonArray);
				break;
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void delete() {
		if (this.configFile.exists())
			this.configFile.delete();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
