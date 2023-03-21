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
package de.avatar.mr.index.api;

import org.eclipse.emf.ecore.EPackage;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface EPackageIndexService{
	
	public static final String DOC_TYPE = "doc_type";
	public static final String EPACKAGE_NS_URI = "epackage_ns_uri";
	public static final String EPACKAGE_NAME = "epackage_name";
	public static final String ECLASSIFIER_NAME = "eclassifier_name";
	public static final String ECLASSIFIER_NAME_LOWER = "eclassifier_name_lower";
	public static final String ECLASSIFIER_ALIAS = "eclassifier_alias";
	public static final String ECLASSIFIER_ALIAS_LOWER = "eclassifier_alias_lower";
	public static final String ECLASSIFIER_SUPER_TYPES = "eclassifier_super_types";
	public static final String ESTRUCTURAL_FEATURE_NAME = "estrucutral_feature_name";
	public static final String ESTRUCTURAL_FEATURE_NAME_LOWER = "estrucutral_feature_name_lower";
	public static final String ESTRUCTURAL_FEATURE_ALIAS = "estrucutral_feature_alias";
	public static final String ESTRUCTURAL_FEATURE_ALIAS_LOWER = "estrucutral_feature_alias_lower";
	public static final String ESTRUCTURAL_FEATURE_TYPE = "estrucutral_feature_type";
	public static final String EELEMENT_DESCRIPTION = "eelement_description";

	void indexEPackage(EPackage ePackage, boolean add);
	
	void deleteEPackage(EPackage ePackage);
	
	void resetIndex();

}
