package com.envisioniot.enos.third_party_protocol.core.element;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateDeviceInfo {
    private String externalDeviceId;
    private String externalModelId;
    private StringI18n name;
    private String timezone;
    private Map<String, Object> attrs;
    private Map<String, String> tags;
}
