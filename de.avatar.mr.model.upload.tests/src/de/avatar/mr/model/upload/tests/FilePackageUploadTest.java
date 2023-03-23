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
package de.avatar.mr.model.upload.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import de.avatar.mr.model.upload.api.FilePackageUpload;

/**
 * 
 * @author ilenia
 * @since Mar 23, 2023
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class FilePackageUploadTest {
	
	private BundleContext ctx;
	
	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.ctx = ctx;
	}
	
	@Test
	@Order(-1)
	@WithFactoryConfiguration(
			factoryPid = "FilePackageLoader",
			location = "?", 
			name = "test",
			properties = {
					@Property(key = "modelFolder", value = "data/")
			})
	public void testModelUpload(@InjectService(timeout = 1000l) ServiceAware<FilePackageUpload> uploadAware) throws Exception{
		
		Collection<ServiceReference<EPackage>> refs = 
				ctx.getServiceReferences(EPackage.class, "(emf.model.name=test3)");
		assertThat(refs).isNotNull();
		assertThat(refs).isEmpty();
		
		assertThat(uploadAware).isNotNull();
		FilePackageUpload uploadService = uploadAware.getService();
		assertThat(uploadService).isNotNull();
		
		try(InputStream is = new FileInputStream(new File("data2/test3.ecore"))) {
			uploadService.savePackageFile(is, "test3.ecore");
		}
		
		Thread.sleep(1000l);
		
		refs = ctx.getServiceReferences(EPackage.class, "(emf.model.name=test3)");
		assertThat(refs).isNotNull();
		assertThat(refs).hasSize(1);
	}
	
	@AfterEach
	public void afterEach() {
		File file = new File("data/test3.ecore");
		if(file.exists()) file.delete();
	}

}
