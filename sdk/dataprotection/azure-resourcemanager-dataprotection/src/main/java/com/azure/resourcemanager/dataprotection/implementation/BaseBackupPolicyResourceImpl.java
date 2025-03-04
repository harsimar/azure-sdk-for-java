// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.dataprotection.implementation;

import com.azure.core.management.SystemData;
import com.azure.core.util.Context;
import com.azure.resourcemanager.dataprotection.fluent.models.BaseBackupPolicyResourceInner;
import com.azure.resourcemanager.dataprotection.models.BaseBackupPolicy;
import com.azure.resourcemanager.dataprotection.models.BaseBackupPolicyResource;

public final class BaseBackupPolicyResourceImpl
    implements BaseBackupPolicyResource, BaseBackupPolicyResource.Definition, BaseBackupPolicyResource.Update {
    private BaseBackupPolicyResourceInner innerObject;

    private final com.azure.resourcemanager.dataprotection.DataProtectionManager serviceManager;

    public String id() {
        return this.innerModel().id();
    }

    public String name() {
        return this.innerModel().name();
    }

    public String type() {
        return this.innerModel().type();
    }

    public SystemData systemData() {
        return this.innerModel().systemData();
    }

    public BaseBackupPolicy properties() {
        return this.innerModel().properties();
    }

    public String resourceGroupName() {
        return resourceGroupName;
    }

    public BaseBackupPolicyResourceInner innerModel() {
        return this.innerObject;
    }

    private com.azure.resourcemanager.dataprotection.DataProtectionManager manager() {
        return this.serviceManager;
    }

    private String resourceGroupName;

    private String vaultName;

    private String backupPolicyName;

    public BaseBackupPolicyResourceImpl withExistingBackupVault(String resourceGroupName, String vaultName) {
        this.resourceGroupName = resourceGroupName;
        this.vaultName = vaultName;
        return this;
    }

    public BaseBackupPolicyResource create() {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .createOrUpdateWithResponse(
                    resourceGroupName, vaultName, backupPolicyName, this.innerModel(), Context.NONE)
                .getValue();
        return this;
    }

    public BaseBackupPolicyResource create(Context context) {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .createOrUpdateWithResponse(resourceGroupName, vaultName, backupPolicyName, this.innerModel(), context)
                .getValue();
        return this;
    }

    BaseBackupPolicyResourceImpl(
        String name, com.azure.resourcemanager.dataprotection.DataProtectionManager serviceManager) {
        this.innerObject = new BaseBackupPolicyResourceInner();
        this.serviceManager = serviceManager;
        this.backupPolicyName = name;
    }

    public BaseBackupPolicyResourceImpl update() {
        return this;
    }

    public BaseBackupPolicyResource apply() {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .createOrUpdateWithResponse(
                    resourceGroupName, vaultName, backupPolicyName, this.innerModel(), Context.NONE)
                .getValue();
        return this;
    }

    public BaseBackupPolicyResource apply(Context context) {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .createOrUpdateWithResponse(resourceGroupName, vaultName, backupPolicyName, this.innerModel(), context)
                .getValue();
        return this;
    }

    BaseBackupPolicyResourceImpl(
        BaseBackupPolicyResourceInner innerObject,
        com.azure.resourcemanager.dataprotection.DataProtectionManager serviceManager) {
        this.innerObject = innerObject;
        this.serviceManager = serviceManager;
        this.resourceGroupName = Utils.getValueFromIdByName(innerObject.id(), "resourceGroups");
        this.vaultName = Utils.getValueFromIdByName(innerObject.id(), "backupVaults");
        this.backupPolicyName = Utils.getValueFromIdByName(innerObject.id(), "backupPolicies");
    }

    public BaseBackupPolicyResource refresh() {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .getWithResponse(resourceGroupName, vaultName, backupPolicyName, Context.NONE)
                .getValue();
        return this;
    }

    public BaseBackupPolicyResource refresh(Context context) {
        this.innerObject =
            serviceManager
                .serviceClient()
                .getBackupPolicies()
                .getWithResponse(resourceGroupName, vaultName, backupPolicyName, context)
                .getValue();
        return this;
    }

    public BaseBackupPolicyResourceImpl withProperties(BaseBackupPolicy properties) {
        this.innerModel().withProperties(properties);
        return this;
    }
}
