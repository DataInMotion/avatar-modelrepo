/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.rest.application.resource;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
@ObjectClassDefinition
public @interface ModelResourceConfig {

	@AttributeDefinition(description = "The path to the md class overview documentation file")
	String md_file();
	
	@AttributeDefinition(description = "The path to the md documentation with mermaid class diagrams file")
	String md_mermaid_file();
	
	@AttributeDefinition(description = "The path to the md documentation with plantuml class diagrams file")
	String md_plantuml_file();
	
	@AttributeDefinition(description = "The path to the html class overview documentation file")
	String html_file();
	
	@AttributeDefinition(description = "The path to the html documentation with mermaid class diagrams file")
	String html_mermaid_file();
}

