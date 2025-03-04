// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.recoveryservicessiterecovery.models.AzureToAzureVmSyncedConfigDetails;
import com.azure.resourcemanager.recoveryservicessiterecovery.models.InputEndpoint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class AzureToAzureVmSyncedConfigDetailsTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        AzureToAzureVmSyncedConfigDetails model =
            BinaryData
                .fromString(
                    "{\"tags\":{\"jceatlijjjrtva\":\"flwfgziiuci\",\"xk\":\"caszk\",\"ignohi\":\"ccxetyvkun\",\"indedvabbx\":\"kgqogjw\"},\"inputEndpoints\":[{\"endpointName\":\"dei\",\"privatePort\":1416675602,\"publicPort\":277548984,\"protocol\":\"cfxzirzzih\"},{\"endpointName\":\"ypusuvjslczwci\",\"privatePort\":73772123,\"publicPort\":1150687421,\"protocol\":\"fryvdmvxadqac\"}]}")
                .toObject(AzureToAzureVmSyncedConfigDetails.class);
        Assertions.assertEquals("flwfgziiuci", model.tags().get("jceatlijjjrtva"));
        Assertions.assertEquals("dei", model.inputEndpoints().get(0).endpointName());
        Assertions.assertEquals(1416675602, model.inputEndpoints().get(0).privatePort());
        Assertions.assertEquals(277548984, model.inputEndpoints().get(0).publicPort());
        Assertions.assertEquals("cfxzirzzih", model.inputEndpoints().get(0).protocol());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        AzureToAzureVmSyncedConfigDetails model =
            new AzureToAzureVmSyncedConfigDetails()
                .withTags(
                    mapOf(
                        "jceatlijjjrtva",
                        "flwfgziiuci",
                        "xk",
                        "caszk",
                        "ignohi",
                        "ccxetyvkun",
                        "indedvabbx",
                        "kgqogjw"))
                .withInputEndpoints(
                    Arrays
                        .asList(
                            new InputEndpoint()
                                .withEndpointName("dei")
                                .withPrivatePort(1416675602)
                                .withPublicPort(277548984)
                                .withProtocol("cfxzirzzih"),
                            new InputEndpoint()
                                .withEndpointName("ypusuvjslczwci")
                                .withPrivatePort(73772123)
                                .withPublicPort(1150687421)
                                .withProtocol("fryvdmvxadqac")));
        model = BinaryData.fromObject(model).toObject(AzureToAzureVmSyncedConfigDetails.class);
        Assertions.assertEquals("flwfgziiuci", model.tags().get("jceatlijjjrtva"));
        Assertions.assertEquals("dei", model.inputEndpoints().get(0).endpointName());
        Assertions.assertEquals(1416675602, model.inputEndpoints().get(0).privatePort());
        Assertions.assertEquals(277548984, model.inputEndpoints().get(0).publicPort());
        Assertions.assertEquals("cfxzirzzih", model.inputEndpoints().get(0).protocol());
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
