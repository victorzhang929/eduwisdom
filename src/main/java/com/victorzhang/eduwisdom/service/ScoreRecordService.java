package com.victorzhang.eduwisdom.service;

import com.victorzhang.eduwisdom.domain.ScoreRecord;

import java.util.List;

public interface ScoreRecordService extends BaseService<ScoreRecord, String> {
    List<String> listScoreRecord(String userId) throws Exception;
}
