package com.appleframework.server.tools;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Constants {

	Charset UTF_8 = StandardCharsets.UTF_8;
    byte[] EMPTY_BYTES = new byte[0];
    String EMPTY_STRING = "";
    String ANY_HOST = "0.0.0.0";

}
