package com.envisioniot.enos.third_party_protocol.haystack.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurePoint extends PointInfo {
    private Object curVal;
    private long time;
}
