// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.support.models;

import com.azure.core.util.Context;
import com.azure.resourcemanager.support.fluent.models.SupportTicketDetailsInner;
import java.time.OffsetDateTime;
import java.util.List;

/** An immutable client-side representation of SupportTicketDetails. */
public interface SupportTicketDetails {
    /**
     * Gets the id property: Fully qualified resource Id for the resource.
     *
     * @return the id value.
     */
    String id();

    /**
     * Gets the name property: The name of the resource.
     *
     * @return the name value.
     */
    String name();

    /**
     * Gets the type property: The type of the resource.
     *
     * @return the type value.
     */
    String type();

    /**
     * Gets the supportTicketId property: System generated support ticket Id that is unique.
     *
     * @return the supportTicketId value.
     */
    String supportTicketId();

    /**
     * Gets the description property: Detailed description of the question or issue.
     *
     * @return the description value.
     */
    String description();

    /**
     * Gets the problemClassificationId property: Each Azure service has its own set of issue categories, also known as
     * problem classification. This parameter is the unique Id for the type of problem you are experiencing.
     *
     * @return the problemClassificationId value.
     */
    String problemClassificationId();

    /**
     * Gets the problemClassificationDisplayName property: Localized name of problem classification.
     *
     * @return the problemClassificationDisplayName value.
     */
    String problemClassificationDisplayName();

    /**
     * Gets the severity property: A value that indicates the urgency of the case, which in turn determines the response
     * time according to the service level agreement of the technical support plan you have with Azure. Note: 'Highest
     * critical impact', also known as the 'Emergency - Severe impact' level in the Azure portal is reserved only for
     * our Premium customers.
     *
     * @return the severity value.
     */
    SeverityLevel severity();

    /**
     * Gets the enrollmentId property: Enrollment Id associated with the support ticket.
     *
     * @return the enrollmentId value.
     */
    String enrollmentId();

    /**
     * Gets the require24X7Response property: Indicates if this requires a 24x7 response from Azure.
     *
     * @return the require24X7Response value.
     */
    Boolean require24X7Response();

    /**
     * Gets the advancedDiagnosticConsent property: Advanced diagnostic consent to be updated on the support ticket.
     *
     * @return the advancedDiagnosticConsent value.
     */
    Consent advancedDiagnosticConsent();

    /**
     * Gets the problemScopingQuestions property: Problem scoping questions associated with the support ticket.
     *
     * @return the problemScopingQuestions value.
     */
    String problemScopingQuestions();

    /**
     * Gets the supportPlanId property: Support plan id associated with the support ticket.
     *
     * @return the supportPlanId value.
     */
    String supportPlanId();

    /**
     * Gets the contactDetails property: Contact information of the user requesting to create a support ticket.
     *
     * @return the contactDetails value.
     */
    ContactProfile contactDetails();

    /**
     * Gets the serviceLevelAgreement property: Service Level Agreement information for this support ticket.
     *
     * @return the serviceLevelAgreement value.
     */
    ServiceLevelAgreement serviceLevelAgreement();

    /**
     * Gets the supportEngineer property: Information about the support engineer working on this support ticket.
     *
     * @return the supportEngineer value.
     */
    SupportEngineer supportEngineer();

    /**
     * Gets the supportPlanType property: Support plan type associated with the support ticket.
     *
     * @return the supportPlanType value.
     */
    String supportPlanType();

    /**
     * Gets the supportPlanDisplayName property: Support plan type associated with the support ticket.
     *
     * @return the supportPlanDisplayName value.
     */
    String supportPlanDisplayName();

    /**
     * Gets the title property: Title of the support ticket.
     *
     * @return the title value.
     */
    String title();

    /**
     * Gets the problemStartTime property: Time in UTC (ISO 8601 format) when the problem started.
     *
     * @return the problemStartTime value.
     */
    OffsetDateTime problemStartTime();

    /**
     * Gets the serviceId property: This is the resource Id of the Azure service resource associated with the support
     * ticket.
     *
     * @return the serviceId value.
     */
    String serviceId();

    /**
     * Gets the serviceDisplayName property: Localized name of the Azure service.
     *
     * @return the serviceDisplayName value.
     */
    String serviceDisplayName();

    /**
     * Gets the status property: Status of the support ticket.
     *
     * @return the status value.
     */
    String status();

