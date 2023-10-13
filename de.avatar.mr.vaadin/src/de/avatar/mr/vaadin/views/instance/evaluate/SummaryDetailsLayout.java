///**
// * Copyright (c) 2012 - 2023 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package de.avatar.mr.vaadin.views.instance.evaluate;
//
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//
//import de.avatar.mdp.evaluation.EvaluationSummary;
//import de.avatar.mr.vaadin.views.edit.EObjectEvaluatedTermGrid;
//
///**
// * 
// * @author ilenia
// * @since Oct 12, 2023
// */
//public class SummaryDetailsLayout extends VerticalLayout {
//
//	/** serialVersionUID */
//	private static final long serialVersionUID = -1089482693813831659L;
//	
//	public void fillLayout(EvaluationSummary summary) {
//		setSizeFull();
//		EObjectEvaluatedTermGrid evaluatedTermGrid = new EObjectEvaluatedTermGrid();
//		evaluatedTermGrid.setItems(summary.getEvaluatedTerms());
//		add(evaluatedTermGrid);
//	}
//
//}
