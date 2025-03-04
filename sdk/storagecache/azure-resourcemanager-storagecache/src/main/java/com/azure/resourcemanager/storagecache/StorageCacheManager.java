// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.storagecache;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.HttpPipelinePosition;
import com.azure.core.http.policy.AddDatePolicy;
import com.azure.core.http.policy.AddHeadersFromContextPolicy;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.HttpPolicyProviders;
import com.azure.core.http.policy.RequestIdPolicy;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.policy.UserAgentPolicy;
import com.azure.core.management.http.policy.ArmChallengeAuthenticationPolicy;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Configuration;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.storagecache.fluent.StorageCacheManagementClient;
import com.azure.resourcemanager.storagecache.implementation.AmlFilesystemsImpl;
import com.azure.resourcemanager.storagecache.implementation.AscOperationsImpl;
import com.azure.resourcemanager.storagecache.implementation.AscUsagesImpl;
import com.azure.resourcemanager.storagecache.implementation.CachesImpl;
import com.azure.resourcemanager.storagecache.implementation.OperationsImpl;
import com.azure.resourcemanager.storagecache.implementation.ResourceProvidersImpl;
import com.azure.resourcemanager.storagecache.implementation.SkusImpl;
import com.azure.resourcemanager.storagecache.implementation.StorageCacheManagementClientBuilder;
import com.azure.resourcemanager.storagecache.implementation.StorageTargetOperationsImpl;
import com.azure.resourcemanager.storagecache.implementation.StorageTargetsImpl;
import com.azure.resourcemanager.storagecache.implementation.UsageModelsImpl;
import com.azure.resourcemanager.storagecache.models.AmlFilesystems;
import com.azure.resourcemanager.storagecache.models.AscOperations;
import com.azure.resourcemanager.storagecache.models.AscUsages;
import com.azure.resourcemanager.storagecache.models.Caches;
import com.azure.resourcemanager.storagecache.models.Operations;
import com.azure.resourcemanager.storagecache.models.ResourceProviders;
import com.azure.resourcemanager.storagecache.models.Skus;
import com.azure.resourcemanager.storagecache.models.StorageTargetOperations;
import com.azure.resourcemanager.storagecache.models.StorageTargets;
import com.azure.resourcemanager.storagecache.models.UsageModels;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entry point to StorageCacheManager. Azure Managed Lustre provides a fully managed Lustre® file system, integrated
 * with Blob storage, for use on demand. These operations create and manage Azure Managed Lustre file systems.
 */
public final class StorageCacheManager {
    private AmlFilesystems amlFilesystems;

    private ResourceProviders resourceProviders;

    private Operations operations;

    private Skus skus;

    private UsageModels usageModels;

    private AscOperations ascOperations;

    private AscUsages ascUsages;

    private Caches caches;

    private StorageTargets storageTargets;

    private StorageTargetOperations storageTargetOperations;

    private final StorageCacheManagementClient clientObject;

