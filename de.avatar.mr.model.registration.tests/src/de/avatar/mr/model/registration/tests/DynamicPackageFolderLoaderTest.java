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
package de.avatar.mr.model.registration.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;


/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class DynamicPackageFolderLoaderTest {
	
	private BundleContext ctx;
	
	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.ctx = ctx;
		
	}
	
	@Test
	@Order(-1)
	@WithFactoryConfiguration(
			factoryPid = "DynamicPackageFolderLoader",
			location = "?", 
			name = "dynamicFolderLoader",
			properties = {
					@Property(key = "modelFolders", value = "data/", type = Type.Array)
			})
	public void testModelRegistrationFromFolder() throws Exception{
		
		Thread.sleep(1000l);
		Collection<ServiceReference<EPackage>> refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test)");
		assertThat(refs).isNotNull();
		assertThat(refs).isNotEmpty();
		assertThat(refs).hasSize(1);
		
		refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test2)");
		assertThat(refs).isNotNull();
		assertThat(refs).isNotEmpty();
		assertThat(refs).hasSize(1);
	}
	

}
