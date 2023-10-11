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
package de.avatar.mr.search.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
public class SearchHelper {
	
	public static final Logger logger = Logger.getLogger(SearchHelper.class.getName());
	
	public static List<Document> doExecuteSearch(Query query, IndexSearcher searcher ) {		
		IndexReader reader = searcher.getIndexReader();
		try {
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			List<Document> result = new ArrayList<>();
			Arrays.asList(topDocs.scoreDocs).stream().map(sd -> sd.doc).map(id -> {
				try {
					return reader.storedFields().document(id);
				}  catch (IOException e) {
					logger.log(Level.SEVERE, String.format("Exception while reading matching document from index. Skipping it."), e);
					return null;
				}
			})
			.filter(Objects::nonNull)
			.forEach(result::add);
			return result;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Exception while searching for %s", query));
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public static List<Query> createQueries(String term, String query) {
		Query exactMatchQuery = new TermQuery(new Term(term, query));
		Query wildcardQuery = new WildcardQuery(new Term(term, "*" + query + "*"));
		Query fuzzyQuery = new FuzzyQuery(new Term(term, query));				
		return List.of(new BoostQuery(exactMatchQuery, 2.0f), new BoostQuery(wildcardQuery, 1.5f), new BoostQuery(fuzzyQuery, 0.5f));		
	}

}
