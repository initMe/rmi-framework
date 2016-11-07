package com.utils;

import java.io.IOException;
import java.net.Socket;

/** socket工具类 */
public class SocketUtil {
	/** 检查socket连接是否正常 */
	public static boolean checkSocket(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			socket.setSoTimeout(1000*3);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
}
