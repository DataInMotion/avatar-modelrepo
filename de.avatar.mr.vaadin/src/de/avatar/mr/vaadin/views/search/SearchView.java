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
package de.avatar.mr.vaadin.views.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.gecko.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;

import de.avatar.mr.model.documentation.provider.ModelDocumentationProvider;
import de.avatar.mr.model.documentation.provider.ModelDocumentationProviderConfig;
import de.avatar.mr.search.api.EClassifierSearchService;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.search.api.EStructuralFeatureSearchService;
import de.avatar.mr.vaadin.common.EPackageGrid;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
@Route(value = "search", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Search")
@Component(name = "SearchView", service=SearchView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class SearchView extends VerticalLayout{

	@Reference
	EPackageSearchService ePackageSearchService;

	@Reference
	EClassifierSearchService eClassifierSearchService;

	@Reference
	EStructuralFeatureSearchService eStructuralFeatureSearchService;

	@Reference
	ModelDocumentationProvider modelDocumentationProvider;

	/** serialVersionUID */
	private static final long serialVersionUID = 2866387053274392884L;
	private static final String OPTION_SEARCH_EPACKAGE = "EPackage";
	private static final String OPTION_SEARCH_ECLASSIFIER = "EClassifier";
	private static final String OPTION_SEARCH_ESTRUCTURALFEATURE = "EStructuralFeature";

	private ModelDocumentationProviderConfig modelDocProviderConfig;

	@Activate
	public void renderView() {
		setSizeFull();
		this.modelDocProviderConfig = modelDocumentationProvider.getConfig();

		RadioButtonGroup<String> optionsGroup = new RadioButtonGroup<>();
		optionsGroup.setLabel("Search");
		optionsGroup.setItems(OPTION_SEARCH_EPACKAGE, OPTION_SEARCH_ECLASSIFIER, OPTION_SEARCH_ESTRUCTURALFEATURE);
		optionsGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

		HorizontalLayout searchLayout = new HorizontalLayout();
		searchLayout.setWidthFull();
		searchLayout.setAlignItems(Alignment.BASELINE);
		TextField searchField = new TextField("Enter search");
		TextField typeSearchField = new TextField("Enter feature type");
		typeSearchField.setVisible(false);
		Button searchBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchBtn.setEnabled(false);
		Button clearBtn = new Button(new Icon(VaadinIcon.ERASER));

		searchLayout.add(searchField, typeSearchField, searchBtn, clearBtn);		

		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.setWidthFull();
		gridLayout.setVisible(false);
		EPackageGrid ePackageGrid = new EPackageGrid();
		ePackageGrid.setClassName("grid-component");

		EClassifierGrid eClassifierGrid = new EClassifierGrid();
		eClassifierGrid.setClassName("grid-component");

		EStructuralFeatureGrid eStructuralFeatureGrid = new EStructuralFeatureGrid();		
		eStructuralFeatureGrid.setClassName("grid-component");


		HorizontalLayout docLayout = new HorizontalLayout();
		docLayout.setSizeFull();
		docLayout.setVisible(false);
		Element html = new Element("object");
		html.setAttribute("type", "text/html");
		html.getStyle().set("display", "block");
		html.getStyle().set("width", "100%");
		html.getStyle().set("height", "100%");

		add(optionsGroup, searchLayout, gridLayout, docLayout);

		//		Listeners
		optionsGroup.addValueChangeListener(evt -> {
			docLayout.setVisible(false);
			docLayout.removeAll();
			typeSearchField.setValue("");
			searchField.setValue("");
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});
			gridLayout.setVisible(false);
			if(evt.getValue() == null) {
				typeSearchField.setVisible(false);
				searchBtn.setEnabled(false);
				return;
			}
			searchBtn.setEnabled(true);
			switch(evt.getValue()) {
			case OPTION_SEARCH_EPACKAGE: case OPTION_SEARCH_ECLASSIFIER:
				typeSearchField.setVisible(false);
				return;
			case OPTION_SEARCH_ESTRUCTURALFEATURE:
				typeSearchField.setVisible(true);
				return;
			}
		});

		clearBtn.addClickListener(evt -> {
			optionsGroup.setValue(null);
			typeSearchField.setValue("");
			searchField.setValue("");
			typeSearchField.setVisible(false);
			searchBtn.setEnabled(false);
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});
			gridLayout.setVisible(false);
			docLayout.setVisible(false);
			docLayout.removeAll();
		});

		searchBtn.addClickListener(evt -> {
			gridLayout.getChildren()
			.filter(c -> c.getElement().getClassList().contains("grid-component"))
			.forEach(c -> {
				gridLayout.remove(c);
			});
			docLayout.setVisible(false);
			docLayout.removeAll();
			switch(optionsGroup.getValue()) {			
			case OPTION_SEARCH_EPACKAGE:
				List<EPackage> ePackages = ePackageSearchService.searchEPackages(searchField.getValue());
				ePackageGrid.setItems(ePackages);
				gridLayout.add(ePackageGrid);
				break;
			case OPTION_SEARCH_ECLASSIFIER:
				List<EClassifier> eClassifiers = eClassifierSearchService.searchEClassifiersByName(searchField.getValue());
				eClassifierGrid.setItems(eClassifiers);
				gridLayout.add(eClassifierGrid);
				break;
			case OPTION_SEARCH_ESTRUCTURALFEATURE:
				List<EStructuralFeature> eStructuralFeatures = 
				eStructuralFeatureSearchService.searchEStructuralFeaturesByNameAndType(searchField.getValue(), typeSearchField.getValue());
				eStructuralFeatureGrid.setItems(eStructuralFeatures);
				gridLayout.add(eStructuralFeatureGrid);
				break;
			}
			gridLayout.setVisible(true);
		});

		ePackageGrid.addSelectionListener(evt ->  {
			EPackage ePackage = evt.getFirstSelectedItem().orElse(null);
			if(ePackage != null) {
				String docFilePath = Paths.get(modelDocProviderConfig.output_root_folder(), modelDocProviderConfig.output_html_mermaid_folder(), 
						ePackage.getName().concat(".html")).toString();
				if(modelDocumentationProvider.retrieveDocumentation(docFilePath, true, ePackage, EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM) != null) {
					File docFile = new File(docFilePath);
					StreamResource streamRes = createReportResource(docFile);			
					html.setAttribute("data", streamRes);
					docLayout.setVisible(true);
					docLayout.removeAll();
					docLayout.getElement().appendChild(html);
				}
				else {
					Notification.show("Error generating documentation for EPackage " + ePackage.getName()).addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
		});

		eClassifierGrid.addSelectionListener(evt ->  {
			EClassifier eClassifier = evt.getFirstSelectedItem().orElse(null);
			if(eClassifier != null) {
				String docFilePath = Paths.get(modelDocProviderConfig.output_root_folder(), modelDocProviderConfig.output_html_mermaid_folder(), 
						eClassifier.getEPackage().getName().concat("_").concat(eClassifier.getName()).concat(".html")).toString();

				if(modelDocumentationProvider.retrieveDocumentation(docFilePath, true, eClassifier, EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM) != null) {
					File docFile = new File(docFilePath);
					StreamResource streamRes = createReportResource(docFile);			
					html.setAttribute("data", streamRes);
					docLayout.setVisible(true);
					docLayout.removeAll();
					docLayout.getElement().appendChild(html);
				}
				else {
					Notification.show("Error generating documentation for EPackage " + eClassifier.getName()).addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
		});
	}

	private StreamResource createReportResource(File docFile) {
		StreamResource res = new StreamResource(docFile.getName(), () -> {
			try {
				return new FileInputStream(docFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
		res.setContentType("text/html");
		return res;
	}

}
