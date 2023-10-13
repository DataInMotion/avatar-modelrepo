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
package de.avatar.mr.example.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.gecko.emf.repository.EMFRepository;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.PromiseFactory;

import com.github.javafaker.Faker;

import de.avatar.mdp.apis.DBObjectsEvaluator;
import de.avatar.mdp.medicalrecord.EmergencyContact;
import de.avatar.mdp.medicalrecord.InsuranceDetails;
import de.avatar.mdp.medicalrecord.MedicalHistory;
import de.avatar.mdp.medicalrecord.MedicalRecord;
import de.avatar.mdp.medicalrecord.MedicalRecordFactory;
import de.avatar.mdp.medicalrecord.MedicalRecordPackage;
import de.avatar.mdp.medicalrecord.PatientInfo;
import de.avatar.mr.example.model.person.Address;
import de.avatar.mr.example.model.person.Contact;
import de.avatar.mr.example.model.person.ContactType;
import de.avatar.mr.example.model.person.ContextType;
import de.avatar.mr.example.model.person.Person;
import de.avatar.mr.example.model.person.PersonFactory;
import de.avatar.mr.example.model.person.PersonPackage;

@Component(immediate=true, name = "ModelInstanceExampleComponent")
public class ModelInstanceExampleComponent {

	@Reference(target="(repo_id=avatarmdp.avatarmdp)")
	ComponentServiceObjects<EMFRepository> repoSO;
	
//	@Reference(target=("(repositorySO.target=(repo_id=avatarmdp.avatarmdp))"))
	@Reference
	DBObjectsEvaluator dbObjEvaluator;

	private static final Logger LOGGER = Logger.getLogger(ModelInstanceExampleComponent.class.getName());
	private static final List<String> HEALTH_INSURANCE_COMPANIES_GERMANY = List.of("AOK", "BARMER", "HKK", "hkk health insurance", "KNIGHTSHAFT",
			"DAK health", "KKH", "Techniker Krankenkasse", "TK", "HEK", "Hanseatic Health Insurance", "BKK", "IKK"); 

	private Faker faker = new Faker();
	private PromiseFactory factory = new PromiseFactory(Executors.newFixedThreadPool(4));


	@Activate
	public void activate() {
		factory.submit(() -> {
			doCreateModelInstanceExamples();
			return true;
		}).onSuccess(t -> LOGGER.info("Finished creating sample model instances!"))
		.onFailure(t -> LOGGER.log(Level.SEVERE, String.format("Something went wrong when creating sample model insatnces!"), t));		
	}

	private void doCreateModelInstanceExamples() {
		EMFRepository repo = repoSO.getService();
		try {
			List<EObject> existingMedicalRecords = repo.getAllEObjects(MedicalRecordPackage.eINSTANCE.getMedicalRecord());
			List<EObject> existingPersons = repo.getAllEObjects(PersonPackage.eINSTANCE.getPerson());
			boolean createMedicalRecords = existingMedicalRecords.isEmpty();
			boolean createPersons = existingPersons.isEmpty();

			if(createMedicalRecords) {
				repo.save(createMRInstances(10).stream().map(mr -> (EObject) mr).toList());
			}
			if(createPersons) {
				List<Person> persons = createPersonInstances(10);
				repo.save(persons.stream().map(p -> p.getAddress()).toList().stream().flatMap(al -> al.stream()).map(a -> (EObject) a).toList());
				repo.save(persons.stream().map(p -> (EObject) p).toList());
			}
			if(createMedicalRecords || createPersons) dbObjEvaluator.evaluate();
		} finally {
			repoSO.ungetService(repo);
		}
	}
	
	private List<MedicalRecord> createMRInstances(int numberOfInstances) {
		List<MedicalRecord> medicalRecords = new ArrayList<>(numberOfInstances);
		for(int i = 0; i < numberOfInstances; i++) {
			medicalRecords.add(doCreateMedicalRecordInstance());
		}	
		return medicalRecords;
	}

