package com.vz.eduwisdom.test;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.vz.eduwisdom.domain.Resource;
import com.vz.eduwisdom.domain.ScoreRecord;
import com.vz.eduwisdom.mapper.ScoreRecordMapper;
import com.vz.eduwisdom.service.RecommendationService;
import com.vz.eduwisdom.service.ResourceService;
import com.vz.eduwisdom.service.ScoreRecordService;
import com.vz.eduwisdom.util.CommonUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static com.vz.eduwisdom.util.Constants.USER_ID;
import static com.vz.eduwisdom.util.Constants.UTF_8;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:spring/spring-common.xml"})
public class ResourceTest {

    @Autowired
    @Qualifier("resourceService")
    private ResourceService resourceService;

    @Autowired
    @Qualifier("recommendationService")
    private RecommendationService recommendationService;

    @Autowired
    @Qualifier("scoreRecordService")
    private ScoreRecordService scoreRecordService;

    @Autowired
    @Qualifier("scoreRecordMapper")
    private ScoreRecordMapper scoreRecordMapper;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        request.setCharacterEncoding(UTF_8);
        response = new MockHttpServletResponse();
        request.getSession().setAttribute(USER_ID, "C4CA4238A0B923820DCC509A6F75849B");
    }

    @Test
    public void testListPaging() throws Exception {
        Resource resource = new Resource();
        Map<String, Object> map = resourceService.listPaging(resource, "1", "1", "", "", null);
        for (Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

    }

    @Test
    public void testListNewestResource() throws Exception {
        List<Map<String, Object>> list = resourceService.listNewestResource();
        for (Map<String, Object> map : list) {
            for (Map.Entry entry : map.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        }
    }

    @Test
    public void testDownloadResource() throws Exception {
        ScoreRecord scoreRecord = new ScoreRecord("C4CA4238A0B923820DCC509A6F75849B", "65FD3294A01B4304B983FEF0FB86FB09", CommonUtils.getDateTime());
        ScoreRecord scoreRecordDB = scoreRecordMapper.get(scoreRecord);
        System.out.println(scoreRecordDB);
    }

    @Test
    public void testResourceBrowseCount() throws Exception {
        Resource resource = resourceService.getById("968F2C132B9641AF8B8CBD73367AF2D9");
        Resource updateResourceDownloadOrBrowseCount = new Resource();
        int resourceBrowseCount = Integer.parseInt(resource.getResourceBrowseCount()) + 1;
        updateResourceDownloadOrBrowseCount.setResourceDownloadCount(String.valueOf(resourceBrowseCount));

        updateResourceDownloadOrBrowseCount.setId("968F2C132B9641AF8B8CBD73367AF2D9");
        resourceService.update(updateResourceDownloadOrBrowseCount);
    }

    @Test
    public void testListResource() throws Exception {
        List<Resource> list = resourceService.list();
        System.out.println(list.toString());
    }

    @Test
    public void testUserBaseRecommendation() throws TasteException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-common.xml");
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        /*MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setDatabaseName("vz-eduwisdom");*/
        JDBCDataModel model = new MySQLJDBCDataModel(dataSource, "resource_score", "user_id", "resource_id", "rating", "rating_time");

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(4, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        List<RecommendedItem> recommendations = recommender.recommend(5844279643990646201l, 2);
        for (RecommendedItem recommendation : recommendations) {
            //输出推荐结果
            System.out.println(recommendation);
        }

    }

    @Test
    public void testListVerifyResource() throws Exception {
        Resource resource = new Resource();
        System.out.println(resourceService.listPaging(resource, "0", "10", null, null, null));
    }


    @javax.annotation.Resource
    MySQLJDBCDataModel model;
    @Test
    public void testUserBasedRecommendation() throws Exception {
        // 指定用户相似度计算方法，这里采用皮尔森相关度
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        // 指定用户邻居数量，这里为2
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        // 构建基于用户的推荐系统
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
        List<RecommendedItem> recommendations = recommender.recommend(5844279643990646201L, 2);
        // 打印推荐结果
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

    @Test
    public void testItemBasedRecommendation() throws Exception {
        // 指定用户相似度计算方法，这里采用皮尔森相关度
        ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
        // 构建基于用户的推荐系统
        Recommender recommender = new GenericItemBasedRecommender(model, similarity);
        // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
        List<RecommendedItem> recommendations = recommender.recommend(5844279643990646201L, 2);
        // 打印推荐结果
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

    @Test
    public void testSaveScoreRecord() throws Exception {
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.setUserId("5844279643990646201");
        scoreRecord.setResourceId("313414239140184064");
        scoreRecord.setRatingTime("2017-05-15 16:40:04");
        scoreRecord.setRating("3");
        scoreRecord.setScoreFlag("0");
        boolean flag = scoreRecordService.update(scoreRecord);
        System.out.println(flag);
    }

}
