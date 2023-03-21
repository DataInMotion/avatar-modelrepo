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

import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
public class EClassifierGrid extends Grid<EClassifier> {

	/** serialVersionUID */
	private static final long serialVersionUID = -4491254272729637947L;
	
	public EClassifierGrid() {
		addColumn(eClassifier -> {
			return eClassifier.getEPackage().getName();
		}).setHeader("EPackage Name").setAutoWidth(true);
		addColumn(eClassifier -> {
			return eClassifier.getEPackage().getNsURI();
		}).setHeader("EPackage Ns URI").setAutoWidth(true);
		addColumn(EClassifier::getName).setHeader("EClassifier Name").setAutoWidth(true);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<EClassifier> setItems(Collection<EClassifier> items) {
		if(items.isEmpty()) {
			setVisible(false);
			Notification.show("No item matching your search criteria has been found").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
		}
		else setVisible(true);
		return super.setItems(items);
	}

}
