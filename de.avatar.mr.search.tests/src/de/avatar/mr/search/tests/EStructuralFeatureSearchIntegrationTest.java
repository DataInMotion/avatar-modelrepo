/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.search.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
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
import de.avatar.mr.search.api.EStructuralFeatureSearchService;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class EStructuralFeatureSearchIntegrationTest {
	
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
			@InjectService(timeout= 1000l) ServiceAware<EStructuralFeatureSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) {
		
		assertThat(indexAware).isNotNull();
		assertThat(searchAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		EStructuralFeatureSearchService searchService = searchAware.getService();
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
	public void testSearchEStrucuturalFeatureByName(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EStructuralFeatureSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		indexService.indexEPackage(modelService, true);
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EStructuralFeatureSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EStructuralFeature> result = searchService.searchEStructuralFeaturesByName("name");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(4);
		
		result = searchService.searchEStructuralFeaturesByName("first");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
		
		result = searchService.searchEStructuralFeaturesByName("address");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		EStructuralFeature match = result.get(0);
		assertThat(match.getName()).isEqualTo("address");
		
		result = searchService.searchEStructuralFeaturesByName("route");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		match = result.get(0);
		assertThat(match.getName()).isEqualTo("street");
		
		result = searchService.searchEStructuralFeaturesByName("another");
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
	public void testSearchEStrucuturalFeatureByType(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EStructuralFeatureSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		indexService.indexEPackage(modelService, true);
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EStructuralFeatureSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EStructuralFeature> result = searchService.searchEStructuralFeaturesByType("Date");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
		
		result = searchService.searchEStructuralFeaturesByType("date");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
		
		result = searchService.searchEStructuralFeaturesByType("String");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(13);
		
		result = searchService.searchEStructuralFeaturesByType("int");
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
	public void testSearchEStrucuturalFeatureByTypeAndName(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EStructuralFeatureSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) throws InterruptedException {
		
		assertThat(indexAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		PersonPackage modelService = modelAware.getService();
		assertThat(modelService).isNotNull();
		
		indexService.indexEPackage(modelService, true);
		
		Thread.sleep(500);
		
		assertThat(searchAware).isNotNull();
		EStructuralFeatureSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EStructuralFeature> result = searchService.searchEStructuralFeaturesByNameAndType("add", "Address");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		
		result = searchService.searchEStructuralFeaturesByNameAndType("gdkgskerö", "date");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		
		result = searchService.searchEStructuralFeaturesByNameAndType("name", "String");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(4);
		
		result = searchService.searchEStructuralFeaturesByNameAndType("name", "int");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

}
