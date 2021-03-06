/*
 * */
package com.synectiks.process.common.plugins.sidecar.collectors;

import com.mongodb.client.MongoCollection;
import com.synectiks.process.common.plugins.sidecar.rest.models.NodeDetails;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import com.synectiks.process.common.plugins.sidecar.services.CollectorService;
import com.synectiks.process.common.plugins.sidecar.services.ConfigurationService;
import com.synectiks.process.common.plugins.sidecar.services.SidecarService;
import com.synectiks.process.common.testing.inject.TestPasswordSecretModule;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.shared.bindings.ObjectMapperModule;
import com.synectiks.process.server.shared.bindings.ValidatorModule;

import org.bson.Document;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.validation.Validator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
@UseModules({ObjectMapperModule.class, ValidatorModule.class, TestPasswordSecretModule.class})
public class SidecarServiceImplTest {
    private static final String collectionName = "sidecars";
    @Mock
    private CollectorService collectorService;

    @Mock private ConfigurationService configurationService;

    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private SidecarService sidecarService;

    @Before
    public void setUp(MongoJackObjectMapperProvider mapperProvider,
                      Validator validator) throws Exception {
        this.sidecarService = new SidecarService(collectorService, configurationService,  mongodb.mongoConnection(), mapperProvider, validator);
    }

    @Test
    public void testCountEmptyCollection() throws Exception {
        final long result = this.sidecarService.count();

        assertEquals(0, result);
    }

    @Test
    @MongoDBFixtures("collectorsMultipleDocuments.json")
    public void testCountNonEmptyCollection() throws Exception {
        final long result = this.sidecarService.count();

        assertEquals(3, result);
    }

    @Test
    public void testSaveFirstRecord() throws Exception {
        String nodeId = "nodeId";
        String nodeName = "nodeName";
        String version = "0.0.1";
        String os = "DummyOS 1.0";
        final Sidecar sidecar = Sidecar.create(
                nodeId,
                nodeName,
                NodeDetails.create(
                        os,
                        null,
                        null,
                        null,
                        null),
                version
                );

        final Sidecar result = this.sidecarService.save(sidecar);
        MongoCollection<Document> collection = mongodb.mongoConnection().getMongoDatabase().getCollection(collectionName);
        Document document = collection.find().first();
        Document nodeDetails = document.get("node_details", Document.class);

        assertNotNull(result);
        assertEquals(nodeId, document.get("node_id"));
        assertEquals(nodeName, document.get("node_name"));
        assertEquals(version, document.get("sidecar_version"));
        assertEquals(os, nodeDetails.get("operating_system"));
    }

    @Test
    @MongoDBFixtures("collectorsMultipleDocuments.json")
    public void testAll() throws Exception {
        final List<Sidecar> sidecars = this.sidecarService.all();

        assertNotNull(sidecars);
        assertEquals(3, sidecars.size());
    }

    @Test
    public void testAllEmptyCollection() throws Exception {
        final List<Sidecar> sidecars = this.sidecarService.all();

        assertNotNull(sidecars);
        assertEquals(0, sidecars.size());
    }

    @Test
    @MongoDBFixtures("collectorsMultipleDocuments.json")
    public void testFindById() throws Exception {
        final String collector1id = "uniqueid1";

        final Sidecar sidecar = this.sidecarService.findByNodeId(collector1id);

        assertNotNull(sidecar);
        assertEquals(collector1id, sidecar.nodeId());
    }

    @Test
    @MongoDBFixtures("collectorsMultipleDocuments.json")
    public void testFindByIdNonexisting() throws Exception {
        final String collector1id = "nonexisting";

        final Sidecar sidecar = this.sidecarService.findByNodeId(collector1id);

        assertNull(sidecar);
    }

    @Test
    @MongoDBFixtures("collectorsMultipleDocuments.json")
    public void testDestroy() throws Exception {
        final Sidecar sidecar = mock(Sidecar.class);
        when(sidecar.id()).thenReturn("581b3bff8e4dc4270055dfcb");

        final int result = this.sidecarService.delete(sidecar.id());
        assertEquals(1, result);
        assertEquals(2, mongodb.mongoConnection().getMongoDatabase().getCollection(collectionName).count());
    }
}
