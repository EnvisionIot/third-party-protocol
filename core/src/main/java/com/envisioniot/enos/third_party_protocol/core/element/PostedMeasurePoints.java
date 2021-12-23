package com.envisioniot.enos.third_party_protocol.core.element;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PostedMeasurePoints extends GenericDeviceId {
    private Map<String, Object> measurepoints;
    private long time;
}
