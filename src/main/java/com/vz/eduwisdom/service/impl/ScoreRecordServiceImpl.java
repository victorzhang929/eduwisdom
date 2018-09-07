package com.vz.eduwisdom.service.impl;

import java.util.List;

import com.vz.eduwisdom.domain.ScoreRecord;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.ScoreRecordMapper;
import com.vz.eduwisdom.service.ScoreRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("scoreRecordService")
public class ScoreRecordServiceImpl extends BaseServiceImpl<ScoreRecord, String> implements ScoreRecordService {

    @Autowired
    @Qualifier("scoreRecordMapper")
    private ScoreRecordMapper scoreRecordMapper;

    @Override
    protected BaseMapper<ScoreRecord, String> getMapper() {
        return scoreRecordMapper;
    }

    @Override
    public List<String> listScoreRecord(String userId) throws Exception {
        return scoreRecordMapper.listScoreRecord(userId);
    }
}
