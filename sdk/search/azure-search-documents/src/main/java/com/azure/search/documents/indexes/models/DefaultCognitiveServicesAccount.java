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

/** An empty object that represents the default Azure AI service resource for a skillset. */
@Fluent
public final class DefaultCognitiveServicesAccount extends CognitiveServicesAccount {
    /** Creates an instance of DefaultCognitiveServicesAccount class. */
    public DefaultCognitiveServicesAccount() {}

    /** {@inheritDoc} */
    @Override
    public DefaultCognitiveServicesAccount setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("@odata.type", "#Microsoft.Azure.Search.DefaultCognitiveServices");
        jsonWriter.writeStringField("description", getDescription());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of DefaultCognitiveServicesAccount from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of DefaultCognitiveServicesAccount if the JsonReader was pointing to an instance of it, or
     *     null if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing the polymorphic discriminator.
     * @throws IOException If an error occurs while reading the DefaultCognitiveServicesAccount.
     */
    public static DefaultCognitiveServicesAccount fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(
                reader -> {
                    DefaultCognitiveServicesAccount deserializedDefaultCognitiveServicesAccount =
                            new DefaultCognitiveServicesAccount();
                    while (reader.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = reader.getFieldName();
                        reader.nextToken();

                        if ("@odata.type".equals(fieldName)) {
                            String odataType = reader.getString();
                            if (!"#Microsoft.Azure.Search.DefaultCognitiveServices".equals(odataType)) {
                                throw new IllegalStateException(
                                        "'@odata.type' was expected to be non-null and equal to '#Microsoft.Azure.Search.DefaultCognitiveServices'. The found '@odata.type' was '"
                                                + odataType
                                                + "'.");
                            }
                        } else if ("description".equals(fieldName)) {
                            deserializedDefaultCognitiveServicesAccount.setDescription(reader.getString());
                        } else {
                            reader.skipChildren();
                        }
                    }

                    return deserializedDefaultCognitiveServicesAccount;
                });
    }
}
