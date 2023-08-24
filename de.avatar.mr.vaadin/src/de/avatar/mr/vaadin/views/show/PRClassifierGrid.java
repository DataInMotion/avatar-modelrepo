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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.prmeta.EvaluationLevelType;
import de.avatar.mdp.prmeta.PRClassifier;
import de.avatar.mdp.prmeta.PRFeature;

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
			label.getElement().getStyle().set("color", getColorByEvaluationLevel(classifier.getEvaluationLevel()));
			label.getElement().setProperty("title", getTooltipByEvaluationLevel(classifier.getEvaluationLevel()));
		})).setHeader("EClassifier").setAutoWidth(true);
		
		addColumn(new ComponentRenderer<>(()-> new Grid<PRFeature>(), (grid, classifier) -> {
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, feature) -> {
				label.setText(feature.getFeature().getName());
				label.getElement().getStyle().set("color", getColorByEvaluationLevel(feature.getEvaluationLevel()));
				label.getElement().setProperty("title", getTooltipByEvaluationLevel(feature.getEvaluationLevel()));
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
	
	private String getColorByEvaluationLevel(EvaluationLevelType evaluationLevel) {
		switch(evaluationLevel) {
		case NOT_RELEVANT: default:
			return "black";
		case RELEVANT:
			return "red";
		case POTENTIALLY_RELEVANT:
			return "orange";
		}
	}
	
	private String getTooltipByEvaluationLevel(EvaluationLevelType evaluationLevel) {
		switch(evaluationLevel) {
		case NOT_RELEVANT: default:
			return "No warnings for this element based on the selected criteria";
		case RELEVANT:
			return "High risk element based on the selected criteria";
		case POTENTIALLY_RELEVANT:
			return "Potential risk element based on the selected criteria";
		}
	}
}
