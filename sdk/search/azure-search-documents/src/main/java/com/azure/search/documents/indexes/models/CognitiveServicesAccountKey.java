// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
//
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is regenerated.
package com.azure.search.documents.indexes.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** The multi-region account key of an Azure AI service resource that's attached to a skillset. */
@Fluent
public final class CognitiveServicesAccountKey extends CognitiveServicesAccount {

    /*
     * The key used to provision the Azure AI service resource attached to a skillset.
     */
    private String key;

    /**
     * Creates an instance of CognitiveServicesAccountKey class.
     *
     * @param key the key value to set.
     */
    public CognitiveServicesAccountKey(String key) {
        this.key = key;
    }

    /**
     * Get the key property: The key used to provision the Azure AI service resource attached to a skillset.
     *
     * @return the key value.
     */
    public String getKey() {
        return this.key;
    }

    /** {@inheritDoc} */
    @Override
    public CognitiveServicesAccountKey setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("@odata.type", "#Microsoft.Azure.Search.CognitiveServicesByKey");
        jsonWriter.writeStringField("description", getDescription());
        jsonWriter.writeStringField("key", this.key);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of CognitiveServicesAccountKey from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of CognitiveServicesAccountKey if the JsonReader was pointing to an instance of it, or null
     *     if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties or the
     *     polymorphic discriminator.
     * @throws IOException If an error occurs while reading the CognitiveServicesAccountKey.
     */
    public static CognitiveServicesAccountKey fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(
                reader -> {
                    String description = null;
                    boolean keyFound = false;
                    String key = null;
                    while (reader.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = reader.getFieldName();
                        reader.nextToken();
                        if ("@odata.type".equals(fieldName)) {
                            String odataType = reader.getString();
                            if (!"#Microsoft.Azure.Search.CognitiveServicesByKey".equals(odataType)) {
                                throw new IllegalStateException(
                                        "'@odata.type' was expected to be non-null and equal to '#Microsoft.Azure.Search.CognitiveServicesByKey'. The found '@odata.type' was '"
                                                + odataType
                                                + "'.");
                            }
                        } else if ("description".equals(fieldName)) {
                            description = reader.getString();
                        } else if ("key".equals(fieldName)) {
                            key = reader.getString();
                            keyFound = true;
                        } else {
                            reader.skipChildren();
                        }
                    }
                    if (keyFound) {
                        CognitiveServicesAccountKey deserializedCognitiveServicesAccountKey =
                                new CognitiveServicesAccountKey(key);
                        deserializedCognitiveServicesAccountKey.setDescription(description);
                        return deserializedCognitiveServicesAccountKey;
                    }
                    List<String> missingProperties = new ArrayList<>();
                    if (!keyFound) {
                        missingProperties.add("key");
                    }
                    throw new IllegalStateException(
                            "Missing required property/properties: " + String.join(", ", missingProperties));
                });
    }

    /**
     * Set the key property: The key used to provision the cognitive service resource attached to a skillset.
     *
     * @param key the key value to set.
     * @return the CognitiveServicesAccountKey object itself.
     */
    public CognitiveServicesAccountKey setKey(String key) {
        this.key = key;
        return this;
    }
}
