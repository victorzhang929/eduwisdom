package com.victorzhang.eduwisdom.service.impl;

import com.victorzhang.eduwisdom.domain.Message;
import com.victorzhang.eduwisdom.mapper.BaseMapper;
import com.victorzhang.eduwisdom.mapper.MessageMapper;
import com.victorzhang.eduwisdom.service.MessageService;
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