    /**
     * Gets the createdDate property: Time in UTC (ISO 8601 format) when the support ticket was created.
     *
     * @return the createdDate value.
     */
    OffsetDateTime createdDate();

    /**
     * Gets the modifiedDate property: Time in UTC (ISO 8601 format) when the support ticket was last modified.
     *
     * @return the modifiedDate value.
     */
    OffsetDateTime modifiedDate();

    /**
     * Gets the fileWorkspaceName property: File workspace name.
     *
     * @return the fileWorkspaceName value.
     */
    String fileWorkspaceName();

    /**
     * Gets the technicalTicketDetails property: Additional ticket details associated with a technical support ticket
     * request.
     *
     * @return the technicalTicketDetails value.
     */
    TechnicalTicketDetails technicalTicketDetails();

    /**
     * Gets the quotaTicketDetails property: Additional ticket details associated with a quota support ticket request.
     *
     * @return the quotaTicketDetails value.
     */
    QuotaTicketDetails quotaTicketDetails();

    /**
     * Gets the secondaryConsent property: This property indicates secondary consents for the support ticket.
     *
     * @return the secondaryConsent value.
     */
    List<SecondaryConsent> secondaryConsent();

    /**
     * Gets the inner com.azure.resourcemanager.support.fluent.models.SupportTicketDetailsInner object.
     *
     * @return the inner object.
     */
    SupportTicketDetailsInner innerModel();

    /** The entirety of the SupportTicketDetails definition. */
    interface Definition extends DefinitionStages.Blank, DefinitionStages.WithCreate {
    }

    /** The SupportTicketDetails definition stages. */
    interface DefinitionStages {
        /** The first stage of the SupportTicketDetails definition. */
        interface Blank extends WithCreate {
        }

