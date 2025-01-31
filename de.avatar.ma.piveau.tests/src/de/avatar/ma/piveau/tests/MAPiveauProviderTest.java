package de.avatar.ma.piveau.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.piveau.api.PiveauRegistry;
import org.gecko.piveau.api.connector.DatasetConnector;
import org.gecko.piveau.api.connector.DistributionConnector;
import org.gecko.piveau.dcat.Dataset;
import org.gecko.piveau.dcat.Distribution;
import org.gecko.piveau.terms.StandardType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.PromiseFactory;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@ExtendWith(MockitoExtension.class)
@RequireEMF
public class MAPiveauProviderTest {
	
	public interface Demo {
		
	}
	
	@Mock
	private EPackage testPackage;
	@Mock
	private EPackage toastPackage;
	@Mock
	private Demo testDemo;
	@Mock
	private DatasetConnector datasetAdapter;
	@Mock
	private DistributionConnector distributionAdapter;
	
	private BundleContext ctx;
	private ServiceRegistration<EPackage> testRegistration;
	private ServiceRegistration<EPackage> toastRegistration;
	private ServiceRegistration<Demo> testDemoRegistration;
	private ServiceRegistration<DatasetConnector> datasetConRegistration;
	private ServiceRegistration<DistributionConnector> distributionConRegistration;
	
	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.ctx = ctx;
		Dictionary<String, String> props = Dictionaries.dictionaryOf("piveau.connector", "TEST");
		datasetConRegistration = ctx.registerService(DatasetConnector.class, datasetAdapter, props);
		distributionConRegistration = ctx.registerService(DistributionConnector.class, distributionAdapter, props);
	}
	
	@AfterEach
	public void after() {
		if (datasetConRegistration != null) {
			datasetConRegistration.unregister();
		}
		if (distributionConRegistration != null) {
			distributionConRegistration.unregister();
		}
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetRest(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		when(toastPackage.getName()).thenReturn("toast");
		when(toastPackage.getNsURI()).thenReturn("https://toast");
				
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MAs Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.avatar.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(10000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("emf.model.name", "toast", "Piveau", "ma-ds", "osgi.jaxrs.resource", "true", "osgi.jaxrs.name", "mytest"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.avatar.de/demo/ma-ds", dataset.getAbout());
		assertEquals(4, registry.getActiveDistributions().size());
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA REST for model 'toast'"));
		assertEquals(1, dist.getDescription().size());
		assertEquals("REST Endpoint 'mytest' for model 'toast'", dist.getDescription().get(0).getValue());
		assertEquals(1, dist.getConformsTo().size());
		StandardType standard = dist.getConformsTo().get(0).getStandard();
		assertEquals(1, standard.getTitle().size());
		assertEquals("toast", standard.getTitle().get(0).getValue());
		assertEquals("https://toast", standard.getAbout());
		
		assertEquals(4, dataset.getDistribution().size());
		assertEquals(4, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetRestNoModel(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MA Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.test.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(1000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("emf.model.name", "blub", "Piveau", "ma-ds", "osgi.jaxrs.resource", "true"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.test.de/demo/ma-ds", dataset.getAbout());
		assertEquals(4, registry.getActiveDistributions().size());
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA REST for model 'blub'"));
		assertTrue(dist.getDescription().isEmpty());
		assertTrue(dist.getConformsTo().isEmpty());
		
		assertEquals(4, dataset.getDistribution().size());
		assertEquals(4, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetRestNoModelProperty(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MA Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.test.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(1000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("Piveau", "ma-ds", "osgi.jaxrs.resource", "true"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.test.de/demo/ma-ds", dataset.getAbout());
		assertEquals(4, registry.getActiveDistributions().size());
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA REST for model 'NONE'"));
		assertTrue(dist.getDescription().isEmpty());
		assertTrue(dist.getConformsTo().isEmpty());
		
		assertEquals(4, dataset.getDistribution().size());
		assertEquals(4, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetGraphQL(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		when(testPackage.getName()).thenReturn("test");
		when(testPackage.getNsURI()).thenReturn("https://test");
		
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MA Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.test.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(1000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "test-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds", "ma.graphql", "true"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.test.de/demo/ma-ds", dataset.getAbout());
		
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA GraphQL for model 'test'"));
		assertEquals(1, dist.getDescription().size());
		assertEquals("GraphQL Endpoint for model 'test'", dist.getDescription().get(0).getValue());
		assertEquals(1, dist.getConformsTo().size());
		StandardType standard = dist.getConformsTo().get(0).getStandard();
		assertEquals(1, standard.getTitle().size());
		assertEquals("test", standard.getTitle().get(0).getValue());
		assertEquals("https://test", standard.getAbout());
		
		assertEquals(1, dataset.getDistribution().size());
		assertEquals(1, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetGraphQLNoModel(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MA Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.test.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(1000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("emf.model.name", "blub", "Piveau", "ma-ds", "ma.graphql", "true"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.test.de/demo/ma-ds", dataset.getAbout());
		assertEquals(1, registry.getActiveDistributions().size());
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA GraphQL for model 'blub'"));
		assertEquals(1, dist.getDescription().size());
		assertEquals("GraphQL Endpoint for model 'blub'", dist.getDescription().get(0).getValue());
		assertTrue(dist.getConformsTo().isEmpty());
		
		assertEquals(1, dataset.getDistribution().size());
		assertEquals(1, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}
	
	@Test
	@WithFactoryConfiguration(name="test", location = "?", factoryPid = "PiveauAdapter", properties = {
			@Property(key = "distributionConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "datasetConnector.target", value = "(piveau.connector=TEST)"),
			@Property(key = "distributionProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "datasetProvider.target", value = "(piveau.provider=MA)"),
			@Property(key = "tracker.dataset", value = "true")
	})
	public void testDatasetGraphQLNoModelProperty(@InjectService(cardinality = 0) ServiceAware<PiveauRegistry> adapterAware, @InjectService ConfigurationAdmin ca) throws InterruptedException, IOException {
		assertTrue(adapterAware.isEmpty());
		
		PromiseFactory pf = new PromiseFactory(Executors.newSingleThreadExecutor());
		// Dataset that was provided to the connector mock. It should have the right setup with correct distribution references
		AtomicReference<Dataset> updatedDatasetRef = new AtomicReference<>();
		when(datasetAdapter.createDatasetAsync(any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(0);
			return pf.resolved(ds);
		});
		when(distributionAdapter.updateDistributions(any(), any(), anyString(), anyString())).thenAnswer(im->{
			Dataset ds = im.getArgument(1);
			updatedDatasetRef.set(ds);
			return ds;
		});
		
		Configuration providerConfig = ca.getFactoryConfiguration("MAPiveauProvider", "ma", "?");
		Dictionary<String, String> props = new Hashtable<>();
		props.put("dataset.catalogueId", "demo"); 
		props.put("dataset.id", "ma-ds");  
		props.put("dataset.title.en", "Example MA Dataset");  
		props.put("dataset.title.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.description.en", "This is an example MA Dataset"); 
		props.put("dataset.description.de", "Das ist ein Beispiel-Datenset für MA"); 
		props.put("dataset.issued", "NOW"); 
		props.put("dataset.distributionHost", "ma.test.de");
		providerConfig.update(props);
		
		PiveauRegistry registry = adapterAware.waitForService(1000l);
		assertNotNull(registry);
		
		testRegistration = ctx.registerService(EPackage.class, testPackage, Dictionaries.dictionaryOf("emf.model.name", "test", "Piveau", "ma-ds"));
		toastRegistration = ctx.registerService(EPackage.class, toastPackage, Dictionaries.dictionaryOf("emf.model.name", "toast"));
		testDemoRegistration = ctx.registerService(Demo.class, testDemo, Dictionaries.dictionaryOf("Piveau", "ma-ds", "ma.graphql", "true"));
		
		Dataset dataset = registry.getActiveDataset();
		assertNotNull(dataset);
		assertEquals(2, dataset.getTitle().size());
		assertEquals(2, dataset.getDescription().size());
		assertEquals("https://ma.test.de/demo/ma-ds", dataset.getAbout());
		assertEquals(1, registry.getActiveDistributions().size());
		Distribution dist = registry.getActiveDistributions().get(0);
		assertTrue(dist.getTitle().getValue().startsWith("MA GraphQL for model 'NONE'"));
		assertEquals(1, dist.getDescription().size());
		assertEquals("GraphQL Endpoint for model 'NONE'", dist.getDescription().get(0).getValue());
		assertTrue(dist.getConformsTo().isEmpty());
		
		assertEquals(1, dataset.getDistribution().size());
		assertEquals(1, updatedDatasetRef.get().getDistribution().size());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		testDemoRegistration.unregister();
		testRegistration.unregister();
		toastRegistration.unregister();
		
		dataset = registry.getActiveDataset();
		assertTrue(registry.getActiveDistributions().isEmpty());
		assertTrue(dataset.getDistribution().isEmpty());
		assertTrue(updatedDatasetRef.get().getDistribution().isEmpty());
		assertEquals(updatedDatasetRef.get().getAbout(), dataset.getAbout());
		
		verify(datasetAdapter, times(1)).createDatasetAsync(any(), anyString(), anyString());
		verify(distributionAdapter, times(2)).updateDistributions(anyList(), any(), anyString(), anyString());
	}

}
