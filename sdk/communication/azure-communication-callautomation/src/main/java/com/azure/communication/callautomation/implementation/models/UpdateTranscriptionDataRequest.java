// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.communication.callautomation.implementation.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The UpdateTranscriptionDataRequest model. */
@Fluent
public final class UpdateTranscriptionDataRequest {
    /*
     * Defines new locale for transcription.
     */
    @JsonProperty(value = "locale", required = true)
    private String locale;

    /**
     * Get the locale property: Defines new locale for transcription.
     *
     * @return the locale value.
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * Set the locale property: Defines new locale for transcription.
     *
     * @param locale the locale value to set.
     * @return the UpdateTranscriptionDataRequest object itself.
     */
    public UpdateTranscriptionDataRequest setLocale(String locale) {
        this.locale = locale;
        return this;
    }
}
