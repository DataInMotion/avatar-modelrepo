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
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EPackage;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
public class EPackageGrid extends Grid<EPackage> {
	
	Predicate<String> filter = s -> s.startsWith("https://geckoprojects.org") || 
			s.startsWith("http://avatar.de/mdp/evaluation") || s.startsWith("http://avatar.de/mdp/privacyrelatedmeta");

	/** serialVersionUID */
	private static final long serialVersionUID = -8884867020877692130L;
	
	public EPackageGrid() {
		addColumn(EPackage::getName).setHeader("Name").setAutoWidth(true);
		addColumn(EPackage::getNsURI).setHeader("Ns URI").setAutoWidth(true);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.HasListDataView#setItems(java.util.Collection)
	 */
	@Override
	public GridListDataView<EPackage> setItems(Collection<EPackage> items) {
		items = items.stream().filter(p -> !filter.test(p.getNsURI())).toList();
		if(items.isEmpty()) {
			setVisible(false);
			Notification.show("No item matching your search criteria has been found").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
		}
		else setVisible(true);
		return super.setItems(items);
	}
	
	

}
