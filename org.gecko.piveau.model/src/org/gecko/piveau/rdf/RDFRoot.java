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
package org.gecko.piveau.rdf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

import org.eclipse.emf.ecore.xml.type.AnyType;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>RDF Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getType <em>Type</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getAbout <em>About</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getDatatype <em>Datatype</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getNodeID <em>Node ID</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getResource <em>Resource</em>}</li>
 *   <li>{@link org.gecko.piveau.rdf.RDFRoot#getRDF <em>RDF</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
@ProviderType
public interface RDFRoot extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_Mixed()
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
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_XMLNSPrefixMap()
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
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' containment reference.
	 * @see #setType(Resource)
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_Type()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='type' namespace='##targetNamespace'"
	 * @generated
	 */
	Resource getType();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.rdf.RDFRoot#getType <em>Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' containment reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(Resource value);

	/**
	 * Returns the value of the '<em><b>About</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>About</em>' attribute.
	 * @see #setAbout(String)
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_About()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='about' namespace='##targetNamespace'"
	 * @generated
	 */
	String getAbout();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.rdf.RDFRoot#getAbout <em>About</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>About</em>' attribute.
	 * @see #getAbout()
	 * @generated
	 */
	void setAbout(String value);

	/**
	 * Returns the value of the '<em><b>Datatype</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Datatype</em>' attribute.
	 * @see #setDatatype(String)
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_Datatype()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='datatype' namespace='##targetNamespace'"
	 * @generated
	 */
	String getDatatype();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.rdf.RDFRoot#getDatatype <em>Datatype</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Datatype</em>' attribute.
	 * @see #getDatatype()
	 * @generated
	 */
	void setDatatype(String value);

	/**
	 * Returns the value of the '<em><b>Node ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node ID</em>' attribute.
	 * @see #setNodeID(String)
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_NodeID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='nodeID' namespace='##targetNamespace'"
	 * @generated
	 */
	String getNodeID();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.rdf.RDFRoot#getNodeID <em>Node ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node ID</em>' attribute.
	 * @see #getNodeID()
	 * @generated
	 */
	void setNodeID(String value);

	/**
	 * Returns the value of the '<em><b>Resource</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource</em>' attribute.
	 * @see #setResource(String)
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_Resource()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='resource' namespace='##targetNamespace'"
	 * @generated
	 */
	String getResource();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.rdf.RDFRoot#getResource <em>Resource</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource</em>' attribute.
	 * @see #getResource()
	 * @generated
	 */
	void setResource(String value);

	/**
	 * Returns the value of the '<em><b>RDF</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.xml.type.AnyType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>RDF</em>' containment reference list.
	 * @see org.gecko.piveau.rdf.RdfPackage#getRDFRoot_RDF()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='RDF' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<AnyType> getRDF();

} // RDFRoot
