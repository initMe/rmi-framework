package com.manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

import com.utils.LoggerUtil;

public class RmiRemoteExecutor extends RMISocketFactory {
	private int port = 8500;
	public RmiRemoteExecutor(int port) {
		super();
		this.port = port;
	}
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		if (port == 0) {
			port = this.port;
			LoggerUtil.info(this.getClass(), "开始rmi客户端请求:[host:"+host+"; port"+port+"]");
		}
		return new Socket(host, port);
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		if (port == 0) {
			port = this.port;
			LoggerUtil.info(this.getClass(), "开始rmi服务端发布:[port"+port+"]");
		}
		return new ServerSocket(port);
	}
	
}