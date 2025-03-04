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
import java.util.Objects;

/** Tokenizes the input into n-grams of the given size(s). This tokenizer is implemented using Apache Lucene. */
@Fluent
public final class NGramTokenizer extends LexicalTokenizer {

    /*
     * The minimum n-gram length. Default is 1. Maximum is 300. Must be less than the value of maxGram.
     */
    private Integer minGram;

    /*
     * The maximum n-gram length. Default is 2. Maximum is 300.
     */
    private Integer maxGram;

    /*
     * Character classes to keep in the tokens.
     */
    private List<TokenCharacterKind> tokenChars;

    /**
     * Creates an instance of NGramTokenizer class.
     *
     * @param name the name value to set.
     */
    public NGramTokenizer(String name) {
        super(name);
    }

    /**
     * Get the minGram property: The minimum n-gram length. Default is 1. Maximum is 300. Must be less than the value of
     * maxGram.
     *
     * @return the minGram value.
     */
    public Integer getMinGram() {
        return this.minGram;
    }

    /**
     * Set the minGram property: The minimum n-gram length. Default is 1. Maximum is 300. Must be less than the value of
     * maxGram.
     *
     * @param minGram the minGram value to set.
     * @return the NGramTokenizer object itself.
     */
    public NGramTokenizer setMinGram(Integer minGram) {
        this.minGram = minGram;
        return this;
    }

    /**
     * Get the maxGram property: The maximum n-gram length. Default is 2. Maximum is 300.
     *
     * @return the maxGram value.
     */
    public Integer getMaxGram() {
        return this.maxGram;
    }

    /**
     * Set the maxGram property: The maximum n-gram length. Default is 2. Maximum is 300.
     *
     * @param maxGram the maxGram value to set.
     * @return the NGramTokenizer object itself.
     */
    public NGramTokenizer setMaxGram(Integer maxGram) {
        this.maxGram = maxGram;
        return this;
    }

    /**
     * Get the tokenChars property: Character classes to keep in the tokens.
     *
     * @return the tokenChars value.
     */
    public List<TokenCharacterKind> getTokenChars() {
        return this.tokenChars;
    }

    /**
     * Set the tokenChars property: Character classes to keep in the tokens.
     *
     * @param tokenChars the tokenChars value to set.
     * @return the NGramTokenizer object itself.
     */
    public NGramTokenizer setTokenChars(List<TokenCharacterKind> tokenChars) {
        this.tokenChars = tokenChars;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("@odata.type", "#Microsoft.Azure.Search.NGramTokenizer");
        jsonWriter.writeStringField("name", getName());
        jsonWriter.writeNumberField("minGram", this.minGram);
        jsonWriter.writeNumberField("maxGram", this.maxGram);
        jsonWriter.writeArrayField(
                "tokenChars",
                this.tokenChars,
                (writer, element) -> writer.writeString(Objects.toString(element, null)));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of NGramTokenizer from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of NGramTokenizer if the JsonReader was pointing to an instance of it, or null if it was
     *     pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties or the
     *     polymorphic discriminator.
     * @throws IOException If an error occurs while reading the NGramTokenizer.
     */
    public static NGramTokenizer fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(
                reader -> {
                    boolean nameFound = false;
                    String name = null;
                    Integer minGram = null;
                    Integer maxGram = null;
                    List<TokenCharacterKind> tokenChars = null;
                    while (reader.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = reader.getFieldName();
                        reader.nextToken();
                        if ("@odata.type".equals(fieldName)) {
                            String odataType = reader.getString();
                            if (!"#Microsoft.Azure.Search.NGramTokenizer".equals(odataType)) {
                                throw new IllegalStateException(
                                        "'@odata.type' was expected to be non-null and equal to '#Microsoft.Azure.Search.NGramTokenizer'. The found '@odata.type' was '"
                                                + odataType
                                                + "'.");
                            }
                        } else if ("name".equals(fieldName)) {
                            name = reader.getString();
                            nameFound = true;
                        } else if ("minGram".equals(fieldName)) {
                            minGram = reader.getNullable(JsonReader::getInt);
                        } else if ("maxGram".equals(fieldName)) {
                            maxGram = reader.getNullable(JsonReader::getInt);
                        } else if ("tokenChars".equals(fieldName)) {
                            tokenChars =
                                    reader.readArray(reader1 -> TokenCharacterKind.fromString(reader1.getString()));
                        } else {
                            reader.skipChildren();
                        }
                    }
                    if (nameFound) {
                        NGramTokenizer deserializedNGramTokenizer = new NGramTokenizer(name);
                        deserializedNGramTokenizer.minGram = minGram;
                        deserializedNGramTokenizer.maxGram = maxGram;
                        deserializedNGramTokenizer.tokenChars = tokenChars;
                        return deserializedNGramTokenizer;
                    }
                    List<String> missingProperties = new ArrayList<>();
                    if (!nameFound) {
                        missingProperties.add("name");
                    }
                    throw new IllegalStateException(
                            "Missing required property/properties: " + String.join(", ", missingProperties));
                });
    }

    /**
     * Set the tokenChars property: Character classes to keep in the tokens.
     *
     * @param tokenChars the tokenChars value to set.
     * @return the NGramTokenizer object itself.
     */
    public NGramTokenizer setTokenChars(TokenCharacterKind... tokenChars) {
        this.tokenChars = (tokenChars == null) ? null : java.util.Arrays.asList(tokenChars);
        return this;
    }
}
