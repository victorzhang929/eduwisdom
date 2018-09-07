package com.vz.eduwisdom.test;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Log;
import com.vz.eduwisdom.service.LogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static com.vz.eduwisdom.util.Constants.ADMIN_ROLE_ID;
import static com.vz.eduwisdom.util.Constants.FIND_PASSWORD;
import static com.vz.eduwisdom.util.Constants.LOGIN_SYSTEM;
import static com.vz.eduwisdom.util.Constants.ROLE_ID;
import static com.vz.eduwisdom.util.Constants.USER_ID;
import static com.vz.eduwisdom.util.Constants.UTF_8;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:spring/spring-common.xml"})
public class LogTest {

    @Autowired
    @Qualifier("logService")
    private LogService logService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        request.setCharacterEncoding(UTF_8);
        response = new MockHttpServletResponse();
        request.getSession().setAttribute(USER_ID, "C4CA4238A0B923820DCC509A6F75849B");
        request.getSession().setAttribute(ROLE_ID, ADMIN_ROLE_ID);
    }

    @Test
    public void testSave() throws Exception {
        Log log = new Log();
        log.setId("B12FA30147A04EC78BA0CCBFDD17A083");
        log.setLogType(" 登录系统 ");
        log.setLogContent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        log.setUserId("C4CA4238A0B923820DCC509A6F75849B");
        log.setUserDate("2017-04-02 15:20:13");
        log.setUserIp("127.0.0.1");
        logService.save(log);
    }

    @Test
    public void testSaveLogByLogTypeAndLogContent() throws Exception {
        logService.saveLogByLogTypeAndLogContent(FIND_PASSWORD, "victorzhang0929@hotmail.com");
    }

    @Test
    public void testListLogType() throws Exception {
        List<Map<String, Object>> list = logService.listLogType(request);
        for (Map<String, Object> map : list) {
            for (Map.Entry entry : map.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        }
    }

    @Test
    public void testListPaging() throws Exception {
        Log log = new Log();
        log.setLogType(LOGIN_SYSTEM);
        String page = "0";
        String pageSize = "10";
        Map<String, Object> map = logService.listPaging(log, page, pageSize, null, null, null);
        for (Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
    }

}
