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

package com.appleframework.server.netty.protocol;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Created by ohun on 2015/12/19.
 * length(4)+cmd(1)+cc(2)+flags(1)+sessionId(4)+lrc(1)+body(n)
 *
 * @author ohun@live.cn
 */
@SuppressWarnings("unchecked")
public class Packet {
	
    public static final int DATA_LEN = 13;
    
    public static final byte FLAG_CRYPTO = 1;
    public static final byte FLAG_COMPRESS = 2;
    public static final byte FLAG_BIZ_ACK = 4;
    public static final byte FLAG_AUTO_ACK = 8;
    public static final byte FLAG_JSON_BODY = 16;

    public byte cmd; //命令
    transient public byte[] body;

    public Packet(byte cmd) {
        this.cmd = cmd;
    }
    
    public Packet() {
    }

    public Packet(Command cmd) {
        this.cmd = cmd.cmd;
    }

    public int getBodyLength() {
        return body == null ? 0 : body.length;
    }

    public <T> T getBody() {
        return (T) body;
    }

    public <T> void setBody(T body) {
        this.body = (byte[]) body;
    }

    public short calcCheckCode() {
        short checkCode = 0;
        if (body != null) {
            for (int i = 0; i < body.length; i++) {
                checkCode += (body[i] & 0x0ff);
            }
        }
        return checkCode;
    }

    public InetSocketAddress sender() {
        return null;
    }

    public void setRecipient(InetSocketAddress sender) {
    }

    public Packet response(Command command) {
        return new Packet(command);
    }

    public Object toFrame(Channel channel) {
        return this;
    }

    public static Packet decodePacket(Packet packet, ByteBuf in, int bodyLength) {
    	int dataLen = in.readableBytes();
		byte[] request = new byte[dataLen];// 接收byte数据
		in.readBytes(request);		
        packet.body = request;
        packet.cmd = request[0];
        return packet;
    }
    
    public static Packet decodePacket(Packet packet, byte[] request) {
        packet.body = request;
        packet.cmd = request[0];
        return packet;
    }

    public static void encodePacket(Packet packet, ByteBuf out) {
        /*if (packet.cmd == Command.HEARTBEAT.cmd) {
            out.writeByte(Packet.);
        } else {
            out.writeInt(packet.getBodyLength());
            out.writeByte(packet.cmd);
            out.writeShort(packet.cc);
            out.writeByte(packet.flags);
            out.writeInt(packet.sessionId);
            out.writeByte(packet.lrc);
            if (packet.getBodyLength() > 0) {
                out.writeBytes(packet.body);
            }
        }*/
        packet.body = null;
    }

    
}
