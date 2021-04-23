package me.protect;

import me.protect.connection.LoginUtil;
import me.protect.imain.ICheck;
import me.protect.imain.IDownload;
import me.protect.imain.IHWID;
import me.protect.imain.IImage;
import me.protect.imain.ILogin;
import me.protect.utils.Check;
import me.protect.utils.Downloader;
import me.protect.utils.ImageUtil;
import me.protect.utils.hwid.HWID;

public class Protect {

	public static String name = "SatanProtect";
	public static String version = ".1B";

	public static Protect INSTANCE = new Protect();

	public static IDownload DOWNLOADER = new Downloader();
	public static ILogin LOGIN = new LoginUtil();
	public static IHWID HWID = new HWID();
	public static IImage IMAGE = new ImageUtil();
	public static ICheck CHECK = new Check();

}
