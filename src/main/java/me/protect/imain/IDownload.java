package me.protect.imain;

import java.io.File;
import java.net.URL;

public interface IDownload {
	
	File download(URL url, File dstFile) throws Exception;

}
