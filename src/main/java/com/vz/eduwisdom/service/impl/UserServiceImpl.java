package com.vz.eduwisdom.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.vz.eduwisdom.domain.User;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.UserMapper;
import com.vz.eduwisdom.service.LogService;
import com.vz.eduwisdom.service.UserService;
import com.vz.eduwisdom.util.CommonUtils;
import com.vz.eduwisdom.util.EmailUtils;
import com.vz.eduwisdom.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.vz.eduwisdom.util.Constants.CHECK_TWICE_PASSWORD_IS_SAME;
import static com.vz.eduwisdom.util.Constants.CHECK_URL;
import static com.vz.eduwisdom.util.Constants.DO_RESET_PASSWORD_SUCCESS;
import static com.vz.eduwisdom.util.Constants.EMAIL_URL_NOT_FIND;
import static com.vz.eduwisdom.util.Constants.FIND_PASSWORD;
import static com.vz.eduwisdom.util.Constants.LOGIN_FAIL;
import static com.vz.eduwisdom.util.Constants.LOGIN_OUT;
import static com.vz.eduwisdom.util.Constants.LOGIN_SUCCESS;
import static com.vz.eduwisdom.util.Constants.LOGIN_SYSTEM;
import static com.vz.eduwisdom.util.Constants.LOG_INSERT_ERROR;
import static com.vz.eduwisdom.util.Constants.NEW_PASSWORD_CAN_NOT_EMPTY;
import static com.vz.eduwisdom.util.Constants.OLD_PASSWORD_CAN_NOT_EMPTY;
import static com.vz.eduwisdom.util.Constants.OLD_PASSWORD_IS_NOT_RIGHT;
import static com.vz.eduwisdom.util.Constants.REPEAT_PASSWORD_CAN_NOT_EMPTY;
import static com.vz.eduwisdom.util.Constants.ROLE_ID;
import static com.vz.eduwisdom.util.Constants.SEND_EMAIL_MSG;
import static com.vz.eduwisdom.util.Constants.SEND_PASSWORD_URL_TO_EMAIL;
import static com.vz.eduwisdom.util.Constants.TRY_LOGIN;
import static com.vz.eduwisdom.util.Constants.TRY_RESET_PASSWORD;
import static com.vz.eduwisdom.util.Constants.UPDATE;
import static com.vz.eduwisdom.util.Constants.USER_ID;
import static com.vz.eduwisdom.util.Constants.USER_INFO_UPDATE_ERROR;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_AGENT = "user-agent";

    @Autowired
    @Qualifier("userMapper")
    private UserMapper userMapper;

    @Autowired
    @Qualifier("logService")
    private LogService logService;

    @Override
    protected BaseMapper<User, String> getMapper() {
        return userMapper;
    }

    @Override
    public User doLoginByUsernameAndPassword(String username, String password, HttpServletRequest request) throws Exception {
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            User userObj = new User(username, new MD5Utils().getMD5ofStr(password));
            logger.info(username + TRY_LOGIN);
            User user = userMapper.get(userObj);
            if (user != null) {
                request.getSession().setAttribute(USER_ID, user.getId());
                request.getSession().setAttribute(ROLE_ID, user.getRoleId());
                logService.saveLogByLogTypeAndLogContent(LOGIN_SYSTEM, request.getHeader(USER_AGENT));
                logger.info(username + LOGIN_SUCCESS);
                return user;
            }
            logger.error(user + LOGIN_FAIL);
        }
        return null;
    }

    @Override
    public String doGetUserByEmail(String userEmail) throws Exception {
        if (StringUtils.isNotEmpty(userEmail)) {
            logger.info(userEmail + TRY_RESET_PASSWORD);
            User user = userMapper.get(new User(userEmail));
            if (user != null) {
                EmailUtils.sendResetPasswordEmail(user);
                logger.info(userEmail + SEND_PASSWORD_URL_TO_EMAIL);
                boolean flag = logService.saveLogByLogTypeAndLogContent(FIND_PASSWORD, userEmail, user.getId());
                if (!flag) {
                    logger.error(LOG_INSERT_ERROR);
                    return null;
                }
                return SEND_EMAIL_MSG;
            }
            logger.warn(userEmail + EMAIL_URL_NOT_FIND);
        }
        return null;
    }

    @Override
    public String doResetPassword(String username, String checkCode, String password, String rePassword) throws Exception {
        User userObj = new User();
        userObj.setUsername(username);
        User user = userMapper.get(userObj);
        String result = CHECK_URL;
        String _checkCode = new MD5Utils().getMD5ofStr(username + user.getRandomCode());
        if (StringUtils.equals(_checkCode, checkCode)) {
            result = updateUser(user, password, rePassword);
        }
        return result;
    }

    @Override
    public String doResetPassword(String oldPassword, String password, String rePassword, HttpServletRequest request) throws Exception {
         String result ;
        if (StringUtils.isEmpty(oldPassword)) {
            result = OLD_PASSWORD_CAN_NOT_EMPTY;
        } else {
            String userId = CommonUtils.sesAttr(request, USER_ID);
            User user = new User();
            user.setId(userId);
            user.setPassword(new MD5Utils().getMD5ofStr(oldPassword));
            //judge oldPassword is exist
           if (!userMapper.doJudgePasswordIsRight(user)) {
                result = OLD_PASSWORD_IS_NOT_RIGHT;
                logger.error(OLD_PASSWORD_IS_NOT_RIGHT);
            } else {
                result = updateUser(user, password, rePassword);
            }
        }
        return result;
    }

    @Override
    public void doExit(HttpServletRequest request) throws Exception {
        String userId = CommonUtils.sesAttr(request, USER_ID);
        User user = super.getById(userId);
        logger.info(user.getUsername() + LOGIN_OUT);
        logService.saveLogByLogTypeAndLogContent(LOGIN_OUT, request.getHeader(USER_AGENT));
        request.getSession().removeAttribute(USER_ID);
        request.getSession().removeAttribute(ROLE_ID);
    }

    @Override
    public Map<String, Object> getByUserInfo(String userId) throws Exception {
        return userMapper.getUserInfo(userId);
    }

    @Override
    public List<Map<String, Object>> listUserType() throws Exception {
        return userMapper.listUserType();
    }

    private String updateUser(User user, String password, String rePassword) throws Exception {
        String result;
        if (StringUtils.isEmpty(password)) {
            result = NEW_PASSWORD_CAN_NOT_EMPTY;
        } else if (StringUtils.isEmpty(rePassword)) {
            result = REPEAT_PASSWORD_CAN_NOT_EMPTY;
        } else if (!StringUtils.equals(password, rePassword)) {
            result = CHECK_TWICE_PASSWORD_IS_SAME;
        } else {
            user.setPassword(new MD5Utils().getMD5ofStr(password));
            user.setGmtModify(CommonUtils.getDateTime());
            if (!super.update(user)) {
                throw new SQLException(USER_INFO_UPDATE_ERROR);
            }
            logService.saveLogByLogTypeAndLogContent(UPDATE, DO_RESET_PASSWORD_SUCCESS, user.getId());
            result = DO_RESET_PASSWORD_SUCCESS;
        }
        return result;
    }


}
