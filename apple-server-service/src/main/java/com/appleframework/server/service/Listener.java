package com.appleframework.server.service;

public interface Listener {
	
    void onSuccess(Object... args);

    void onFailure(Throwable cause);
}