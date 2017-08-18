package com.victorzhang.eduwisdom.service;

import com.victorzhang.eduwisdom.domain.Log;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface LogService extends BaseService<Log, String> {
    boolean saveLogByLogTypeAndLogContent(String logType, String logContent, String userId) throws Exception;

    boolean saveLogByLogTypeAndLogContent(String logType, String logContent) throws Exception;

    List<Map<String, Object>> listLogType(HttpServletRequest request) throws Exception;
}
