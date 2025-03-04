// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.postgresqlflexibleserver.models;

import com.azure.core.util.Context;
import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.PrivateEndpointConnectionInner;

/** Resource collection API of PrivateEndpointConnectionOperations. */
public interface PrivateEndpointConnectionOperations {
    /**
     * Approve or reject a private endpoint connection with a given name.
     *
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param serverName The name of the server.
     * @param privateEndpointConnectionName The name of the private endpoint connection.
     * @param parameters The required parameters for updating private endpoint connection.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the private endpoint connection resource.
     */
    PrivateEndpointConnection update(
        String resourceGroupName,
        String serverName,
        String privateEndpointConnectionName,
        PrivateEndpointConnectionInner parameters);

    /**
     * Approve or reject a private endpoint connection with a given name.
     *
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param serverName The name of the server.
     * @param privateEndpointConnectionName The name of the private endpoint connection.
     * @param parameters The required parameters for updating private endpoint connection.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the private endpoint connection resource.
     */
    PrivateEndpointConnection update(
        String resourceGroupName,
        String serverName,
        String privateEndpointConnectionName,
        PrivateEndpointConnectionInner parameters,
        Context context);

    /**
     * Deletes a private endpoint connection with a given name.
     *
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param serverName The name of the server.
     * @param privateEndpointConnectionName The name of the private endpoint connection.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void delete(String resourceGroupName, String serverName, String privateEndpointConnectionName);

    /**
     * Deletes a private endpoint connection with a given name.
     *
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param serverName The name of the server.
     * @param privateEndpointConnectionName The name of the private endpoint connection.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    void delete(String resourceGroupName, String serverName, String privateEndpointConnectionName, Context context);
}
