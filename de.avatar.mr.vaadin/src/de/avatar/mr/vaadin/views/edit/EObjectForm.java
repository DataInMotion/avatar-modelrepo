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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import de.avatar.mr.vaadin.common.EObjectGrid;
import de.avatar.mr.vaadin.common.ViewHelper;

/**
 * 
 * @author ilenia
 * @since Oct 5, 2023
 */
public class EObjectForm extends Div {

	/** serialVersionUID */
	private static final long serialVersionUID = 2822781093743108470L;
	private static final Logger LOGGER = Logger.getLogger(EObjectForm.class.getName());
	private EObject eObj;
	private List<EObject> eObjs = new ArrayList<>();
	private Component layout;

	public EObjectForm(EClass eClass, boolean isMany) {
		setSizeFull();
		if(isMany) {
			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.setSizeFull();
			EObjectGrid eObjGrid = new EObjectGrid(eClass);
			eObjGrid.setVisible(false);
			
			Button addBtn = new Button("Add " + ViewHelper.extractElementName(eClass), evt -> {
				EObject eObj = EcoreUtil.create(eClass);
				Dialog dialog = createEObjectDialog(eObj);
				dialog.open();	
				dialog.addOpenedChangeListener(e -> {
					if(!e.getSource().isOpened()) {
						eObjGrid.setItems(eObjs);
					}
				});
			});
			mainLayout.add(addBtn, eObjGrid);
			add(mainLayout);
			layout = mainLayout;
		} else {
			EObject eObj = EcoreUtil.create(eClass);
			this.eObj = eObj;
			layout = createEObjectFormLayout(eObj);
			add(layout);
		}
	}

	public Component getMainLayout() {
		return layout;
	}

	public EObject getEObject() {
		return eObj;
	}
	
	public List<EObject> getEObjects() {
		return eObjs;
	}
	
	private FormLayout createEObjectFormLayout(EObject eObj) {
		FormLayout formLayout = new FormLayout();
		formLayout.setSizeFull();
		eObj.eClass().getEAllAttributes().forEach(attribute -> {
			Component component = createFormComponent(attribute, eObj);
			if(component != null) formLayout.add(component);
		});

		formLayout.setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 2));
		return formLayout;
	}
	
	private Component createFormComponent(EAttribute attribute, EObject eObj) {
		
		String attributeName = ViewHelper.extractElementName(attribute);
		if(attribute.isMany()) {
			LOGGER.warning(String.format("Attribute %s has cardinality multiple, which is currently not supported", attributeName));
			return null;
		}
		EClassifier attributeType = attribute.getEType();
//		TODO: understand how to deal with proxy attribute types
		if(attributeType.eIsProxy()) {
			attributeType = (EClassifier) EcoreUtil.resolve(attributeType, eObj.eClass().getEPackage());
		}
		if("EString".equals(attributeType.getName())) {
			TextField txtField = new TextField(attributeName);
			txtField.setTooltipText(ViewHelper.extractElementDocumentation(attribute));
			txtField.addValueChangeListener(evt -> {
				eObj.eSet(attribute, evt.getValue());
			});
			return txtField;
		} else if("EDouble".equals(attributeType.getName())) {
			NumberField numField = new NumberField(attributeName);
			numField.setTooltipText(ViewHelper.extractElementDocumentation(attribute));
			numField.addValueChangeListener(evt -> {
				eObj.eSet(attribute, evt.getValue());
			});
			return numField;
		} else if("EDate".equals(attributeType.getName())) {
			DatePicker dateField = new DatePicker(attributeName);
			dateField.setTooltipText(ViewHelper.extractElementDocumentation(attribute));
			dateField.addValueChangeListener(evt -> {
				eObj.eSet(attribute, ViewHelper.toDate(evt.getValue()));
			});
			return dateField;
		} else if(attributeType instanceof EEnum enumerator) {
			ComboBox<String> comboField = new ComboBox<>(attributeName);
			comboField.setTooltipText(ViewHelper.extractElementDocumentation(attribute));
			comboField.setItems(enumerator.getELiterals().stream().map(literal-> literal.getName()).toList());
			comboField.addValueChangeListener(evt -> {
				EDataType dataType = (EDataType) attribute.getEType();
				Object obj = EcoreUtil.createFromString(dataType, evt.getValue());
				eObj.eSet(attribute, obj);
			});
			return comboField;
		}
		LOGGER.warning(String.format("Attribute %s is of type %s, which is currently not supported", attributeName, attributeType.getName()));
		return null;
	}

	private Dialog createEObjectDialog(EObject eObj) {
		Dialog dialog = new Dialog();
		dialog.setWidth("70%");
		dialog.setHeight("70%");
		dialog.setHeaderTitle("Add " + ViewHelper.extractElementName(eObj.eClass()));
		
		FormLayout formLayout = createEObjectFormLayout(eObj);
		dialog.add(formLayout);
		
		Button cancelButton = new Button("Cancel", e -> dialog.close());
		Button saveButton = new Button("Save", e -> {
			eObjs.add(eObj);
			dialog.close();
		});
		dialog.getFooter().add(cancelButton);
    	dialog.getFooter().add(saveButton);
		return dialog;
	}
}
