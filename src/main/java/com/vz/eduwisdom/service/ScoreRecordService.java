package com.vz.eduwisdom.service;

import java.util.List;

import com.vz.eduwisdom.domain.ScoreRecord;

public interface ScoreRecordService extends BaseService<ScoreRecord, String> {
    List<String> listScoreRecord(String userId) throws Exception;
}
