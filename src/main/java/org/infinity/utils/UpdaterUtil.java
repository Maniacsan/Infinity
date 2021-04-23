package org.infinity.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.infinity.InfMain;
import org.infinity.ui.UpdateUI;

import me.protect.Protect;

public class UpdaterUtil {

	public static boolean checkUpdate() {
		String currentVersion = InfMain.getVersion();

		if (getUpdateVersion().equalsIgnoreCase(currentVersion)) {
			return true;
		} else {

			Helper.openScreen(new UpdateUI());

			(new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1500);
						
						downloadClient();
					} catch (Exception e) {

					}
				}
			}).start();
			return false;
		}
	}

	public static String getUpdateVersion() {
		String version = "";
		try {
			httpsSertificate();
			version = readURLToString("https://whyuleet.ru/infinity/version/version.txt");
			version = version.replace("Version: ", "");

		} catch (Exception e) {

		}
		return version.replaceAll("[\\\r\\\n]+", "");
	}

	public static String readURLToString(String url) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		}
	}

	public static void downloadClient() {
		new Thread(() -> {

			try {
				File path = new File(Helper.minecraftClient.runDirectory, "mods");
				File file = new File(path, "Infinity-" + getUpdateVersion() + ".jar");
				httpsSertificate();
				if (Protect.DOWNLOADER.download(
						new URL("https://whyuleet.ru/infinity/jar/Infinity-" + getUpdateVersion() + ".jar"),
						file) != null) {
					seldDestructionJar();
					Helper.minecraftClient.stop();
				}
			} catch (Exception e) {
			}
		}).start();
	}

	public static void seldDestructionJar() {
		try {
			File infJar = new File(InfMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			infJar.delete();
		} catch (URISyntaxException e) {
		}

	}

	public static void httpsSertificate() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

	}

}