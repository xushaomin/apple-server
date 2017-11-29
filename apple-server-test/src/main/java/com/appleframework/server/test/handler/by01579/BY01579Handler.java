/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.appleframework.server.test.handler.by01579;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.server.netty.connection.Connection;
import com.appleframework.server.netty.message.MessageHandler;
import com.appleframework.server.netty.protocol.Packet;
import com.appleframework.server.test.handler.BY015CMD;
import com.appleframework.server.test.handler.ProtocolProcessing;
import com.appleframework.server.test.handler.by01579.BY01579GpsProtocol;
import com.appleframework.server.test.handler.by01579.BY01579LoginProtocol;
import com.appleframework.server.test.model.PacketSplicingData;
import com.appleframework.server.test.utils.HexUtil;
import com.appleframework.server.test.utils.NettyUtil;

/**
 * Created by ohun on 2015/12/23.
 *
 * @author ohun@live.cn
 */
public final class BY01579Handler implements MessageHandler {
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static Map<Byte, ProtocolProcessing> handlers = new HashMap<>();
    
    private static void register(BY015CMD command, ProtocolProcessing handler) {
        handlers.put(command.cmd, handler);
    }
    
    static {
    	register(BY015CMD.LOGIN, new BY01579LoginProtocol());
    	register(BY015CMD.GPS, new BY01579GpsProtocol());
    	/*register(BY015CMD.APS, "APS");
    	register(BY015CMD.HEARTBEAT, "HEARTBEAT");
    	register(BY015CMD.HAVINGHEIGHT, "HAVINGHEIGHT");
    	register(BY015CMD.ALART, "ALART");
    	register(BY015CMD.COMMAND, "COMMAND");*/
    }
    
    public static ProtocolProcessing router(Byte key) {
    	return handlers.get(key);
    }

    @Override
    public void handle(Packet packet, Connection connection) {

		byte[] req = packet.getBody();
		int bufLen = req.length;
        String remoteClient = NettyUtil.getRemoteAddress(connection);

		byte[] packetSplicingData = PacketSplicingData.get(remoteClient);
		int packetLen = packetSplicingData == null ? 0 : packetSplicingData.length;

		if (packetSplicingData != null && packetLen > 0) {
			req = ArrayUtils.addAll(packetSplicingData, req);// 合并粘包的未处理数据
			PacketSplicingData.remove(remoteClient);
		}
    	
		boolean isReceiving = false; // 是否识别了数据头
		int beginIdx = 0; // 此条有效指令的起始索引位置
		int sumLen = 0; // 单条数据的长度(分包用到)
		for (int i = 0; i < bufLen; i++) {
			byte b = req[i];
			byte nextByte = i == bufLen - 1 ? 0 : req[i + 1];

			// 检查协议分隔符 "["
			if (b == 0x79 && nextByte == 0x79) {
				beginIdx = i;
				isReceiving = true;
				byte[] tempBt = new byte[] { req[i + 2], req[i + 3] };
				sumLen = i + 2 + 2 + Integer.parseInt(HexUtil.bin2hexstr(tempBt), 16) + 2;
			}

			if (!isReceiving) {
				continue;
			}
			if (i == sumLen - 1) {
				isReceiving = false;// 分包处理，重置isReceiving
				// 取得消息指令
				byte[] bs = ArrayUtils.subarray(req, beginIdx, sumLen);
				String command = HexUtil.bin2hexstr(bs);
				logger.info("Server收到015协议数据:{}", command);
				System.out.println(command);
				// BY015 translateDevice015 = new BY015(bs);
				handle(remoteClient, bs, connection);
			}
			// 处理分包情况
			if (isReceiving && i == bufLen - 1 && beginIdx > 0) {
				PacketSplicingData.put(remoteClient, ArrayUtils.subarray(req, beginIdx, i));
			}
		}
    }
    
    private ProtocolProcessing router(byte cmd) {
    	return handlers.get(cmd);
    }
    
