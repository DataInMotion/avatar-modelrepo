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
package de.avatar.mr.model.registration.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * 
 * @author ilenia
 * @since Mar 23, 2023
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class DynamicPackageLoaderTest {

private BundleContext ctx;
	
	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.ctx = ctx;
		
	}
	
	@Test
	@Order(-1)
	@WithFactoryConfiguration(
			factoryPid = "DynamicPackageLoader",
			location = "?", 
			name = "test",
			properties = {
					@Property(key = "id", value = "test"),
					@Property(key = "url", value = "data/test.ecore")
			})
	public void testDynamicPackageLoaderModifiedConfig(@InjectService() ServiceAware<ConfigurationAdmin> configAdminAware) throws Exception{
		
		Thread.sleep(1000l);
		Collection<ServiceReference<EPackage>> refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test)");
		assertThat(refs).isNotNull();
		assertThat(refs).isNotEmpty();
		assertThat(refs).hasSize(1);
		
		assertThat(configAdminAware).isNotNull();
		ConfigurationAdmin configAdmin = configAdminAware.getService();
		assertThat(configAdmin).isNotNull();
		Configuration config = configAdmin.getFactoryConfiguration("DynamicPackageLoader", "test");
		assertThat(config.getProperties().isEmpty()).isFalse();
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("url", "data/test2.ecore");
		properties.put("id", "test2");
		config.update(properties);
		
		Thread.sleep(1000l);
		
		refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test)");
		assertThat(refs).isNotNull();
		assertThat(refs).isEmpty();
		
		refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test2)");
		assertThat(refs).isNotNull();
		assertThat(refs).isNotEmpty();
		assertThat(refs).hasSize(1);
	}
	
}
