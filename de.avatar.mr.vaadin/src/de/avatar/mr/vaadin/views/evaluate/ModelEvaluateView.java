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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.avatar.mdp.evaluation.EvaluationSummary;
import de.avatar.mdp.evaluation.Relevance;
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

	@Reference(target = "(&(component.name=GDPRModelEvaluator)(modelName=multilabel_v2))")
	ModelEvaluator gdprSkiLearnModelEvaluator;	
	
	@Reference(target = "(&(component.name=GDPRModelSuggesterRetrainer)(modelName=multilabel_v2))")
	ModelSuggesterRetrainer gdprSkiLearnSuggesterRetrainer;
	
	@Reference(target = "(&(component.name=GDPRModelEvaluator)(modelName=spacy_textcat_ner_negex))")
	ModelEvaluator gdprSpacyModelEvaluator;	
	
	@Reference(target = "(&(component.name=GDPRModelSuggesterRetrainer)(modelName=spacy_textcat_ner_negex))")
	ModelSuggesterRetrainer gdprSpacySuggesterRetrainer;
	
	@Reference
	PRMetaModelService prMetaModelService;

	/** serialVersionUID */
	private static final long serialVersionUID = 7098092065550709063L;
	private static final List<String> EVALUATION_MODELS = List.of("GDPR-sklearn", "GDPR-spacy", "OPEN_DATA", "OTHER");
	private EvaluationSummary displayedSummary;
	private EPackage selectedEPackage;
	private ModelEvaluator currentModelEvaluator;

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
		EvaluatedTermGrid summaryGrid = new EvaluatedTermGrid();
		Button saveBtn = new Button("Save Model", evt -> {
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
				Map<String, List<Relevance>> evaluatedTermsMap = new HashMap<>();
				evaluatedTerms.stream().forEach(t -> {
					t.getEvaluations().stream().forEach(e -> {
						List<Relevance> relevances = e.getRelevance();
//						We need to look whether the user has set some of the categories to relevant or potentially relevant that 
//						previously were not relevant. If so, we have to set the NONE category to non-relevant
//						The same for the other way around: we have to set the NONE category to relevant if the user has set all the other
//						categories to non-relevant
						boolean setNoneToRelevant = true;
						for(Relevance relevance : relevances) {
							if(!"NONE".equals(relevance.getCategory()) && !RelevanceLevelType.NOT_RELEVANT.equals(relevance.getLevel())) {
								setNoneToRelevant = false;
								break;
							}
						}
						relevances.stream().filter(r -> "NONE".equals(r.getCategory())).findFirst().get().setLevel(setNoneToRelevant ? RelevanceLevelType.RELEVANT : RelevanceLevelType.NOT_RELEVANT);
						evaluatedTermsMap.put(e.getInput(), relevances);
					});
				});
				try {
					if(currentModelEvaluator == gdprSkiLearnModelEvaluator) {
						gdprSkiLearnSuggesterRetrainer.retrainModelSuggester(evaluatedTermsMap);
					} else if(currentModelEvaluator == gdprSpacyModelEvaluator) {
						gdprSpacySuggesterRetrainer.retrainModelSuggester(evaluatedTermsMap);
					}
					Notification.show(String.format("The suggestion model is being retrained with the additional provided documents. Please, notice that it might take a while."))
					.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				} catch(Exception e) {
					Notification.show(String.format("Error while retraining the suggester model.")).addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
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
			ComboBox<String> combo = new ComboBox<>();
			Button btn = new Button("Evaluate");
			btn.setEnabled(false);
			btn.addClickListener(evt -> {
				switch(combo.getValue()) {
				case "GDPR-sklearn":
					selectedEPackage = ePackage;
					displayedSummary = gdprSkiLearnModelEvaluator.evaluateModel(ePackage);
					currentModelEvaluator = gdprSkiLearnModelEvaluator;
					summaryGrid.setItems(displayedSummary.getEvaluatedTerms());
					summaryLayout.setVisible(true);
					break;
				case "GDPR-spacy":
					selectedEPackage = ePackage;
					displayedSummary = gdprSpacyModelEvaluator.evaluateModel(ePackage);
					currentModelEvaluator = gdprSpacyModelEvaluator;
					summaryGrid.setItems(displayedSummary.getEvaluatedTerms());
					summaryLayout.setVisible(true);
					break;
				case "OTHER", "OPEN_DATA": default:
					break;
				}
			});

			combo.setItems(EVALUATION_MODELS);
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