    private StorageCacheManager(HttpPipeline httpPipeline, AzureProfile profile, Duration defaultPollInterval) {
        Objects.requireNonNull(httpPipeline, "'httpPipeline' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        this.clientObject =
            new StorageCacheManagementClientBuilder()
                .pipeline(httpPipeline)
                .endpoint(profile.getEnvironment().getResourceManagerEndpoint())
                .subscriptionId(profile.getSubscriptionId())
                .defaultPollInterval(defaultPollInterval)
                .buildClient();
    }

    /**
     * Creates an instance of StorageCache service API entry point.
     *
     * @param credential the credential to use.
     * @param profile the Azure profile for client.
     * @return the StorageCache service API instance.
     */
    public static StorageCacheManager authenticate(TokenCredential credential, AzureProfile profile) {
        Objects.requireNonNull(credential, "'credential' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        return configure().authenticate(credential, profile);
    }

    /**
     * Creates an instance of StorageCache service API entry point.
     *
     * @param httpPipeline the {@link HttpPipeline} configured with Azure authentication credential.
     * @param profile the Azure profile for client.
     * @return the StorageCache service API instance.
     */
    public static StorageCacheManager authenticate(HttpPipeline httpPipeline, AzureProfile profile) {
        Objects.requireNonNull(httpPipeline, "'httpPipeline' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        return new StorageCacheManager(httpPipeline, profile, null);
    }

    /**
     * Gets a Configurable instance that can be used to create StorageCacheManager with optional configuration.
     *
     * @return the Configurable instance allowing configurations.
     */
    public static Configurable configure() {
        return new StorageCacheManager.Configurable();
    }

    /** The Configurable allowing configurations to be set. */
    public static final class Configurable {
        private static final ClientLogger LOGGER = new ClientLogger(Configurable.class);

        private HttpClient httpClient;
        private HttpLogOptions httpLogOptions;
        private final List<HttpPipelinePolicy> policies = new ArrayList<>();
        private final List<String> scopes = new ArrayList<>();
        private RetryPolicy retryPolicy;
        private RetryOptions retryOptions;
        private Duration defaultPollInterval;

        private Configurable() {
        }

        /**
         * Sets the http client.
         *
         * @param httpClient the HTTP client.
         * @return the configurable object itself.
         */
        public Configurable withHttpClient(HttpClient httpClient) {
            this.httpClient = Objects.requireNonNull(httpClient, "'httpClient' cannot be null.");
            return this;
        }

        /**
         * Sets the logging options to the HTTP pipeline.
         *
         * @param httpLogOptions the HTTP log options.
         * @return the configurable object itself.
         */
        public Configurable withLogOptions(HttpLogOptions httpLogOptions) {
            this.httpLogOptions = Objects.requireNonNull(httpLogOptions, "'httpLogOptions' cannot be null.");
            return this;
        }

        /**
         * Adds the pipeline policy to the HTTP pipeline.
         *
         * @param policy the HTTP pipeline policy.
         * @return the configurable object itself.
         */
        public Configurable withPolicy(HttpPipelinePolicy policy) {
            this.policies.add(Objects.requireNonNull(policy, "'policy' cannot be null."));
            return this;
        }

        /**
         * Adds the scope to permission sets.
         *
         * @param scope the scope.
         * @return the configurable object itself.
         */
        public Configurable withScope(String scope) {
            this.scopes.add(Objects.requireNonNull(scope, "'scope' cannot be null."));
            return this;
        }

        /**
         * Sets the retry policy to the HTTP pipeline.
         *
         * @param retryPolicy the HTTP pipeline retry policy.
         * @return the configurable object itself.
         */
        public Configurable withRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = Objects.requireNonNull(retryPolicy, "'retryPolicy' cannot be null.");
            return this;
        }

        /**
         * Sets the retry options for the HTTP pipeline retry policy.
         *
         * <p>This setting has no effect, if retry policy is set via {@link #withRetryPolicy(RetryPolicy)}.
         *
         * @param retryOptions the retry options for the HTTP pipeline retry policy.
         * @return the configurable object itself.
         */
        public Configurable withRetryOptions(RetryOptions retryOptions) {
            this.retryOptions = Objects.requireNonNull(retryOptions, "'retryOptions' cannot be null.");
            return this;
        }

        /**
         * Sets the default poll interval, used when service does not provide "Retry-After" header.
         *
         * @param defaultPollInterval the default poll interval.
         * @return the configurable object itself.
         */
        public Configurable withDefaultPollInterval(Duration defaultPollInterval) {
            this.defaultPollInterval =
                Objects.requireNonNull(defaultPollInterval, "'defaultPollInterval' cannot be null.");
            if (this.defaultPollInterval.isNegative()) {
                throw LOGGER
                    .logExceptionAsError(new IllegalArgumentException("'defaultPollInterval' cannot be negative"));
            }
            return this;
        }

        /**
         * Creates an instance of StorageCache service API entry point.
         *
         * @param credential the credential to use.
         * @param profile the Azure profile for client.
         * @return the StorageCache service API instance.
         */
        public StorageCacheManager authenticate(TokenCredential credential, AzureProfile profile) {
            Objects.requireNonNull(credential, "'credential' cannot be null.");
            Objects.requireNonNull(profile, "'profile' cannot be null.");

            StringBuilder userAgentBuilder = new StringBuilder();
            userAgentBuilder
                .append("azsdk-java")
                .append("-")
                .append("com.azure.resourcemanager.storagecache")
                .append("/")
                .append("1.0.0-beta.9");
            if (!Configuration.getGlobalConfiguration().get("AZURE_TELEMETRY_DISABLED", false)) {
                userAgentBuilder
                    .append(" (")
                    .append(Configuration.getGlobalConfiguration().get("java.version"))
                    .append("; ")
                    .append(Configuration.getGlobalConfiguration().get("os.name"))
                    .append("; ")
                    .append(Configuration.getGlobalConfiguration().get("os.version"))
                    .append("; auto-generated)");
            } else {
                userAgentBuilder.append(" (auto-generated)");
            }

            if (scopes.isEmpty()) {
                scopes.add(profile.getEnvironment().getManagementEndpoint() + "/.default");
            }
            if (retryPolicy == null) {
                if (retryOptions != null) {
                    retryPolicy = new RetryPolicy(retryOptions);
                } else {
                    retryPolicy = new RetryPolicy("Retry-After", ChronoUnit.SECONDS);
                }
            }
            List<HttpPipelinePolicy> policies = new ArrayList<>();
            policies.add(new UserAgentPolicy(userAgentBuilder.toString()));
            policies.add(new AddHeadersFromContextPolicy());
            policies.add(new RequestIdPolicy());
            policies
                .addAll(
                    this
                        .policies
                        .stream()
                        .filter(p -> p.getPipelinePosition() == HttpPipelinePosition.PER_CALL)
                        .collect(Collectors.toList()));
            HttpPolicyProviders.addBeforeRetryPolicies(policies);
            policies.add(retryPolicy);
            policies.add(new AddDatePolicy());
            policies.add(new ArmChallengeAuthenticationPolicy(credential, scopes.toArray(new String[0])));
            policies
                .addAll(
                    this
                        .policies
                        .stream()
                        .filter(p -> p.getPipelinePosition() == HttpPipelinePosition.PER_RETRY)
                        .collect(Collectors.toList()));
            HttpPolicyProviders.addAfterRetryPolicies(policies);
            policies.add(new HttpLoggingPolicy(httpLogOptions));
            HttpPipeline httpPipeline =
                new HttpPipelineBuilder()
                    .httpClient(httpClient)
                    .policies(policies.toArray(new HttpPipelinePolicy[0]))
                    .build();
            return new StorageCacheManager(httpPipeline, profile, defaultPollInterval);
        }
    }

    /**
     * Gets the resource collection API of AmlFilesystems. It manages AmlFilesystem.
     *
     * @return Resource collection API of AmlFilesystems.
     */
    public AmlFilesystems amlFilesystems() {
        if (this.amlFilesystems == null) {
            this.amlFilesystems = new AmlFilesystemsImpl(clientObject.getAmlFilesystems(), this);
        }
        return amlFilesystems;
    }

    /**
     * Gets the resource collection API of ResourceProviders.
     *
     * @return Resource collection API of ResourceProviders.
     */
    public ResourceProviders resourceProviders() {
        if (this.resourceProviders == null) {
            this.resourceProviders = new ResourceProvidersImpl(clientObject.getResourceProviders(), this);
        }
        return resourceProviders;
    }

    /**
     * Gets the resource collection API of Operations.
     *
     * @return Resource collection API of Operations.
     */
    public Operations operations() {
        if (this.operations == null) {
            this.operations = new OperationsImpl(clientObject.getOperations(), this);
        }
        return operations;
    }

    /**
     * Gets the resource collection API of Skus.
     *
     * @return Resource collection API of Skus.
     */
    public Skus skus() {
        if (this.skus == null) {
            this.skus = new SkusImpl(clientObject.getSkus(), this);
        }
        return skus;
    }

    /**
     * Gets the resource collection API of UsageModels.
     *
     * @return Resource collection API of UsageModels.
     */
    public UsageModels usageModels() {
        if (this.usageModels == null) {
            this.usageModels = new UsageModelsImpl(clientObject.getUsageModels(), this);
        }
        return usageModels;
    }

    /**
     * Gets the resource collection API of AscOperations.
     *
     * @return Resource collection API of AscOperations.
     */
    public AscOperations ascOperations() {
        if (this.ascOperations == null) {
            this.ascOperations = new AscOperationsImpl(clientObject.getAscOperations(), this);
        }
        return ascOperations;
    }

    /**
     * Gets the resource collection API of AscUsages.
     *
     * @return Resource collection API of AscUsages.
     */
    public AscUsages ascUsages() {
        if (this.ascUsages == null) {
            this.ascUsages = new AscUsagesImpl(clientObject.getAscUsages(), this);
        }
        return ascUsages;
    }

    /**
     * Gets the resource collection API of Caches. It manages Cache.
     *
     * @return Resource collection API of Caches.
     */
    public Caches caches() {
        if (this.caches == null) {
            this.caches = new CachesImpl(clientObject.getCaches(), this);
        }
        return caches;
    }

    /**
     * Gets the resource collection API of StorageTargets. It manages StorageTarget.
     *
     * @return Resource collection API of StorageTargets.
     */
    public StorageTargets storageTargets() {
        if (this.storageTargets == null) {
            this.storageTargets = new StorageTargetsImpl(clientObject.getStorageTargets(), this);
        }
        return storageTargets;
    }

    /**
     * Gets the resource collection API of StorageTargetOperations.
     *
     * @return Resource collection API of StorageTargetOperations.
     */
    public StorageTargetOperations storageTargetOperations() {
        if (this.storageTargetOperations == null) {
            this.storageTargetOperations =
                new StorageTargetOperationsImpl(clientObject.getStorageTargetOperations(), this);
        }
        return storageTargetOperations;
    }

    /**
     * @return Wrapped service client StorageCacheManagementClient providing direct access to the underlying
     *     auto-generated API implementation, based on Azure REST API.
     */
    public StorageCacheManagementClient serviceClient() {
        return this.clientObject;
    }
}
