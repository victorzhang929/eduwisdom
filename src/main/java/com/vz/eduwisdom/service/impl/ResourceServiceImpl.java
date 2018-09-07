package com.vz.eduwisdom.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vz.eduwisdom.domain.BrowseRecord;
import com.vz.eduwisdom.domain.DownloadRecord;
import com.vz.eduwisdom.domain.Resource;
import com.vz.eduwisdom.domain.Score;
import com.vz.eduwisdom.domain.ScoreRecord;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.BrowseRecordMapper;
import com.vz.eduwisdom.mapper.DownloadRecordMapper;
import com.vz.eduwisdom.mapper.ResourceMapper;
import com.vz.eduwisdom.mapper.ScoreRecordMapper;
import com.vz.eduwisdom.service.BrowseRecordService;
import com.vz.eduwisdom.service.CommentService;
import com.vz.eduwisdom.service.DownloadRecordService;
import com.vz.eduwisdom.service.FlagService;
import com.vz.eduwisdom.service.RecommendationService;
import com.vz.eduwisdom.service.ResourceService;
import com.vz.eduwisdom.service.ScoreRecordService;
import com.vz.eduwisdom.service.UserService;
import com.vz.eduwisdom.util.CommonUtils;
import com.vz.eduwisdom.util.query.GenericQueryParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.vz.eduwisdom.util.Constants.ADMIN_ROLE_ID;
import static com.vz.eduwisdom.util.Constants.BROWSE_SCORE_FLAG;
import static com.vz.eduwisdom.util.Constants.DATA;
import static com.vz.eduwisdom.util.Constants.DOT_STRING;
import static com.vz.eduwisdom.util.Constants.DOWNLOAD_BROWSE_SCORE_FLAG;
import static com.vz.eduwisdom.util.Constants.DOWNLOAD_SCORE_FLAG;
import static com.vz.eduwisdom.util.Constants.EMPTY_STRING;
import static com.vz.eduwisdom.util.Constants.INSERT_ERROR;
import static com.vz.eduwisdom.util.Constants.NO_ACCESS_PERMISSION;
import static com.vz.eduwisdom.util.Constants.RESOURCE_VERIFY_SUCCESS;
import static com.vz.eduwisdom.util.Constants.ROLE_ID;
import static com.vz.eduwisdom.util.Constants.SCORE_FLAG;
import static com.vz.eduwisdom.util.Constants.TAG_MATCHING;
import static com.vz.eduwisdom.util.Constants.UPDATE_ERROR;
import static com.vz.eduwisdom.util.Constants.UPDATE_SUCCESS;
import static com.vz.eduwisdom.util.Constants.USER_ID;


@Service("resourceService")
public class ResourceServiceImpl extends BaseServiceImpl<Resource, String> implements ResourceService {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String DOWNLOAD_FLAG = "downloadFlag";
    private static final String BROWSE_FLAG = "browseFlag";

    private static final String USER_FLAG = "0";
    private static final String ITEM_FLAG = "1";

    @Autowired
    @Qualifier("browseRecordService")
    private BrowseRecordService browseRecordService;

    @Autowired
    @Qualifier("downloadRecordService")
    private DownloadRecordService downloadRecordService;

    @Autowired
    @Qualifier("commentService")
    private CommentService commentService;

    @Autowired
    @Qualifier("scoreRecordService")
    private ScoreRecordService scoreRecordService;

    @Autowired
    @Qualifier("flagService")
    private FlagService flagService;

    @Autowired
    @Qualifier("recommendationService")
    private RecommendationService recommendationService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("resourceMapper")
    private ResourceMapper resourceMapper;

    @Autowired
    @Qualifier("scoreRecordMapper")
    private ScoreRecordMapper scoreRecordMapper;

    @Autowired
    @Qualifier("downloadRecordMapper")
    private DownloadRecordMapper downloadRecordMapper;

    @Autowired
    @Qualifier("browseRecordMapper")
    private BrowseRecordMapper browseRecordMapper;

