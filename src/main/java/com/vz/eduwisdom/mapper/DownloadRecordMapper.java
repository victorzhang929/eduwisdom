package com.vz.eduwisdom.mapper;

import com.vz.eduwisdom.domain.DownloadRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadRecordMapper extends BaseMapper<DownloadRecord, String> {
    int countByResourceDownload(DownloadRecord downloadRecord);
}
