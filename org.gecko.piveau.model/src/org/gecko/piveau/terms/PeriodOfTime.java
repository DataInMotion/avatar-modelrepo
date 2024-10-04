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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Period Of Time</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.piveau.terms.PeriodOfTime#getPeriodOfTime <em>Period Of Time</em>}</li>
 * </ul>
 *
 * @see org.gecko.piveau.terms.TermsPackage#getPeriodOfTime()
 * @model extendedMetaData="name='PeriodOfTime' kind='elementOnly'"
 * @generated
 */
@ProviderType
public interface PeriodOfTime extends EObject {
	/**
	 * Returns the value of the '<em><b>Period Of Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Period Of Time</em>' containment reference.
	 * @see #setPeriodOfTime(PeriodOfTimeType)
	 * @see org.gecko.piveau.terms.TermsPackage#getPeriodOfTime_PeriodOfTime()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='PeriodOfTime' namespace='##targetNamespace'"
	 * @generated
	 */
	PeriodOfTimeType getPeriodOfTime();

	/**
	 * Sets the value of the '{@link org.gecko.piveau.terms.PeriodOfTime#getPeriodOfTime <em>Period Of Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Period Of Time</em>' containment reference.
	 * @see #getPeriodOfTime()
	 * @generated
	 */
	void setPeriodOfTime(PeriodOfTimeType value);

} // PeriodOfTime
