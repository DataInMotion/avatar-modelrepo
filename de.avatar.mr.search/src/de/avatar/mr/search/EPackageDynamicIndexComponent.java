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
package de.avatar.mr.search;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.search.helper.SearchHelper;

/**
 * 
 * @author ilenia
 * @since Mar 21, 2023
 */
@Component(immediate = true, name = "EPackageDynamicIndexComponent", service = EPackageDynamicIndexComponent.class )
public class EPackageDynamicIndexComponent {
	
	@Reference
	EPackageIndexService ePackageIndexService;
	
	@Reference(target = "(id=ePackage)", cardinality = ReferenceCardinality.MANDATORY)
	private ComponentServiceObjects<IndexSearcher> searcherSO;
	
	private Set<String> ePackagesSet;
	
	public void activate() {
		ePackagesSet = new HashSet<>();
		IndexSearcher searcher = searcherSO.getService();
		try {
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();	
			queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EPackage")), Occur.MUST);
			List<Document> docs = SearchHelper.doExecuteSearch(queryBuilder.build(), searcher);
			docs.forEach(d -> {
				if(d.get(EPackageIndexService.EPACKAGE_NS_URI) != null) {
					ePackagesSet.add(d.get(EPackageIndexService.EPACKAGE_NS_URI));
				}		
			});
		} finally {
			searcherSO.ungetService(searcher);
		}
		System.out.println("Finished activating Dynamic Index Component! " + ePackagesSet.size());
	}


	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, 
			policyOption = ReferencePolicyOption.GREEDY, unbind = "deleteEPackage")
	public void indexEPackage(EPackage ePackage, Map<String, Object> properties) {
		if(ePackagesSet == null) activate();
		boolean add = ePackagesSet.add(ePackage.getNsURI());
		System.out.println("Adding ePackage " + ePackage.getName() + " " + add);
		ePackageIndexService.indexEPackage(ePackage, add);
	}
	
	public void deleteEPackage(EPackage ePackage) {
//		We do not want to remove the EPackage from the index here
	}
	
}
