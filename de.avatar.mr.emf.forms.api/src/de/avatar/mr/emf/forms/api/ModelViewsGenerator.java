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
package de.avatar.mr.emf.forms.api;

import org.eclipse.emf.ecore.EPackage;
import org.osgi.annotation.versioning.ProviderType;

/**
 * This service provider should be responsible for the creation of model views 
 * starting from an EPackage
 * @author ilenia
 * @since Oct 10, 2023
 */
@ProviderType
public interface ModelViewsGenerator{
	
	void generateModelViews(EPackage ePackage, String pathToEcore);

}
