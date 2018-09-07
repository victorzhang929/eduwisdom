package com.vz.eduwisdom.mapper;

import com.vz.eduwisdom.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper extends BaseMapper<Message, String> {
}
