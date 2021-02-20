package me.infinity.file.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.infinity.InfMain;
import me.infinity.features.Module;
import me.infinity.features.Settings;
import me.infinity.utils.FileUtil;
import net.minecraft.block.Block;

public class Config {

	public String name;
	private File configFile;
	private ArrayList<ConfigData> data;

	public Config(File configFile, Module module, boolean refresh) {
		this.name = "";
		this.configFile = configFile;
		this.data = new ArrayList<>();
		loadConfig(refresh);
	}

	public Config(String name) {
		this.name = name;
		this.configFile = new File(ConfigManager.dir + File.separator + name + ".json");
		this.data = new ArrayList<>();
		save();
	}

	public void save() {
		InfMain.getModuleManager().getList().forEach(data -> this.data.add(new ConfigData(data)));
		saveConfig();
	}

	public void load() {
		// System.out.println("Enabled check = true");
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
					data.add(new ConfigData(entry.getKey(),
							jsonObject.has("Enabled") && jsonObject.get("Enabled").getAsBoolean(),
							jsonObject.get("Visible").getAsBoolean(),
							jsonObject.has("Key") ? jsonObject.get("Key").getAsInt() : -2));
					data.forEach(data -> {
						for (Module module : InfMain.getModuleManager().getList()) {
							if (module.getName().equalsIgnoreCase(data.getName())) {
								module.setEnabled(data.isEnabled());
								for (Settings setting : module.getSettings()) {
									if (jsonObject.has(setting.getName())) {
										if (module.getName().equalsIgnoreCase(setting.getModule().getName())) {
											if (setting.isBoolean()) {
												setting.setToggle(jsonObject.get(setting.getName()).getAsBoolean());
											} else if (setting.isValueDouble()) {
												setting.setCurrentValueDouble(
														jsonObject.get(setting.getName()).getAsDouble());
											} else if (setting.isValueFloat()) {
												setting.setCurrentValueFloat(
														jsonObject.get(setting.getName()).getAsFloat());
											} else if (setting.isValueInt()) {
												setting.setCurrentValueInt(
														jsonObject.get(setting.getName()).getAsInt());
											} else if (setting.isMode()) {
												setting.setCurrentMode(jsonObject.get(setting.getName()).getAsString());
											} else if (setting.isColor()) {
												setting.setColor(jsonObject.get(setting.getName()).getAsInt());
											}
										}
									}
								}
							}
						}
					});
				}
			}

		} catch (IOException | JsonSyntaxException e) {
		}
	}

	/**
	 * Json one love - three hundred time laterrr this workkkk
	 */
	public void saveConfig() {
		try {
			if (!configFile.exists())
				configFile.createNewFile();
			JsonObject json = new JsonObject();
			for (ConfigData data : data) {
				JsonObject dataJson = new JsonObject();
				JsonArray jsonArray = new JsonArray();
				dataJson.addProperty("Enabled", Boolean.valueOf(data.isEnabled()));
				dataJson.addProperty("Visible", Boolean.valueOf(data.isVisible()));
				dataJson.addProperty("Key", Integer.valueOf(data.getKey()));
				for (me.infinity.features.Module module : InfMain.getModuleManager().getList()) {
					List<Settings> settings = module.getSettings();
					if (settings != null) {
						for (Settings setting : settings) {
							if (data.getName().equalsIgnoreCase(setting.getModule().getName())) {
								if (setting.isBoolean())
									dataJson.addProperty(setting.getName(), setting.isToggle());
								else if (setting.isValueDouble()) {
									dataJson.addProperty(setting.getName(), setting.getCurrentValueDouble());
								} else if (setting.isValueFloat()) {
									dataJson.addProperty(setting.getName(), setting.getCurrentValueFloat());
								} else if (setting.isValueInt()) {
									dataJson.addProperty(setting.getName(), setting.getCurrentValueInt());
								} else if (setting.isMode()) {
									dataJson.addProperty(setting.getName(), setting.getCurrentMode());
								} else if (setting.isColor()) {
									dataJson.addProperty(setting.getName(), setting.getColor().getRGB());
								} else if (setting.isBlock()) {
									for (Block blocks : setting.getBlocks()) {
										jsonArray.add(Block.getRawIdFromState(blocks.getDefaultState()));
									}
									dataJson.add("Blocks", jsonArray);
								}
							}
						}
					}
				}
				json.add(data.getName(), dataJson);
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
