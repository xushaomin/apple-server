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

package com.appleframework.server.event;

/**
 * 链接超时，用户解绑的时候,用户主动关闭链接，才会触发该事件
 */
public final class OfflineEvent implements Event {

    private final Object connection;
    private final String userId;

    public OfflineEvent(Object connection, String userId) {
        this.connection = connection;
        this.userId = userId;
    }

    public Object getConnection() {
        return connection;
    }

    public String getUserId() {
        return userId;
    }
}
