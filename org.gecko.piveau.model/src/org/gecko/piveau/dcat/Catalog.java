/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Data In Motion - initial API and implementation
 */
package org.gecko.piveau.dcat;

import org.eclipse.emf.common.util.EList;

import org.gecko.piveau.rdf.Resource;

import org.gecko.piveau.skos.ConceptScheme;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Catalog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 *         dcat:Catalog represents a catalog, which is a dataset in which
 *         each individual item is a metadata record
 *         describing some resource; the scope of dcat:Catalog
 *         is collections of metadata about datasets or data services.
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getRecord <em>Record</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getDataset <em>Dataset</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getService <em>Service</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getThemeTaxonomy <em>Theme Taxonomy</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getHasPart <em>Has Part</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Catalog#getHomepage <em>Homepage</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog()
 * @model extendedMetaData="name='Catalog' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Catalog extends Dataset {
	/**
	 * Returns the value of the '<em><b>Catalog</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.dcat.Catalog}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Catalog</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_Catalog()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='catalog' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Catalog> getCatalog();

	/**
	 * Returns the value of the '<em><b>Record</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.dcat.CatalogRecord}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Record</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_Record()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='record' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<CatalogRecord> getRecord();

	/**
	 * Returns the value of the '<em><b>Dataset</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.dcat.DatasetContainer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dataset</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_Dataset()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='dataset' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<DatasetContainer> getDataset();

	/**
	 * Returns the value of the '<em><b>Service</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.dcat.DataService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_Service()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='service' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<DataService> getService();

	/**
	 * Returns the value of the '<em><b>Theme Taxonomy</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.skos.ConceptScheme}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Theme Taxonomy</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_ThemeTaxonomy()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='themeTaxonomy' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ConceptScheme> getThemeTaxonomy();

	/**
	 * Returns the value of the '<em><b>Has Part</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.rdf.Resource}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Has Part</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_HasPart()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='hasPart' namespace='http://purl.org/dc/terms/'"
	 * @generated
	 */
	EList<Resource> getHasPart();

	/**
	 * Returns the value of the '<em><b>Homepage</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Homepage</em>' containment reference.
	 * @see #setHomepage(Resource)
	 * @see org.gecko.piveau.dcat.DcatPackage#getCatalog_Homepage()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='homepage' namespace='http://xmlns.com/foaf/0.1/'"
	 * @generated
	 */
	Resource getHomepage();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.dcat.Catalog#getHomepage <em>Homepage</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Homepage</em>' containment reference.
	 * @see #getHomepage()
	 * @generated
	 */
	void setHomepage(Resource value);

} // Catalog
