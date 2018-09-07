package com.vz.eduwisdom.mapper;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Log;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import static com.vz.eduwisdom.util.Constants.USER_ID;

@Repository
public interface LogMapper extends BaseMapper<Log, String> {

    List<Map<String, Object>> listLogType(@Param(USER_ID) String userId) throws Exception;
}
