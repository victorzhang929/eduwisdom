package com.victorzhang.eduwisdom.util.query;

import java.util.Map;

public interface QueryParam extends Map<String, Object> {
    QueryParam fill(String paramString, Object paramObject);
}
