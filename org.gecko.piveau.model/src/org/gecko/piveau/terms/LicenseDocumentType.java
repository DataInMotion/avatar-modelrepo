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
package org.gecko.piveau.terms;

import org.eclipse.emf.common.util.EList;

import org.gecko.piveau.rdf.PlainLiteral;
import org.gecko.piveau.rdf.Resource;

import org.gecko.piveau.skos.Concept;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>License Document Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.terms.LicenseDocumentType#getType <em>Type</em>}</li>
 *   <li>{@link org.gecko.piveau.terms.LicenseDocumentType#getTitle <em>Title</em>}</li>
 *   <li>{@link org.gecko.piveau.terms.LicenseDocumentType#getDescription <em>Description</em>}</li>
 *   <li>{@link org.gecko.piveau.terms.LicenseDocumentType#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link org.gecko.piveau.terms.LicenseDocumentType#getNodeID <em>Node ID</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType()
 * @model extendedMetaData="name='LicenseDocument_._type' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface LicenseDocumentType extends Resource {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.skos.Concept}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' containment reference list.
	 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType_Type()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='type' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Concept> getType();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.rdf.PlainLiteral}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' containment reference list.
	 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType_Title()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='title' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PlainLiteral> getTitle();

	/**
	 * Returns the value of the '<em><b>Description</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.rdf.PlainLiteral}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' containment reference list.
	 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType_Description()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='description' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PlainLiteral> getDescription();

	/**
	 * Returns the value of the '<em><b>Identifier</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.rdf.PlainLiteral}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identifier</em>' containment reference list.
	 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType_Identifier()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='identifier' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PlainLiteral> getIdentifier();

	/**
	 * Returns the value of the '<em><b>Node ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node ID</em>' attribute.
	 * @see #setNodeID(String)
	 * @see org.gecko.piveau.terms.TermsPackage#getLicenseDocumentType_NodeID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='nodeID' namespace='http://www.w3.org/1999/02/22-rdf-syntax-ns#'"
	 * @generated
	 */
	String getNodeID();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.terms.LicenseDocumentType#getNodeID <em>Node ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node ID</em>' attribute.
	 * @see #getNodeID()
	 * @generated
	 */
	void setNodeID(String value);

} // LicenseDocumentType
