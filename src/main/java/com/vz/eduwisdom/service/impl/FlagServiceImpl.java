package com.vz.eduwisdom.service.impl;

import java.sql.SQLException;
import java.util.Map;

import com.vz.eduwisdom.mapper.FlagMapper;
import com.vz.eduwisdom.service.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.vz.eduwisdom.util.Constants.UPDATE_ERROR;

@Service("flagService")
public class FlagServiceImpl implements FlagService {

    @Autowired
    @Qualifier("flagMapper")
    private FlagMapper flagMapper;

    @Override
    public void updateFlag(String flag) throws Exception {
        int successCount = flagMapper.updateFlag(flag);
        if (successCount <= 0) {
            throw new SQLException(UPDATE_ERROR);
        }
    }

    @Override
    public Map<String, Object> getFlagValue() throws Exception {
        return flagMapper.getFlagValue();
    }
}
