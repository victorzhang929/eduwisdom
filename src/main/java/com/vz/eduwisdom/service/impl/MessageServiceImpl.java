package com.vz.eduwisdom.service.impl;

import com.vz.eduwisdom.domain.Message;
import com.vz.eduwisdom.mapper.BaseMapper;
import com.vz.eduwisdom.mapper.MessageMapper;
import com.vz.eduwisdom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("messageService")
public class MessageServiceImpl extends BaseServiceImpl<Message, String> implements MessageService {

    @Autowired
    @Qualifier("messageMapper")
    private MessageMapper messageMapper;

    @Override
    protected BaseMapper<Message, String> getMapper() {
        return messageMapper;
    }

}
