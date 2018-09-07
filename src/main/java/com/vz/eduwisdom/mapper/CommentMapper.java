package com.vz.eduwisdom.mapper;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends BaseMapper<Comment, String> {
    int removeByResourceId(String resourceId) throws Exception;

    List<Map<String, Object>> listByResourceId(String resourceId) throws Exception;
}
