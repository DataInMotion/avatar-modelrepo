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

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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

import de.avatar.mdp.apis.api.PRMetaModelService;
import de.avatar.mdp.prmeta.EvaluationCriteriumType;
import de.avatar.mdp.prmeta.PRModel;
import de.avatar.mdp.prmeta.PRPackage;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.vaadin.common.EPackageGrid;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Aug 9, 2023
 */
@Route(value = "show", layout = MainView.class)
@PageTitle("Show")
@Component(name = "ModelShowView", service=ModelShowView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class ModelShowView extends VerticalLayout{

	/** serialVersionUID */
	private static final long serialVersionUID = 883893074464566608L;
	
	private static final List<String> EVALUATION_MODELS = List.of("GDPR-sklearn", "GDPR-spacy", "OPEN_DATA", "OTHER");
	
	@Reference
	EPackageSearchService ePackageSearchService;
	
	@Reference
	PRMetaModelService prMetaModelService;
	
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
		
		VerticalLayout showLayout = new VerticalLayout();
		showLayout.setSizeFull();
		showLayout.setVisible(false);
		ComboBox<String> criteriumCombo = new ComboBox<>();
		criteriumCombo.setLabel("Select the criteria:");
		criteriumCombo.setItems(EVALUATION_MODELS);
		
		PRClassifierGrid prClassifierGrid = new PRClassifierGrid();
		prClassifierGrid.setVisible(false);
		showLayout.add(criteriumCombo, prClassifierGrid);
		
		add(searchLayout, gridLayout, showLayout);
		
//		Listeners
		clearBtn.addClickListener(evt -> {
			searchField.setValue("");
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});
			selectedEPackage = null;
			showLayout.setVisible(false);
			prClassifierGrid.setItems(Collections.emptyList());
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
		
		ePackageGrid.addColumn(new ComponentRenderer<>(Button::new, (button, epackage) -> {
			button.setIcon(new Icon(VaadinIcon.EYE));
			button.addClickListener(evt -> {
				showLayout.setVisible(true);
				selectedEPackage = epackage;
			});
		})).setHeader("Inspect").setAutoWidth(true);
		
		criteriumCombo.addValueChangeListener(evt -> {
			List<PRModel> prModels = prMetaModelService.getPRModelByNsURI(selectedEPackage.getNsURI());
					
			if(prModels.isEmpty()) {
				Notification.show(String.format("EPackage %s has never been evaluated. Please, trigger an evaluation first.", selectedEPackage.getName())).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
				return;
			}
			
			PRPackage prPackage = null;
			switch(evt.getValue()) {
			case "GDPR-sklearn":
				prPackage = prModels.get(0).getPrPackage().stream()
					.filter(prp -> prp.getEvaluationCriterium().equals(EvaluationCriteriumType.GDPR) && "multilabel_v2".equals(prp.getEvaluationModelUsed())).findFirst().orElse(null);
				break;		
			case "GDPR-spacy":
				prPackage = prModels.get(0).getPrPackage().stream()
				.filter(prp -> prp.getEvaluationCriterium().equals(EvaluationCriteriumType.GDPR) && "spacy_textcat_ner_negex".equals(prp.getEvaluationModelUsed())).findFirst().orElse(null);
				break;	
			case "OPEN_DATA":
				prPackage = prModels.get(0).getPrPackage().stream().filter(prp -> prp.getEvaluationCriterium().equals(EvaluationCriteriumType.getByName(evt.getValue()))).findFirst().orElse(null);
				break;	
			}
				
			if(prPackage == null) {
				Notification.show(String.format("EPackage %s has never been evaluated according to the %s standards. We are just showing you the EPackage without evaluation.", selectedEPackage.getName(), evt.getValue())).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
				prPackage = prModels.get(0).getPrPackage().stream().filter(prp -> prp.getEvaluationCriterium().equals(EvaluationCriteriumType.NONE)).findFirst().orElse(null);
			}
			EcoreUtil.resolveAll(prPackage);
			prClassifierGrid.setItems(prPackage.getPrClassifier());
		});
	}
}
