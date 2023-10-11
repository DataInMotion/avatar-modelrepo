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
package de.avatar.mr.vaadin.common;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/**
 * 
 * @author ilenia
 * @since Oct 5, 2023
 */
public class EObjectGrid extends Grid<EObject> {

	/** serialVersionUID */
	private static final long serialVersionUID = -2071400292903820306L;

	public EObjectGrid(EClass eClass) {
		eClass.getEAllAttributes().forEach(attribute -> {
			String attributeName = ViewHelper.extractElementName(attribute);
			EClassifier attributeType = attribute.getEType();
//			TODO: understand how to deal with proxy attribute types
//			if(attributeType.eIsProxy()) {
//				attributeType = (EClassifier) EcoreUtil.resolve(attributeType, eClass.getEPackage());
//			}
			if("EString".equals(attributeType.getName())) {
				addColumn(new ComponentRenderer<>(TextField::new, (txtField, eObj) -> {
					if(attribute.isRequired()) txtField.setRequired(true);
					String value = (String) eObj.eGet(attribute) != null ? (String) eObj.eGet(attribute) : "";
					txtField.setValue(value);
					txtField.setTooltipText(ViewHelper.createTooltip(attribute));
					txtField.addValueChangeListener(evt -> {
						eObj.eSet(attribute, evt.getValue());
					});
				})).setHeader(attributeName).setAutoWidth(true);				
			} else if("EDouble".equals(attributeType.getName())) {
				addColumn(new ComponentRenderer<>(NumberField::new, (numField, eObj) -> {
					numField.setValue((Double) eObj.eGet(attribute));
					numField.setTooltipText(ViewHelper.createTooltip(attribute));
					numField.addValueChangeListener(evt -> {
						eObj.eSet(attribute, evt.getValue());
					});
				})).setHeader(attributeName).setAutoWidth(true);		
			} else if("EDate".equals(attributeType.getName())) {
				addColumn(new ComponentRenderer<>(DatePicker::new, (dateField, eObj) -> {
					if(attribute.isRequired()) dateField.setRequired(true);
					dateField.setValue(ViewHelper.toLocalDate((Date) eObj.eGet(attribute)));
					dateField.setTooltipText(ViewHelper.createTooltip(attribute));
					dateField.addValueChangeListener(evt -> {
						eObj.eSet(attribute, ViewHelper.toDate(evt.getValue()));
					});
				})).setHeader(attributeName).setAutoWidth(true);	
			} else if(attributeType instanceof EEnum enumerator) {
				addColumn(new ComponentRenderer<>(() -> new ComboBox<String>(), (comboField, eObj) -> {
					if(attribute.isRequired()) comboField.setRequired(true);
					comboField.setItems(enumerator.getELiterals().stream().map(literal-> literal.getName()).toList());
					comboField.setValue(((Enumerator) eObj.eGet(attribute)).getLiteral());
					comboField.setTooltipText(ViewHelper.createTooltip(attribute));
					comboField.addValueChangeListener(evt -> {
						Object obj = EcoreUtil.createFromString((EDataType) attributeType, evt.getValue());
						eObj.eSet(attribute, obj);
					});
				})).setHeader(attributeName).setAutoWidth(true);
			}
		});
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<EObject> setItems(Collection<EObject> items) {
		if(items.isEmpty()) {
			setVisible(false);
		}
		else setVisible(true);
		return super.setItems(items);
	}

}
