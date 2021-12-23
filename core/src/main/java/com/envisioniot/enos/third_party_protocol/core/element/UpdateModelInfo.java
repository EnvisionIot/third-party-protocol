package com.envisioniot.enos.third_party_protocol.core.element;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * To identify a model, you user can make use of externalDeviceId,
 * externalModelId or modelId. However, we don't allow more than one
 * provided.
 *
 * @author jian.zhang4
 */
@Getter
@Setter
public class UpdateModelInfo {
    private String externalDeviceId;
    private String externalModelId;

    private StringI18n name;
    private Map<String, Object> measurepoints;
    private Map<String, Object> attrs;
    private Map<String, String> tags;
}