	private MedicalRecord doCreateMedicalRecordInstance() {
		MedicalRecord medicalRecord = MedicalRecordFactory.eINSTANCE.createMedicalRecord();

		PatientInfo patientInfo = MedicalRecordFactory.eINSTANCE.createPatientInfo();
		patientInfo.setName(faker.name().fullName());
		patientInfo.setAddress(faker.address().fullAddress());
		patientInfo.setBirthDate(faker.date().birthday());
		patientInfo.setPhone((String) selectRandomElement(List.of(faker.phoneNumber().phoneNumber(), faker.phoneNumber().cellPhone()).toArray(new String[] {})));
		patientInfo.setHeight(faker.number().numberBetween(150, 195));
		patientInfo.setWeight(faker.number().randomDouble(1, 40, 150));
		medicalRecord.setPatientInfo(patientInfo);

		EmergencyContact emergencyContact = MedicalRecordFactory.eINSTANCE.createEmergencyContact();
		emergencyContact.setAddress(faker.address().fullAddress());
		emergencyContact.setName(faker.name().fullName());
		emergencyContact.setPhone((String) selectRandomElement(List.of(faker.phoneNumber().phoneNumber(), faker.phoneNumber().cellPhone()).toArray(new String[] {})));
		medicalRecord.setEmergencyContact(emergencyContact);

		MedicalHistory medicalHistory = MedicalRecordFactory.eINSTANCE.createMedicalHistory();
		medicalHistory.setMedicalProblems(faker.medical().diseaseName());
		medicalHistory.setMedications(faker.medical().medicineName());
		medicalRecord.setMedicalHistory(medicalHistory);

		InsuranceDetails insuranceDetails = MedicalRecordFactory.eINSTANCE.createInsuranceDetails();
		insuranceDetails.setExpiryDate(faker.date().future(600, TimeUnit.DAYS));
		insuranceDetails.setInsuranceName((String) selectRandomElement(HEALTH_INSURANCE_COMPANIES_GERMANY.toArray(new String[] {})));
		insuranceDetails.setInsuranceNumber(faker.idNumber().ssnValid());
		medicalRecord.setInsuranceDetails(insuranceDetails);

		return medicalRecord;
	}

	private List<Person> createPersonInstances(int numberOfInstances) {
		List<Person> persons = new ArrayList<Person>(numberOfInstances);	
		for(int i = 0; i < numberOfInstances; i++) {
			persons.add(doCreatePersonInstance());
		}	
		return persons;
	}
	
	private Person doCreatePersonInstance() {
		Person person = PersonFactory.eINSTANCE.createPerson();
		person.setFirstName(faker.name().firstName());
		person.setLastName(faker.name().lastName());
		person.setBirthDate(faker.date().birthday());

		for(int i = 0; i < 2; i++) {
			Address address = PersonFactory.eINSTANCE.createAddress();
			address.setCity(faker.address().city());
			address.setStreet(faker.address().streetName() + ", " + faker.address().streetAddressNumber());
			address.setZip(faker.address().zipCode());
			address.setState(faker.address().state());
			address.setContext((ContextType) selectRandomElement(ContextType.values()));
			person.getAddress().add(address);
		}

		for(int i = 0; i < 4; i++) {
			Contact contact = PersonFactory.eINSTANCE.createContact();
			contact.setType((ContactType) selectRandomElement(ContactType.values()));
			contact.setContext((ContextType) selectRandomElement(ContextType.values()));
			switch(contact.getType()) {
			case EMAIL:
				contact.setValue(faker.internet().emailAddress());
				break;
			case INSTAGRAM, LINKEDIN, TWITTER:
				contact.setValue(faker.name().username());
			break;
			case MOBILE:
				contact.setValue(faker.phoneNumber().cellPhone());
				break;
			case PHONE:
				contact.setValue(faker.phoneNumber().phoneNumber());
				break;
			case OTHER:
				contact.setValue((String) selectRandomElement(List.of(faker.internet().emailAddress(), 
						faker.name().username(), faker.phoneNumber().cellPhone(), 
						faker.phoneNumber().phoneNumber()).toArray(new String[] {})));
				break;
			}
			person.getContact().add(contact);
		}
		return person;
	}

	private <T extends Object> Object selectRandomElement(T[] elements)  {
		Random rndm = new Random();
		int rndmIndx = rndm.nextInt(elements.length);
		Object rndmElem = elements[rndmIndx];
		return rndmElem;
	}
}
