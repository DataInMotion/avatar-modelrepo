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
package org.gecko.piveau.dcatde;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

import org.gecko.piveau.rdf.PlainLiteral;
import org.gecko.piveau.rdf.Resource;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>DCATDE Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.dcatde.DCATDERoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.gecko.piveau.dcatde.DCATDERoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.gecko.piveau.dcatde.DCATDERoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.gecko.piveau.dcatde.DCATDERoot#getLicenseAttributionByText <em>License Attribution By Text</em>}</li>
 *   <li>{@link org.gecko.piveau.dcatde.DCATDERoot#getContributorID <em>Contributor ID</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
@ProviderType
public interface DCATDERoot extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XMLNS Prefix Map</em>' map.
	 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot_XMLNSPrefixMap()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;" transient="true"
	 *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
	 * @generated
	 */
	EMap<String, String> getXMLNSPrefixMap();

	/**
	 * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XSI Schema Location</em>' map.
	 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>License Attribution By Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>License Attribution By Text</em>' containment reference.
	 * @see #setLicenseAttributionByText(PlainLiteral)
	 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot_LicenseAttributionByText()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='licenseAttributionByText' namespace='##targetNamespace'"
	 * @generated
	 */
	PlainLiteral getLicenseAttributionByText();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.dcatde.DCATDERoot#getLicenseAttributionByText <em>License Attribution By Text</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>License Attribution By Text</em>' containment reference.
	 * @see #getLicenseAttributionByText()
	 * @generated
	 */
	void setLicenseAttributionByText(PlainLiteral value);

	/**
	 * Returns the value of the '<em><b>Contributor ID</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contributor ID</em>' containment reference.
	 * @see #setContributorID(Resource)
	 * @see org.gecko.piveau.dcatde.DcatDEPackage#getDCATDERoot_ContributorID()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='contributorID' namespace='##targetNamespace'"
	 * @generated
	 */
	Resource getContributorID();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.dcatde.DCATDERoot#getContributorID <em>Contributor ID</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contributor ID</em>' containment reference.
	 * @see #getContributorID()
	 * @generated
	 */
	void setContributorID(Resource value);

} // DCATDERoot
