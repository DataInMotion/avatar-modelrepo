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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.avatar.mr.index.api.EPackageIndexService;
import de.avatar.mr.search.api.EStructuralFeatureSearchService;
import de.avatar.mr.search.helper.SearchHelper;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
 @Component(name = "EStructuralFeatureSearchService", service = EStructuralFeatureSearchService.class)
public class EStructuralFeatureSearchServiceImpl implements EStructuralFeatureSearchService {
	
	@Reference(target = "(id=ePackage)")
	private ComponentServiceObjects<IndexSearcher> searcherSO;
	

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EStructuralFeatureSearchService#searchEStructuralFeaturesByName(java.lang.String)
	 */
	@Override
	public List<EStructuralFeature> searchEStructuralFeaturesByName(String query) {
		Objects.requireNonNull(query, "Cannot search EStructuralFeature with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder internalBuilder = new BooleanQuery.Builder();				
		List<Query> nameQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_NAME_LOWER, query.toLowerCase());
		List<Query> aliasQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_ALIAS_LOWER, query.toLowerCase());
		nameQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		aliasQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));
		
		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EStructuralFeature")), Occur.MUST);
		queryBuilder.add(internalBuilder.build(), Occur.MUST);

		return executeSearch(queryBuilder.build());		
	}

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EStructuralFeatureSearchService#searchEStructuralFeaturesByType(java.lang.String)
	 */
	@Override
	public List<EStructuralFeature> searchEStructuralFeaturesByType(String query) {
		Objects.requireNonNull(query, "Cannot search EStructuralFeature with a null query");
		query = query.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();		
		
		BooleanQuery.Builder internalBuilder = new BooleanQuery.Builder();			
		List<Query> typeQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_TYPE, query);
		typeQueryList.forEach(q -> internalBuilder.add(q, Occur.SHOULD));

		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EStructuralFeature")), Occur.MUST);
		queryBuilder.add(internalBuilder.build(), Occur.MUST);
		
		return executeSearch(queryBuilder.build());		
	}

	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.search.api.EStructuralFeatureSearchService#searchEStructuralFeaturesByNameAndType(java.lang.String, java.lang.String)
	 */
	@Override
	public List<EStructuralFeature> searchEStructuralFeaturesByNameAndType(String name, String type) {
		Objects.requireNonNull(name, "Cannot search EStructuralFeature with a null query");
		Objects.requireNonNull(type, "Cannot search EStructuralFeature with a null query");
		name = name.trim();
		type = type.trim();
		
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();	
		queryBuilder.add(new TermQuery(new Term(EPackageIndexService.DOC_TYPE, "EStructuralFeature")), Occur.MUST);

		BooleanQuery.Builder nameBuilder = new BooleanQuery.Builder();	
		List<Query> nameQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_NAME_LOWER, name.toLowerCase());
		List<Query> aliasQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_ALIAS_LOWER, name.toLowerCase());
		nameQueryList.forEach(q -> nameBuilder.add(q, Occur.SHOULD));
		aliasQueryList.forEach(q -> nameBuilder.add(q, Occur.SHOULD));
		
		BooleanQuery.Builder typeBuilder = new BooleanQuery.Builder();	
		List<Query> typeQueryList = SearchHelper.createQueries(EPackageIndexService.ESTRUCTURAL_FEATURE_TYPE, type);
		typeQueryList.forEach(q -> typeBuilder.add(q, Occur.SHOULD));
		
		queryBuilder.add(nameBuilder.build(), Occur.MUST);
		queryBuilder.add(typeBuilder.build(), Occur.MUST);
		
		return executeSearch(queryBuilder.build());		
	}

	private List<EStructuralFeature> executeSearch(Query query) {
		IndexSearcher searcher = searcherSO.getService();
		try {
			List<Document> docs = SearchHelper.doExecuteSearch(query, searcher);
			List<EStructuralFeature> result = new ArrayList<>();
			docs.forEach(document -> {
				String featureName = document.get(EPackageIndexService.ESTRUCTURAL_FEATURE_NAME);
				String classifierName = document.get(EPackageIndexService.ECLASSIFIER_NAME);
				String ePackageURI = document.get(EPackageIndexService.EPACKAGE_NS_URI);
				if(featureName == null || classifierName == null || ePackageURI == null) throw new RuntimeException("Index Document Attributes are null!");
				EPackage.Registry ePackageRegistry = EPackage.Registry.INSTANCE;
				EPackage ePackage = ePackageRegistry.getEPackage(ePackageURI);
				if(ePackage == null) {
					throw new RuntimeException(String.format("Cannot find EPackage %s for matching EStructuralFeature %s!", ePackageURI, featureName));
				}
				EClassifier classifier = ePackage.getEClassifier(classifierName);
				if(classifier == null || !(classifier instanceof EClass)) {
					throw new RuntimeException(String.format("EClassifier %s is null or is not an EClass. Cannot retrieve EStructuralFeature %s from it!", classifierName, featureName));
				}
				result.add(((EClass) classifier).getEStructuralFeature(featureName));
			});
			return result;
		} finally {
			searcherSO.ungetService(searcher);
		}
	}

}
