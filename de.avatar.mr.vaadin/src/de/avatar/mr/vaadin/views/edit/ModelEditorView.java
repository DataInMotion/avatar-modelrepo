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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.avatar.mdp.apis.api.EObjectEvaluator;
import de.avatar.mdp.evaluation.EvaluationSummary;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.vaadin.common.EPackageGrid;
import de.avatar.mr.vaadin.common.ViewHelper;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Oct 5, 2023
 */
@Route(value = "editor", layout = MainView.class)
@PageTitle("Editor")
@Component(name = "ModelEditorView", service=ModelEditorView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class ModelEditorView extends VerticalLayout {
	
	@Reference
	EPackageSearchService ePackageSearchService;
	
	@Reference(target = "(&(component.name=GDPREObjectEvaluator)(modelName=ner_fake_pii_generator))")
	EObjectEvaluator gdprEObjectEvaluator;	
	

	/** serialVersionUID */
	private static final long serialVersionUID = 7815473134149106934L;
	private EClass eClassToCreate;
	
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
		
		VerticalLayout editorLayout = new VerticalLayout();
		editorLayout.setSizeFull();
		editorLayout.setVisible(false);
		add(searchLayout, gridLayout, editorLayout);
		
//		Listeners
		clearBtn.addClickListener(evt -> {
			searchField.setValue("");
			ViewHelper.removeComponentChildrenByClassName(gridLayout, "grid-component");
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
			Button btn = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
			btn.addClickListener(evt -> {
				Dialog dialog = createEClassDialog(ePackage);
				dialog.open();
				dialog.addOpenedChangeListener(e -> {
					if(!e.getSource().isOpened()) {
						if(eClassToCreate != null) {
							Map<String, EObjectForm> formContentMap = new LinkedHashMap<>();
							EObjectForm mainObjForm = new EObjectForm(eClassToCreate, false);
							if(mainObjForm.getMainLayout().getChildren().distinct().count() != 0) {
								formContentMap.put(ViewHelper.extractElementName(eClassToCreate), mainObjForm);
							}	
							eClassToCreate.getEAllReferences().forEach(ref -> {
								EObjectForm refForm = new EObjectForm(ref.getEReferenceType(), ref.isMany());
								if(refForm.getMainLayout().getChildren().distinct().count() != 0) {
									formContentMap.put(ViewHelper.extractElementName(ref), refForm);
								}	
							});
							
							ModelEditorTabSheet tabSheet = new ModelEditorTabSheet(formContentMap);
							tabSheet.setSizeFull();
							tabSheet.setClassName("editor-component");
							Button evaluateBtn = new Button("Evaluate", e2 -> {
//								Combine the object with its refs
								EObject mainEObj = mainObjForm.getEObject();
								eClassToCreate.getEAllReferences().forEach(ref -> {
									if(ref.isMany()) {
										mainEObj.eSet(ref, formContentMap.get(ViewHelper.extractElementName(ref)).getEObjects());
									} else {
										mainEObj.eSet(ref, formContentMap.get(ViewHelper.extractElementName(ref)).getEObject());
									}
								});
//								Evaluation with the model instance suggester
								EvaluationSummary evaluationSummary = gdprEObjectEvaluator.evaluateEObject(mainEObj);
								createSummaryDialog(evaluationSummary).open();
							});
							evaluateBtn.setClassName("editor-component");
							ViewHelper.removeComponentChildrenByClassName(editorLayout, "editor-component");
							editorLayout.add(tabSheet, evaluateBtn);
							editorLayout.setVisible(true);
						}	
					}
				});				
			});
			layout.add(btn);
		})).setHeader("Create Instance").setAutoWidth(true);
	}
	
	private Dialog createSummaryDialog(EvaluationSummary summary) {
		Dialog dialog = new Dialog();
    	dialog.setWidth("70%");
    	dialog.setHeight("70%");
    	dialog.setHeaderTitle("EObject Evaluation Summary");
    	
    	EObjectEvaluatedTermGrid grid = new EObjectEvaluatedTermGrid();
    	grid.setItems(summary.getEvaluatedTerms());
    	
    	dialog.add(grid);
    	Button okButton = new Button("OK", e -> dialog.close());
    	dialog.getFooter().add(okButton);
    	return dialog;
	}
	
	private Dialog createEClassDialog(EPackage ePackage) {
		Dialog dialog = new Dialog();
    	dialog.setWidth("70%");
    	dialog.setHeight("70%");
    	dialog.setHeaderTitle("Select Object to create");
    	
    	RadioButtonGroup<String> objectsGroup = new RadioButtonGroup<>("Select the EObject to create:");
    	objectsGroup.setItems(ePackage.getEClassifiers()
    			.stream()
    			.filter(ec -> ec instanceof EClass)
    			.map(ec -> ec.getName())
    			.toList());
    	objectsGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    	dialog.add(objectsGroup);
    	Button cancelButton = new Button("Cancel", e -> dialog.close());
    	Button saveButton = new Button("Confirm", e -> {
    		
    		eClassToCreate = ePackage.getEClassifiers()
    				.stream()
    				.filter(ec -> ec instanceof EClass)
    				.map(ec -> (EClass) ec)
    				.filter(ec -> ec.getName().equals(objectsGroup.getValue()))
    				.findFirst()
    				.orElse(null);
    		dialog.close();
    	});
    	dialog.getFooter().add(cancelButton);
    	dialog.getFooter().add(saveButton);
    	return dialog;
	}

}
