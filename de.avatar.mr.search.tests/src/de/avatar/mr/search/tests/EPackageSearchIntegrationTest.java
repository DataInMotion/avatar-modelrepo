/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.search.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import de.avatar.mr.example.model.person.PersonPackage;
import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.search.api.EPackageSearchService;


/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class EPackageSearchIntegrationTest {
	

	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		
	}
	
	@Test
	@Order(-1)
	@WithFactoryConfiguration(
			factoryPid = "EMFLuceneIndex",
			location = "?", 
			name = "ePackage",
			properties = {
					@Property(key = "id", value = "ePackage"),
					@Property(key = "directory.type", value = "ByteBuffer")
			})
	public void testServices(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EPackageSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) {
		
		assertThat(indexAware).isNotNull();
		assertThat(searchAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		EPackageSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
	}
	
	@Test
	@WithFactoryConfiguration(
			factoryPid = "EMFLuceneIndex",
			location = "?", 
			name = "ePackage",
			properties = {
					@Property(key = "id", value = "ePackage"),
					@Property(key = "directory.type", value = "ByteBuffer")
			})
	public void testSearchEPackageByName(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EPackageSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EPackageSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EPackage> result = searchService.searchEPackagesByName("person");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		
		EPackage match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByName("per");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByName("SO");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByName("another");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

	@Test
	@WithFactoryConfiguration(
			factoryPid = "EMFLuceneIndex",
			location = "?", 
			name = "ePackage",
			properties = {
					@Property(key = "id", value = "ePackage"),
					@Property(key = "directory.type", value = "ByteBuffer")
			})
	public void testSearchEPackageByURI(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EPackageSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EPackageSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EPackage> result = searchService.searchEPackagesByNsURI("person");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		
		EPackage match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByNsURI("avatar.de");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByNsURI("1.0");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackagesByNsURI("another");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

	@Test
	@WithFactoryConfiguration(
			factoryPid = "EMFLuceneIndex",
			location = "?", 
			name = "ePackage",
			properties = {
					@Property(key = "id", value = "ePackage"),
					@Property(key = "directory.type", value = "ByteBuffer")
			})
	public void testSearchEPackageByNameAndURI(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EPackageSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EPackageSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EPackage> result = searchService.searchEPackages("person");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		
		EPackage match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackages("avatar.de");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackages("SO");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackages("1.0");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getNsURI()).isEqualTo(modelService.getNsURI());
		
		result = searchService.searchEPackages("another");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}
}
