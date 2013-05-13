package com.qrobot.mobilemanager.netty;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;


/**
 * Simplistic QR client.
 */
public class QRClient {

	private final String host;
	private final int port;
	private QRClientService service;
	private Channel channel;
	private ClientBootstrap bootstrap;
	private int robotNo;
	
	public QRClient(QRClientService  service, String host, int port) {
		this.service = service;
		this.host = host;
		this.port = port;
		this.channel = null;
		this.bootstrap = null;
		this.robotNo = 0;
		
	}
	
	public int login(int robotNo) {
//		if (robotNo<=0)
//			return -1;
		this.robotNo = robotNo;
		if (channel==null || !channel.isConnected()) {
			bootstrap = new ClientBootstrap(
					new NioClientSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));

			// Configure the pipeline factory.
			bootstrap.setPipelineFactory(new QRClientPipelineFactory(service));
			bootstrap.setOption("tcpNoDelay", true);  
	        bootstrap.setOption("keepAlive", true);  
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
					port));

			// Wait until the connection attempt succeeds or fails.
			channel = future.awaitUninterruptibly().getChannel();
			if (!future.isSuccess()) {
				future.getCause().printStackTrace();
				bootstrap.releaseExternalResources();
				return -1;
			}
		}	
		ChannelFuture lastWriteFuture = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("tgw_l7_forward\r\nHost: ").append(host).append(":")
				.append(port).append("\r\n\r\n");
		String strTGWHead = strbuf.toString();
		channel.write(strTGWHead);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		int function = Command.IM_FUNC_LOGIN.getValue();
		short size = 8;
		MsgLogin msglogin = new MsgLogin(function, size, robotNo);
		ChannelBuffer buffer = ChannelBuffers.buffer(msglogin.getBuf().length);
		buffer.writeBytes(msglogin.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}
	
	public int relogin() {
		if (robotNo<=0)
			return -1;
		if (channel==null || !channel.isConnected()) {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
					port));

			// Wait until the connection attempt succeeds or fails.
			channel = future.awaitUninterruptibly().getChannel();
			if (!future.isSuccess()) {
				future.getCause().printStackTrace();
				bootstrap.releaseExternalResources();
				return -1;
			}
		}	
		ChannelFuture lastWriteFuture = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("tgw_l7_forward\r\nHost: ").append(host).append(":")
				.append(port).append("\r\n\r\n");
		String strTGWHead = strbuf.toString();
		channel.write(strTGWHead);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		int function = Command.IM_FUNC_LOGIN.getValue();
		short size = 8;
		MsgLogin msglogin = new MsgLogin(function, size, robotNo);
		ChannelBuffer buffer = ChannelBuffers.buffer(msglogin.getBuf().length);
		buffer.writeBytes(msglogin.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}
	
	public int getBatchUsers(int pageSize, int pageNo) {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_GET_BATCH_USERS.getValue();
		short size = 12;
		MsgGetBatchUsers msgGetBatchUsers = new MsgGetBatchUsers(function, size, pageSize, pageNo);
		ChannelBuffer buffer = ChannelBuffers.buffer(msgGetBatchUsers.getBuf().length);
		buffer.writeBytes(msgGetBatchUsers.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}
	//
	public int sendChat(int robotNo, String msg, int msgSize) {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_CHAT.getValue();
		byte[] bytMsg = msg.getBytes(Charset.forName("UTF-8"));
		int len = bytMsg.length;
		short size = (short)(12 + len);
		MsgImChat msgchatdata = new MsgImChat(function, size, robotNo, len,  bytMsg);
		ChannelBuffer buffer = ChannelBuffers.buffer(msgchatdata.getBuf().length);
		buffer.writeBytes(msgchatdata.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}
	
	public int sendUserData(int robotNo, byte[] msg, int dataSize) {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_USERDATA.getValue();
		short size = (short)(12 + dataSize);
		MsgSendUserData msguserdata = new MsgSendUserData(function, size, robotNo, dataSize, msg);
		ChannelBuffer buffer = ChannelBuffers.buffer(msguserdata.getBuf().length);
		buffer.writeBytes(msguserdata.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}
	
/*	public int setClockData(int robotNo, byte[] clockData, int dataSize) {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_SET_CLOCKDATA.getValue();
		short size = (short)(8 + dataSize);
		MsgSetClockData msgclockdata = new MsgSetClockData(function, size, robotNo, clockData, dataSize);
		ChannelBuffer buffer = ChannelBuffers.buffer(msgclockdata.getBuf().length);
		buffer.writeBytes(msgclockdata.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}*/
	
	public int setClockData(int robotNo, byte[] clockData, int dataSize) {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_SET_CLOCKDATA.getValue();
		short size = (short)(12 + dataSize);
		MsgSendUserData msguserdata = new MsgSendUserData(function, size, robotNo, dataSize, clockData);
		ChannelBuffer buffer = ChannelBuffers.buffer(msguserdata.getBuf().length);
		buffer.writeBytes(msguserdata.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}

	
	public int sendHeartBeat() {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_HEART_BEAT.getValue();
		short size = 8;
		MsgHeartBeat msgHeartBeat = new MsgHeartBeat(function, size, 0);
		ChannelBuffer buffer = ChannelBuffers.buffer(msgHeartBeat.getBuf().length);
		buffer.writeBytes(msgHeartBeat.getBuf());
		lastWriteFuture = channel.write(buffer);
		
		return 1;
	}

	
	public int logout() {
		if (channel==null)
			return -1;
		if (!channel.isConnected())
		{
			int ret = relogin();
			if (ret!=1)
				return ret;
		}
		ChannelFuture lastWriteFuture = null;
		int function = Command.IM_FUNC_LOGOUT.getValue();
		short size = 8;
		MsgLogout msglogout = new MsgLogout(function, size, 0);
		ChannelBuffer buffer = ChannelBuffers.buffer(msglogout.getBuf().length);
		buffer.writeBytes(msglogout.getBuf());
		lastWriteFuture = channel.write(buffer);
		if (lastWriteFuture != null) {
			lastWriteFuture.awaitUninterruptibly();
		}
		channel.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
		channel = null;
		bootstrap = null;
		return 1;
	}

	/**
	 * 连续发送数据至服务器
	 * 
	 * @author simple
	 * 
	 */
	class MyWorkThread extends Thread {
		private Channel channel;
		private ClientBootstrap bootstrap;

		public MyWorkThread(ClientBootstrap bootstrap, Channel channel) {
			super();
			this.channel = channel;
			this.bootstrap = bootstrap;
		}

		@Override
		public void run() {
			ChannelFuture lastWriteFuture = null;
			int i = 0;
			// TODO Auto-generated method stub
			if (true) {
				int function = Command.IM_FUNC_LOGIN.getValue();
				short size = 8;
				int robotNo = 7002;
				MsgLogin msglogin = new MsgLogin(function, size, robotNo);

				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				// Sends the received line to the server.
				byte[] buf = msglogin.getBuf();
				ChannelBuffer buffer = ChannelBuffers.buffer(msglogin.getBuf().length);
				buffer.writeBytes(msglogin.getBuf());
				lastWriteFuture = channel.write(buffer);

				// If user typed the 'bye' command, wait until the server closes
				// the connection.
				
				 
				 channel.getCloseFuture().awaitUninterruptibly(); 
				 
			}
			if (lastWriteFuture != null) {
				lastWriteFuture.awaitUninterruptibly();
			}
			channel.close().awaitUninterruptibly();
			bootstrap.releaseExternalResources();
		}

	}
}