/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.avatar.mr.vaadin.views.upload;

import java.io.IOException;

import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.avatar.mr.model.upload.api.FilePackageUpload;
import de.avatar.mr.vaadin.views.main.MainView;

/**
 * 
 * @author ilenia
 * @since Mar 23, 2023
 */
@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
@Component(name = "UploadView", service=UploadView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class UploadView extends VerticalLayout {
	
	@Reference
	FilePackageUpload modelUploadService;

	/** serialVersionUID */
	private static final long serialVersionUID = 2024867858695791126L;
	
	@Activate
	public void renderView() {
		
		HorizontalLayout uploadLayout = new HorizontalLayout();
		uploadLayout.setAlignItems(Alignment.BASELINE);
		Label uploadLabel = new Label("Upload Model");
		Button uploadBtn = new Button(new Icon(VaadinIcon.UPLOAD));
		FileBuffer buffer = new FileBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes(".ecore");
		uploadLayout.add(uploadLabel, uploadBtn);
		
		Button saveBtn = new Button("Save");
		saveBtn.setEnabled(false);
		add(uploadLayout, saveBtn);
		
//		Listeners
		uploadBtn.addClickListener(evt -> {		
			uploadLayout.add(upload);
			upload.addSucceededListener(event -> {
				saveBtn.setEnabled(true);
			});
			upload.getElement().addEventListener("file-remove", event -> {				
				uploadLayout.remove(upload);
				saveBtn.setEnabled(false);
			});
		});
		
		saveBtn.addClickListener(evt -> {
			try {
				modelUploadService.savePackageFile(buffer.getInputStream(), buffer.getFileName());
				Notification.show("Model saved and registered successfully!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			} catch (IOException e) {
				Notification.show("Something went wrong when saving the uploaded file!").addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		});
	}
}
