package com.vz.eduwisdom.mapper;

import java.util.List;
import java.util.Map;

import com.vz.eduwisdom.domain.Resource;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceMapper extends BaseMapper<Resource, String> {
    List<Map<String, Object>> listNewestResource() throws Exception;

    List<Map<String, Object>> listHotResource(int count) throws Exception;
}
