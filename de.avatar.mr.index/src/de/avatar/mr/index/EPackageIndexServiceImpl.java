/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.index;

import java.io.IOException;

import org.eclipse.emf.ecore.EPackage;
import org.gecko.emf.search.document.EObjectDocumentIndexObjectContext;
import org.gecko.search.IndexActionType;
import org.gecko.search.document.LuceneIndexService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.index.helper.EPackageIndexHelper;

@Component(name="EPackageIndexService", service = EPackageIndexService.class, scope = ServiceScope.SINGLETON)
public class EPackageIndexServiceImpl implements EPackageIndexService{

	@Reference(target = "(id=ePackage)")
	private LuceneIndexService<EObjectDocumentIndexObjectContext> ePackageIndex;
	
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.index.api.EPackageIndexService#indexEPackage(org.eclipse.emf.ecore.EPackage, boolean)
	 */
	@Override
	public void indexEPackage(EPackage ePackage, boolean add) {	
		if(add) {
			indexEPackage(ePackage, IndexActionType.ADD);
		}
		else {
			indexEPackage(ePackage, IndexActionType.MODIFY);
		}			
	}

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.index.api.EPackageIndexService#deleteEPackage(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	public void deleteEPackage(EPackage ePackage) {
		indexEPackage(ePackage, IndexActionType.REMOVE);		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.index.api.EPackageIndexService#resetIndex()
	 */
	@Override
	public void resetIndex() {
		try {
			ePackageIndex.getIndexWriter().deleteAll();
			ePackageIndex.commit();
		} catch (IOException e) {
			System.err.println("Could not delete EPackage index " + e);
		}		
		
	}
	
	private void indexEPackage(EPackage EPackage, IndexActionType actionType) {
		EObjectDocumentIndexObjectContext context = EPackageIndexHelper.mapEPackage(EPackage, actionType);			
		ePackageIndex.handleContextSync(context);
	}

}
