package com.vz.eduwisdom.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vz.eduwisdom.domain.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService extends BaseService<Resource, String> {
    void doUploadResource(MultipartFile resourceFile, String resourceName, String resourceDescription, String resourceTag, String resourceType, HttpServletRequest request) throws Exception;

    void doDownloadResource(HttpServletResponse response, HttpServletRequest request, String id) throws Exception;

    List<Map<String, Object>> listNewestResource() throws Exception;

    List<Map<String,Object>> listHotResource(int count) throws Exception;

    void removeRecordAboutThisResource(String id) throws Exception;

    Map<String, Object> getResourceDetail(String id) throws Exception;

    void updateResourceBrowseCount(String resourceId, HttpServletRequest request) throws Exception;

    String doVerifyResource(String resourceId, String verifyType, HttpServletRequest request) throws Exception;

    Map<String, Object> listRecommendationResource(String page, String pageSize, HttpServletRequest request) throws Exception;
}
