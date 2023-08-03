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
package de.avatar.mr.vaadin.views.evaluate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.evaluation.EvaluatedTerm;
import de.avatar.mdp.evaluation.Evaluation;

/**
 * 
 * @author ilenia
 * @since Aug 1, 2023
 */
public class EvaluatedTermGrid extends Grid<EvaluatedTerm>{

	/** serialVersionUID */
	private static final long serialVersionUID = -1336911553554291158L;
	private List<EvaluatedTerm> currentItems = new ArrayList<>();
	
	public EvaluatedTermGrid() {
		addColumn(new ComponentRenderer<>(Label::new, (text, term) -> {
			text.setText(term.getFeatureClassifierName().concat("::").concat(term.getEvaluatedFeature().getName()));
		})).setHeader("Feature Name").setAutoWidth(true);
		addColumn(new ComponentRenderer<>(() -> new Grid<Evaluation>(), (evaluationsGrid, term) -> {
			evaluationsGrid.setWidth("100%");
			evaluationsGrid.addColumn(new ComponentRenderer<>(Label::new, (label, evaluation) -> {
				label.setText(evaluation.getInput());
				label.setWidth("50%");
				label.getElement().getStyle().set("color", evaluation.isRelevant() ? "green" : "red");
				label.getElement().getStyle().set("white-space", "pre-wrap");
				label.getElement().getStyle().set("display", "inline-block");
			})).setHeader("Document").setAutoWidth(true);
			evaluationsGrid.addColumn(new ComponentRenderer<>(Checkbox::new, (checkbox, evaluation) -> {
				checkbox.setValue(evaluation.isRelevant());
				checkbox.setWidth("50%");
				checkbox.setReadOnly(false);
				checkbox.addValueChangeListener(evt -> {
					evaluation.setRelevant(evt.getValue());
					evaluationsGrid.getDataProvider().refreshAll();
				});
			})).setHeader("Is Relevant?").setAutoWidth(true);
			evaluationsGrid.setItems(term.getEvaluations());
			evaluationsGrid.setMaxHeight("200px");
		})).setHeader("Evaluated Docs").setAutoWidth(true);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<EvaluatedTerm> setItems(Collection<EvaluatedTerm> items) {
		if(items.isEmpty()) setVisible(false);
		else setVisible(true);
		items = items.stream().sorted((i1,i2) -> {
			return i1.getFeatureClassifierName().compareTo(i2.getFeatureClassifierName());
		}).toList();
		currentItems = (List<EvaluatedTerm>) items;
		return super.setItems(items);
	}
	
	public List<EvaluatedTerm> getCurrentItems() {
		return List.copyOf(currentItems);
	}
}
