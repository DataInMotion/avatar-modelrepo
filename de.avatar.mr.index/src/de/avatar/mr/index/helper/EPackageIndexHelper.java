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
package de.avatar.mr.index.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.gecko.emf.search.document.EObjectContextObjectBuilder;
import org.gecko.emf.search.document.EObjectDocumentIndexObjectContext;
import org.gecko.search.IndexActionType;

import de.avatar.mr.index.api.EPackageIndexService;

/**
 * 
 * @author ilenia
 * @since Mar 20, 2023
 */
public class EPackageIndexHelper {
	
	public static final String EXTENDED_METADATA_ANNOTATION_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	

	public static EObjectDocumentIndexObjectContext mapEPackageNew(EPackage ePackage) {
		return mapEPackage(ePackage, IndexActionType.ADD);
	}

	public static EObjectDocumentIndexObjectContext mapEPackageUpdate(EPackage ePackage) {
		return mapEPackage(ePackage, IndexActionType.MODIFY);
	}
	
//	TODO: we should index the EPackage in a document, each EClassifier in a separate document and each EStructureFeature in a separate doc as well
//	Then we should add all these documents to the same index
//	Each document should have a field called type with value Package for EPackage doc, Classifier for EClassifier docs and StructureFeature for EstructureFeature docs
//	We should also take the type of a structural feature without the whole jibbery jabbery, so if it's a EDouble we should see just Double
	public static EObjectDocumentIndexObjectContext mapEPackage(EPackage ePackage, IndexActionType indexAction) {
		
		List<Document> documents = new ArrayList<>();
		Document packageDoc = new Document();
		packageDoc.add(new StringField("id", ePackage.getNsURI(), Store.YES));
		packageDoc.add(new StringField(EPackageIndexService.DOC_TYPE, "EPackage", Store.NO));
		packageDoc.add(new StringField(EPackageIndexService.EPACKAGE_NS_URI, ePackage.getNsURI(), Store.YES));
		packageDoc.add(new StringField(EPackageIndexService.EPACKAGE_NAME, ePackage.getName(), Store.YES));
		ePackage.getEAnnotations().stream().filter(ann -> ann.getDetails().containsKey("documentation")).forEach(ann -> {
			packageDoc.add(new TextField(EPackageIndexService.EELEMENT_DESCRIPTION, ann.getDetails().get("documentation"), Store.NO));
		});
		documents.add(packageDoc);
		
		ePackage.getEClassifiers().forEach(ec -> {
			Document classifierDoc = new Document();			
			classifierDoc.add(new StringField("id", ePackage.getNsURI(), Store.YES));
			classifierDoc.add(new StringField(EPackageIndexService.DOC_TYPE, "EClassifier", Store.NO));
			classifierDoc.add(new StringField(EPackageIndexService.ECLASSIFIER_NAME, ec.getName(), Store.YES));
			classifierDoc.add(new StringField(EPackageIndexService.ECLASSIFIER_NAME_LOWER, ec.getName().toLowerCase(), Store.NO));
			classifierDoc.add(new StringField(EPackageIndexService.EPACKAGE_NS_URI, ePackage.getNsURI(), Store.YES));
			classifierDoc.add(new StringField(EPackageIndexService.EPACKAGE_NAME, ePackage.getName(), Store.YES));
			ec.eClass().getESuperTypes().forEach(st -> {
				classifierDoc.add(new StringField(EPackageIndexService.ECLASSIFIER_SUPER_TYPES, st.getName(), Store.YES));
			});
			ec.getEAnnotations().stream().filter(ann -> ann.getDetails().containsKey("documentation")).forEach(ann -> {
				classifierDoc.add(new TextField(EPackageIndexService.EELEMENT_DESCRIPTION, ann.getDetails().get("documentation"), Store.NO));
			});
			ec.getEAnnotations().stream()
			.filter(ann -> EXTENDED_METADATA_ANNOTATION_SOURCE.equals(ann.getSource()))
			.filter(ann -> ann.getDetails().containsKey("name"))
			.forEach(ann -> {
				classifierDoc.add(new TextField(EPackageIndexService.ECLASSIFIER_ALIAS, ann.getDetails().get("name"), Store.NO));
				classifierDoc.add(new TextField(EPackageIndexService.ECLASSIFIER_ALIAS_LOWER, ann.getDetails().get("name").toLowerCase(), Store.NO));
			});
			documents.add(classifierDoc);
			
			if(ec instanceof EClass) {
				EClass eclazz = (EClass) ec;
				eclazz.getEAllStructuralFeatures().forEach(sf -> {
					Document featureDoc = new Document();	
					featureDoc.add(new StringField("id", ePackage.getNsURI(), Store.YES));
					featureDoc.add(new StringField(EPackageIndexService.DOC_TYPE, "EStructuralFeature", Store.NO));
					featureDoc.add(new StringField(EPackageIndexService.ECLASSIFIER_NAME, ec.getName(), Store.YES));
					featureDoc.add(new StringField(EPackageIndexService.EPACKAGE_NS_URI, ePackage.getNsURI(), Store.YES));
					featureDoc.add(new StringField(EPackageIndexService.EPACKAGE_NAME, ePackage.getName(), Store.YES));
					featureDoc.add(new StringField(EPackageIndexService.ESTRUCTURAL_FEATURE_NAME, sf.getName(), Store.YES));
					featureDoc.add(new StringField(EPackageIndexService.ESTRUCTURAL_FEATURE_NAME_LOWER, sf.getName().toLowerCase(), Store.NO));
					featureDoc.add(new StringField(EPackageIndexService.ESTRUCTURAL_FEATURE_TYPE, sf.getEType().getInstanceClass().getSimpleName(), Store.NO));
					sf.getEAnnotations().stream().filter(ann -> ann.getDetails().containsKey("documentation")).forEach(ann -> {
						featureDoc.add(new TextField(EPackageIndexService.EELEMENT_DESCRIPTION, ann.getDetails().get("documentation"), Store.NO));
					});
					sf.getEAnnotations().stream()
					.filter(ann -> EXTENDED_METADATA_ANNOTATION_SOURCE.equals(ann.getSource()))
					.filter(ann -> ann.getDetails().containsKey("name"))
					.forEach(ann -> {
						featureDoc.add(new TextField(EPackageIndexService.ESTRUCTURAL_FEATURE_ALIAS, ann.getDetails().get("name"), Store.NO));
						featureDoc.add(new TextField(EPackageIndexService.ESTRUCTURAL_FEATURE_ALIAS_LOWER, ann.getDetails().get("name").toLowerCase(), Store.NO));
					});
					
					documents.add(featureDoc);
				});
			}
		});
		
		EObjectContextObjectBuilder builder = (EObjectContextObjectBuilder) EObjectContextObjectBuilder.create()
				.withDocuments(documents).withSourceObject(ePackage)
				.withIndexActionType(indexAction);

		if (IndexActionType.MODIFY.equals(indexAction) || IndexActionType.REMOVE.equals(indexAction)) {
			builder.withIdentifyingTerm(new Term("id", ePackage.getNsURI()));
		}
		return builder.build();
	}

}
