package com.qrobot.mobilemanager.netty;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
* Handles a client-side channel.
*/
public class QRClientHandler extends SimpleChannelUpstreamHandler {


	private QrobotIMNotify imNotify;
	
	public void setIMNotify(QrobotIMNotify imNotify)
	{
		this.imNotify = imNotify;
	}
   @Override
   public void handleUpstream(
           ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
       if (e instanceof ChannelStateEvent) {
    	   System.out.println("handleUpstream:"+e.toString());
       }
       super.handleUpstream(ctx, e);
   }

   @Override
   public void messageReceived(
           ChannelHandlerContext ctx, MessageEvent e) {
	   ProtocolResult result = (ProtocolResult) e.getMessage();  
       if (result instanceof MsgLoginResult) {  
    	   MsgLoginResult msgloginResult = (MsgLoginResult)result;
    	   int ret = msgloginResult.getResult();
    	   if (imNotify!=null)
    		   imNotify.IMLoginNotify(ret);
       } 
       if (result instanceof MsgLoginoutNotify) {  
    	   MsgLoginoutNotify msgloginoutNotify = (MsgLoginoutNotify)result;
    	   int robotNo = msgloginoutNotify.getRobotNo();
    	   int onLine = msgloginoutNotify.getNotify();
    	   if (imNotify!=null)
    		   imNotify.IMUserStatusNotify(robotNo, onLine);
       }  
       if (result instanceof MsgGetBachUserAck) {  
    	   MsgGetBachUserAck msggetBatchUsersNotify = (MsgGetBachUserAck)result;
    	   int nPageNo = msggetBatchUsersNotify.getPageNo();
    	   int nPageSize = msggetBatchUsersNotify.getPageSize();
    	   int nCount = msggetBatchUsersNotify.getCount();
    	   ArrayList<MsgUserStatus> lstUserStatus = msggetBatchUsersNotify.getUserStatus();
    	   if (imNotify!=null)
    		   imNotify.IMBatchUserNotify(nPageNo, nPageSize, lstUserStatus, nCount);
       }
       if (result instanceof MsgGetChatData) {  
    	   MsgGetChatData msggetchatdata = (MsgGetChatData)result;
    	   int robotNo = msggetchatdata.getRobotNo();
    	   int msgSize = msggetchatdata.getMsgSize();
    	   byte[] arrMsg = msggetchatdata.getMsg();
    	   String msg = new String(arrMsg, Charset.forName("UTF-8"));
    	   if (imNotify!=null)
    		   imNotify.IMUserDataNotify(robotNo, msg, msgSize);
       }  
       if (result instanceof MsgGetClockData) {  
    	   MsgGetClockData msggetuserdata = (MsgGetClockData)result;
    	   int robotNo = msggetuserdata.getRobotNo();
    	   int dataSize = msggetuserdata.getDataSize();
    	   byte[] clockData = msggetuserdata.getClockData();
    	   if (imNotify!=null)
    		   imNotify.IMClockDataNotify(robotNo, clockData, dataSize);
       }  
       if (result instanceof MsgGetUserData) {  
    	   MsgGetUserData msggetuserdata = (MsgGetUserData)result;
    	   int robotNo = msggetuserdata.getRobotNo();
    	   int dataSize = msggetuserdata.getDataSize();
    	   byte[] msg = msggetuserdata.getMsg();
    	   if (imNotify!=null)
    		   imNotify.IMUserDataNotify(robotNo, msg, dataSize);
       }  
       if (result instanceof MsgErrorResult) {  
    	   MsgErrorResult msgerrordata = (MsgErrorResult)result;
    	   int errCode = msgerrordata.getErrCode();
    	   byte[] msg = msgerrordata.getErrMsg();
    	   if (imNotify!=null)
    		   imNotify.IMErrorNotify(errCode, msg);
       }  
   }

   @Override
   public void exceptionCaught(
           ChannelHandlerContext ctx, ExceptionEvent e) {
	   System.out.println("exceptionCaught:"+e.getCause());
       e.getChannel().close();
   }
}