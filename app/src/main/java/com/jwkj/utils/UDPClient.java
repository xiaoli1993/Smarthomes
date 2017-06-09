package com.jwkj.utils;

import android.content.Intent;

import com.jwkj.global.Constants;
import com.nuowei.smarthome.MyApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient {
	private static UDPClient UDPc = null;
	DatagramSocket datagramSocket = null;

	private UDPClient() {
	};

	public synchronized static UDPClient getInstance() {
		if (null == UDPc) {
			synchronized (UDPClient.class) {
				UDPc = new UDPClient();
			}
		}
		return UDPc;
	}

	public static void send(byte[] messgae, int port, String ip) throws Exception {
		int server_port = port;
		DatagramSocket s = new DatagramSocket();
		InetAddress local = null;
			// 换成服务器端IP
		local = InetAddress.getByName(ip);
		DatagramPacket p = new DatagramPacket(messgae, messgae.length, local,server_port);
		if(s!=null){
			s.send(p);
		}
	}

	public void startListner(final int port) {
		new Thread() {
			@Override
			public void run() {
				listner(port);
			}
		}.start();
	}

	private void listner(int port) {
		// UDP服务器监听的端口
		// 接收的字节大小，客户端发送的数据不能超过这个大小
		byte[] message = new byte[256];
		try {
			// 建立Socket连接
			datagramSocket = new DatagramSocket(port);
			DatagramPacket datagramPacket = new DatagramPacket(message,
					message.length);
			try {
				while (true) {
					// 准备接收数据
					datagramSocket.receive(datagramPacket);
					sendInitPwd(message[0], message[1]);
//					Log.e("dxsUDP", "data-->" + Arrays.toString(message));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void sendInitPwd(int cmd, int result) {
		if (cmd == 49) {
			Intent i = new Intent();
			i.setAction(Constants.P2P.RET_SET_INIT_PASSWORD);
			i.putExtra("result", result);
			MyApplication.app.sendBroadcast(i);
		}
	}
	public void StopListen() {
		if (null != datagramSocket) {
			datagramSocket.close();
			datagramSocket = null;
		}
	}


}
