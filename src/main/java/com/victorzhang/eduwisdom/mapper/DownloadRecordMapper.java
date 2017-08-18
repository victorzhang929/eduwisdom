package com.victorzhang.eduwisdom.mapper;

import com.victorzhang.eduwisdom.domain.DownloadRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadRecordMapper extends BaseMapper<DownloadRecord, String> {
    int countByResourceDownload(DownloadRecord downloadRecord);
}
