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
package de.jena.mdo.piveau.adapter;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

import dcat.Dataset;

/**
 * Adapter for Piveau to register distributions or/and data sets for a given catalogue 
 * @author Mark Hoffmann
 * @since 25.11.2022
 */
@ProviderType
public interface PiveauDatasetAdapter {
	
	public static final String DATASET_URI = "datasets";
	public static final String DATASET_INDEX_URI = "indexdataset";
	
	Dataset createDataset(Map<String, Object> data, String datasetId, String catalogueId);
	
	boolean deleteDataset(Map<String, Object> data, String datasetId, String catalogueId);

}
