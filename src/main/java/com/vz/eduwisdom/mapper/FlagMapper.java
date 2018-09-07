package com.vz.eduwisdom.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface FlagMapper {
    int updateFlag(String flag) throws Exception;

    Map<String, Object> getFlagValue() throws Exception;
}
