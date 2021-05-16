package org.infinity.file.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.infinity.InfMain;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.Setting;
import org.infinity.utils.system.FileUtil;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;

public class Config {

	public String name;
	private File configFile;

	public Config(File configFile, Module module, boolean refresh) {
		this.name = "";
		this.configFile = configFile;
		loadConfig(refresh);
	}

	public Config(String name) {
		this.name = name;
		this.configFile = new File(ConfigManager.dir + File.separator + name + ".json");
	}

	public void save() {
		saveConfig(false);
	}

	public void add() {
		saveConfig(true);
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

						if (module.getName().equalsIgnoreCase(entry.getKey())) {
							if (Boolean.valueOf(jsonObject.get("Enabled").getAsBoolean())) {
								module.setEnabled(true);
							}

							module.setVisible(jsonObject.get("Visible").getAsBoolean());
							module.setKey(jsonObject.get("Key").getAsInt());

							for (Setting setting : module.getSettings()) {
								if (setting.getModule().getName().equalsIgnoreCase(module.getName())) {

									// fix crash with new settings
									if (jsonObject.get(setting.getName()) == null
											|| jsonObject.get(setting.getName()).isJsonNull())
										continue;

									if (setting.isMode()) {
										setting.setCurrentMode(jsonObject.get(setting.getName()).getAsString());
									} else if (setting.isBoolean()) {
										setting.setToggle(jsonObject.get(setting.getName()).getAsBoolean());
									} else if (setting.isValueDouble()) {
										setting.setCurrentValueDouble(jsonObject.get(setting.getName()).getAsDouble());
									} else if (setting.isValueFloat()) {
										setting.setCurrentValueFloat(jsonObject.get(setting.getName()).getAsFloat());
									} else if (setting.isValueInt()) {
										setting.setCurrentValueInt(jsonObject.get(setting.getName()).getAsInt());
									} else if (setting.isColor()) {
										setting.setColor(jsonObject.get(setting.getName()).getAsInt());
									} else if (setting.isBlock()) {
										JsonArray jsonArray = null;
										final JsonElement blockIds = jsonObject.get(setting.getName());
										if (blockIds != null)
											jsonArray = blockIds.getAsJsonArray();
										if (jsonArray != null) {
											for (JsonElement jsonElement : jsonArray) {
												if (jsonElement != null)
													setting.addBlockFromId(jsonElement.getAsInt());
											}
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (IOException | JsonSyntaxException e) {
			System.out.println(e);
		}
	}

	public void saveConfig(boolean def) {
		try {
			if (!configFile.exists())
				configFile.createNewFile();
			JsonObject json = new JsonObject();
			for (Module m : InfMain.getModuleManager().getList()) {

				if (m.getCategory().equals(Category.HIDDEN))
					continue;

				JsonObject dataJson = new JsonObject();
				JsonArray jsonArray = new JsonArray();
				dataJson.addProperty("Enabled", Boolean.valueOf(def ? m.isDefaultEnabled() : m.isEnabled()));
				dataJson.addProperty("Visible", Boolean.valueOf(def ? m.isDefaultVisible() : m.isVisible()));
				dataJson.addProperty("Key", Integer.valueOf(def ? m.getDefaultKey() : m.getKey()));
				List<Setting> settings = m.getSettings();

				if (settings != null) {
					for (Setting setting : settings) {
						if (setting.isBoolean())
							dataJson.addProperty(setting.getName(),
									def ? setting.isDefaultToogle() : setting.isToggle());
						else if (setting.isValueDouble()) {
							dataJson.addProperty(setting.getName(),
									def ? setting.getDefaultDouble() : setting.getCurrentValueDouble());
						} else if (setting.isValueFloat()) {
							dataJson.addProperty(setting.getName(),
									def ? setting.getDefaultFloat() : setting.getCurrentValueFloat());
						} else if (setting.isValueInt()) {
							dataJson.addProperty(setting.getName(),
									def ? setting.getDefaultInt() : setting.getCurrentValueInt());
						} else if (setting.isMode()) {
							dataJson.addProperty(setting.getName(),
									def ? setting.getDefaultMode() : setting.getCurrentMode());
						} else if (setting.isColor()) {
							dataJson.addProperty(setting.getName(),
									def ? setting.getDefaultColor().getRGB() : setting.getColor().getRGB());
						} else if (setting.isBlock()) {
							for (Block blocks : setting.getBlocks()) {
								jsonArray.add(Block.getRawIdFromState(blocks.getDefaultState()));
							}
							dataJson.add(setting.getName(), jsonArray);
						}
					}
				}
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

}
