/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.emf.osgi.components.dynamic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


/**
 * 
 * @author ilenia
 * @since Mar 22, 2023
 */
@Component(immediate=true, name = "DynamicPackageFolderLoader", service = DynamicPackageFolderLoader.class, scope = ServiceScope.SINGLETON, 
configurationPid = "DynamicPackageFolderLoader", configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = org.gecko.emf.osgi.components.dynamic.DynamicPackageFolderLoader.Config.class, factory = true)
@RequireConfigurationAdmin
public class DynamicPackageFolderLoader {
	
	private ConfigurationAdmin configAdmin;
	private Map<String, Configuration> registrationsMap = new HashMap<>();
	
	private static final Logger LOGGER = Logger.getLogger(DynamicPackageFolderLoader.class.getName());

	@ObjectClassDefinition(
			description = "A list of URL can be configured, where the ecore files are expected to be stored. All the ecore files found will then be registered through a DynamicPackageLoader"
			)
	public @interface Config {		
		
		@AttributeDefinition(description = "A list of folders where to look for ecore to be registered.")
		String[] modelFolders() default {};
	}
	
	@Activate
	public DynamicPackageFolderLoader(@Reference ConfigurationAdmin configAdmin, Config config) throws ConfigurationException {
		this.configAdmin = configAdmin;		
		try {
			loadModels(config.modelFolders());
		} catch (Exception e) {
			throw new ConfigurationException("modelFolders", "Error when loading models form folders " + config.modelFolders(), e);
		}		
	}
	
	@Deactivate
	public void deactivate() {
		registrationsMap.values().forEach(r -> {
			try {
				r.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		registrationsMap.clear();
	}
	
	private void loadModels(String[] modelFolders) {
		if(modelFolders == null || modelFolders.length == 0) return;
		for(String modelFolder : modelFolders) {
			Path folder = Paths.get(modelFolder);
			if(!Files.exists(folder)) {
				LOGGER.severe(String.format("Folder %s does not exist. Skipping it!", modelFolder));
				continue;
			}
			if(!Files.isDirectory(folder)) {
				LOGGER.severe(String.format("Folder %s is not a directory. Skipping it!", modelFolder));
				continue;
			}
			try {
				Files.list(folder).filter(p -> Files.isRegularFile(p)).filter(p -> p.getFileName().toString().endsWith(".ecore")).forEach(p -> {
					try {
						Dictionary<String, Object> properties = new Hashtable<String, Object>();
						properties.put("id", p.getFileName().toString());
						properties.put("url", p.toString());
						properties.put("additionalRest", true);
						Configuration packageLoaderConfig = configAdmin.getFactoryConfiguration("DynamicPackageLoader", p.toString());
						packageLoaderConfig.update(properties);
						registrationsMap.put(p.toString(), packageLoaderConfig);
						System.out.println("Registered DynamicPackageLoader");
					} catch(IOException e) {
						throw new IllegalStateException("Error loading ecore files from folder " + folder, e);
					}		
					
				});
			} catch(IOException e) {
				throw new IllegalStateException("Error loading ecore files from folder " + folder, e);
			}			
		}
	}
}