        /**
         * The stage of the SupportTicketDetails definition which contains all the minimum required properties for the
         * resource to be created, but also allows for any other optional properties to be specified.
         */
        interface WithCreate
            extends DefinitionStages.WithSupportTicketId,
                DefinitionStages.WithDescription,
                DefinitionStages.WithProblemClassificationId,
                DefinitionStages.WithSeverity,
                DefinitionStages.WithRequire24X7Response,
                DefinitionStages.WithAdvancedDiagnosticConsent,
                DefinitionStages.WithProblemScopingQuestions,
                DefinitionStages.WithSupportPlanId,
                DefinitionStages.WithContactDetails,
                DefinitionStages.WithServiceLevelAgreement,
                DefinitionStages.WithSupportEngineer,
                DefinitionStages.WithTitle,
                DefinitionStages.WithProblemStartTime,
                DefinitionStages.WithServiceId,
                DefinitionStages.WithFileWorkspaceName,
                DefinitionStages.WithTechnicalTicketDetails,
                DefinitionStages.WithQuotaTicketDetails,
                DefinitionStages.WithSecondaryConsent {
            /**
             * Executes the create request.
             *
             * @return the created resource.
             */
            SupportTicketDetails create();

            /**
             * Executes the create request.
             *
             * @param context The context to associate with this operation.
             * @return the created resource.
             */
            SupportTicketDetails create(Context context);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify supportTicketId. */
        interface WithSupportTicketId {
            /**
             * Specifies the supportTicketId property: System generated support ticket Id that is unique..
             *
             * @param supportTicketId System generated support ticket Id that is unique.
             * @return the next definition stage.
             */
            WithCreate withSupportTicketId(String supportTicketId);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify description. */
        interface WithDescription {
            /**
             * Specifies the description property: Detailed description of the question or issue..
             *
             * @param description Detailed description of the question or issue.
             * @return the next definition stage.
             */
            WithCreate withDescription(String description);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify problemClassificationId. */
        interface WithProblemClassificationId {
            /**
             * Specifies the problemClassificationId property: Each Azure service has its own set of issue categories,
             * also known as problem classification. This parameter is the unique Id for the type of problem you are
             * experiencing..
             *
             * @param problemClassificationId Each Azure service has its own set of issue categories, also known as
             *     problem classification. This parameter is the unique Id for the type of problem you are experiencing.
             * @return the next definition stage.
             */
            WithCreate withProblemClassificationId(String problemClassificationId);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify severity. */
        interface WithSeverity {
            /**
             * Specifies the severity property: A value that indicates the urgency of the case, which in turn determines
             * the response time according to the service level agreement of the technical support plan you have with
             * Azure. Note: 'Highest critical impact', also known as the 'Emergency - Severe impact' level in the Azure
             * portal is reserved only for our Premium customers..
             *
             * @param severity A value that indicates the urgency of the case, which in turn determines the response
             *     time according to the service level agreement of the technical support plan you have with Azure.
             *     Note: 'Highest critical impact', also known as the 'Emergency - Severe impact' level in the Azure
             *     portal is reserved only for our Premium customers.
             * @return the next definition stage.
             */
            WithCreate withSeverity(SeverityLevel severity);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify require24X7Response. */
        interface WithRequire24X7Response {
            /**
             * Specifies the require24X7Response property: Indicates if this requires a 24x7 response from Azure..
             *
             * @param require24X7Response Indicates if this requires a 24x7 response from Azure.
             * @return the next definition stage.
             */
            WithCreate withRequire24X7Response(Boolean require24X7Response);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify advancedDiagnosticConsent. */
        interface WithAdvancedDiagnosticConsent {
            /**
             * Specifies the advancedDiagnosticConsent property: Advanced diagnostic consent to be updated on the
             * support ticket..
             *
             * @param advancedDiagnosticConsent Advanced diagnostic consent to be updated on the support ticket.
             * @return the next definition stage.
             */
            WithCreate withAdvancedDiagnosticConsent(Consent advancedDiagnosticConsent);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify problemScopingQuestions. */
        interface WithProblemScopingQuestions {
            /**
             * Specifies the problemScopingQuestions property: Problem scoping questions associated with the support
             * ticket..
             *
             * @param problemScopingQuestions Problem scoping questions associated with the support ticket.
             * @return the next definition stage.
             */
            WithCreate withProblemScopingQuestions(String problemScopingQuestions);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify supportPlanId. */
        interface WithSupportPlanId {
            /**
             * Specifies the supportPlanId property: Support plan id associated with the support ticket..
             *
             * @param supportPlanId Support plan id associated with the support ticket.
             * @return the next definition stage.
             */
            WithCreate withSupportPlanId(String supportPlanId);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify contactDetails. */
        interface WithContactDetails {
            /**
             * Specifies the contactDetails property: Contact information of the user requesting to create a support
             * ticket..
             *
             * @param contactDetails Contact information of the user requesting to create a support ticket.
             * @return the next definition stage.
             */
            WithCreate withContactDetails(ContactProfile contactDetails);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify serviceLevelAgreement. */
        interface WithServiceLevelAgreement {
            /**
             * Specifies the serviceLevelAgreement property: Service Level Agreement information for this support
             * ticket..
             *
             * @param serviceLevelAgreement Service Level Agreement information for this support ticket.
             * @return the next definition stage.
             */
            WithCreate withServiceLevelAgreement(ServiceLevelAgreement serviceLevelAgreement);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify supportEngineer. */
        interface WithSupportEngineer {
            /**
             * Specifies the supportEngineer property: Information about the support engineer working on this support
             * ticket..
             *
             * @param supportEngineer Information about the support engineer working on this support ticket.
             * @return the next definition stage.
             */
            WithCreate withSupportEngineer(SupportEngineer supportEngineer);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify title. */
        interface WithTitle {
            /**
             * Specifies the title property: Title of the support ticket..
             *
             * @param title Title of the support ticket.
             * @return the next definition stage.
             */
            WithCreate withTitle(String title);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify problemStartTime. */
        interface WithProblemStartTime {
            /**
             * Specifies the problemStartTime property: Time in UTC (ISO 8601 format) when the problem started..
             *
             * @param problemStartTime Time in UTC (ISO 8601 format) when the problem started.
             * @return the next definition stage.
             */
            WithCreate withProblemStartTime(OffsetDateTime problemStartTime);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify serviceId. */
        interface WithServiceId {
            /**
             * Specifies the serviceId property: This is the resource Id of the Azure service resource associated with
             * the support ticket..
             *
             * @param serviceId This is the resource Id of the Azure service resource associated with the support
             *     ticket.
             * @return the next definition stage.
             */
            WithCreate withServiceId(String serviceId);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify fileWorkspaceName. */
        interface WithFileWorkspaceName {
            /**
             * Specifies the fileWorkspaceName property: File workspace name..
             *
             * @param fileWorkspaceName File workspace name.
             * @return the next definition stage.
             */
            WithCreate withFileWorkspaceName(String fileWorkspaceName);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify technicalTicketDetails. */
        interface WithTechnicalTicketDetails {
            /**
             * Specifies the technicalTicketDetails property: Additional ticket details associated with a technical
             * support ticket request..
             *
             * @param technicalTicketDetails Additional ticket details associated with a technical support ticket
             *     request.
             * @return the next definition stage.
             */
            WithCreate withTechnicalTicketDetails(TechnicalTicketDetails technicalTicketDetails);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify quotaTicketDetails. */
        interface WithQuotaTicketDetails {
            /**
             * Specifies the quotaTicketDetails property: Additional ticket details associated with a quota support
             * ticket request..
             *
             * @param quotaTicketDetails Additional ticket details associated with a quota support ticket request.
             * @return the next definition stage.
             */
            WithCreate withQuotaTicketDetails(QuotaTicketDetails quotaTicketDetails);
        }

        /** The stage of the SupportTicketDetails definition allowing to specify secondaryConsent. */
        interface WithSecondaryConsent {
            /**
             * Specifies the secondaryConsent property: This property indicates secondary consents for the support
             * ticket.
             *
             * @param secondaryConsent This property indicates secondary consents for the support ticket.
             * @return the next definition stage.
             */
            WithCreate withSecondaryConsent(List<SecondaryConsent> secondaryConsent);
        }
    }

    /**
     * Begins update for the SupportTicketDetails resource.
     *
     * @return the stage of resource update.
     */
    SupportTicketDetails.Update update();

    /** The template for SupportTicketDetails update. */
    interface Update
        extends UpdateStages.WithSeverity,
            UpdateStages.WithStatus,
            UpdateStages.WithContactDetails,
            UpdateStages.WithAdvancedDiagnosticConsent,
            UpdateStages.WithSecondaryConsent {
        /**
         * Executes the update request.
         *
         * @return the updated resource.
         */
        SupportTicketDetails apply();

        /**
         * Executes the update request.
         *
         * @param context The context to associate with this operation.
         * @return the updated resource.
         */
        SupportTicketDetails apply(Context context);
    }

    /** The SupportTicketDetails update stages. */
    interface UpdateStages {
        /** The stage of the SupportTicketDetails update allowing to specify severity. */
        interface WithSeverity {
            /**
             * Specifies the severity property: Severity level..
             *
             * @param severity Severity level.
             * @return the next definition stage.
             */
            Update withSeverity(SeverityLevel severity);
        }

        /** The stage of the SupportTicketDetails update allowing to specify status. */
        interface WithStatus {
            /**
             * Specifies the status property: Status to be updated on the ticket..
             *
             * @param status Status to be updated on the ticket.
             * @return the next definition stage.
             */
            Update withStatus(Status status);
        }

        /** The stage of the SupportTicketDetails update allowing to specify contactDetails. */
        interface WithContactDetails {
            /**
             * Specifies the contactDetails property: Contact details to be updated on the support ticket..
             *
             * @param contactDetails Contact details to be updated on the support ticket.
             * @return the next definition stage.
             */
            Update withContactDetails(UpdateContactProfile contactDetails);
        }

        /** The stage of the SupportTicketDetails update allowing to specify advancedDiagnosticConsent. */
        interface WithAdvancedDiagnosticConsent {
            /**
             * Specifies the advancedDiagnosticConsent property: Advanced diagnostic consent to be updated on the
             * support ticket..
             *
             * @param advancedDiagnosticConsent Advanced diagnostic consent to be updated on the support ticket.
             * @return the next definition stage.
             */
            Update withAdvancedDiagnosticConsent(Consent advancedDiagnosticConsent);
        }

        /** The stage of the SupportTicketDetails update allowing to specify secondaryConsent. */
        interface WithSecondaryConsent {
            /**
             * Specifies the secondaryConsent property: This property indicates secondary consents for the support
             * ticket.
             *
             * @param secondaryConsent This property indicates secondary consents for the support ticket.
             * @return the next definition stage.
             */
            Update withSecondaryConsent(List<SecondaryConsent> secondaryConsent);
        }
    }

    /**
     * Refreshes the resource to sync with Azure.
     *
     * @return the refreshed resource.
     */
    SupportTicketDetails refresh();

    /**
     * Refreshes the resource to sync with Azure.
     *
     * @param context The context to associate with this operation.
     * @return the refreshed resource.
     */
    SupportTicketDetails refresh(Context context);
}
