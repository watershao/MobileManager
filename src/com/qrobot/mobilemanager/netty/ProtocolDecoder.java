package com.qrobot.mobilemanager.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.channel.Channel;

public class ProtocolDecoder extends FrameDecoder {
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {
		if (buffer.readableBytes()<2)
			return null;
		int len = msg_get_length(buffer);
		if (buffer.readableBytes()<len)
			return null;
		int nFunction = ProtocolResult.readInt(buffer);
		ProtocolResult result = null;
        if (nFunction==Command.IM_FUNC_LOGIN_RESULT.getValue())
        {
        	result = new MsgLoginResult(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_LOGINOUT_NOTIFY.getValue())
        {
        	result = new MsgLoginoutNotify(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_GET_BATCH_USERS_ACK.getValue())
        {
        	result = new MsgGetBachUserAck(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_CHAT.getValue())
        {
        	result = new MsgGetChatData(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_USERDATA.getValue())
        {
        	result = new MsgGetUserData(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_SET_CLOCKDATA.getValue())
        {
        	result = new MsgGetClockData(nFunction, buffer);
        }
        else if (nFunction==Command.IM_FUNC_OPERATION_ERROR.getValue())
        {
        	result = new MsgErrorResult(nFunction, buffer);
        }
		return result;
	}
	
	private static int msg_get_length(ChannelBuffer buffer) {
		int len = buffer.readByte() << 8;
		len |= buffer.readByte();
		return len;
	}
	
}