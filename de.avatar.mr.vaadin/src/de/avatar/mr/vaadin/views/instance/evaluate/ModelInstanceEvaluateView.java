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

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.avatar.mdp.apis.DBObjectsEvaluator;
import de.avatar.mdp.evaluation.EvaluationSummary;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.vaadin.common.EPackageGrid;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Oct 12, 2023
 */
@Route(value = "instance-evaluation", layout = MainView.class)
@PageTitle("Model Instance Evaluation")
@Component(name = "ModelInstanceEvaluateView", service=ModelInstanceEvaluateView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class ModelInstanceEvaluateView extends VerticalLayout{

	/** serialVersionUID */
	private static final long serialVersionUID = 5684569107741570413L;
	
	@Reference
	EPackageSearchService ePackageSearchService;
	
	@Reference
	DBObjectsEvaluator dbObjEvaluator;
	
	@Activate 
	public void renderView() {
		setSizeFull();
		HorizontalLayout searchLayout = new HorizontalLayout();
		searchLayout.setWidthFull();
		searchLayout.setAlignItems(Alignment.BASELINE);
		TextField searchField = new TextField("Enter search");
		Button searchBtn = new Button(new Icon(VaadinIcon.SEARCH));
		Button clearBtn = new Button(new Icon(VaadinIcon.ERASER));
		searchLayout.add(searchField, searchBtn, clearBtn);		

		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.setWidthFull();
		gridLayout.setVisible(false);
		EPackageGrid ePackageGrid = new EPackageGrid();
		ePackageGrid.setClassName("grid-component");
		
		VerticalLayout summaryLayout = new VerticalLayout();
		summaryLayout.setSizeFull();
		summaryLayout.setVisible(false);
		EvaluationSummaryGrid summaryGrid = new EvaluationSummaryGrid();
		
		summaryLayout.add(summaryGrid);
		add(searchLayout, gridLayout, summaryLayout);
		
//		Listeners
		clearBtn.addClickListener(evt -> {
			searchField.setValue("");
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});
			summaryLayout.setVisible(false);
			summaryGrid.setItems(Collections.emptyList());
			gridLayout.setVisible(false);
		});

		searchBtn.addClickListener(evt -> {
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});

			List<EPackage> ePackages = ePackageSearchService.searchEPackages(searchField.getValue());
			ePackageGrid.setItems(ePackages);
			gridLayout.add(ePackageGrid);			
			gridLayout.setVisible(true);
		});

		ePackageGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, ePackage) -> {
			layout.setAlignItems(Alignment.CENTER);
			Button showBtn = new Button("Show Reports");
			showBtn.addClickListener(evt -> {
//				Displays evaluation summary for the selected EPakcage
				List<EvaluationSummary> summaries = dbObjEvaluator.getEvaluationSummariesForEPackage(ePackage);
				if(summaries.isEmpty()) Notification.show(String.format("No EvaluationSummary is present for EPackage %s. "
						+ "Might be the db has no entries of that type.", ePackage.getName()));
				summaryGrid.setItems(summaries);
				summaryLayout.setVisible(true);
			});			
			layout.add(showBtn);
		})).setHeader("Evaluation Reports").setAutoWidth(true);
	}
}
