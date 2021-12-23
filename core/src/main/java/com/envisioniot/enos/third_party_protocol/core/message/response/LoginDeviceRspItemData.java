package com.envisioniot.enos.third_party_protocol.core.message.response;

import com.envisioniot.enos.third_party_protocol.core.element.GenericDeviceId;
import lombok.Getter;
import lombok.Setter;

/**
 * The login response data just needs to return device id information.
 */
@Getter @Setter
public class LoginDeviceRspItemData extends BaseRspItemData {
    /**
     * For the explanation of the following fields, please refer to {@link GenericDeviceId}
     */
    private String externalDeviceId;

    private String assetId;

    private String productKey;
    private String deviceKey;
}
