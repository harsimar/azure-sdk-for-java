// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** InMageRcm update protection container mapping. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "instanceType")
@JsonTypeName("InMageRcm")
@Fluent
public final class InMageRcmUpdateContainerMappingInput extends ReplicationProviderSpecificUpdateContainerMappingInput {
    /*
     * A value indicating whether agent auto upgrade has to be enabled.
     */
    @JsonProperty(value = "enableAgentAutoUpgrade", required = true)
    private String enableAgentAutoUpgrade;

    /** Creates an instance of InMageRcmUpdateContainerMappingInput class. */
    public InMageRcmUpdateContainerMappingInput() {
    }

    /**
     * Get the enableAgentAutoUpgrade property: A value indicating whether agent auto upgrade has to be enabled.
     *
     * @return the enableAgentAutoUpgrade value.
     */
    public String enableAgentAutoUpgrade() {
        return this.enableAgentAutoUpgrade;
    }

    /**
     * Set the enableAgentAutoUpgrade property: A value indicating whether agent auto upgrade has to be enabled.
     *
     * @param enableAgentAutoUpgrade the enableAgentAutoUpgrade value to set.
     * @return the InMageRcmUpdateContainerMappingInput object itself.
     */
    public InMageRcmUpdateContainerMappingInput withEnableAgentAutoUpgrade(String enableAgentAutoUpgrade) {
        this.enableAgentAutoUpgrade = enableAgentAutoUpgrade;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        super.validate();
        if (enableAgentAutoUpgrade() == null) {
            throw LOGGER
                .logExceptionAsError(
                    new IllegalArgumentException(
                        "Missing required property enableAgentAutoUpgrade in model"
                            + " InMageRcmUpdateContainerMappingInput"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(InMageRcmUpdateContainerMappingInput.class);
}
