package com.vz.eduwisdom.service.impl;

import com.vz.eduwisdom.domain.BrowseRecord;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.BrowseRecordMapper;
import com.vz.eduwisdom.service.BrowseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("browseRecordService")
public class BrowseRecordServiceImpl extends BaseServiceImpl<BrowseRecord, String> implements BrowseRecordService {

    @Autowired
    @Qualifier("browseRecordMapper")
    private BrowseRecordMapper browseRecordMapper;

    @Override
    protected BaseMapper<BrowseRecord, String> getMapper() {
        return browseRecordMapper;
    }
}
