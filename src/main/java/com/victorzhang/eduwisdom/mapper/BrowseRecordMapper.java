package com.victorzhang.eduwisdom.mapper;

import com.victorzhang.eduwisdom.domain.BrowseRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface BrowseRecordMapper extends BaseMapper<BrowseRecord, String> {
    int countByResourceBrowse(BrowseRecord browseRecord) throws Exception;
}
