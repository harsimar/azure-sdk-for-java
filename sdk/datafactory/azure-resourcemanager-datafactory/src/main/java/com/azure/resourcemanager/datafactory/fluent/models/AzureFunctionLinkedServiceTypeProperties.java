// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.datafactory.models.CredentialReference;
import com.azure.resourcemanager.datafactory.models.SecretBase;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Azure Function linked service properties.
 */
@Fluent
public final class AzureFunctionLinkedServiceTypeProperties {
    /*
     * The endpoint of the Azure Function App. URL will be in the format https://<accountName>.azurewebsites.net.
     */
    @JsonProperty(value = "functionAppUrl", required = true)
    private Object functionAppUrl;

    /*
     * Function or Host key for Azure Function App.
     */
    @JsonProperty(value = "functionKey")
    private SecretBase functionKey;

    /*
     * The encrypted credential used for authentication. Credentials are encrypted using the integration runtime
     * credential manager. Type: string.
     */
    @JsonProperty(value = "encryptedCredential")
    private String encryptedCredential;

    /*
     * The credential reference containing authentication information.
     */
    @JsonProperty(value = "credential")
    private CredentialReference credential;

    /*
     * Allowed token audiences for azure function.
     */
    @JsonProperty(value = "resourceId")
    private Object resourceId;

    /*
     * Type of authentication (Required to specify MSI) used to connect to AzureFunction. Type: string (or Expression
     * with resultType string).
     */
    @JsonProperty(value = "authentication")
    private Object authentication;

    /**
     * Creates an instance of AzureFunctionLinkedServiceTypeProperties class.
     */
    public AzureFunctionLinkedServiceTypeProperties() {
    }

    /**
     * Get the functionAppUrl property: The endpoint of the Azure Function App. URL will be in the format
     * https://&lt;accountName&gt;.azurewebsites.net.
     * 
     * @return the functionAppUrl value.
     */
    public Object functionAppUrl() {
        return this.functionAppUrl;
    }

    /**
     * Set the functionAppUrl property: The endpoint of the Azure Function App. URL will be in the format
     * https://&lt;accountName&gt;.azurewebsites.net.
     * 
     * @param functionAppUrl the functionAppUrl value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withFunctionAppUrl(Object functionAppUrl) {
        this.functionAppUrl = functionAppUrl;
        return this;
    }

    /**
     * Get the functionKey property: Function or Host key for Azure Function App.
     * 
     * @return the functionKey value.
     */
    public SecretBase functionKey() {
        return this.functionKey;
    }

    /**
     * Set the functionKey property: Function or Host key for Azure Function App.
     * 
     * @param functionKey the functionKey value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withFunctionKey(SecretBase functionKey) {
        this.functionKey = functionKey;
        return this;
    }

    /**
     * Get the encryptedCredential property: The encrypted credential used for authentication. Credentials are
     * encrypted using the integration runtime credential manager. Type: string.
     * 
     * @return the encryptedCredential value.
     */
    public String encryptedCredential() {
        return this.encryptedCredential;
    }

    /**
     * Set the encryptedCredential property: The encrypted credential used for authentication. Credentials are
     * encrypted using the integration runtime credential manager. Type: string.
     * 
     * @param encryptedCredential the encryptedCredential value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withEncryptedCredential(String encryptedCredential) {
        this.encryptedCredential = encryptedCredential;
        return this;
    }

    /**
     * Get the credential property: The credential reference containing authentication information.
     * 
     * @return the credential value.
     */
    public CredentialReference credential() {
        return this.credential;
    }

    /**
     * Set the credential property: The credential reference containing authentication information.
     * 
     * @param credential the credential value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withCredential(CredentialReference credential) {
        this.credential = credential;
        return this;
    }

    /**
     * Get the resourceId property: Allowed token audiences for azure function.
     * 
     * @return the resourceId value.
     */
    public Object resourceId() {
        return this.resourceId;
    }

    /**
     * Set the resourceId property: Allowed token audiences for azure function.
     * 
     * @param resourceId the resourceId value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withResourceId(Object resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    /**
     * Get the authentication property: Type of authentication (Required to specify MSI) used to connect to
     * AzureFunction. Type: string (or Expression with resultType string).
     * 
     * @return the authentication value.
     */
    public Object authentication() {
        return this.authentication;
    }

    /**
     * Set the authentication property: Type of authentication (Required to specify MSI) used to connect to
     * AzureFunction. Type: string (or Expression with resultType string).
     * 
     * @param authentication the authentication value to set.
     * @return the AzureFunctionLinkedServiceTypeProperties object itself.
     */
    public AzureFunctionLinkedServiceTypeProperties withAuthentication(Object authentication) {
        this.authentication = authentication;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (functionAppUrl() == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                "Missing required property functionAppUrl in model AzureFunctionLinkedServiceTypeProperties"));
        }
        if (functionKey() != null) {
            functionKey().validate();
        }
        if (credential() != null) {
            credential().validate();
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(AzureFunctionLinkedServiceTypeProperties.class);
}
