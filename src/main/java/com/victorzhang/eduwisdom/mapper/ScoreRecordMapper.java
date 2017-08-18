package com.victorzhang.eduwisdom.mapper;

import com.victorzhang.eduwisdom.domain.ScoreRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord, String> {
    List<String> listScoreRecord(String userId) throws Exception;
}