package com.vz.eduwisdom.service;

import java.util.Map;

public interface FlagService {
    void updateFlag(String flag) throws Exception;

    Map<String, Object> getFlagValue() throws Exception;
}
