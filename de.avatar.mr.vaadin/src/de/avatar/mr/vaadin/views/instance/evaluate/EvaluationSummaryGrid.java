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
package de.avatar.mr.vaadin.views.instance.evaluate;

import java.util.Date;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.evaluation.EvaluationSummary;
import de.avatar.mr.vaadin.views.edit.EObjectEvaluatedTermGrid;

/**
 * 
 * @author ilenia
 * @since Oct 12, 2023
 */
public class EvaluationSummaryGrid extends Grid<EvaluationSummary> {

	/** serialVersionUID */
	private static final long serialVersionUID = 97752688316763518L;

	public EvaluationSummaryGrid() {
		setSizeFull();
		addColumn(EvaluationSummary::getEvaluationCriterium).setHeader("Evaluation Criterium").setAutoWidth(true);
		addColumn(EvaluationSummary::getEvaluationModelUsed).setHeader("Evaluator Model Used").setAutoWidth(true);
		addColumn(new ComponentRenderer<>(Label::new, (label, summary) -> {
			label.setText(new Date(summary.getEvaluationTimestamp()).toString());
		})).setHeader("Evaluation Date").setAutoWidth(true);
		setDetailsVisibleOnClick(true);
		setItemDetailsRenderer(new ComponentRenderer<>(VerticalLayout::new, (layout, summary) -> {
					layout.setSizeFull();
					EObjectEvaluatedTermGrid grid = new EObjectEvaluatedTermGrid();
					grid.setItems(summary.getEvaluatedTerms());
					layout.add(grid);
				}));
	}
}
