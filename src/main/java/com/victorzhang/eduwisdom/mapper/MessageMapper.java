package com.victorzhang.eduwisdom.mapper;

import com.victorzhang.eduwisdom.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper extends BaseMapper<Message, String> {
}
