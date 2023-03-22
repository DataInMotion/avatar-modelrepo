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
package de.avatar.mr.model.documentation.provider;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
@Component(immediate = true, name = "ModelDynamicDocumentationComponent", service = ModelDynamicDocumentationComponent.class)
public class ModelDynamicDocumentationComponent {

	private ModelDocumentationProvider modelDocProvider;
	
	@Activate
	public ModelDynamicDocumentationComponent(@Reference ModelDocumentationProvider modelDocProvider) {
		this.modelDocProvider = modelDocProvider;
	}
	
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
			policyOption = ReferencePolicyOption.GREEDY, unbind = "removeModelDocumentation")
	public void generateModelDocumentation(EPackage ePackage, Map<String, Object> properties) {
		if(modelDocProvider.hasEPackageChanged(ePackage)) {
			modelDocProvider.generateAllPackageDocumentation(ePackage);
			modelDocProvider.generateAllEClassifiersDocumentation(ePackage);
		}
	}
	
	public void removeModelDocumentation(EPackage ePackage) {
//		We do not want to remove the EPackage from the docs here
	}
	
}
