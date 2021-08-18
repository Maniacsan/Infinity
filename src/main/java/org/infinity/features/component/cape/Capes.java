package org.infinity.features.component.cape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.infinity.features.component.cape.cape.CapeProvider;
import org.infinity.features.component.cape.config.Config;
import org.infinity.features.component.cape.mixinterface.PlayerSkinProviderAccess;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.system.Http;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

public class Capes {

	private static Config config;

	public String CURRENT_CAPE = "";
	public String CURRENT_NAME = "";

	private List<Cape> capes = new ArrayList<>();
	private String CAPES = "https://whyuleet.ru/infinity/api/cape/capes.json";
	private String ADD_URL = "https://whyuleet.ru/infinity/api/cape/cape-add.php";
	private String DELETE_URL = "https://whyuleet.ru/infinity/api/cape/cape-delete.php";

	public Role role;

	public void setCurrent(String name, String url) {
		CURRENT_NAME = name;
		CURRENT_CAPE = url;
	}

	public void addCape(String cape) {
		GameProfile profile = Helper.MC.getSession().getProfile();
		addCape(profile.getId().toString(), cape);
	}

	public void deleteCape() {
		GameProfile profile = Helper.MC.getSession().getProfile();
		deleteCape(profile.getId().toString());
	}

	public void addCape(String uuid, String cape) {
		try {
			CURRENT_CAPE = cape;
			Unirest.post(ADD_URL).field("uuid", uuid).field("cape", cape).asString();
			InfMain.INSTANCE.SETTINGS.save();
			InfMain.getUser().setUUID(uuid);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void deleteCape(String uuid) {
		try {
			CURRENT_CAPE = "";
			Unirest.post(DELETE_URL).field("uuid", uuid).asString();
			InfMain.INSTANCE.SETTINGS.save();
			InfMain.getUser().setUUID(uuid);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void updateCape() {
		if (CURRENT_CAPE != null && !CURRENT_CAPE.isEmpty() && !isLocked(CURRENT_NAME))
			addCape(CURRENT_CAPE);
	}

	public void updateCapes() {
		capes.clear();

		Stream<String> lines = Http.get(CAPES).sendLines();
		if (lines != null)
			lines.forEach(s -> {
				String[] split = s.split(" ");
				String name = split[0];
				String role = split.length >= 3 ? split[2].toUpperCase() : "USER";

				if (split.length >= 2) {
					capes.add(new Cape(name, split[1], role));
				}
			});
	}

	public List<Cape> getCapes() {
		return capes;
	}

	public boolean isLocked(String capeName) {
		boolean isPremium = isPremium(capeName);
		boolean isAdmin = isAdmin(capeName);
		boolean isModerator = isModerator(capeName);
		boolean isYoutube = isYoutube(capeName);

		if (Helper.isUser()) {
			if (isPremium || isAdmin || isModerator || isYoutube)
				return true;
		}
		if (Helper.isPremium()) {
			if (isAdmin || isModerator || isYoutube)
				return true;
		}

		if (Helper.isModerator() || Helper.isYouTube()) {
			if (isAdmin)
				return true;
		}

		return false;
	}

	public boolean isPremium(String capeName) {
		List<Cape> filteredCapes = capes.stream().filter(c -> c.getRole() == Role.PREMIUM).collect(Collectors.toList());
		for (Cape cape : filteredCapes) {
			if (cape.getName().equalsIgnoreCase(capeName))
				return true;
		}
		return false;
	}

	public boolean isModerator(String capeName) {
		List<Cape> filteredCapes = capes.stream().filter(c -> c.getRole() == Role.MODERATOR).collect(Collectors.toList());
		for (Cape cape : filteredCapes) {
			if (cape.getName().equalsIgnoreCase(capeName))
				return true;
		}
		return false;
	}

	public boolean isYoutube(String capeName) {
		List<Cape> filteredCapes = capes.stream().filter(c -> c.getRole() == Role.YOUTUBE).collect(Collectors.toList());
		for (Cape cape : filteredCapes) {
			if (cape.getName().equalsIgnoreCase(capeName))
				return true;
		}
		return false;
	}

	public boolean isAdmin(String capeName) {
		List<Cape> filteredCapes = capes.stream().filter(c -> c.getRole() == Role.ADMIN).collect(Collectors.toList());
		for (Cape cape : filteredCapes) {
			if (cape.getName().equalsIgnoreCase(capeName))
				return true;
		}
		return false;
	}

	public static Config getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	private static void loadConfig() {
		config = new Config();
	}

	public void onInitialize(MinecraftClient client) {
		PlayerSkinProviderAccess skinProviderAccess = (PlayerSkinProviderAccess) client.getSkinProvider();
		skinProviderAccess.setCapeProvider(new CapeProvider(skinProviderAccess.getSkinCacheDir(),
				skinProviderAccess.getTextureManager(), Util.getMainWorkerExecutor(), client.getNetworkProxy()));
		updateCape();
	}

	public enum Role {
		ADMIN, MODERATOR, PREMIUM, YOUTUBE, USER;
	}

	public class Cape {
		private String name;
		private String url;
		private Role role;

		public Cape(String name, String url, String role) {
			this.name = name;
			this.url = url;
			this.role = Role.valueOf(role);
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public Role getRole() {
			return role;
		}
	}
}
