package com.victorzhang.eduwisdom.service;

import com.victorzhang.eduwisdom.domain.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends BaseService<Comment, String> {
    int removeByResourceId(String resourceId) throws Exception;

    List<Map<String,Object>> listByResourceId(String resourceId) throws Exception;
}
