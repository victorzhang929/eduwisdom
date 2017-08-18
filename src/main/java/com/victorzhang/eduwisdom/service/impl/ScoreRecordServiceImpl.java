package com.victorzhang.eduwisdom.service.impl;

import com.victorzhang.eduwisdom.domain.ScoreRecord;
import com.victorzhang.eduwisdom.mapper.BaseMapper;
import com.victorzhang.eduwisdom.mapper.ScoreRecordMapper;
import com.victorzhang.eduwisdom.service.ScoreRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

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
