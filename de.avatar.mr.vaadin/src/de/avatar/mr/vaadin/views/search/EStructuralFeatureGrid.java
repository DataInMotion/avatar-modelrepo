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
package de.avatar.mr.vaadin.views.search;

import java.util.Collection;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;


/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
public class EStructuralFeatureGrid extends Grid<EStructuralFeature> {

	/** serialVersionUID */
	private static final long serialVersionUID = -3380128907075910141L;
	public static final String EXTENDED_METADATA_ANNOTATION_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	
	
	public EStructuralFeatureGrid() {
		addColumn(sf -> {
			return sf.getEContainingClass().getEPackage().getName();
		}).setHeader("EPackage Name").setAutoWidth(true);
		addColumn(sf -> {
			return sf.getEContainingClass().getEPackage().getNsURI();
		}).setHeader("EPackage Ns URI").setAutoWidth(true);
		addColumn(sf -> {
			return sf.getEContainingClass().getName();
		}).setHeader("EClass Name").setAutoWidth(true);
		addColumn(EStructuralFeature::getName).setHeader("EStructuralFeature Name").setAutoWidth(true);
		addColumn(sf -> {
			return sf.getEType().getName();
		}).setHeader("Type").setAutoWidth(true);
		setItemDetailsRenderer(new ComponentRenderer<>(VerticalLayout::new, (layout, feature) -> {
			layout.setWidthFull();
			HorizontalLayout infoLayout = new HorizontalLayout();
			infoLayout.setAlignItems(Alignment.BASELINE);
			EAnnotation aliasAnnotation = feature.getEAnnotations().stream()
			.filter(ann -> EXTENDED_METADATA_ANNOTATION_SOURCE.equals(ann.getSource()))
			.filter(ann -> ann.getDetails().containsKey("name"))
			.findFirst().orElse(null);
			if(aliasAnnotation != null) {
				TextField alias = new TextField("Alias");
				alias.setReadOnly(true);
				alias.setValue(aliasAnnotation.getDetails().get("name"));
				infoLayout.add(alias);
			}
			
			Checkbox isMany = new Checkbox("Is many?");
			isMany.setReadOnly(true);
			isMany.setValue(feature.isMany());
			Checkbox isMandatory = new Checkbox("Is mandatory?");
			isMandatory.setReadOnly(true);
			isMandatory.setValue(feature.isRequired());
			infoLayout.add(isMany, isMandatory);
			if(feature instanceof EReference) {
				EReference ref = (EReference) feature;
				Checkbox isContainement = new Checkbox("Is containement?");
				isContainement.setReadOnly(true);
				isContainement.setValue(ref.isContainment());
				infoLayout.add(isContainement);
			} else if(feature instanceof EAttribute) {
				EAttribute att = (EAttribute) feature;
				Checkbox isID = new Checkbox("Is ID field?");
				isID.setReadOnly(true);
				isID.setValue(att.isID());
				infoLayout.add(isID);
			}
			layout.add(infoLayout);
			
			EAnnotation docAnnotation = feature.getEAnnotations().stream()
					.filter(ann -> ann.getDetails().containsKey("documentation"))
					.findFirst().orElse(null);
			if(docAnnotation != null) {
				HorizontalLayout annLayout = new HorizontalLayout();
				annLayout.setWidthFull();
				annLayout.setEnabled(false);
				TextArea doc = new TextArea("Documentation");
				doc.setWidthFull();
				doc.setReadOnly(true);
				doc.setValue(docAnnotation.getDetails().get("documentation"));
				annLayout.add(doc);
				layout.add(annLayout);
			}			
			
		}));
	}

	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<EStructuralFeature> setItems(Collection<EStructuralFeature> items) {
		if(items.isEmpty()) {
			setVisible(false);
			Notification.show("No item matching your search criteria has been found").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
		}
		else setVisible(true);
		return super.setItems(items);
	}
}
