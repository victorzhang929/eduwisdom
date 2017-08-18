package com.victorzhang.eduwisdom.service.impl;

import com.victorzhang.eduwisdom.domain.BrowseRecord;
import com.victorzhang.eduwisdom.mapper.BaseMapper;
import com.victorzhang.eduwisdom.mapper.BrowseRecordMapper;
import com.victorzhang.eduwisdom.service.BrowseRecordService;
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
