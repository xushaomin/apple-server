package com.appleframework.server.test.handler;

public enum BY015CMD {

	// 0x01 isLoginData
	// 0x22 isGpsData
	// -3   isAGps
	// 0x24 isHavingHeight
	// 0x13 isHeartbeatData
	// 0x21 isCommandResponseData
	// 0x26 isAlarmData
    
	LOGIN(0x01),
	GPS(0x22),
	APS(-3),
	HEARTBEAT(0x13),
    HAVINGHEIGHT(0x24),
    ALART(0x26),
    COMMAND(0x21);

	BY015CMD(int cmd) {
        this.cmd = (byte) cmd;
    }
    
	BY015CMD(byte cmd) {
        this.cmd = cmd;
    }

    public final byte cmd;

    public static BY015CMD toCMD(byte b) {
    	BY015CMD[] values = values();
        return values[b - 1];
    }
}
