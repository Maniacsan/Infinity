package org.infinity.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.infinity.features.component.macro.Macro;
import org.infinity.main.InfMain;
import org.infinity.utils.system.FileUtil;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class MacrosFile {

	public static File dir = new File(InfMain.getDirection() + File.separator);
	public static File macroFile = FileUtil.createJsonFile(dir, "macro");

	public void loadMacro() {
		try {
			String text = FileUtil.readFile(macroFile.getAbsolutePath(), StandardCharsets.UTF_8);
			if (text.isEmpty())
				return;

			JsonObject configurationObject = new GsonBuilder().create().fromJson(text, JsonObject.class);

			if (configurationObject == null)
				return;

			for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
				if (entry.getValue() instanceof JsonObject) {
					JsonObject jsonObject = (JsonObject) entry.getValue();
					InfMain.getMacroManager().getList()
							.add(new Macro(jsonObject.get("Message").getAsString(), jsonObject.get("Key").getAsInt()));
				}
			}
		} catch (IOException | JsonSyntaxException e) {
		}
	}

	public void saveMacro() {
		if (!dir.exists())
			dir.mkdirs();

		JsonObject json = new JsonObject();
		for (Macro macro : InfMain.getMacroManager().getList()) {
			JsonObject macroJson = new JsonObject();
			macroJson.addProperty("Key", macro.getKey());
			macroJson.addProperty("Message", macro.getMessage());
			json.add("Macro", macroJson);
		}
		FileUtil.saveJsonObjectToFile(json, macroFile);
	}

}
