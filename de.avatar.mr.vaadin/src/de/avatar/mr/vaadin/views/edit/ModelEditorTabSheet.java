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

import java.util.Map;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabSheet;

/**
 * 
 * @author ilenia
 * @since Oct 5, 2023
 */
public class ModelEditorTabSheet extends Div {

	/** serialVersionUID */
	private static final long serialVersionUID = -6651540304263357653L;
	
	public ModelEditorTabSheet(Map<String,? extends Div> contentMap) {
		TabSheet tabSheet = new TabSheet();
		contentMap.forEach((name, content) -> {
			tabSheet.add(name, content);
		});
		add(tabSheet);
	}

}
