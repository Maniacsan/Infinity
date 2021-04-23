package me.protect.connection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import me.protect.imain.ILogin;

public class LoginUtil implements ILogin {

	private Auth auth;
	public static ArrayList<String> username = new ArrayList<>();
	public static ArrayList<String> password = new ArrayList<>();

	@Override
	public Auth login(String username, String password) {
		try {
			String url = "https://whyuleet.ru/community/";
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date();

			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.post(url + "entry/signInWithHWID")
					.field("ClientHour", formatter.format(date)).field("Email", username).field("Password", password)
					.field("Sign In", "Sign In").field("DeliveryType", "VIEW").field("DeliveryMethod", "JSON")
					.field("hwid", Protect.HWID.getHWID()).asString();

			JSONObject responseJSON = new JSONObject(response.getBody());

			String result = String.valueOf(responseJSON.getBoolean("FormSaved"));
			String userName = responseJSON.getString("RedirectUrl").replace(url, "");

			if (responseJSON.getBoolean("FormSaved")) {
				if (LoginUtil.username.size() == 0)
					LoginUtil.username.add(username);

				if (LoginUtil.password.size() == 0)
					LoginUtil.password.add(password);

				Protect.CHECK.setResult(() -> result, userName);

				return auth = new Auth(AuthType.valueOf("SUCCESS"));
			} else
				return auth = new Auth(AuthType.valueOf("NOLICENSE"));

		} catch (Exception ex) {
			return auth = new Auth(AuthType.valueOf("ERROR"));
		}
	}

	public Auth getAuth() {
		return auth;
	}

}