    @Override
    protected BaseMapper<Resource, String> getMapper() {
        return resourceMapper;
    }

    @Override
    public void doUploadResource(MultipartFile resourceFile, String resourceName, String resourceDescription, String resourceTag, String resourceType, HttpServletRequest request) throws Exception {
        String uploadPath = CommonUtils.getUploadPath(request);
        String fileName = resourceFile.getOriginalFilename();
        String serverFileName = fileName.substring(0, fileName.lastIndexOf(DOT_STRING)) + System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf(DOT_STRING));
        String fileServerPath = uploadPath + serverFileName;
        String userId = CommonUtils.sesAttr(request, USER_ID);
        String resourceId = CommonUtils.newUuid();
        String gmtCreate = CommonUtils.getDateTime();
        Resource resource = new Resource(resourceId, resourceName, resourceDescription, resourceTag, resourceType, fileServerPath, userId, gmtCreate);
        if (!super.save(resource)) {
            throw new SQLException(INSERT_ERROR);
        }

        try {
            File file = new File(fileServerPath);
            resourceFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveScoreRecord(String userId, String resourceId, String ratingTime) throws Exception {
        ScoreRecord scoreRecord = new ScoreRecord(userId, resourceId, Score.FIVE.toString(), ratingTime, SCORE_FLAG);
        if (scoreRecordMapper.save(scoreRecord) <= 0) {
            throw new SQLException(INSERT_ERROR);
        }
    }

    @Override
    public void doDownloadResource(HttpServletResponse response, HttpServletRequest request, String id) throws Exception {
        InputStream fis = null;
        OutputStream fos = null;
        Resource resource = super.getById(id);
        try {
            File file = new File(resource.getResourceServerPath());
            String fileName = file.getName();
            String downloadFileName = fileName.substring(0, fileName.lastIndexOf(DOT_STRING)) + CommonUtils.currTimestamp() + fileName.substring(fileName.lastIndexOf(DOT_STRING));
            fis = new BufferedInputStream(new FileInputStream(resource.getResourceServerPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            response.reset();
            response.setHeader(CONTENT_DISPOSITION, "attachment;filename=" + CommonUtils.formatDownloadFileName(downloadFileName));
            response.setHeader(CONTENT_LENGTH, String.valueOf(file.length()));
            fos = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            fos.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
        //更新resource表中resource_download_count
        updateResourceDownloadOrBrowseCount(id, DOWNLOAD_FLAG);
        //插入或者更新resource_download表格
        saveOrUpdateResourceDownload(request, id);
    }

    @Override
    public List<Map<String, Object>> listNewestResource() throws Exception {
        return resourceMapper.listNewestResource();
    }

    @Override
    public List<Map<String, Object>> listHotResource(int count) throws Exception {
        return resourceMapper.listHotResource(count);
    }

    @Override
    public void removeRecordAboutThisResource(String resourceId) throws Exception {
        downloadRecordService.remove(resourceId);
        browseRecordService.remove(resourceId);
        scoreRecordService.remove(resourceId);
        commentService.removeByResourceId(resourceId);
    }

    @Override
    public Map<String, Object> getResourceDetail(String id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Resource resource = getById(id);
        map.put("resource", resource);
        map.put("comments", CommonUtils.dataNull(commentService.listByResourceId(id)));
        map.put("averageScore", getAverageScore(scoreRecordService.list(id)));
        return map;
    }

    @Override
    public void updateResourceBrowseCount(String resourceId, HttpServletRequest request) throws Exception {
        updateResourceDownloadOrBrowseCount(resourceId, BROWSE_FLAG);
        saveOrUpdateResourceBrowse(resourceId, request);
    }

    @Override
    public String doVerifyResource(String resourceId, String verifyType, HttpServletRequest request) throws Exception {
        //admin permission
        if (StringUtils.equals(CommonUtils.sesAttr(request, ROLE_ID), ADMIN_ROLE_ID)) {
            if (StringUtils.equals(verifyType, RESOURCE_VERIFY_SUCCESS)) {
                //审核通过评分为5
                Resource resourceDB = getById(resourceId);
                saveScoreRecord(resourceDB.getUserId(), resourceId, CommonUtils.getDateTime());
            }
            Resource resource = new Resource(resourceId, verifyType);
            if (!update(resource)) {
                throw new SQLException(UPDATE_ERROR);
            }
            return UPDATE_SUCCESS;
        }
        throw new IllegalAccessException(NO_ACCESS_PERMISSION);
    }

    @Override
    public Map<String, Object> listRecommendationResource(String page, String pageSize, HttpServletRequest request) throws Exception {
        String flag = String.valueOf(flagService.getFlagValue().get("user_item_flag"));
        String userId = CommonUtils.sesAttr(request, USER_ID);
        List<RecommendedItem> items = getItems(flag, Long.parseLong(userId));
        List<Map<String, Object>> resourceDetail = listRecommendedSourceDetail(items, userId);

        Map<String, Object> result = new HashMap<>();
        result = CommonUtils.para4Page(result, CommonUtils.paraPage(page), CommonUtils.paraPageSize(pageSize), resourceDetail.size());
        if (resourceDetail.size() > 0) {
            result.put(DATA, CommonUtils.dataNull(resourceDetail));
        } else {
            result.put(DATA, EMPTY_STRING);
        }
        return result;
    }

    private List<Map<String, Object>> listRecommendedSourceDetail(List<RecommendedItem> items, String userId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        for (RecommendedItem item : items) {
            map = new HashMap<>();
            map.put("resource", getById(String.valueOf(item.getItemID())));
            map.put("score", String.format("%.2f",item.getValue()));
            list.add(map);
        }

        List<Resource> tagMatchingResourceList = getTagMatchingResource(userId);
        Map<String, Object> tagMatchingResourceMap;
        for (Resource tagMatchingResource : tagMatchingResourceList) {
            tagMatchingResourceMap = new HashMap<>();
            tagMatchingResourceMap.put("resource", tagMatchingResource);
            tagMatchingResourceMap.put("score", TAG_MATCHING);
            list.add(tagMatchingResourceMap);
        }
        if (list.size() > 10) {
            list = list.subList(0, 10);
        }
        return CommonUtils.dataNull(list);
    }

    private List<Resource> getTagMatchingResource(String userId) throws Exception {
        String tag = userService.getById(userId).getTag();
        Resource resource = new Resource(tag);
        return list(resource);
    }

    private List<RecommendedItem> getItems(String flag, long userId) throws Exception {
        List<RecommendedItem> items = new ArrayList<>();
        if (StringUtils.equals(flag, USER_FLAG)) {
            items = recommendationService.listUserBasedRecommendationResource(userId);
        }
        if (StringUtils.equals(flag, ITEM_FLAG)) {
            items = recommendationService.listItemBasedRecommendationResource(userId);
        }
        return items;
    }

    private double getAverageScore(List<ScoreRecord> scoreRecords) {
        double sum = 0.0;
        for (ScoreRecord scoreRecord : scoreRecords) {
            sum += Double.parseDouble(scoreRecord.getRating());
        }
        double average = sum / scoreRecords.size();
        BigDecimal decimal = new BigDecimal(average);
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void updateResourceDownloadOrBrowseCount(String id, String flagAboutDownloadOrBrowse) throws Exception {
        Resource resource = getById(id);
        Resource updateResourceDownloadOrBrowseCount = new Resource();
        if (StringUtils.equals(DOWNLOAD_FLAG, flagAboutDownloadOrBrowse)) {
            int resourceDownloadCount = Integer.parseInt(resource.getResourceDownloadCount()) + 1;
            updateResourceDownloadOrBrowseCount.setResourceDownloadCount(String.valueOf(resourceDownloadCount));
        }
        if (StringUtils.equals(BROWSE_FLAG, flagAboutDownloadOrBrowse)) {
            int resourceBrowseCount = Integer.parseInt(resource.getResourceBrowseCount()) + 1;
            updateResourceDownloadOrBrowseCount.setResourceBrowseCount(String.valueOf(resourceBrowseCount));
        }

        updateResourceDownloadOrBrowseCount.setId(id);
        update(updateResourceDownloadOrBrowseCount);
    }

    private void saveOrUpdateResourceDownload(HttpServletRequest request, String id) throws Exception {
        String resourceId = id;
        String downloadTime = CommonUtils.getDateTime();
        String userId = CommonUtils.sesAttr(request, USER_ID);
        DownloadRecord downloadRecord = new DownloadRecord(resourceId, downloadTime, userId);
        int count = downloadRecordMapper.countByResourceDownload(downloadRecord);
        if (count > 0) { //数据库中存在数据，更新操作
            downloadRecordMapper.update(downloadRecord);
        } else {//数据库中不存在该记录，插入操作
            downloadRecordMapper.save(downloadRecord);
            //评分表插入或更新操作
            saveOrUpdateScoreRecord(resourceId, userId, DOWNLOAD_FLAG);
        }
    }

    private void saveOrUpdateResourceBrowse(String resourceId, HttpServletRequest request) throws Exception {
        String browseTime = CommonUtils.getDateTime();
        String userId = CommonUtils.sesAttr(request, USER_ID);
        BrowseRecord browseRecord = new BrowseRecord(resourceId, browseTime, userId);
        int count = browseRecordMapper.countByResourceBrowse(browseRecord);
        if (count > 0) { //数据库中存在数据，更新操作
            browseRecordMapper.update(browseRecord);
        } else {//数据库中不存在该记录，插入操作
            browseRecordMapper.save(browseRecord);
            //评分表插入或更新操作
            saveOrUpdateScoreRecord(resourceId, userId, BROWSE_FLAG);
        }
    }

    private void saveOrUpdateScoreRecord(String resourceId, String userId, String flagAboutDownloadOrBrowse) throws Exception {
        GenericQueryParam param = new GenericQueryParam();
        param.fill("resourceId", resourceId);
        param.fill("userId", userId);
        int scoreCount = scoreRecordMapper.count(param);

        ScoreRecord scoreRecord = new ScoreRecord(userId, resourceId, CommonUtils.getDateTime());
        if (scoreCount <= 0) {//数据库resource_score表中不存在该纪录，插入
            scoreRecord.setRating(Score.TWO.toString());
            scoreRecord.setScoreFlag(DOWNLOAD_SCORE_FLAG);
            scoreRecordMapper.save(scoreRecord);
        } else {//数据库resource_score表中存在该记录，更新操作
            ScoreRecord scoreRecordByDB = scoreRecordMapper.get(scoreRecord);
            if (StringUtils.equals(flagAboutDownloadOrBrowse, DOWNLOAD_FLAG)) {
                //score_flag为BROWSE_SCORE_FLAG，评分更新4，并将score_flag表示为DOWNLOAD_BORWSE_SCORE_FLAG
                if (StringUtils.equals(scoreRecordByDB.getScoreFlag(), BROWSE_SCORE_FLAG)) {
                    scoreRecord.setRating(Score.FOUR.toString());
                    scoreRecord.setScoreFlag(DOWNLOAD_BROWSE_SCORE_FLAG);
                }
            }
            if (StringUtils.equals(flagAboutDownloadOrBrowse, BROWSE_FLAG)) {
                //score_flag为DOWNLOAD_SCORE_FLAG，评分更新4，并将score_flag表示为DOWNLOAD_BORWSE_SCORE_FLAG
                if (StringUtils.equals(scoreRecordByDB.getScoreFlag(), DOWNLOAD_SCORE_FLAG)) {
                    scoreRecord.setRating(Score.FOUR.toString());
                    scoreRecord.setScoreFlag(DOWNLOAD_BROWSE_SCORE_FLAG);
                }
            }
        }
    }
}
