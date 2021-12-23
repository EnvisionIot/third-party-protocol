package com.envisioniot.enos.third_party_protocol.core.message.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDeviceRspItemData extends BaseRspItemData {
    private String externalDeviceId;
    private String externalModelId;

    /**
     * The following info would only be returned when we have
     * successfully created the device in EnOS cloud.
     */
    private String assetId;
    private String productKey;
    private String deviceKey;
    private String deviceSecret;
}
