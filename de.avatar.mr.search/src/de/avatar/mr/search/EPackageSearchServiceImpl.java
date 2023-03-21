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
import org.eclipse.emf.ecore.EPackage;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.search.api.EPackageSearchService;
import de.avatar.mr.search.helper.SearchHelper;

@Component(name="EPackageSearchService")
public class EPackageSearchServiceImpl implements EPackageSearchService {

	@Reference(target = "(id=ePackage)")
	private ComponentServiceObjects<IndexSearcher> searcherSO;

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EPackageSearchService#searchEPackagesByName(java.lang.String)
	 */
	@Override
	public List<EPackage> searchEPackagesByName(String query) {
		Objects.requireNonNull(query, "Cannot search EPackage with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder nameBuilder = new BooleanQuery.Builder();		
		List<Query> nameQueryList = SearchHelper.createQueries(EPackageIndexService.EPACKAGE_NAME, query.toLowerCase());
		nameQueryList.forEach(q -> nameBuilder.add(q, Occur.SHOULD));
		
		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EPackage")), Occur.MUST);
		queryBuilder.add(nameBuilder.build(), Occur.MUST);

		return executeSearch(queryBuilder.build());		
	}


	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EPackageSearchService#searchEPackagesByNsURI(java.lang.String)
	 */
	@Override
	public List<EPackage> searchEPackagesByNsURI(String query) {
		Objects.requireNonNull(query, "Cannot search EPackage with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder nsBuilder = new BooleanQuery.Builder();		
		List<Query> nsQueryList = SearchHelper.createQueries(EPackageIndexService.EPACKAGE_NS_URI, query.toLowerCase());
		nsQueryList.forEach(q -> nsBuilder.add(q, Occur.SHOULD));

		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EPackage")), Occur.MUST);
		queryBuilder.add(nsBuilder.build(), Occur.MUST);

		return executeSearch(queryBuilder.build());		
	}



	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EPackageSearchService#searchEPackages(java.lang.String)
	 */
	@Override
	public List<EPackage> searchEPackages(String query) {
		Objects.requireNonNull(query, "Cannot search EPackage with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder internalBuilder = new BooleanQuery.Builder();		
		List<Query> nameQueryList = SearchHelper.createQueries(EPackageIndexService.EPACKAGE_NAME, query.toLowerCase());
		List<Query> nsQueryList = SearchHelper.createQueries(EPackageIndexService.EPACKAGE_NS_URI, query.toLowerCase());
		nameQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		nsQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		
		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EPackage")), Occur.MUST);
		queryBuilder.add(internalBuilder.build(), Occur.MUST);

		return executeSearch(queryBuilder.build());		
	}
	
	private List<EPackage> executeSearch(Query query) {
		IndexSearcher searcher = searcherSO.getService();
		try {
			List<Document> docs = SearchHelper.doExecuteSearch(query, searcher);
			List<EPackage> result = new ArrayList<>();
			docs.forEach(document -> {
				String ePackageURI = document.get(EPackageIndexService.EPACKAGE_NS_URI);
				if(ePackageURI == null) throw new RuntimeException("Index Document Attributes are null!");
				EPackage.Registry ePackageRegistry = EPackage.Registry.INSTANCE;
				EPackage ePackage = ePackageRegistry.getEPackage(ePackageURI);
				if(ePackage == null) {
					throw new RuntimeException(String.format("No EPackage %s found within EPackageRegistry", ePackageURI));
				}
				result.add(ePackage);
			});
			return result;
		} finally {
			searcherSO.ungetService(searcher);
		}
	}
}
