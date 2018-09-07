package com.vz.eduwisdom.service.impl;

import com.vz.eduwisdom.domain.DownloadRecord;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.DownloadRecordMapper;
import com.vz.eduwisdom.service.DownloadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("downloadRecordService")
public class DownloadRecordServiceImpl extends BaseServiceImpl<DownloadRecord,String> implements DownloadRecordService {

    @Autowired
    @Qualifier("downloadRecordMapper")
    private DownloadRecordMapper downloadRecordMapper;

    @Override
    protected BaseMapper<DownloadRecord, String> getMapper() {
        return downloadRecordMapper;
    }
}
