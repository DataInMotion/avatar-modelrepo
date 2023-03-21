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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.search.api.EClassifierSearchService;
import de.avatar.mr.search.helper.SearchHelper;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
@Component(name = "EClassifierSearchService", service = EClassifierSearchService.class)
public class EClassifierSearchServiceImpl implements EClassifierSearchService {

	@Reference(target = "(id=ePackage)")
	private ComponentServiceObjects<IndexSearcher> searcherSO;
		
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EClassifierSearchService#searchEClassifiersByName(java.lang.String)
	 */
	@Override
	public List<EClassifier> searchEClassifiersByName(String query) {
		Objects.requireNonNull(query, "Cannot search EClassifier with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder internalBuilder = new BooleanQuery.Builder();			
		List<Query> nameQueryList = SearchHelper.createQueries(EPackageIndexService.ECLASSIFIER_NAME_LOWER, query.toLowerCase());
		List<Query> aliasQueryList = SearchHelper.createQueries(EPackageIndexService.ECLASSIFIER_ALIAS_LOWER, query.toLowerCase());
		nameQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		aliasQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		
		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EClassifier")), Occur.MUST);
		queryBuilder.add(internalBuilder.build(), Occur.MUST);

		return executeSearch(queryBuilder.build());	
	}
	
	private List<EClassifier> executeSearch(Query query) {
		IndexSearcher searcher = searcherSO.getService();
		try {
			List<Document> docs = SearchHelper.doExecuteSearch(query, searcher);
			List<EClassifier> result = new ArrayList<>();
			docs.forEach(document -> {
				String classifierName = document.get(EPackageIndexService.ECLASSIFIER_NAME);
				String ePackageURI = document.get(EPackageIndexService.EPACKAGE_NS_URI);
				if(classifierName == null || ePackageURI == null) throw new RuntimeException("Index Document Attributes are null!");
				EPackage.Registry ePackageRegistry = EPackage.Registry.INSTANCE;
				EPackage ePackage = ePackageRegistry.getEPackage(ePackageURI);
				if(ePackage == null) {
					throw new RuntimeException(String.format("Cannot find EPackage %s for matching EClassifier %s!", ePackageURI, classifierName));
				}
				result.add(ePackage.getEClassifier(classifierName));
			});
			return result;
		} finally {
			searcherSO.ungetService(searcher);
		}
		
	}
}
