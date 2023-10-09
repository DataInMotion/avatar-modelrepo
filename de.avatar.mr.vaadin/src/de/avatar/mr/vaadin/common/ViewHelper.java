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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.ENamedElement;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * 
 * @author ilenia
 * @since Oct 5, 2023
 */
public class ViewHelper {
	
	private static final String MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	
	public static String extractElementName(ENamedElement element) {
		String attributeName = "";
		String[] attributeNames = splitCamelCase(element.getName());
		for(String name : attributeNames) {
			attributeName += name + " ";
		}
		attributeName = attributeName.strip();
		if(!attributeName.isEmpty()) attributeName = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return attributeName;
	}
	
	public static String[] splitCamelCase(String input) {
		return input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
	}
	
	public static Date toDate(LocalDate localDate) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
		return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
	}
	
	public static LocalDate toLocalDate(Date date) {
		return  date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static void removeComponentChildrenByClassName(Component component, String className) {
		component.getChildren()
		.filter(c -> c.getElement().getClassList().contains(className))
		.forEach(c -> {
			if(component instanceof HasComponents hasComponents) hasComponents.remove(c);
		});
	}
	
	public static String extractElementDocumentation(ENamedElement element) {
		EAnnotation annotation = element.getEAnnotation(MODEL_ANNOTATION_SOURCE);
		if(annotation == null) return null;
		if(!annotation.getDetails().containsKey("documentation")) {
			return null;
		}
		return annotation.getDetails().get("documentation");
	}

}
