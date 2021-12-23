package com.envisioniot.enos.third_party_protocol.core.element;

import com.envisioniot.enos.third_party_protocol.core.message.request.CreateDeviceRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * To identify a device, user has 3 options: <br/>
 * a) use device id from their external system. And this only makes sense when user
 * has auto created their devices through {@link CreateDeviceRequest}. <br/>
 * b) use enos asset id <br/>
 * c) use enos productKey and deviceKey <br/>
 *
 * @author jian.zhang4
 */
@Getter
@Setter
public class GenericDeviceId {
    private String clientId;

    private String externalDeviceId;

    private String assetId;

    private String productKey;
    private String deviceKey;

    @Override
    final public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericDeviceId)) {
            return false;
        }
        GenericDeviceId that = (GenericDeviceId) o;
        if (StringUtils.isNotEmpty(assetId)) {
            return assetId.equals(that.assetId);
        }
        if (StringUtils.isNotEmpty(productKey) && StringUtils.isNotEmpty(deviceKey)) {
            return productKey.equals(that.productKey) && deviceKey.equals(that.deviceKey);
        }
        return Objects.equals(externalDeviceId, that.externalDeviceId);
    }

    @Override
    final public int hashCode() {
        if (StringUtils.isNotEmpty(assetId)) {
            return Objects.hash(assetId);
        }
        if (StringUtils.isNotEmpty(productKey) && StringUtils.isNotEmpty(deviceKey)) {
            return Objects.hash(productKey, deviceKey);
        }
        return Objects.hash(externalDeviceId);
    }
}
