package org.infinity.features.component.cape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.system.Http;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Capes {

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

	public void register(String cape) {
		register(Helper.MC.getSession().getUsername(), cape);
	}

	public void register(String name, String cape) {
		try {
			CURRENT_CAPE = cape;
			Unirest.post(ADD_URL).field("name", name).field("cape", cape).asString();
			InfMain.INSTANCE.SETTINGS.save();
			InfMain.getUser().setUsername(name);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		destroy(Helper.MC.getSession().getUsername());
	}

	public void destroy(String name) {
		try {
			CURRENT_CAPE = "";
			Unirest.post(DELETE_URL).field("name", name).asString();
			InfMain.INSTANCE.SETTINGS.save();
			InfMain.getUser().setUsername(name);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		update(Helper.MC.getSession().getUsername());
	}

	public void update(String name) {
		if (CURRENT_CAPE != null && !CURRENT_CAPE.isEmpty() && !locked(CURRENT_NAME))
			register(name, CURRENT_CAPE);
		else if (CURRENT_CAPE.isEmpty())
			destroy(name);
	}

	public void remove(String name) {
		try {
			Unirest.post(DELETE_URL).field("name", name).asString();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void initCapes() {
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

	public boolean locked(String capeName) {
		boolean premium = premiumCape(capeName);
		boolean admin = adminCape(capeName);
		boolean moderator = moderatorCape(capeName);
		boolean youtube = youtubeCape(capeName);

		if (Helper.isUser())
			return premium || admin || moderator || youtube;

		if (Helper.isPremium())
			return admin || moderator || youtube;

		if (Helper.isModerator() || Helper.isYouTube())
			return admin;

		return false;
	}

	public boolean premiumCape(String capeName) {
		var premiumCapes = capes.stream().filter(c -> c.getRole().equals(Role.PREMIUM)).map(Cape::getName).toList();
		return premiumCapes.contains(capeName);
	}

	public boolean moderatorCape(String capeName) {
		var moderatorCapes = capes.stream().filter(c -> c.getRole().equals(Role.MODERATOR)).map(Cape::getName).toList();
		return moderatorCapes.contains(capeName);
	}

	public boolean youtubeCape(String capeName) {
		var youtubeCapes = capes.stream().filter(c -> c.getRole().equals(Role.YOUTUBE)).map(Cape::getName).toList();
		return youtubeCapes.contains(capeName);
	}

	public boolean adminCape(String capeName) {
		var adminCapes = capes.stream().filter(c -> c.getRole().equals(Role.ADMIN)).map(Cape::getName).toList();
		return adminCapes.contains(capeName);
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
