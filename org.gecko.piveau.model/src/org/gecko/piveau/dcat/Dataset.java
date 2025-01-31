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

import java.math.BigDecimal;

import javax.xml.datatype.Duration;

import org.eclipse.emf.common.util.EList;

import org.gecko.piveau.dcatde.ContributorID;

import org.gecko.piveau.rdf.Resource;

import org.gecko.piveau.skos.Concept;

import org.gecko.piveau.terms.Location;
import org.gecko.piveau.terms.PeriodOfTime;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dataset</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 *         dcat:Dataset represents a dataset. A dataset is a collection of data, published or curated by a single agent.
 *         Data comes in many forms including numbers, words, pixels, imagery, sound and other multi-media, and potentially
 *         other types, any of which might be collected into a dataset.
 *       
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getDistribution <em>Distribution</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getSpatialResolutionInMeters <em>Spatial Resolution In Meters</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getTemporalResolution <em>Temporal Resolution</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getAccrualPeriodicity <em>Accrual Periodicity</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getSpatial <em>Spatial</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getTemporal <em>Temporal</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getWasGeneratedBy <em>Was Generated By</em>}</li>
 *   <li>{@link org.gecko.piveau.dcat.Dataset#getContributorID <em>Contributor ID</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.dcat.DcatPackage#getDataset()
 * @model extendedMetaData="name='Dataset' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface Dataset extends DcatResource {
	/**
	 * Returns the value of the '<em><b>Distribution</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.rdf.Resource}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Distribution</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_Distribution()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='distribution' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Resource> getDistribution();

	/**
	 * Returns the value of the '<em><b>Spatial Resolution In Meters</b></em>' attribute list.
	 * The list contents are of type {@link java.math.BigDecimal}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spatial Resolution In Meters</em>' attribute list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_SpatialResolutionInMeters()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.Decimal"
	 *        extendedMetaData="kind='element' name='spatialResolutionInMeters' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<BigDecimal> getSpatialResolutionInMeters();

	/**
	 * Returns the value of the '<em><b>Temporal Resolution</b></em>' attribute list.
	 * The list contents are of type {@link javax.xml.datatype.Duration}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temporal Resolution</em>' attribute list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_TemporalResolution()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.Duration"
	 *        extendedMetaData="kind='element' name='temporalResolution' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Duration> getTemporalResolution();

	/**
	 * Returns the value of the '<em><b>Accrual Periodicity</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Accrual Periodicity</em>' containment reference.
	 * @see #setAccrualPeriodicity(Concept)
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_AccrualPeriodicity()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='accrualPeriodicity' namespace='http://purl.org/dc/terms/'"
	 * @generated
	 */
	Concept getAccrualPeriodicity();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.dcat.Dataset#getAccrualPeriodicity <em>Accrual Periodicity</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Accrual Periodicity</em>' containment reference.
	 * @see #getAccrualPeriodicity()
	 * @generated
	 */
	void setAccrualPeriodicity(Concept value);

	/**
	 * Returns the value of the '<em><b>Spatial</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.terms.Location}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spatial</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_Spatial()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='spatial' namespace='http://purl.org/dc/terms/'"
	 * @generated
	 */
	EList<Location> getSpatial();

	/**
	 * Returns the value of the '<em><b>Temporal</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.piveau.terms.PeriodOfTime}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temporal</em>' containment reference list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_Temporal()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='temporal' namespace='http://purl.org/dc/terms/'"
	 * @generated
	 */
	EList<PeriodOfTime> getTemporal();

	/**
	 * Returns the value of the '<em><b>Was Generated By</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Was Generated By</em>' attribute list.
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_WasGeneratedBy()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='element' name='wasGeneratedBy' namespace='http://www.w3.org/ns/prov#'"
	 * @generated
	 */
	EList<String> getWasGeneratedBy();

	/**
	 * Returns the value of the '<em><b>Contributor ID</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contributor ID</em>' containment reference.
	 * @see #setContributorID(ContributorID)
	 * @see org.gecko.piveau.dcat.DcatPackage#getDataset_ContributorID()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' namespace='http://dcat-ap.de/def/dcatde/'"
	 * @generated
	 */
	ContributorID getContributorID();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.dcat.Dataset#getContributorID <em>Contributor ID</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contributor ID</em>' containment reference.
	 * @see #getContributorID()
	 * @generated
	 */
	void setContributorID(ContributorID value);

} // Dataset
