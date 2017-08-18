package com.victorzhang.eduwisdom.mapper;

import com.victorzhang.eduwisdom.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapper extends BaseMapper<Comment, String> {
    int removeByResourceId(String resourceId) throws Exception;

    List<Map<String, Object>> listByResourceId(String resourceId) throws Exception;
}
