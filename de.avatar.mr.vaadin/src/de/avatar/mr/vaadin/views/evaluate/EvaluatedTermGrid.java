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

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.evaluation.EvaluatedTerm;
import de.avatar.mdp.evaluation.Evaluation;
import de.avatar.mdp.evaluation.Relevance;
import de.avatar.mdp.evaluation.RelevanceLevelType;

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
			text.setText(term.getElementClassifierName().concat("::").concat(term.getEvaluatedModelElement().getName()));
		})).setHeader("Element Name").setAutoWidth(true).setFlexGrow(1);
		addColumn(new ComponentRenderer<>(() -> new Grid<Evaluation>(), (evaluationsGrid, term) -> {
			evaluationsGrid.setWidth("100%");
			evaluationsGrid.addColumn(new ComponentRenderer<>(Label::new, (label, evaluation) -> {
				label.setText(evaluation.getInput());
				label.setTitle(determineTooltipFromRelevance(evaluation));
				label.setWidth("30%");
				label.getElement().getStyle()
					.set("color", determineColorFromHighestRelevance(evaluation));
				label.getElement().getStyle().set("white-space", "pre-wrap");
				label.getElement().getStyle().set("display", "inline-block");
			})).setHeader("Document").setAutoWidth(true).setFlexGrow(1);
			evaluationsGrid.addColumn(new ComponentRenderer<>(() -> new Grid<Relevance>(), (relGrid, evaluation) -> {
				relGrid.setItems(evaluation.getRelevance());
				relGrid.addColumn(Relevance::getCategory).setHeader("Category").setAutoWidth(true);
				relGrid.addColumn(new ComponentRenderer<>(() -> new ComboBox<RelevanceLevelType>(), (combobox, relevance) -> {
					combobox.setItems(RelevanceLevelType.values());
					combobox.setValue(relevance.getLevel());
					combobox.setWidthFull();
					combobox.setReadOnly(false);
					combobox.addValueChangeListener(evt -> {
						relevance.setLevel(evt.getValue());
						evaluationsGrid.getDataProvider().refreshAll();
					});
				})).setHeader("Level").setAutoWidth(true).setFlexGrow(1);
				relGrid.setWidthFull();
				relGrid.setMaxHeight("150px");
			})).setHeader("Relevance").setAutoWidth(true).setFlexGrow(1);
			evaluationsGrid.setItems(term.getEvaluations());
			evaluationsGrid.setMaxHeight("300px");
		})).setHeader("Evaluated Docs").setAutoWidth(true).setFlexGrow(1);
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
			return i1.getElementClassifierName().compareTo(i2.getElementClassifierName());
		}).toList();
		currentItems = (List<EvaluatedTerm>) items;
		return super.setItems(items);
	}
	
	public List<EvaluatedTerm> getCurrentItems() {
		return List.copyOf(currentItems);
	}
	
	private String determineColorFromHighestRelevance(Evaluation evaluation) {
		String color = "black";
		for(Relevance relevance : evaluation.getRelevance()) {
			if(relevance.getLevel().equals(RelevanceLevelType.RELEVANT)) {
				return "red";
			} else if(relevance.getLevel().equals(RelevanceLevelType.POTENTIALLY_RELEVANT)) {
				color = "orange";
			}
		}
		return color;
	}
	
	private String determineTooltipFromRelevance(Evaluation evaluation) {
		List<String> relevantCategories = evaluation.getRelevance().stream().
				filter(r -> r.getLevel().equals(RelevanceLevelType.RELEVANT)).
				map(r -> r.getCategory()).toList();
		List<String> potRelevantCategories = evaluation.getRelevance().stream().
				filter(r -> r.getLevel().equals(RelevanceLevelType.POTENTIALLY_RELEVANT)).
				map(r -> r.getCategory()).toList();
		if(relevantCategories.isEmpty() && potRelevantCategories.isEmpty()) return "";
		String tooltip = "";
		if(!relevantCategories.isEmpty()) {
			tooltip += "Term is relevant for categories: ";
			for(String c : relevantCategories) tooltip += c + " ";
		}
		if(!potRelevantCategories.isEmpty()) {
			if(!tooltip.isEmpty()) tooltip += "\n";
			tooltip += "Term is potentially relevant for categories: ";
			for(String c : potRelevantCategories) tooltip += c + " ";
		}
		return tooltip;
	}
}
