package org.infinity.protect.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.infinity.event.TickEvent;
import org.infinity.main.InfMain;
import org.infinity.protect.Handler;
import org.infinity.utils.ConnectUtil;
import org.infinity.utils.Helper;
import org.json.JSONObject;

import com.darkmagician6.eventapi.EventTarget;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import me.protect.Protect;
import me.protect.connection.Auth;
import me.protect.connection.Auth.AuthType;

public class ReLoginProcess extends Handler {

	@EventTarget
	public void onTick(TickEvent event) {
		if (Helper.getWorld() != null) {
			if (InfMain.reLogin) {
				if (ConnectUtil.checkJarSize()) {
					if (Protect.LOGIN.getAuth().getUsername() == null
							|| Protect.LOGIN.getAuth().getPassword() == null) {
						me.protect.utils.PHelper.makeCrash();
						return;
					}

					if (login(Protect.LOGIN.getAuth().getUsername(), Protect.LOGIN.getAuth().getPassword()) != null) {

						if (Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {
							InfMain.reLogin = false;
							setInit(false);
						} else
							me.protect.utils.PHelper.makeCrash();
					} else {
						me.protect.utils.PHelper.makeCrash();
					}
				}
			}
		}
	}

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

			if (responseJSON.getBoolean("FormSaved")) {
				Auth authSuccess = new Auth(AuthType.valueOf("SUCCESS"), username, password);

				Protect.LOGIN.setAuth(authSuccess);

				return authSuccess;
			} else {
				Auth noLicense = new Auth(AuthType.valueOf("NOLICENSE"), username, password);
				Protect.LOGIN.setAuth(noLicense);

				return noLicense;
			}

		} catch (Exception ex) {
			Auth error = new Auth(AuthType.valueOf("ERROR"), username, password);
			Protect.LOGIN.setAuth(error);

			return error;
		}

	}

}
