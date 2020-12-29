package com.pingchat.authenticationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import com.pingchat.authenticationservice.model.dto.DSNodeDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ObjectConverter {
    private final ObjectMapper objectMapper;

    public ObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DSNodeDto convertDSNode(DSNodeEntity dsNodeEntity) {
        DSNodeDto dsNodeDto = objectMapper.convertValue(dsNodeEntity, DSNodeDto.class);

        return dsNodeDto;
    }
}
