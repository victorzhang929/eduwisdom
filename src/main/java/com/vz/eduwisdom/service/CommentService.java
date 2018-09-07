package com.vz.eduwisdom.service;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Comment;

public interface CommentService extends BaseService<Comment, String> {
    int removeByResourceId(String resourceId) throws Exception;

    List<Map<String,Object>> listByResourceId(String resourceId) throws Exception;
}
