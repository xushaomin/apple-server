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

package com.appleframework.server.test.handler;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.server.netty.connection.Connection;
import com.appleframework.server.netty.message.MessageHandler;
import com.appleframework.server.netty.protocol.Packet;
import com.appleframework.server.test.utils.HexUtil;

/**
 * Created by ohun on 2015/12/23.
 *
 * @author ohun@live.cn
 */
public final class B15Handler implements MessageHandler {
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(Packet packet, Connection connection) {
        //connection.send(packet);//ping -> pong
        //Logs.HB.info("ping -> pong, {}", connection);
    	byte[] req = packet.getBody();
    	int bufLen = req.length;
    	
    	boolean isReceiving = false;//是否识别了数据头
        int beginIdx = 0;//此条有效指令的起始索引位置
        int sumLen = 0;//单条数据的长度(分包用到)
        for (int i = 0; i < bufLen; i++) {
            byte b = req[i];
            byte nextByte = i == bufLen-1 ? 0 : req[i+1];
            
            // 检查协议分隔符 "["
            if ((b == 0x78 && nextByte == 0x78)) {
                beginIdx = i;
                isReceiving = true;
                sumLen =i+ 2 + 1 + req[i+2] + 2;
            }else if(b == 0x79 && nextByte == 0x79){
                beginIdx = i;
                isReceiving = true;
                byte[] tempBt = new byte[]{req[i+2],req[i+3]};
                //sumLen =i+ 2 + 2 + Integer.parseInt(HexUtil.bin2hexstr(tempBt), 16) + 2;
            }
            
            if (!isReceiving) {
                continue;
            }
            if(i == sumLen-1){
                isReceiving = false;//分包处理，重置isReceiving
                // 取得消息指令
                byte[] bs = ArrayUtils.subarray(req, beginIdx, sumLen);
                String command = HexUtil.bin2hexstr(bs);
                logger.info("Server收到015协议数据:{}" ,command);
                //BY015 translateDevice015 = new BY015(bs);
                //handleBY015(remoteClient,bs,translateDevice015,ctx);
            }
            //处理分包情况
            if(isReceiving && i == bufLen-1 && beginIdx >0){
                //SelectorServer.packetSplicingData.put( remoteClient, ArrayUtils.subarray(req, beginIdx, i) );
            }
        }
    }
}