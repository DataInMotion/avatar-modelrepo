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
package de.avatar.mr.vaadin.views.show;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.prmeta.PRClassifier;
import de.avatar.mdp.prmeta.PRModelElement;
import de.avatar.mdp.prmeta.Relevance;
import de.avatar.mdp.prmeta.RelevanceLevelType;

/**
 * 
 * @author ilenia
 * @since Aug 9, 2023
 */
public class PRClassifierGrid extends Grid<PRClassifier> {

	/** serialVersionUID */
	private static final long serialVersionUID = -4567276995318247328L;
	
	public PRClassifierGrid() {
		setWidth("100%");
		addColumn(new ComponentRenderer<>(Label::new, (label, prClassifier) -> {
			label.setText(prClassifier.getEClassifier().getName());
			label.getElement().getStyle().set("color", determineColorFromHighestRelevance(prClassifier.getRelevance()));
			label.getElement().setProperty("title", determineTooltipFromRelevance(prClassifier.getRelevance()));
		})).setHeader("EClassifier").setResizable(true).setFlexGrow(1);
		addColumn(new ComponentRenderer<>(Label::new, (label, prClassifier) -> {
			if(prClassifier.getEClassifier() instanceof EClass) label.setText("EClass");
			else if(prClassifier.getEClassifier() instanceof EEnum) label.setText("EEnum");
			else if(prClassifier.getEClassifier() instanceof EDataType) label.setText("EDataType");
			else label.setText("UNKNOWN");
		})).setHeader("Type").setResizable(true).setFlexGrow(0);
		
		addColumn(new ComponentRenderer<>(()-> new Grid<PRModelElement>(), (grid, prClassifier) -> {
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, prModelElement) -> {
				label.setText(prModelElement.getModelElement().getName());
				label.getElement().getStyle().set("color", determineColorFromHighestRelevance(prModelElement.getRelevance()));
				label.getElement().setProperty("title", determineTooltipFromRelevance(prModelElement.getRelevance()));
			})).setHeader("Name").setAutoWidth(true);
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, prModelElement) -> {
				if(prModelElement.getModelElement() instanceof EStructuralFeature feature) {
					label.setText(feature.getEType().getName());
				} else if(prModelElement.getModelElement() instanceof EEnumLiteral literal) {
					label.setText("EEnumLiteral");				
				} else if(prModelElement.getModelElement() instanceof EDataType eDataType) {
					label.setText(eDataType.getInstanceClassName());				
				}
			})).setHeader("EType").setResizable(true);
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, prModelElement) -> {
				if(prModelElement.getModelElement() instanceof EStructuralFeature feature) {
					label.setText(feature.getLowerBound() + "..." + feature.getUpperBound());
				} else label.setText("N/A");	
			})).setHeader("Cardinality").setResizable(true);
			grid.setItems(prClassifier.getPrModelElement());
		})).setHeader("Elements").setResizable(true).setFlexGrow(4);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<PRClassifier> setItems(Collection<PRClassifier> items) {
		if(items.isEmpty()) setVisible(false);
		else setVisible(true);
		return super.setItems(items);
	}
	
	private String determineTooltipFromRelevance(List<Relevance> relevances) {
		List<String> relevantCategories = relevances.stream().
				filter(r -> r.getLevel().equals(RelevanceLevelType.RELEVANT)).
				map(r -> r.getCategory()).toList();
		List<String> potRelevantCategories = relevances.stream().
				filter(r -> r.getLevel().equals(RelevanceLevelType.POTENTIALLY_RELEVANT)).
				map(r -> r.getCategory()).toList();
		if(relevantCategories.isEmpty() && potRelevantCategories.isEmpty()) return "";
		String tooltip = "";
		if(!relevantCategories.isEmpty()) {
			tooltip += "Element is relevant for categories: ";
			for(String c : relevantCategories) tooltip += c + " ";
		}
		if(!potRelevantCategories.isEmpty()) {
			if(!tooltip.isEmpty()) tooltip += "\n";
			tooltip += "Element is potentially relevant for categories: ";
			for(String c : potRelevantCategories) tooltip += c + " ";
		}
		return tooltip;
	}
	
	private String determineColorFromHighestRelevance(List<Relevance> relevances) {
		String color = "black";
		for(Relevance relevance : relevances) {
			if(relevance.getLevel().equals(RelevanceLevelType.RELEVANT)) {
				return "red";
			} else if(relevance.getLevel().equals(RelevanceLevelType.POTENTIALLY_RELEVANT)) {
				color = "orange";
			}
		}
		return color;
	}
}
