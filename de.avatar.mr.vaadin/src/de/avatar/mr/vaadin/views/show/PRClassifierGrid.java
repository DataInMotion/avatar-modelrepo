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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.prmeta.PRClassifier;
import de.avatar.mdp.prmeta.PRFeature;
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
		addColumn(new ComponentRenderer<>(Label::new, (label, classifier) -> {
			label.setText(classifier.getEClassifier().getName());
			label.getElement().getStyle().set("color", determineColorFromHighestRelevance(classifier.getRelevance()));
			label.getElement().setProperty("title", determineTooltipFromRelevance(classifier.getRelevance()));
		})).setHeader("EClassifier").setAutoWidth(true);
		
		addColumn(new ComponentRenderer<>(()-> new Grid<PRFeature>(), (grid, classifier) -> {
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, feature) -> {
				label.setText(feature.getFeature().getName());
				label.getElement().getStyle().set("color", determineColorFromHighestRelevance(feature.getRelevance()));
				label.getElement().setProperty("title", determineTooltipFromRelevance(feature.getRelevance()));
			})).setHeader("Name").setAutoWidth(true);
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, feature) -> {
				label.setText(feature.getFeature().getEType().getName());
			})).setHeader("EType").setAutoWidth(true);
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, feature) -> {
				label.setText(feature.getFeature().getLowerBound() + "..." + feature.getFeature().getUpperBound());
			})).setHeader("Cardinality").setAutoWidth(true);
			grid.setItems(classifier.getPrFeature());
		})).setHeader("EStructuralFeatures").setAutoWidth(true);
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
