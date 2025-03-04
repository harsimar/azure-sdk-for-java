// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.netapp.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.netapp.models.MonthlySchedule;
import org.junit.jupiter.api.Assertions;

public final class MonthlyScheduleTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        MonthlySchedule model =
            BinaryData
                .fromString(
                    "{\"snapshotsToKeep\":1521931041,\"daysOfMonth\":\"krmnjijpxacqqud\",\"hour\":402319478,\"minute\":217779301,\"usedBytes\":3011567239664455561}")
                .toObject(MonthlySchedule.class);
        Assertions.assertEquals(1521931041, model.snapshotsToKeep());
        Assertions.assertEquals("krmnjijpxacqqud", model.daysOfMonth());
        Assertions.assertEquals(402319478, model.hour());
        Assertions.assertEquals(217779301, model.minute());
        Assertions.assertEquals(3011567239664455561L, model.usedBytes());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        MonthlySchedule model =
            new MonthlySchedule()
                .withSnapshotsToKeep(1521931041)
                .withDaysOfMonth("krmnjijpxacqqud")
                .withHour(402319478)
                .withMinute(217779301)
                .withUsedBytes(3011567239664455561L);
        model = BinaryData.fromObject(model).toObject(MonthlySchedule.class);
        Assertions.assertEquals(1521931041, model.snapshotsToKeep());
        Assertions.assertEquals("krmnjijpxacqqud", model.daysOfMonth());
        Assertions.assertEquals(402319478, model.hour());
        Assertions.assertEquals(217779301, model.minute());
        Assertions.assertEquals(3011567239664455561L, model.usedBytes());
    }
}
