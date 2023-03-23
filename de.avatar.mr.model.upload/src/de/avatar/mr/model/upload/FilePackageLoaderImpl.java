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
package de.avatar.mr.model.upload;

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

import de.avatar.mr.model.upload.api.FilePackageLoader;


/**
 * 
 * @author ilenia
 * @since Mar 22, 2023
 */
@Component(immediate=true, name = "FilePackageLoader", service = FilePackageLoader.class, scope = ServiceScope.SINGLETON, 
configurationPid = "FilePackageLoader", configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = de.avatar.mr.model.upload.FilePackageLoaderImpl.Config.class, factory = true)
@RequireConfigurationAdmin
public class FilePackageLoaderImpl implements FilePackageLoader{

	private ConfigurationAdmin configAdmin;
	private Map<String, Configuration> registrationsMap = new HashMap<>();
	private Config config;

	private static final Logger LOGGER = Logger.getLogger(FilePackageLoaderImpl.class.getName());

	@ObjectClassDefinition(
			description = "An URL can be configured, where the ecore files are expected to be stored. All the ecore files found will then be registered through a DynamicPackageLoader"
			)
	public @interface Config {		

		@AttributeDefinition(description = "A folder where to look for ecore to be registered.")
		String modelFolder() default "";
	}

	@Activate
	public FilePackageLoaderImpl(@Reference ConfigurationAdmin configAdmin, Config config) throws ConfigurationException {
		this.configAdmin = configAdmin;	
		this.config = config;
		try {
			loadModels(config.modelFolder());
		} catch (Exception e) {
			throw new ConfigurationException("modelFolder", "Error when loading models form folder " + config.modelFolder(), e);
		}		
	}

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.model.upload.api.FilePackageLoader#loadModel(java.nio.file.Path[])
	 */
	@Override
	public void loadModel(Path... modelFilePath) {
		if(modelFilePath == null) return;
		for(Path p : modelFilePath) {
			if(!Files.exists(p)) {
				LOGGER.severe(String.format("Path %s does not exist. Skipping it!", p));
				continue;
			}
			if(!Files.isRegularFile(p)) {
				LOGGER.severe(String.format("Path %s is not a regular file. Skipping it!", p));
				continue;
			}
			if(!p.getFileName().toString().endsWith(".ecore")) {
				LOGGER.severe(String.format("Path %s is not an ecore file. Skipping it!", p));
				continue;
			}
			try {
				Dictionary<String, Object> properties = new Hashtable<String, Object>();
				properties.put("id", p.getFileName().toString());
				properties.put("url", p.toString());
				properties.put("additionalRest", true);
				Configuration packageLoaderConfig = configAdmin.getFactoryConfiguration("DynamicPackageLoader", p.toString(), "?");
				packageLoaderConfig.update(properties);
				registrationsMap.put(p.toString(), packageLoaderConfig);
				System.out.println("Registered DynamicPackageLoader for " + p.toString());
			} catch(IOException e) {
				throw new IllegalStateException("Error loading ecore file " + p.toString(), e);
			}	
		}		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.model.upload.api.FilePackageLoader#getPackageStoragePath()
	 */
	@Override
	public Path getPackageStoragePath() {
		return Path.of(config.modelFolder());
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

	private void loadModels(String modelFolder) {
		if(modelFolder == null) return;		
		Path folder = Paths.get(modelFolder);
		if(!Files.exists(folder)) {
			LOGGER.severe(String.format("Folder %s does not exist. Skipping it!", modelFolder));
			return;
		}
		if(!Files.isDirectory(folder)) {
			LOGGER.severe(String.format("Folder %s is not a directory. Skipping it!", modelFolder));
			return;
		}
		try {
			loadModel(Files.list(folder).toArray(size -> new Path[size]));
		} catch(IOException e) {
			throw new IllegalStateException("Error loading ecore files from folder " + folder, e);
		}
	}

	
}
