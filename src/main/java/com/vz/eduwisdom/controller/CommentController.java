package com.vz.eduwisdom.controller;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.vz.eduwisdom.util.CommonUtils;
import com.vz.eduwisdom.domain.Comment;
import com.vz.eduwisdom.service.CommentService;
import com.vz.eduwisdom.util.query.GenericQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.vz.eduwisdom.util.Constants.*;
import static com.vz.eduwisdom.util.Constants.UPDATE_ERROR;
import static com.vz.eduwisdom.util.Constants.UPDATE_SUCCESS;
import static com.vz.eduwisdom.util.Constants.USER_ID;

@Controller
@RequestMapping("comment")
public class CommentController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    @Qualifier("commentService")
    private CommentService commentService;

    @RequestMapping("/forwardCommentResourceUI.do")
    public String forwardCommentResourceUI() {
        return "commentResource";
    }

    @RequestMapping("/listPaging.do")
    @ResponseBody
    public Map<String, Object> listPaging(String _page, String _pageSize, String resourceName, String resourceType, String startDate, String endDate) throws Exception {
        Comment comment = new Comment(CommonUtils.sesAttr(request, USER_ID));
        GenericQueryParam param = new GenericQueryParam();
        param.fill("resourceName", resourceName);
        param.fill("resourceType", resourceType);
        return commentService.listPaging(comment, _page, _pageSize, startDate, endDate, param);
    }

    @RequestMapping(value = "/updateComment.do", produces = {"text/javascript;charset=UTF-8"})
    @ResponseBody
    public String updateComment(String commentId, String commentContent) throws Exception {
        Comment comment = new Comment(commentId, commentContent);
        if (!commentService.update(comment)) {
            throw new SQLException(UPDATE_ERROR);
        }
        return UPDATE_SUCCESS;
    }

    @RequestMapping(value = "/deleteComment.do", produces = {"text/javascript;charset=UTF-8"})
    @ResponseBody
    public String deleteComment(String commentId) throws Exception {
        if (!commentService.remove(commentId)) {
            throw new SQLException(REMOVE_ERROR);
        }
        return REMOVE_SUCCESS;
    }

    @RequestMapping("/saveComment.do")
    @ResponseBody
    public void saveComment(String resourceId, String commentContent) throws Exception {
        String id = CommonUtils.newUuid();
        String userId = CommonUtils.sesAttr(request, USER_ID);
        String commentTime = CommonUtils.getDateTime();
        Comment comment = new Comment(id, commentContent, userId, resourceId, commentTime);
        if(!commentService.save(comment)){
            throw new SQLException(INSERT_ERROR);
        }
    }
}
