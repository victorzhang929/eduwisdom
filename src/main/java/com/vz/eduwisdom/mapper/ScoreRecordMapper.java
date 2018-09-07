package com.vz.eduwisdom.mapper;

import java.util.List;

import com.vz.eduwisdom.domain.ScoreRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord, String> {
    List<String> listScoreRecord(String userId) throws Exception;
}