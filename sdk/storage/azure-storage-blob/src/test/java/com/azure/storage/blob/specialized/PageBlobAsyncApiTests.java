// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.blob.specialized;

import com.azure.core.exception.UnexpectedLengthException;
import com.azure.core.http.HttpRange;
import com.azure.core.http.rest.Response;
import com.azure.core.test.utils.MockTokenCredential;
import com.azure.core.test.utils.TestUtils;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.FluxUtil;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobTestBase;
import com.azure.storage.blob.models.BlobAudience;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobRange;
import com.azure.storage.blob.models.BlobRequestConditions;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.ClearRange;
import com.azure.storage.blob.models.CopyStatusType;
import com.azure.storage.blob.models.PageBlobCopyIncrementalRequestConditions;
import com.azure.storage.blob.models.PageBlobRequestConditions;
import com.azure.storage.blob.models.PageRange;
import com.azure.storage.blob.models.PublicAccessType;
import com.azure.storage.blob.models.SequenceNumberActionType;
import com.azure.storage.blob.options.BlobGetTagsOptions;
import com.azure.storage.blob.options.ListPageRangesDiffOptions;
import com.azure.storage.blob.options.ListPageRangesOptions;
import com.azure.storage.blob.options.PageBlobCopyIncrementalOptions;
import com.azure.storage.blob.options.PageBlobCreateOptions;
import com.azure.storage.common.implementation.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageBlobAsyncApiTests extends BlobTestBase {

    private PageBlobAsyncClient bc;
    private String blobName;

    @BeforeEach
    public void setup() {
        blobName = generateBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();
        bc.create(PageBlobClient.PAGE_BYTES).block();
    }

    @Test
    public void createAllNull() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();

        StepVerifier.create(bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, null, null, null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 201);
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertNull(r.getValue().getContentMd5());
                assertTrue(r.getValue().isServerEncrypted());
            })
            .verifyComplete();
    }

    @Test
    public void createMin() {
        assertAsyncResponseStatusCode(bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, null, null, null),
            201);
    }

    @Test
    public void createSequenceNumber() {
        bc.createWithResponse(PageBlobClient.PAGE_BYTES, 2L, null, null, null).block();
        StepVerifier.create(bc.getProperties())
            .assertNext(r -> assertEquals(r.getBlobSequenceNumber(), 2))
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("createHeadersSupplier")
    public void createHeaders(String cacheControl, String contentDisposition, String contentEncoding,
                              String contentLanguage, byte[] contentMD5, String contentType) {
        BlobHttpHeaders headers = new BlobHttpHeaders().setCacheControl(cacheControl)
            .setContentDisposition(contentDisposition)
            .setContentEncoding(contentEncoding)
            .setContentLanguage(contentLanguage)
            .setContentMd5(contentMD5)
            .setContentType(contentType);

        bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, headers, null, null).block();

        contentType = (contentType == null) ? "application/octet-stream" : contentType;
        String finalContentType = contentType;

        StepVerifier.create(bc.getPropertiesWithResponse(null))
            .assertNext(r -> {
                assertTrue(validateBlobProperties(r, cacheControl, contentDisposition, contentEncoding, contentLanguage,
                    contentMD5, finalContentType));
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createHeadersSupplier() throws NoSuchAlgorithmException {
        return Stream.of(Arguments.of(null, null, null, null, null, null),
            Arguments.of("control", "disposition", "encoding", "language",
                Base64.getEncoder().encode(MessageDigest.getInstance("MD5").digest(DATA.getDefaultBytes())),
                "type"));
    }

    @ParameterizedTest
    @MethodSource("createMetadataSupplier")
    public void createMetadata(String key1, String value1, String key2, String value2) {
        Map<String, String> metadata = new HashMap<>();
        if (key1 != null) {
            metadata.put(key1, value1);
        }
        if (key2 != null) {
            metadata.put(key2, value2);
        }
        bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, null, metadata, null).block();
        StepVerifier.create(bc.getPropertiesWithResponse(null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 200);
                assertEquals(r.getValue().getMetadata(), metadata);
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createMetadataSupplier() {
        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of("foo", "bar", "fizz", "buzz")
        );
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("createTagsSupplier")
    public void createTags(String key1, String value1, String key2, String value2) {
        Map<String, String> tags = new HashMap<>();
        if (key1 != null) {
            tags.put(key1, value1);
        }
        if (key2 != null) {
            tags.put(key2, value2);
        }

        bc.createWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES).setTags(tags)).block();

        StepVerifier.create(bc.getTagsWithResponse(new BlobGetTagsOptions()))
            .assertNext(r -> {
                assertResponseStatusCode(r, 200);
                assertEquals(r.getValue(), tags);
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createTagsSupplier() {
        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of("foo", "bar", "fizz", "buzz"),
            Arguments.of(" +-./:=_  +-./:=_", " +-./:=_", null, null)
        );
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void createAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                         String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, null, null, bac),
            201);
    }

    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void createACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                             String leaseID, String tags) {
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.createWithResponse(PageBlobClient.PAGE_BYTES, null, null,
            null, bac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void createError() {
        StepVerifier.create(bc.createWithResponse(PageBlobClient.PAGE_BYTES,
            null, null, null, new BlobRequestConditions().setLeaseId("id")))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void createIfNotExistsAllNull() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();

        StepVerifier.create(bc.createIfNotExistsWithResponse(
            new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES)))
            .assertNext(r -> {
                assertResponseStatusCode(r, 201);
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertNull(r.getValue().getContentMd5());
                assertTrue(r.getValue().isServerEncrypted());
            })
            .verifyComplete();
    }

    @Test
    public void createIfNotExistsBlobThatAlreadyExists() {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();
        PageBlobCreateOptions options = new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES);

        assertAsyncResponseStatusCode(bc.createIfNotExistsWithResponse(options), 201);
        assertAsyncResponseStatusCode(bc.createIfNotExistsWithResponse(options), 409);
    }

    @Test
    public void createIfNotExistsMin() {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();

        assertAsyncResponseStatusCode(bc.createIfNotExistsWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES)), 201);
    }

    @Test
    public void createIfNotExistsSequenceNumber() {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();

        bc.createIfNotExistsWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES).setSequenceNumber(2L)).block();

        StepVerifier.create(bc.getProperties())
            .assertNext(r -> assertEquals(r.getBlobSequenceNumber(), 2))
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("createIfNotExistsHeadersSupplier")
    public void createIfNotExistsHeaders(String cacheControl, String contentDisposition, String contentEncoding,
                                         String contentLanguage, byte[] contentMD5, String contentType) {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();

        BlobHttpHeaders headers = new BlobHttpHeaders().setCacheControl(cacheControl)
            .setContentDisposition(contentDisposition)
            .setContentEncoding(contentEncoding)
            .setContentLanguage(contentLanguage)
            .setContentMd5(contentMD5)
            .setContentType(contentType);

        bc.createIfNotExistsWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES).setHeaders(headers)).block();

        contentType = (contentType == null) ? "application/octet-stream" : contentType;
        String finalContentType = contentType;

        StepVerifier.create(bc.getPropertiesWithResponse(null))
            .assertNext(r -> {
                assertTrue(validateBlobProperties(r, cacheControl, contentDisposition, contentEncoding, contentLanguage,
                    contentMD5, finalContentType));
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createIfNotExistsHeadersSupplier() throws NoSuchAlgorithmException {
        return Stream.of(
            Arguments.of(null, null, null, null, null, null),
            Arguments.of("control", "disposition", "encoding", "language",
                MessageDigest.getInstance("MD5").digest(DATA.getDefaultBytes()), "type"));
    }

    @ParameterizedTest
    @MethodSource("createIfNotExistsMetadataSupplier")
    public void createIfNotExistsMetadata(String key1, String value1, String key2, String value2) {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();

        Map<String, String> metadata = new HashMap<>();
        if (key1 != null) {
            metadata.put(key1, value1);
        }
        if (key2 != null) {
            metadata.put(key2, value2);
        }

        bc.createIfNotExistsWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES).setMetadata(metadata)).block();

        StepVerifier.create(bc.getPropertiesWithResponse(null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 200);
                assertEquals(r.getValue().getMetadata(), metadata);
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createIfNotExistsMetadataSupplier() {
        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of("foo", "bar", "fizz", "buzz"));
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("createIfNotExistsTagsSupplier")
    public void createIfNotExistsTags(String key1, String value1, String key2, String value2) {
        String blobName = ccAsync.getBlobAsyncClient(generateBlobName()).getBlobName();
        bc = ccAsync.getBlobAsyncClient(blobName).getPageBlobAsyncClient();

        Map<String, String> tags = new HashMap<>();
        if (key1 != null) {
            tags.put(key1, value1);
        }
        if (key2 != null) {
            tags.put(key2, value2);
        }

        bc.createIfNotExistsWithResponse(new PageBlobCreateOptions(PageBlobClient.PAGE_BYTES).setTags(tags)).block();

        StepVerifier.create(bc.getTagsWithResponse(new BlobGetTagsOptions()))
            .assertNext(r -> {
                assertResponseStatusCode(r, 200);
                assertEquals(r.getValue(), tags);
            })
            .verifyComplete();
    }

    private static Stream<Arguments> createIfNotExistsTagsSupplier() {
        return Stream.of(
            Arguments.of(null, null, null, null),
            Arguments.of("foo", "bar", "fizz", "buzz"),
            Arguments.of(" +-./:=_  +-./:=_", " +-./:=_", null, null));
    }

    @Test
    public void uploadPage() {
        StepVerifier.create(bc.uploadPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 201);
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertNotNull(r.getHeaders().getValue("x-ms-content-crc64"));
                assertEquals(r.getValue().getBlobSequenceNumber(), 0);
                assertTrue(r.getValue().isServerEncrypted());
            })
            .verifyComplete();
    }

    @Test
    public void uploadPageMin() {
        assertAsyncResponseStatusCode(bc.uploadPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, null), 201);
    }

    @ParameterizedTest
    @MethodSource("uploadPageIASupplier")
    public void uploadPageIA(Integer dataSize, Throwable exceptionType) {
        Flux<ByteBuffer> data = (dataSize == null) ? null : Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)));

        StepVerifier.create(bc.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES * 2 - 1), data))
            .verifyError(exceptionType.getClass());
    }

    private static Stream<Arguments> uploadPageIASupplier() {
        return Stream.of(
            Arguments.of(null, new NullPointerException()),
            Arguments.of(PageBlobClient.PAGE_BYTES, new UnexpectedLengthException(null, 0L, 0L /* dummy values */)),
            Arguments.of(PageBlobClient.PAGE_BYTES * 3, new UnexpectedLengthException(null, 0L, 0L /* dummy values */))
        );
    }

    @Test
    public void uploadPageTransactionalMD5() throws NoSuchAlgorithmException {
        byte[] data = getRandomByteArray(PageBlobClient.PAGE_BYTES);
        byte[] md5 = MessageDigest.getInstance("MD5").digest(data);

        assertAsyncResponseStatusCode(bc.uploadPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(data)), md5, null), 201);
    }

    @Test
    public void uploadPageTransactionalMD5Fail() throws NoSuchAlgorithmException {

        StepVerifier.create(bc.uploadPagesWithResponse(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), MessageDigest.getInstance("MD5").digest("garbage".getBytes()), null))
            .verifyErrorSatisfies(r -> {
                BlobStorageException e = assertInstanceOf(BlobStorageException.class, r);
                assertEquals(BlobErrorCode.MD5MISMATCH, e.getErrorCode());
            });
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("uploadPageACSupplier")
    public void uploadPageAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                             String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.uploadPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, pac), 201);
    }

    private static Stream<Arguments> uploadPageACSupplier() {
        return Stream.of(Arguments.of(null, null, null, null, null, null, null, null, null),
            Arguments.of(OLD_DATE, null, null, null, null, null, null, null, null),
            Arguments.of(null, NEW_DATE, null, null, null, null, null, null, null),
            Arguments.of(null, null, RECEIVED_ETAG, null, null, null, null, null, null),
            Arguments.of(null, null, null, GARBAGE_ETAG, null, null, null, null, null),
            Arguments.of(null, null, null, null, RECEIVED_LEASE_ID, null, null, null, null),
            Arguments.of(null, null, null, null, null, 5L, null, null, null),
            Arguments.of(null, null, null, null, null, null, 3L, null, null),
            Arguments.of(null, null, null, null, null, null, null, 0L, null),
            Arguments.of(null, null, null, null, null, null, null, null, "\"foo\" = 'bar'"));
    }

    @ParameterizedTest
    @MethodSource("uploadPageACFailSupplier")
    public void uploadPageACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                 String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual, String tags) {
        noneMatch = setupBlobMatchCondition(bc, noneMatch);
        setupBlobLeaseCondition(bc, leaseID);
        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        StepVerifier.create(bc.uploadPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, pac))
            .verifyError(BlobStorageException.class);
    }

    private static Stream<Arguments> uploadPageACFailSupplier() {
        return Stream.of(
            Arguments.of(NEW_DATE, null, null, null, null, null, null, null, null),
            Arguments.of(null, OLD_DATE, null, null, null, null, null, null, null),
            Arguments.of(null, null, GARBAGE_ETAG, null, null, null, null, null, null),
            Arguments.of(null, null, null, RECEIVED_ETAG, null, null, null, null, null),
            Arguments.of(null, null, null, null, GARBAGE_LEASE_ID, null, null, null, null),
            Arguments.of(null, null, null, null, null, -1L, null, null, null),
            Arguments.of(null, null, null, null, null, null, -1L, null, null),
            Arguments.of(null, null, null, null, null, null, null, 100L, null),
            Arguments.of(null, null, null, null, null, null, null, null, "\"notfoo\" = 'notbar'"));
    }

    @Test
    public void uploadPageError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();

        StepVerifier.create(bc.uploadPagesWithResponse(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, new PageBlobRequestConditions().setLeaseId("id")))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void uploadPageRetryOnTransientFailure() {
        PageBlobAsyncClient clientWithFailure = getBlobAsyncClient(
            ENVIRONMENT.getPrimaryAccount().getCredential(), bc.getBlobUrl(),
            new TransientFailureInjectingHttpPipelinePolicy()).getPageBlobAsyncClient();

        byte[] data = getRandomByteArray(PageBlobClient.PAGE_BYTES);

        clientWithFailure.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(data))).block();

        StepVerifier.create(FluxUtil.collectBytesInByteBufferStream(bc.downloadStream()))
            .assertNext(r ->  TestUtils.assertArraysEqual(r, data))
            .verifyComplete();
    }

    @Test
    public void uploadPageFromURLMin() {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();
        PageBlobAsyncClient destURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        destURL.create(PageBlobClient.PAGE_BYTES).block();
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        destURL.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        StepVerifier.create(bc.uploadPagesFromUrlWithResponse(pageRange, destURL.getBlobUrl(), null, null,
            null, null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 201);
                assertTrue(validateBasicHeaders(r.getHeaders()));
            })
            .verifyComplete();
    }

    @Test
    public void uploadPageFromURLRange() {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();

        byte[] data = getRandomByteArray(PageBlobClient.PAGE_BYTES * 4);

        PageBlobAsyncClient sourceURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        sourceURL.create(PageBlobClient.PAGE_BYTES * 4).block();
        sourceURL.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES * 4 - 1),
            Flux.just(ByteBuffer.wrap(data))).block();

        PageBlobAsyncClient destURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        destURL.create(PageBlobClient.PAGE_BYTES * 2).block();

        destURL.uploadPagesFromUrl(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES * 2 - 1),
            sourceURL.getBlobUrl(), PageBlobClient.PAGE_BYTES * 2L).block();

        StepVerifier.create(FluxUtil.collectBytesInByteBufferStream(destURL.downloadStream()))
            .assertNext(r -> TestUtils.assertArraysEqual(data, PageBlobClient.PAGE_BYTES * 2, r, 0,
                PageBlobClient.PAGE_BYTES * 2))
            .verifyComplete();
    }

    @Test
    public void uploadPageFromURLIA() {
        StepVerifier.create(bc.uploadPagesFromUrl(null, bc.getBlobUrl(), (long) PageBlobClient.PAGE_BYTES))
            .verifyError(IllegalArgumentException.class);
    }

    @Test
    public void uploadPageFromURLMD5() throws NoSuchAlgorithmException {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();

        PageBlobAsyncClient destURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        destURL.create(PageBlobClient.PAGE_BYTES).block();

        byte[] data = getRandomByteArray(PageBlobClient.PAGE_BYTES);
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        bc.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(data))).block();

        StepVerifier.create(destURL.uploadPagesFromUrlWithResponse(pageRange, bc.getBlobUrl(), null,
            MessageDigest.getInstance("MD5").digest(data), null, null))
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    public void uploadPageFromURLMD5Fail() throws NoSuchAlgorithmException {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();
        PageBlobAsyncClient destURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        destURL.create(PageBlobClient.PAGE_BYTES).block();

        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        bc.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        StepVerifier.create(destURL.uploadPagesFromUrlWithResponse(pageRange, bc.getBlobUrl(), null,
            MessageDigest.getInstance("MD5").digest("garbage".getBytes()), null, null))
            .verifyError(BlobStorageException.class);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("uploadPageACSupplier")
    public void uploadPageFromURLDestinationAC(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                               String noneMatch, String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual,
                                               String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();
        PageBlobAsyncClient sourceURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        sourceURL.create(PageBlobClient.PAGE_BYTES).block();
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        sourceURL.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.uploadPagesFromUrlWithResponse(pageRange, sourceURL.getBlobUrl(), null, null, pac,
            null), 201);
    }

    @ParameterizedTest
    @MethodSource("uploadPageACFailSupplier")
    public void uploadPageFromURLDestinationACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                                   String noneMatch, String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual,
                                                   String tags) {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();

        PageBlobAsyncClient sourceURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        sourceURL.create(PageBlobClient.PAGE_BYTES).block();
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        sourceURL.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        noneMatch = setupBlobMatchCondition(bc, noneMatch);
        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        StepVerifier.create(bc.uploadPagesFromUrlWithResponse(
            pageRange, sourceURL.getBlobUrl(), null, null, pac, null))
            .verifyError(BlobStorageException.class);
    }

    @ParameterizedTest
    @MethodSource("uploadPageFromURLSourceACSupplier")
    public void uploadPageFromURLSourceAC(OffsetDateTime sourceIfModifiedSince, OffsetDateTime sourceIfUnmodifiedSince,
                                          String sourceIfMatch, String sourceIfNoneMatch) {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();
        PageBlobAsyncClient sourceURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        sourceURL.create(PageBlobClient.PAGE_BYTES).block();
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        sourceURL.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        sourceIfMatch = setupBlobMatchCondition(sourceURL, sourceIfMatch);
        BlobRequestConditions smac = new BlobRequestConditions()
            .setIfModifiedSince(sourceIfModifiedSince)
            .setIfUnmodifiedSince(sourceIfUnmodifiedSince)
            .setIfMatch(sourceIfMatch)
            .setIfNoneMatch(sourceIfNoneMatch);

        assertAsyncResponseStatusCode(bc.uploadPagesFromUrlWithResponse(pageRange, sourceURL.getBlobUrl(), null, null, null,
            smac), 201);
    }

    private static Stream<Arguments> uploadPageFromURLSourceACSupplier() {
        return Stream.of(Arguments.of(null, null, null, null),
            Arguments.of(OLD_DATE, null, null, null),
            Arguments.of(null, NEW_DATE, null, null),
            Arguments.of(null, null, RECEIVED_ETAG, null),
            Arguments.of(null, null, null, GARBAGE_ETAG));
    }

    @ParameterizedTest
    @MethodSource("uploadPageFromURLSourceACFailSupplier")
    public void uploadPageFromURLSourceACFail(OffsetDateTime sourceIfModifiedSince,
                                              OffsetDateTime sourceIfUnmodifiedSince, String sourceIfMatch, String sourceIfNoneMatch) {
        ccAsync.setAccessPolicy(PublicAccessType.CONTAINER, null).block();
        PageBlobAsyncClient sourceURL = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        sourceURL.create(PageBlobClient.PAGE_BYTES).block();
        PageRange pageRange = new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1);
        sourceURL.uploadPages(pageRange, Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        BlobRequestConditions smac = new BlobRequestConditions()
            .setIfModifiedSince(sourceIfModifiedSince)
            .setIfUnmodifiedSince(sourceIfUnmodifiedSince)
            .setIfMatch(sourceIfMatch)
            .setIfNoneMatch(setupBlobMatchCondition(sourceURL, sourceIfNoneMatch));

        StepVerifier.create(bc.uploadPagesFromUrlWithResponse(
            pageRange, sourceURL.getBlobUrl(), null, null, null, smac))
            .verifyError(BlobStorageException.class);
    }

    private static Stream<Arguments> uploadPageFromURLSourceACFailSupplier() {
        return Stream.of(
            Arguments.of(NEW_DATE, null, null, null),
            Arguments.of(null, OLD_DATE, null, null),
            Arguments.of(null, null, GARBAGE_ETAG, null),
            Arguments.of(null, null, null, RECEIVED_ETAG));
    }

    @Test
    public void clearPage() {
        bc.uploadPagesWithResponse(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES))), null, null).block();

        StepVerifier.create(bc.clearPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), null))
            .assertNext(r -> {
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertNull(r.getValue().getContentMd5());
                assertEquals(0, r.getValue().getBlobSequenceNumber());
            })
            .verifyComplete();

        StepVerifier.create(bc.getPageRanges(new BlobRange(0)))
            .assertNext(r -> assertEquals(0, r.getPageRange().size()))
            .verifyComplete();
    }

    @Test
    public void clearPageMin() {
        StepVerifier.create(bc.clearPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1)))
            .expectNextCount(1)
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("uploadPageACSupplier")
    public void clearPagesAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                             String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual, String tags) {
        bc.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        match = setupBlobMatchCondition(bc, match);
        leaseID = setupBlobLeaseCondition(bc, leaseID);
        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.clearPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), pac), 201);
    }

    @ParameterizedTest
    @MethodSource("uploadPageACFailSupplier")
    public void clearPagesACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                 String leaseID, Long sequenceNumberLT, Long sequenceNumberLTE, Long sequenceNumberEqual, String tags) {
        bc.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();
        noneMatch = setupBlobMatchCondition(bc, noneMatch);
        setupBlobLeaseCondition(bc, leaseID);
        PageBlobRequestConditions pac = new PageBlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfSequenceNumberLessThan(sequenceNumberLT)
            .setIfSequenceNumberLessThanOrEqualTo(sequenceNumberLTE)
            .setIfSequenceNumberEqualTo(sequenceNumberEqual)
            .setTagsConditions(tags);

        StepVerifier.create(bc.clearPagesWithResponse(
            new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1), pac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void clearPageError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();

        StepVerifier.create(bc.clearPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1)))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void getPageRanges() {
        bc.uploadPages(new PageRange().setStart(0).setEnd(PageBlobClient.PAGE_BYTES - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(PageBlobClient.PAGE_BYTES)))).block();

        StepVerifier.create(bc.getPageRangesWithResponse(new BlobRange(0,
            (long) PageBlobClient.PAGE_BYTES), null))
            .assertNext(r -> {
                assertResponseStatusCode(r, 200);
                assertEquals(1, r.getValue().getPageRange().size());
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertEquals("512", r.getHeaders().getValue(X_MS_BLOB_CONTENT_LENGTH));
            })
            .verifyComplete();
    }

    @Test
    public void getPageRangesMin() {
        StepVerifier.create(bc.getPageRanges(null))
            .expectNextCount(1)
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void getPageRangesAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        match = setupBlobMatchCondition(bc, match);
        leaseID = setupBlobLeaseCondition(bc, leaseID);
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.getPageRangesWithResponse(new BlobRange(0, (long) PageBlobClient.PAGE_BYTES),
            bac))
            .expectNextCount(1)
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void getPageRangesACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                    String leaseID, String tags) {
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.getPageRangesWithResponse(
            new BlobRange(0, (long) PageBlobClient.PAGE_BYTES), bac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void getPageRangesError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        StepVerifier.create(bc.getPageRanges(null))
            .verifyError(BlobStorageException.class);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPageRanges() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRanges(new BlobRange(0, (long) 4 * Constants.KB)))
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(0, (long) Constants.KB));
                assertFalse(r.isClear());
            })
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(2 * Constants.KB, (long) Constants.KB));
                assertFalse(r.isClear());
            })
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesRangesPageSize() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        // when: "max results on options"
        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(
            new BlobRange(0, 4L * Constants.KB)).setMaxResultsPerPage(1)).byPage())
            .assertNext(r -> assertEquals(r.getValue().size(), 1))
            .assertNext(r -> assertEquals(r.getValue().size(), 1))
            .verifyComplete();


        // when: "max results on iterableByPage"
        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(
            new BlobRange(0, 4L * Constants.KB))).byPage(1))
            .assertNext(r -> assertEquals(r.getValue().size(), 1))
            .assertNext(r -> assertEquals(r.getValue().size(), 1))
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesContinuationToken() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(
            new BlobRange(0, 4L * Constants.KB)).setMaxResultsPerPage(1)).byPage()
            .flatMap(r -> {
                return bc.listPageRanges(new ListPageRangesOptions(new BlobRange(0, 4L * Constants.KB))).byPage(r.getContinuationToken());
            }))
            .assertNext(r -> {
                assertEquals(r.getValue().size(), 1);
            })
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesRange() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(
            new BlobRange(2 * Constants.KB + 1, 2L * Constants.KB))))
            .expectNextCount(1)
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void listPagesRangesAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                  String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        match = setupBlobMatchCondition(bc, match);
        leaseID = setupBlobLeaseCondition(bc, leaseID);
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(new BlobRange(0,
            (long) PageBlobClient.PAGE_BYTES)).setRequestConditions(bac)))
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void listPageRangesACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                     String leaseID, String tags) {
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);


        StepVerifier.create(bc.listPageRanges(new ListPageRangesOptions(
            new BlobRange(0, (long) PageBlobClient.PAGE_BYTES)).setRequestConditions(bac)).count())
            .verifyError(BlobStorageException.class);
    }

    @ParameterizedTest
    @MethodSource("getPageRangesDiffSupplier")
    public void getPageRangesDiff(List<PageRange> rangesToUpdate, List<PageRange> rangesToClear,
                                  List<PageRange> expectedPageRanges, List<ClearRange> expectedClearRanges) {
        bc.create(4 * Constants.MB, true).block();

        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.MB - 1),
            Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB))));

        String snapId = bc.createSnapshot().block().getSnapshotId();

        rangesToUpdate.forEach(it ->
            bc.uploadPages(it, Flux.just(ByteBuffer.wrap(getRandomByteArray((int) (it.getEnd() - it.getStart()) + 1)))).block());

        rangesToClear.forEach(it -> bc.clearPages(it).block());

        StepVerifier.create(bc.getPageRangesDiffWithResponse(new BlobRange(0, 4L * Constants.MB),
            snapId, null))
            .assertNext(r -> {
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertEquals(expectedPageRanges.size(), r.getValue().getPageRange().size());
                assertEquals(expectedClearRanges.size(), r.getValue().getClearRange().size());

                for (int i = 0; i < expectedPageRanges.size(); i++) {
                    PageRange actualRange = r.getValue().getPageRange().get(i);
                    PageRange expectedRange = expectedPageRanges.get(i);
                    assertEquals(expectedRange.getStart(), actualRange.getStart());
                    assertEquals(expectedRange.getEnd(), actualRange.getEnd());
                }

                for (int i = 0; i < expectedClearRanges.size(); i++) {
                    ClearRange actualRange = r.getValue().getClearRange().get(i);
                    ClearRange expectedRange = expectedClearRanges.get(i);
                    assertEquals(expectedRange.getStart(), actualRange.getStart());
                    assertEquals(expectedRange.getEnd(), actualRange.getEnd());
                }

                assertEquals(Integer.parseInt(r.getHeaders().getValue(X_MS_BLOB_CONTENT_LENGTH)), 4 * Constants.MB);
            })
            .verifyComplete();
    }

    private static Stream<Arguments> getPageRangesDiffSupplier() {
        return Stream.of(
            Arguments.of(createPageRanges(), createPageRanges(), createPageRanges(), createClearRanges()),
            Arguments.of(createPageRanges(0, 511), createPageRanges(), createPageRanges(0, 511),
                createClearRanges()),
            Arguments.of(createPageRanges(), createPageRanges(0, 511), createPageRanges(),
                createClearRanges(0, 511)),
            Arguments.of(createPageRanges(0, 511), createPageRanges(512, 1023),
                createPageRanges(0, 511), createClearRanges(512, 1023)),
            Arguments.of(createPageRanges(0, 511, 1024, 1535), createPageRanges(512, 1023, 1536, 2047),
                createPageRanges(0, 511, 1024, 1535), createClearRanges(512, 1023, 1536, 2047))
        );
    }

    private static List<PageRange> createPageRanges(long... offsets) {
        List<PageRange> pageRanges = new ArrayList<>();

        if (CoreUtils.isNullOrEmpty(Collections.singleton(offsets))) {
            return pageRanges;
        }

        for (int i = 0; i < offsets.length / 2; i++) {
            pageRanges.add(new PageRange().setStart(offsets[i * 2]).setEnd(offsets[i * 2 + 1]));
        }
        return pageRanges;
    }

    static List<ClearRange> createClearRanges(long... offsets) {
        List<ClearRange> clearRanges = new ArrayList<>();

        if (CoreUtils.isNullOrEmpty(Collections.singleton(offsets))) {
            return clearRanges;
        }

        for (int i = 0; i < offsets.length / 2; i++) {
            clearRanges.add(new ClearRange().setStart(offsets[i * 2]).setEnd(offsets[i * 2 + 1]));
        }

        return clearRanges;
    }

    @Test
    public void getPageRangesDiffMin() {
        String snapId = bc.createSnapshot().block().getSnapshotId();

        StepVerifier.create(bc.getPageRangesDiff(null, snapId))
            .assertNext(r -> assertDoesNotThrow(() -> r.getPageRange()))
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void getPageRangesDiffAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                    String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        String snapId = bc.createSnapshot().block().getSnapshotId();
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.getPageRangesDiffWithResponse(new BlobRange(0,
            (long) PageBlobClient.PAGE_BYTES), snapId, bac))
            .expectNextCount(1)
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void getPageRangesDiffACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                        String noneMatch, String leaseID, String tags) {
        String snapId = bc.createSnapshot().block().getSnapshotId();

        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.getPageRangesDiffWithResponse(
            new BlobRange(0, (long) PageBlobClient.PAGE_BYTES), snapId, bac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void getPageRangesDiffError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        StepVerifier.create(bc.getPageRangesDiff(null, "snapshot"))
            .verifyError(BlobStorageException.class);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesRangesDiff() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        data = Flux.just(ByteBuffer.wrap(getRandomByteArray(Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.uploadPages(new PageRange().setStart(2 * Constants.KB).setEnd(3 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRangesDiff(new BlobRange(0, 4L * Constants.KB), snapshot))
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(0L, (long) Constants.KB));
                assertFalse(r.isClear());
            })
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(2 * Constants.KB, (long) Constants.KB));
                assertFalse(r.isClear());
            })
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(Constants.KB, (long) Constants.KB));
                assertTrue(r.isClear());
            })
            .assertNext(r -> {
                assertEquals(r.getRange(), new HttpRange(3 * Constants.KB, (long) Constants.KB));
                assertTrue(r.isClear());
            })
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesRangesDiffPageSize() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        data = Flux.just(ByteBuffer.wrap(getRandomByteArray(Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.uploadPages(new PageRange().setStart(2 * Constants.KB).setEnd(3 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        // when: "max results on options"
        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(new BlobRange(0, 4L * Constants.KB), snapshot).setMaxResultsPerPage(2)).byPage())
            .assertNext(r -> assertEquals(r.getValue().size(), 2))
            .assertNext(r -> assertEquals(r.getValue().size(), 2))
            .verifyComplete();

        // when: "max results on iterableByPage"
        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(new BlobRange(0, 4L * Constants.KB), snapshot)).byPage(2))
            .assertNext(r -> assertEquals(r.getValue().size(), 2))
            .assertNext(r -> assertEquals(r.getValue().size(), 2))
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesDiffContinuationToken() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        data = Flux.just(ByteBuffer.wrap(getRandomByteArray(Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.uploadPages(new PageRange().setStart(2 * Constants.KB).setEnd(3 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(
            new BlobRange(0, 4L * Constants.KB), snapshot).setMaxResultsPerPage(2)).byPage()
            .flatMap(r -> {
                return bc.listPageRangesDiff(new ListPageRangesDiffOptions(new BlobRange(0, 4L * Constants.KB), snapshot)).byPage(r.getContinuationToken());
            }))
            .assertNext(r -> assertEquals(r.getValue().size(), 2))
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @Test
    public void listPagesDiffRange() {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        data = Flux.just(ByteBuffer.wrap(getRandomByteArray(Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(Constants.KB).setEnd(2 * Constants.KB - 1)).block();
        bc.uploadPages(new PageRange().setStart(2 * Constants.KB).setEnd(3 * Constants.KB - 1), data).block();
        bc.clearPages(new PageRange().setStart(3 * Constants.KB).setEnd(4 * Constants.KB - 1)).block();

        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(
            new BlobRange(2 * Constants.KB + 1, 2L * Constants.KB), snapshot)))
            .expectNextCount(2)
            .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void listPageRangesDiffAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                     String leaseID, String tags) {
        bc.create(4 * Constants.KB, true).block();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(getRandomByteArray(4 * Constants.KB)));
        bc.uploadPages(new PageRange().setStart(0).setEnd(4 * Constants.KB - 1), data).block();
        String snapshot = bc.createSnapshot().block().getSnapshotId();

        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        match = setupBlobMatchCondition(bc, match);
        leaseID = setupBlobLeaseCondition(bc, leaseID);
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(leaseID)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(
                new BlobRange(0, (long) PageBlobClient.PAGE_BYTES), snapshot).setRequestConditions(bac)).count())
                .expectNextCount(1)
                .verifyComplete();
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20210608ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void listPageRangesDiffACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                         String noneMatch, String leaseID, String tags) {
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.listPageRangesDiff(new ListPageRangesDiffOptions(
            new BlobRange(0, (long) PageBlobClient.PAGE_BYTES), snapshot).setRequestConditions(bac)).count())
            .verifyError(BlobStorageException.class);
    }

    @ParameterizedTest
    @MethodSource("pageRangeIASupplier")
    public void pageRangeIA(int start, int end) {
        PageRange range = new PageRange().setStart(start).setEnd(end);
        StepVerifier.create(bc.clearPages(range))
            .verifyError(IllegalArgumentException.class);
    }

    private static Stream<Arguments> pageRangeIASupplier() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(-PageBlobClient.PAGE_BYTES, PageBlobClient.PAGE_BYTES - 1),
            Arguments.of(0, 0),
            Arguments.of(1, PageBlobClient.PAGE_BYTES - 1),
            Arguments.of(0, PageBlobClient.PAGE_BYTES),
            Arguments.of(PageBlobClient.PAGE_BYTES * 2, PageBlobClient.PAGE_BYTES - 1));
    }

    @Test
    public void resize() {
        StepVerifier.create(bc.resizeWithResponse(PageBlobClient.PAGE_BYTES * 2, null))
            .assertNext(r -> {
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertNotNull(r.getValue().getBlobSequenceNumber());
            })
            .verifyComplete();

        StepVerifier.create(bc.getProperties())
            .assertNext(r -> assertEquals(PageBlobClient.PAGE_BYTES * 2, r.getBlobSize()))
            .verifyComplete();
    }

    @Test
    public void resizeMin() {
        assertAsyncResponseStatusCode(bc.resizeWithResponse(PageBlobClient.PAGE_BYTES, null),  200);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void resizeAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                         String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.resizeWithResponse(PageBlobClient.PAGE_BYTES * 2, bac), 200);
    }

    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void resizeACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                             String leaseID, String tags) {
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.resizeWithResponse(PageBlobClient.PAGE_BYTES * 2, bac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void resizeError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        StepVerifier.create(bc.resize(0))
            .verifyError(BlobStorageException.class);
    }

    @ParameterizedTest
    @MethodSource("sequenceNumberSupplier")
    public void sequenceNumber(SequenceNumberActionType action, Long number, Long result) {
        StepVerifier.create(bc.updateSequenceNumberWithResponse(action, number, null))
            .assertNext(r -> {
                assertTrue(validateBasicHeaders(r.getHeaders()));
                assertEquals(r.getValue().getBlobSequenceNumber(), result);
            })
            .verifyComplete();

        StepVerifier.create(bc.getProperties())
            .assertNext(r -> assertEquals(result, r.getBlobSequenceNumber()))
            .verifyComplete();
    }

    private static Stream<Arguments> sequenceNumberSupplier() {
        return Stream.of(
            Arguments.of(SequenceNumberActionType.UPDATE, 5L, 5L),
            Arguments.of(SequenceNumberActionType.INCREMENT, null, 1L),
            Arguments.of(SequenceNumberActionType.MAX, 2L, 2L));
    }

    @Test
    public void sequenceNumberMin() {
        assertAsyncResponseStatusCode(bc.updateSequenceNumberWithResponse(SequenceNumberActionType.INCREMENT,
            null, null), 200);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsSupplier")
    public void sequenceNumberAC(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                 String leaseID, String tags) {
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bc.setTags(t).block();
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(setupBlobMatchCondition(bc, match))
            .setIfNoneMatch(noneMatch)
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bc.updateSequenceNumberWithResponse(SequenceNumberActionType.UPDATE, 1L,
            bac), 200);
    }

    @ParameterizedTest
    @MethodSource("com.azure.storage.blob.BlobTestBase#allConditionsFailSupplier")
    public void sequenceNumberACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match, String noneMatch,
                                     String leaseID, String tags) {
        BlobRequestConditions bac = new BlobRequestConditions()
            .setLeaseId(setupBlobLeaseCondition(bc, leaseID))
            .setIfMatch(match)
            .setIfNoneMatch(setupBlobMatchCondition(bc, noneMatch))
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setTagsConditions(tags);

        StepVerifier.create(bc.updateSequenceNumberWithResponse(SequenceNumberActionType.UPDATE, 1L, bac))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void sequenceNumberError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        StepVerifier.create(bc.updateSequenceNumber(SequenceNumberActionType.UPDATE, 0L))
            .verifyError(BlobStorageException.class);
    }

    @Test
    //todo isbr: is there a better way to do this
    public void startIncrementalCopy() {
        ccAsync.setAccessPolicy(PublicAccessType.BLOB, null).block();
        PageBlobAsyncClient bc2 = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        String snapId = bc.createSnapshot().block().getSnapshotId();

        Response<CopyStatusType> copyResponse = bc2.copyIncrementalWithResponse(bc.getBlobUrl(), snapId, null).block();

        CopyStatusType status = copyResponse.getValue();
        OffsetDateTime start = testResourceNamer.now();
        while (status != CopyStatusType.SUCCESS) {
            status = bc2.getProperties().block().getCopyStatus();
            OffsetDateTime currentTime = testResourceNamer.now();
            if (status == CopyStatusType.FAILED || currentTime.minusMinutes(1) == start) {
                throw new RuntimeException("Copy failed or took too long");
            }
            sleepIfRunningAgainstService(1000);
        }

        BlobProperties properties = bc2.getProperties().block();
        assertTrue(properties.isIncrementalCopy());
        assertNotNull(properties.getCopyDestinationSnapshot());
        validateBasicHeaders(copyResponse.getHeaders());
        assertNotNull(copyResponse.getHeaders().getValue(X_MS_COPY_ID));
        assertNotNull(copyResponse.getValue());
    }

    @Test
    public void startIncrementalCopyMin() {
        ccAsync.setAccessPolicy(PublicAccessType.BLOB, null).block();
        PageBlobAsyncClient bc2 = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        String snapshot = bc.createSnapshot().block().getSnapshotId();

        assertAsyncResponseStatusCode(bc2.copyIncrementalWithResponse(bc.getBlobUrl(), snapshot, null), 202);
    }

    @DisabledIf("com.azure.storage.blob.BlobTestBase#olderThan20191212ServiceVersion")
    @ParameterizedTest
    @MethodSource("startIncrementalCopyACSupplier")
    public void startIncrementalCopyAC(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                       String noneMatch, String tags) {
        ccAsync.setAccessPolicy(PublicAccessType.BLOB, null).block();
        PageBlobAsyncClient bu2 = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        Response<CopyStatusType> copyResponse = bu2.copyIncrementalWithResponse(bc.getBlobUrl(), snapshot, null).block();

        CopyStatusType status = copyResponse.getValue();
        OffsetDateTime start = testResourceNamer.now();
        while (status != CopyStatusType.SUCCESS) {
            status = bu2.getProperties().block().getCopyStatus();
            OffsetDateTime currentTime = testResourceNamer.now();
            if (status == CopyStatusType.FAILED || currentTime.minusMinutes(1) == start) {
                throw new RuntimeException("Copy failed or took too long");
            }
            sleepIfRunningAgainstService(1000);
        }
        Map<String, String> t = new HashMap<>();
        t.put("foo", "bar");
        bu2.setTags(t).block();

        snapshot = bc.createSnapshot().block().getSnapshotId();
        match = setupBlobMatchCondition(bu2, match);
        PageBlobCopyIncrementalRequestConditions mac = new PageBlobCopyIncrementalRequestConditions()
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setTagsConditions(tags);

        assertAsyncResponseStatusCode(bu2.copyIncrementalWithResponse(new PageBlobCopyIncrementalOptions(bc.getBlobUrl(),
            snapshot).setRequestConditions(mac)),  202);
    }

    private static Stream<Arguments> startIncrementalCopyACSupplier() {
        return Stream.of(
            Arguments.of(null, null, null, null, null),
            Arguments.of(OLD_DATE, null, null, null, null),
            Arguments.of(null, NEW_DATE, null, null, null),
            Arguments.of(null, null, RECEIVED_ETAG, null, null),
            Arguments.of(null, null, null, GARBAGE_ETAG, null),
            Arguments.of(null, null, null, null, "\"foo\" = 'bar'"));
    }

    @ParameterizedTest
    @MethodSource("startIncrementalCopyACFailSupplier")
    public void startIncrementalCopyACFail(OffsetDateTime modified, OffsetDateTime unmodified, String match,
                                           String noneMatch, String tags) {
        ccAsync.setAccessPolicy(PublicAccessType.BLOB, null).block();
        PageBlobAsyncClient bu2 = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        String snapshot = bc.createSnapshot().block().getSnapshotId();
        bu2.copyIncremental(bc.getBlobUrl(), snapshot).block();
        String finalSnapshot = bc.createSnapshot().block().getSnapshotId();
        noneMatch = setupBlobMatchCondition(bu2, noneMatch);
        PageBlobCopyIncrementalRequestConditions mac = new PageBlobCopyIncrementalRequestConditions()
            .setIfModifiedSince(modified)
            .setIfUnmodifiedSince(unmodified)
            .setIfMatch(match)
            .setIfNoneMatch(noneMatch)
            .setTagsConditions(tags);

        StepVerifier.create(bu2.copyIncrementalWithResponse(
            new PageBlobCopyIncrementalOptions(bc.getBlobUrl(), finalSnapshot).setRequestConditions(mac)))
            .verifyError(BlobStorageException.class);
    }

    private static Stream<Arguments> startIncrementalCopyACFailSupplier() {
        return Stream.of(
            Arguments.of(NEW_DATE, null, null, null, null),
            Arguments.of(null, OLD_DATE, null, null, null),
            Arguments.of(null, null, GARBAGE_ETAG, null, null),
            Arguments.of(null, null, null, RECEIVED_ETAG, null),
            Arguments.of(null, null, null, null, "\"notfoo\" = 'notbar'"));
    }

    @Test
    public void startIncrementalCopyError() {
        bc = ccAsync.getBlobAsyncClient(generateBlobName()).getPageBlobAsyncClient();
        StepVerifier.create(bc.copyIncremental("https://www.error.com", "snapshot"))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void getContainerName() {
        assertEquals(containerName, bc.getContainerName());
    }

    @Test
    public void getPageBlobName() {
        assertEquals(blobName, bc.getBlobName());
    }

    @ParameterizedTest
    @MethodSource("getBlobNameAndBuildClientSupplier")
    public void getBlobNameAndBuildClient(String originalBlobName, String finalBlobName) {
        BlobAsyncClient client = ccAsync.getBlobAsyncClient(originalBlobName);
        PageBlobAsyncClient blockClient = ccAsync.getBlobAsyncClient(client.getBlobName()).getPageBlobAsyncClient();
        assertEquals(blockClient.getBlobName(), finalBlobName);
    }

    private static Stream<Arguments> getBlobNameAndBuildClientSupplier() {
        return Stream.of(
            Arguments.of("blob", "blob"),
            Arguments.of("path/to]a blob", "path/to]a blob"),
            Arguments.of("path%2Fto%5Da%20blob", "path/to]a blob"),
            Arguments.of("斑點", "斑點"),
            Arguments.of("%E6%96%91%E9%BB%9E", "斑點"));
    }

    @Test
    public void createOverwriteFalse() {
        StepVerifier.create(bc.create(512))
            .verifyError(BlobStorageException.class);
    }

    @Test
    public void createOverwriteTrue() {
        StepVerifier.create(bc.create(512, true))
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    // This tests the policy is in the right place because if it were added per retry, it would be after the credentials
    // and auth would fail because we changed a signed header.
    public void perCallPolicy() {
        PageBlobAsyncClient specialBlob = getSpecializedBuilder(bc.getBlobUrl())
            .addPolicy(getPerCallVersionPolicy())
            .buildPageBlobAsyncClient();

        StepVerifier.create(specialBlob.getPropertiesWithResponse(null))
            .assertNext(r ->  assertEquals(r.getHeaders().getValue(X_MS_VERSION), "2017-11-09"))
            .verifyComplete();
    }

    @Test
    public void defaultAudience() {
        PageBlobAsyncClient aadBlob = getSpecializedBuilderWithTokenCredential(bc.getBlobUrl())
            .audience(null)
            .buildPageBlobAsyncClient();

        StepVerifier.create(aadBlob.exists())
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    public void storageAccountAudience() {
        PageBlobAsyncClient aadBlob = getSpecializedBuilderWithTokenCredential(bc.getBlobUrl())
            .audience(BlobAudience.createBlobServiceAccountAudience(ccAsync.getAccountName()))
            .buildPageBlobAsyncClient();

        StepVerifier.create(aadBlob.exists())
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    public void audienceError() {
        PageBlobAsyncClient aadBlob = instrument(new SpecializedBlobClientBuilder()
            .endpoint(bc.getBlobUrl())
            .credential(new MockTokenCredential())
            .audience(BlobAudience.createBlobServiceAccountAudience("badAudience")))
            .buildPageBlobAsyncClient();

        StepVerifier.create(aadBlob.exists())
            .verifyErrorSatisfies(r -> {
                BlobStorageException e = assertInstanceOf(BlobStorageException.class, r);
                assertTrue(e.getErrorCode() == BlobErrorCode.INVALID_AUTHENTICATION_INFO);
            });
    }

    @Test
    public void audienceFromString() {
        String url = String.format("https://%s.blob.core.windows.net/", ccAsync.getAccountName());
        BlobAudience audience = BlobAudience.fromString(url);

        PageBlobAsyncClient aadBlob = getSpecializedBuilderWithTokenCredential(bc.getBlobUrl())
            .audience(audience)
            .buildPageBlobAsyncClient();

        StepVerifier.create(aadBlob.exists())
            .expectNext(true)
            .verifyComplete();
    }



}
