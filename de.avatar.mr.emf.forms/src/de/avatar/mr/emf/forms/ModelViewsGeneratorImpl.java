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
package de.avatar.mr.emf.forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecp.view.spi.model.VContainer;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VDomainModelReference;
import org.eclipse.emf.ecp.view.spi.model.VElement;
import org.eclipse.emf.ecp.view.spi.model.VFeaturePathDomainModelReference;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.model.VViewFactory;
import org.eclipse.emf.ecp.view.spi.model.VViewPackage;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import de.avatar.mr.emf.forms.api.ModelViewsGenerator;

@Component(name = "ModelViewsGenerator", service = ModelViewsGenerator.class, 
configurationPid = "ModelViewsGenerator", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ModelViewsGeneratorImpl implements ModelViewsGenerator {
	
	@Reference
	private ComponentServiceObjects<ResourceSet> rsFactory;
	
	private static final Logger LOGGER = Logger.getLogger(ModelViewsGeneratorImpl.class.getName());
	
	public @interface ModelViewsGeneratorConfig {
		String out_folder() default "";
	}

	private ModelViewsGeneratorConfig config;
	
	@Activate
	public void activate(ModelViewsGeneratorConfig config) {
		this.config = config;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.emf.forms.api.ModelViewsGenerator#generateModelViews(org.eclipse.emf.ecore.EPackage, java.lang.String)
	 */
	@Override
	public void generateModelViews(EPackage ePackage, String pathToEcore) {
		
		ePackage.getEClassifiers().stream().filter(ec -> ec instanceof EClass).map(ec -> (EClass) ec)
			.forEach(ec -> doGenerateModelView(ec, ePackage.getName(), pathToEcore));
		
	}

	/**
	 * Generates and save the model view for the given EClass
	 * 
	 * @param eClass the EClass for which the model view has to be created
	 * @param ePackageName the name of the EPackage to which the EClass belongs
	 * @param pathToEcore the path to the original ecore file for the EClass
	 */
	private void doGenerateModelView(EClass eClass, String ePackageName, String pathToEcore) {
		ResourceSet resSet = rsFactory.getService();
		try {			
			String viewModelFileName = config.out_folder().concat(ePackageName).concat("_").concat(eClass.getName()).concat(".view");
			Resource resource = resSet.createResource(URI.createFileURI(viewModelFileName));
			final VView view = VViewPackage.eINSTANCE.getViewFactory().createView();
			if (view == null) {
				LOGGER.log(Level.SEVERE, String.format("Null VView for EClass %s and EPackage %s. Cannot go on.", eClass.getName(), ePackageName));
				return;
			}
			resource.getContents().add(view);
			view.setRootEClass(eClass);
			view.setName(eClass.getName());
			
			// Update the VView-EClass mapping
			if (pathToEcore != null && !view.getEcorePaths().contains(pathToEcore)) {
				view.getEcorePaths().add(pathToEcore);
			}
			
//			Add children elements
			final Set<EStructuralFeature> eClassFeaturesSet = new LinkedHashSet<EStructuralFeature>(
					view.getRootEClass().getEAllStructuralFeatures());
			addControls(eClass, view, eClassFeaturesSet);
		
			// Save the contents of the resource to the file system.
			resource.save(Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8")); 
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, String.format("IOException when saving view model for EClass %s and EPackage %s", eClass.getName(), ePackageName), e);
		} finally {
			rsFactory.ungetService(resSet);
		}
	}


	private void addControls(EClass rootClass, VElement compositeToFill, Set<EStructuralFeature> features) {
		if (!VContainer.class.isInstance(compositeToFill) && !VView.class.isInstance(compositeToFill)) {
			return;
		}
		final Map<EClass, EReference> childParentReferenceMap = new HashMap<EClass, EReference>();
		getReferenceMap(rootClass, childParentReferenceMap);
		
		for (final EStructuralFeature feature : features) {
			final VControl control = VViewFactory.eINSTANCE.createControl();
			control.setName("Control " + feature.getName()); //$NON-NLS-1$
			control.setReadonly(false);
			
			final List<EReference> bottomUpPath = getReferencePath(rootClass,
				feature.getEContainingClass(), childParentReferenceMap);
//			 How to create the dmr depends on the tooling mode (legacy or segments)
			final VDomainModelReference modelReference = createLegacyDmr(feature, bottomUpPath);

			control.setDomainModelReference(modelReference);

			// add to the composite
			if (VContainer.class.isInstance(compositeToFill)) {
				((VContainer) compositeToFill).getChildren().add(control);
			} else if (VView.class.isInstance(compositeToFill)) {
				((VView) compositeToFill).getChildren().add(control);
			}
		}
	}
	
	
	private VDomainModelReference createLegacyDmr(EStructuralFeature domainFeature,
			List<EReference> bottomUpPath) {
			final VFeaturePathDomainModelReference dmr = VViewFactory.eINSTANCE.createFeaturePathDomainModelReference();
			dmr.getDomainModelEReferencePath().addAll(bottomUpPath);
			dmr.setDomainModelEFeature(domainFeature);
			return dmr;
		}
		
	private void getReferenceMap(EClass parent,
			Map<EClass, EReference> childParentReferenceMap) {
			for (final EReference eReference : parent.getEAllContainments()) {
				if (eReference.getEReferenceType() == null) {
					continue;
				}
				if (!eReference.getEReferenceType().isSuperTypeOf(parent)
					&& childParentReferenceMap.get(eReference.getEReferenceType()) == null) {
					childParentReferenceMap.put(eReference.getEReferenceType(), eReference);
					getReferenceMap(eReference.getEReferenceType(), childParentReferenceMap);
				}
			}
		}
	
	/**
	 * Retrieves the reference path for a selected EClass from the provided map.
	 *
	 * @param rootEClass the root EClass of the view model
	 * @param selectedClass the {@link EClass} to get the reference path for
	 * @param childParentReferenceMap the map to use
	 * @return the reference path
	 */
	private List<EReference> getReferencePath(EClass rootEClass, EClass selectedClass,
		Map<EClass, EReference> childParentReferenceMap) {

		final List<EReference> bottomUpPath = new ArrayList<EReference>();

		if (rootEClass == selectedClass) {
			return bottomUpPath;
		}

		EReference parentReference = childParentReferenceMap.get(selectedClass);
		while (parentReference != null && !bottomUpPath.contains(parentReference)) {
			bottomUpPath.add(parentReference);
			selectedClass = parentReference.getEContainingClass();
			if (selectedClass == rootEClass) {
				break;
			}
			parentReference = childParentReferenceMap.get(selectedClass);
		}
		Collections.reverse(bottomUpPath);
		return bottomUpPath;
	}
}
