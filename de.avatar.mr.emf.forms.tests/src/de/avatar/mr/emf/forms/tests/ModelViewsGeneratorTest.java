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
package de.avatar.mr.emf.forms.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import de.avatar.mdp.medicalrecord.MedicalRecordPackage;
import de.avatar.mr.emf.forms.api.ModelViewsGenerator;


/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class ModelViewsGeneratorTest {
	
	@AfterEach
	public void afterEach(@InjectBundleContext BundleContext ctx) throws IOException {
		Files.list(Path.of("data/out")).forEach(p -> {
			try {
				if(Files.exists(p)) Files.delete(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	@WithFactoryConfiguration(
			factoryPid = "ModelViewsGenerator",
			location = "?", 
			name = "test",
			properties = {
					@Property(key = "out.folder", value = "data/out/")
			})
	public void testServices(@InjectService ServiceAware<ModelViewsGenerator> serviceAware) {
		assertThat(serviceAware).isNotNull();
		ModelViewsGenerator service = serviceAware.getService();
		assertThat(service).isNotNull();
	}
	
	@Test
	@WithFactoryConfiguration(
			factoryPid = "ModelViewsGenerator",
			location = "?", 
			name = "test",
			properties = {
					@Property(key = "out.folder", value = "data/out/")
			})
	public void testModelViewsGenerator(@InjectService ServiceAware<MedicalRecordPackage> modelAware, 
			@InjectService ServiceAware<ModelViewsGenerator> serviceAware) {
		
		assertThat(modelAware).isNotNull();
		MedicalRecordPackage model = modelAware.getService();
		assertThat(model).isNotNull();
		
		assertThat(serviceAware).isNotNull();
		ModelViewsGenerator service = serviceAware.getService();
		assertThat(service).isNotNull();
		service.generateModelViews(model, "/de.avatar.mr.emf.forms.tests/data/medical-record-example.ecore");
//		service.generateModelViews(model, null);
		
		model.getEClassifiers().stream().filter(ec -> ec instanceof EClass).map(ec -> (EClass) ec)
			.forEach(ec -> {
				File file = new File("data/out/"+model.getName()+"_"+ec.getName()+".view");
				assertTrue(file.exists());
			});
	}

}