    private void handle(String remoteClient, byte[] command, Connection connection) {
    	
    	byte cmd = command[4];
    	
    	ProtocolProcessing handler = router(cmd);
    	
    	handler.handler(command, remoteClient);
    	//System.out.println(value);
    	//System.out.println(command[4]);
    	
    	/*System.out.println(BY015CMD.ALART.cmd);
    	System.out.println(BY015CMD.LOGIN.cmd);
    	System.out.println(BY015CMD.GPS.cmd);
    	System.out.println(BY015CMD.APS.cmd);
    	System.out.println(BY015CMD.HEARTBEAT.cmd);
    	System.out.println(BY015CMD.HAVINGHEIGHT.cmd);
    	System.out.println(BY015CMD.COMMAND.cmd);*/
    	

    			
        //if(translateDevice015.isLoginData()) {
            //SelectorServer.REMOTEADDR_TRACKERNO.put(remoteClient, translateDevice015.getSn());
            //SelectorServer.TRACKERNO_REMOTEADDR.put(translateDevice015.getSn(), remoteClient );
        //}
        //String sn = SelectorServer.REMOTEADDR_TRACKERNO.get(remoteClient);
        //translateDevice015.setSn(sn);
        //String time = DateUtil.getDateTime(new Date());
        //if(!BaseUtil.snType.containsKey(sn)){
        //    BaseUtil.snType.put(sn, SelectorServer.MODEL_015);
       // }
        
        //1.处理响应数据
       // if(translateDevice015.isCommandResponseData()) {
            //根据指令类型，获取指令描述（方便日志查看指令类型）
       //     String cmdName = getCmdNameByCode(translateDevice015.getCommandType());
            //加入日志线程队列
       //     SelectorServer.TRACKER_MESSAGE_ALL_QUEUE.add(new String[] { sn, "<-",cmdName+ "                 "  + 
       //             time + "  " + HexUtil.bin2hexstr(command).replaceAll("0+?$", "") });
            //清除指令信息
      //      removeCmd(translateDevice015.getCommandType(), sn);
            
       //     logger.info("<-" + "015 CResp:"+ HexUtil.bin2hexstr(translateDevice015.getDataSerialNumber())+ ">>" + translateDevice015.CMDResponseValueOf());
            //发送指令回复数据给平台
       //     SendReplayMQ.sendMessage(translateDevice015.CMDResponseValueOf());
            //当指令类型为读取ccid时，要把ccid值返回给平台
       //     if(translateDevice015.getCommandType()==9) {
       //         String valueOf = translateDevice015.valueOf();
       //         logger.info("<-"+ "015  Json11:"+ HexUtil.bin2hexstr(translateDevice015.getDataSerialNumber())+ ">>" + valueOf);
       //         Receiver.MQ_MSG_QUEUE.add(valueOf);
        //    }
       // } else {
       //     SelectorServer.TRACKER_MESSAGE_ALL_QUEUE.add(new String[] { sn, "<-", DateUtil.getDateTime(new Date(translateDevice015.getTime())) + "  " + 
       //             DateUtil.getDateTime(new Date(translateDevice015.getCreateTime())) + "  " +HexUtil.bin2hexstr(command).replaceAll("0+?$", "") });
       // }*/
        
        
        //logger.info("015>指令下发");
        //2.指令下发要放在解析响应指令的后面，防止循环发送-处理
        //sendZL(translateDevice015.getSn(), remoteClient,ctx);
        
        //3.处置位置数据
        /*if (translateDevice015.isMccData()){
            String valueOf = translateDevice015.valueOfMcc();
            logger.info("<-"+ "015 mcc  Json:"+ HexUtil.bin2hexstr(translateDevice015.getDataSerialNumber())+ ">>" + valueOf);
            Receiver.MQ_MSG_QUEUE.add(valueOf);
        }
        else if(translateDevice015.isAlarmData() || translateDevice015.isGpsData() || translateDevice015.isHeartbeatData() || translateDevice015.isLoginData()) {
            String valueOf = translateDevice015.valueOf();
            logger.info("<-"+ "015  Json:"+ HexUtil.bin2hexstr(translateDevice015.getDataSerialNumber())+ ">>" + valueOf);
            
            Receiver.MQ_MSG_QUEUE.add(valueOf);
        }*/
        //4.响应终端，通知已经收到发送的数据
        /*if(translateDevice015.isLoginData() || translateDevice015.isAlarmData() || translateDevice015.isHeartbeatData() || translateDevice015.isAGps()) {
            byte[] response = translateDevice015.getResponse();
            SelectorServer.TRACKER_MESSAGE_ALL_QUEUE.add(new String[] { sn, "->", DateUtil.getDateTime(new Date(translateDevice015.getTime())) + "  " + 
                    DateUtil.getDateTime(new Date(translateDevice015.getCreateTime())) + "  " +HexUtil.bin2hexstr(response) });
            SelectorServer.writeMsgToChannel(ctx, response);
        }*/
    }
}