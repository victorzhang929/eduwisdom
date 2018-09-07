package com.vz.eduwisdom.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.vz.eduwisdom.domain.Log;

public interface LogService extends BaseService<Log, String> {
    boolean saveLogByLogTypeAndLogContent(String logType, String logContent, String userId) throws Exception;

    boolean saveLogByLogTypeAndLogContent(String logType, String logContent) throws Exception;

    List<Map<String, Object>> listLogType(HttpServletRequest request) throws Exception;
}
