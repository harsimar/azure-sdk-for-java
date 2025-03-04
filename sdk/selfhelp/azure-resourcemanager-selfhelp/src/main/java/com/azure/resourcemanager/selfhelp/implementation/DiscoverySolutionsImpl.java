// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.selfhelp.implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.selfhelp.fluent.DiscoverySolutionsClient;
import com.azure.resourcemanager.selfhelp.fluent.models.SolutionMetadataResourceInner;
import com.azure.resourcemanager.selfhelp.models.DiscoverySolutions;
import com.azure.resourcemanager.selfhelp.models.SolutionMetadataResource;

public final class DiscoverySolutionsImpl implements DiscoverySolutions {
    private static final ClientLogger LOGGER = new ClientLogger(DiscoverySolutionsImpl.class);

    private final DiscoverySolutionsClient innerClient;

    private final com.azure.resourcemanager.selfhelp.SelfHelpManager serviceManager;

    public DiscoverySolutionsImpl(
        DiscoverySolutionsClient innerClient, com.azure.resourcemanager.selfhelp.SelfHelpManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public PagedIterable<SolutionMetadataResource> list(String scope) {
        PagedIterable<SolutionMetadataResourceInner> inner = this.serviceClient().list(scope);
        return Utils.mapPage(inner, inner1 -> new SolutionMetadataResourceImpl(inner1, this.manager()));
    }

    public PagedIterable<SolutionMetadataResource> list(
        String scope, String filter, String skiptoken, Context context) {
        PagedIterable<SolutionMetadataResourceInner> inner =
            this.serviceClient().list(scope, filter, skiptoken, context);
        return Utils.mapPage(inner, inner1 -> new SolutionMetadataResourceImpl(inner1, this.manager()));
    }

    private DiscoverySolutionsClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.selfhelp.SelfHelpManager manager() {
        return this.serviceManager;
    }
}
