package com.envisioniot.enos.third_party_protocol.core.message.request;


import com.envisioniot.enos.third_party_protocol.core.element.CreateDeviceInfo;
import com.envisioniot.enos.third_party_protocol.core.element.UpdateModelInfo;
import com.envisioniot.enos.third_party_protocol.core.message.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateDeviceRequest extends BaseRequest {
    private List<UpdateModelInfo> models;
    private List<CreateDeviceInfo> devices;

    public CreateDeviceRequest() {
        setMessageType(MessageType.CreateDevice);
    }
}
