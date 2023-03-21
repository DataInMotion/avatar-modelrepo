/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.search.api;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface EPackageSearchService{

	List<EPackage> searchEPackagesByName(String query);
	
	List<EPackage> searchEPackagesByNsURI(String query);
	
	List<EPackage> searchEPackages(String query);

}
