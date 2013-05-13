package com.qrobot.mobilemanager.netty;

import static org.jboss.netty.channel.Channels.*;
  
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.string.StringEncoder;

  /**
   * Creates a newly configured {@link ChannelPipeline} for a new channel.
   */
  public class QRClientPipelineFactory implements
          ChannelPipelineFactory {
  
	  private QRClientService service; 
	  public QRClientPipelineFactory(QRClientService  service) {
		  this.service = service;
	  }
      public ChannelPipeline getPipeline() throws Exception {
          // Create a default pipeline implementation.
          ChannelPipeline pipeline = pipeline();
          QRClientHandler clientHandler = new QRClientHandler();
          if (service!=null)
        	  clientHandler.setIMNotify(service);
          // Add the text line codec combination first,
          //pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
          //        8192, Delimiters.lineDelimiter()));
          pipeline.addLast("decoder", new ProtocolDecoder());
          pipeline.addLast("encoder", new StringEncoder());
  
          // and then business logic.
          pipeline.addLast("handler", clientHandler);
  
          return pipeline;
      }
  }