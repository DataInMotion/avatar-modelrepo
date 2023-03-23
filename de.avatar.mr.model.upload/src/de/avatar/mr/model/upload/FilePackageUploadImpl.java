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
package de.avatar.mr.model.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.avatar.mr.model.upload.api.FilePackageLoader;
import de.avatar.mr.model.upload.api.FilePackageUpload;

/**
 * 
 * @author ilenia
 * @since Mar 23, 2023
 */
@Component(name = "FilePackageUpload", service = FilePackageUpload.class)
public class FilePackageUploadImpl implements FilePackageUpload {

	@Reference
	FilePackageLoader filePackageLoader;
	
	private static final Logger LOGGER = Logger.getLogger(FilePackageUploadImpl.class.getName());
	private Path storageFolder;
	
	@Activate
	public void activate() {
		this.storageFolder = filePackageLoader.getPackageStoragePath();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.avatar.mr.model.upload.api.FilePackageUpload#savePackageFile(java.io.InputStream, java.lang.String)
	 */
	@Override
	public void savePackageFile(InputStream is, String fileName)  throws IOException {
		try {
			File file = copyFile(is, storageFolder.toString() + "/" + fileName);
			filePackageLoader.loadModel(file.toPath());			
		} catch (IOException e) {
			LOGGER.severe(String.format("Error while saving file %s. The corresponding EPackage will NOT be registered!", fileName));
			e.printStackTrace();
			throw e;
		}		
	}

	private File copyFile(InputStream is, String outFile) throws IOException{
		File file = new File(outFile);
		try (is; FileOutputStream os = new FileOutputStream(file);) {
			int read;
			byte[] bytes = new byte[2048];
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			return file;
		} 
	}
}
