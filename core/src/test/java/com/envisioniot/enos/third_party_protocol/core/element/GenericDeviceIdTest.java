package com.envisioniot.enos.third_party_protocol.core.element;


import org.junit.Assert;
import org.junit.Test;

public class GenericDeviceIdTest {

    @Test
    public void test() {
        GenericDeviceId dev1 = new GenericDeviceId();
        dev1.setAssetId("assetId#1");
        dev1.setProductKey("productKey#1");
        dev1.setDeviceKey("deviceKey#1");
        dev1.setExternalDeviceId("externalDevId#1");

        GenericDeviceId dev11 = new GenericDeviceId();
        dev11.setAssetId("assetId#1");

        GenericDeviceId dev12 = new GenericDeviceId();
        dev12.setProductKey("productKey#1");
        dev12.setDeviceKey("deviceKey#1");

        GenericDeviceId dev13 = new GenericDeviceId();
        dev13.setExternalDeviceId("externalDevId#1");

        Assert.assertEquals(dev11, dev1);
        Assert.assertEquals(dev12, dev1);
        Assert.assertEquals(dev13, dev1);

        Assert.assertEquals(dev1, dev11);
        Assert.assertNotEquals(dev1, dev12);
        Assert.assertNotEquals(dev1, dev13);
    }


}
