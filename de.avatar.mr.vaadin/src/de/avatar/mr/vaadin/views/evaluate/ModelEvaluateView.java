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
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.avatar.mdp.apis.api.ModelEvaluator;
import de.avatar.mdp.apis.api.ModelSuggesterRetrainer;
import de.avatar.mdp.apis.api.PRMetaModelService;
import de.avatar.mdp.evaluation.EvaluatedTerm;
import de.avatar.mdp.evaluation.EvaluationCriteriumType;
import de.avatar.mdp.evaluation.EvaluationSummary;
import de.avatar.mdp.evaluation.RelevanceLevelType;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.vaadin.common.EPackageGrid;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Aug 1, 2023
 */
@Route(value = "evaluate", layout = MainView.class)
@PageTitle("Evaluate")
@Component(name = "ModelEvaluateView", service=ModelEvaluateView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class ModelEvaluateView extends VerticalLayout{

	@Reference
	EPackageSearchService ePackageSearchService;

	@Reference(target = "(component.name=GDPRModelEvaluator)")
	ModelEvaluator gdprModelEvaluator;	
	
	@Reference(target = "(component.name=GDPRModelSuggesterRetrainer)")
	ModelSuggesterRetrainer gdprSuggesterRetrainer;
	
	@Reference
	PRMetaModelService prMetaModelService;

	/** serialVersionUID */
	private static final long serialVersionUID = 7098092065550709063L;
	private EvaluationSummary displayedSummary;
	private EPackage selectedEPackage;

	@Activate 
	public void renderView() {
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
		EvaluatedTermGrid summaryGrid = new EvaluatedTermGrid();
		Button saveBtn = new Button("Save Model", evt -> {
//			TODO: here we have to take the items of the SummaryGrid and create the coupled model with the info about gdpr related fields
			prMetaModelService.createPRModel(displayedSummary, selectedEPackage);
			Notification.show(String.format("GDPR constraints added to the model.")).addThemeVariants(NotificationVariant.LUMO_SUCCESS);			
			
			ConfirmDialog retrainDialog = new ConfirmDialog();
			retrainDialog.setHeader("Retrain Suggestion Model");
			retrainDialog.setText(
			        "Do you want to retrain the suggestion model with the annotated features?");

			retrainDialog.setCancelable(false);
			retrainDialog.setRejectable(true);
			retrainDialog.setRejectText("No");
			
			retrainDialog.setConfirmText("Yes");
			retrainDialog.addConfirmListener(event -> {
				List<EvaluatedTerm> evaluatedTerms = summaryGrid.getCurrentItems();
				List<String> relevantDocs = new ArrayList<>();
				List<String> unrelevantDocs = new ArrayList<>();
				evaluatedTerms.stream().forEach(t -> {
					t.getEvaluations().stream().forEach(e -> {
						if(e.getRelevanceLevel().equals(RelevanceLevelType.RELEVANT) || e.getRelevanceLevel().equals(RelevanceLevelType.POTENTIALLY_RELEVANT)) relevantDocs.add(e.getInput());
						else unrelevantDocs.add(e.getInput());
					});
				});
				gdprSuggesterRetrainer.retrainModelSuggester(relevantDocs, unrelevantDocs);
				Notification.show(String.format("The suggestion model is being retrained with the additional provided documents."))
				.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			});
			retrainDialog.open();
		});
		summaryLayout.add(summaryGrid, saveBtn);
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
			ComboBox<EvaluationCriteriumType> combo = new ComboBox<>();
			Button btn = new Button("Evaluate");
			btn.setEnabled(false);
			btn.addClickListener(evt -> {
				switch(combo.getValue()) {
				case GDPR:
					selectedEPackage = ePackage;
					displayedSummary = gdprModelEvaluator.evaluateModel(ePackage);
					summaryGrid.setItems(displayedSummary.getEvaluatedTerms());
					summaryLayout.setVisible(true);
				case OPEN_DATA:
					break;
				case OTHER:
					break;
				default:
					break;
				}
			});

			combo.setItems(EvaluationCriteriumType.VALUES);
			combo.setValue(combo.getEmptyValue());
			combo.addValueChangeListener(evt -> {
				if(evt.getValue() != combo.getEmptyValue()) {
					btn.setEnabled(true);
				}
			});			
			layout.add(combo, btn);
		})).setHeader("Evaluate").setAutoWidth(true);
	}

}
