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
package de.avatar.mr.vaadin.views.edit;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.avatar.mdp.evaluation.EvaluatedTerm;
import de.avatar.mdp.evaluation.Evaluation;

/**
 * 
 * @author ilenia
 * @since Oct 6, 2023
 */
public class EObjectEvaluatedTermGrid extends Grid<EvaluatedTerm> {

	/** serialVersionUID */
	private static final long serialVersionUID = 6334605680655499755L;
	
	public EObjectEvaluatedTermGrid() {
//		setSizeFull();
		addColumn(EvaluatedTerm::getElementClassifierName).setHeader("Classifier Name").setAutoWidth(true);
		addColumn(new ComponentRenderer<>(Label::new, (label, term) -> {
			label.setText(term.getEvaluatedModelElement().getName());
		})).setHeader("Feature Name").setAutoWidth(true);
		addColumn(new ComponentRenderer<>(() -> new Grid<Evaluation>(), (grid, term) -> {
			grid.setMaxHeight("150px");
			grid.setItems(term.getEvaluations());
			grid.addColumn(Evaluation::getInput).setHeader("Original Value").setAutoWidth(true);
			grid.addColumn(new ComponentRenderer<>(Label::new, (label, evaluation) -> {
				String entities = "";
				for(String entity : evaluation.getEntities()) {
					entities += entity + ";";
				}
				if(!entities.isBlank()) entities = entities.substring(0, entities.length()-1);
				label.setText(entities);
			})).setHeader("PII Entities").setAutoWidth(true);
		})).setHeader("Evaluations").setAutoWidth(true);
	}

}
