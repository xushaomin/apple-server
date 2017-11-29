package com.appleframework.server.test.handler.by01578;

import com.appleframework.server.test.handler.ProtocolProcessing;
import com.appleframework.server.test.model.DeviceData;
import com.appleframework.server.test.utils.HexUtil;

public class BY01578LoginProtocol implements ProtocolProcessing {
	    
    @Override
	public void handler(byte[] original, String remoteAddr) {
    	String sn = this.getSn(original);
		DeviceData.add(sn, remoteAddr);
	}

	public String getSn(byte[] original) {
    	return HexUtil.toHexAsciis(original, 4, 8).replaceAll("^(0+)", "");
    }
    
   
}
