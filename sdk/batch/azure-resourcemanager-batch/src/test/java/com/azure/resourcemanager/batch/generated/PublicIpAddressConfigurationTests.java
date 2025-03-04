// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.batch.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.batch.models.IpAddressProvisioningType;
import com.azure.resourcemanager.batch.models.PublicIpAddressConfiguration;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class PublicIpAddressConfigurationTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        PublicIpAddressConfiguration model =
            BinaryData
                .fromString("{\"provision\":\"UserManaged\",\"ipAddressIds\":[\"yrnxxmueedn\",\"rdvstkwqqtch\"]}")
                .toObject(PublicIpAddressConfiguration.class);
        Assertions.assertEquals(IpAddressProvisioningType.USER_MANAGED, model.provision());
        Assertions.assertEquals("yrnxxmueedn", model.ipAddressIds().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        PublicIpAddressConfiguration model =
            new PublicIpAddressConfiguration()
                .withProvision(IpAddressProvisioningType.USER_MANAGED)
                .withIpAddressIds(Arrays.asList("yrnxxmueedn", "rdvstkwqqtch"));
        model = BinaryData.fromObject(model).toObject(PublicIpAddressConfiguration.class);
        Assertions.assertEquals(IpAddressProvisioningType.USER_MANAGED, model.provision());
        Assertions.assertEquals("yrnxxmueedn", model.ipAddressIds().get(0));
    }
}
