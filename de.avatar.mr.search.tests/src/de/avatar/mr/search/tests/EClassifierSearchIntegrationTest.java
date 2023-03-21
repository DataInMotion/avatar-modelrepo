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

import org.eclipse.emf.ecore.EClassifier;
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
import de.avatar.mr.search.api.EClassifierSearchService;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class EClassifierSearchIntegrationTest {
	
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
			@InjectService(timeout= 1000l) ServiceAware<EClassifierSearchService> searchAware, 
			@InjectService(timeout= 1000l) ServiceAware<PersonPackage> modelAware) {
		
		assertThat(indexAware).isNotNull();
		assertThat(searchAware).isNotNull();
		assertThat(modelAware).isNotNull();
		
		EPackageIndexService indexService = indexAware.getService();
		assertThat(indexService).isNotNull();
		
		EClassifierSearchService searchService = searchAware.getService();
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
	public void testSearchEClassifierByName(@InjectService(timeout = 1000l) ServiceAware<EPackageIndexService> indexAware, 
			@InjectService(timeout= 1000l) ServiceAware<EClassifierSearchService> searchAware, 
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
		EClassifierSearchService searchService = searchAware.getService();
		assertThat(searchService).isNotNull();
		
		List<EClassifier> result = searchService.searchEClassifiersByName("Contact");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
		
		result = searchService.searchEClassifiersByName("per");
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		EClassifier match = result.get(0);
		assertThat(match.getName()).isEqualTo("Person");
		
		result = searchService.searchEClassifiersByName("another");
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}

}
