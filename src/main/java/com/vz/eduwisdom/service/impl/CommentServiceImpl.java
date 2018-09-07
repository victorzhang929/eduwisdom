package com.vz.eduwisdom.service.impl;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Comment;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.CommentMapper;
import com.vz.eduwisdom.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("commentService")
public class CommentServiceImpl extends BaseServiceImpl<Comment, String> implements CommentService {

    @Autowired
    @Qualifier("commentMapper")
    private CommentMapper commentMapper;

    @Override
    protected BaseMapper<Comment, String> getMapper() {
        return commentMapper;
    }

    @Override
    public int removeByResourceId(String resourceId) throws Exception {
        return commentMapper.removeByResourceId(resourceId);
    }

    @Override
    public List<Map<String, Object>> listByResourceId(String resourceId) throws Exception {
        return commentMapper.listByResourceId(resourceId);
    }
}
