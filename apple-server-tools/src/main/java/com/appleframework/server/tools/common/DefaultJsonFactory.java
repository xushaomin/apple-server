package com.appleframework.server.tools.common;

import com.appleframework.server.spi.Spi;
import com.appleframework.server.spi.common.Json;
import com.appleframework.server.spi.common.JsonFactory;
import com.appleframework.server.tools.Jsons;

@Spi
public final class DefaultJsonFactory implements JsonFactory, Json {
    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return Jsons.fromJson(json, clazz);
    }

    @Override
    public String toJson(Object json) {
        return Jsons.toJson(json);
    }

    @Override
    public Json get() {
        return this;
    }
}