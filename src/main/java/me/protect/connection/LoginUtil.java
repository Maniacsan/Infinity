package me.protect.connection;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.infinity.utils.ConnectUtil;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import me.protect.imain.ILogin;

public class LoginUtil implements ILogin {

	private Auth auth;

	@Override
	public Auth login(String username, String password) {
		try {
			String url = "https://whyuleet.ru/community/";
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date();

			ConnectUtil.httpsSertificate();

			HttpResponse<String> response = Unirest.post(url + "entry/signInWithHWID")
					.field("ClientHour", formatter.format(date)).field("Email", username).field("Password", password)
					.field("Sign In", "Sign In").field("DeliveryType", "VIEW").field("DeliveryMethod", "JSON")
					.field("hwid", Protect.HWID.getHWID()).asString();

			JSONObject responseJSON = new JSONObject(response.getBody());

			String result = String.valueOf(responseJSON.getBoolean("FormSaved"));
			String[] split = responseJSON.getString("RedirectUrl").replace(url, "").split(":");

			Protect.CHECK.setResult(() -> result, split[0], split[1], split[2]);

			if (responseJSON.getBoolean("FormSaved")) {
				return auth = new Auth(AuthType.valueOf("SUCCESS"), username, password);
			} else
				return auth = new Auth(AuthType.valueOf("NOLICENSE"), username, password);

		} catch (Exception ex) {
			return auth = new Auth(AuthType.valueOf("ERROR"), username, password);
		}

	}

	public Auth getAuth() {
		return auth;
	}

	@Override
	public void setAuth(Auth auth) {
		this.auth = auth;
	}

}
