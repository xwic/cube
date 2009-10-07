/**
 * 
 */
package de.xwic.cube.storage.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolStorageProvider;
import de.xwic.cube.StorageException;

/**
 * Stores the data in a file, using serialization.
 * @author Florian Lippisch
 */
public class FileDataPoolStorageProvider implements IDataPoolStorageProvider {

	private static Logger log = Logger.getLogger(FileDataPoolStorageProvider.class.getName());
	private File dataDir;
	private boolean zipDataPool = false;
	
	/**
	 * @param dataDir
	 */
	public FileDataPoolStorageProvider(File dataDir) {
		super();
		this.dataDir = dataDir;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#containsDataPool(java.lang.String)
	 */
	public boolean containsDataPool(String key) throws StorageException {
		String filename = key + ".datapool";
		File file = new File(dataDir, filename);
		File zipFile = new File(dataDir, filename + ".zip");
		return file.exists() || zipFile.exists();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#listDataPools()
	 */
	public List<String> listDataPools() throws StorageException {
		File[] files = dataDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase().endsWith(".datapool") || filename.toLowerCase().endsWith(".datapool.zip");
			}
		});
		Set<String> keys = new LinkedHashSet<String>();
		for (File f : files) {
			String name = f.getName();
			boolean zipped = name.toLowerCase().endsWith(".zip");
			if (zipped) {
				name = name.substring(0, name.length() - ".datapool.zip".length());
			} else {
				name = name.substring(0, name.length() - ".datapool".length());
			}
			keys.add(name);
		}
		return new ArrayList<String>(keys);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#loadDataPool(java.lang.String)
	 */
	public IDataPool loadDataPool(String key) throws StorageException {
		
		try {
			String filename = key + ".datapool";
			File file = new File(dataDir, filename);
			File zipFile = new File(dataDir, filename + ".zip");
			
			if (!file.exists() && !zipFile.exists()) {
				throw new IllegalArgumentException("A datapool with the key " + key + " does not exist.");
			}
			
			ObjectInputStream ois = null;
			if (file.exists()) {
				// open default data pool
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bip = new BufferedInputStream(fis);
				ois = new ObjectInputStream(bip);
			} else {
				// open zipped data pool
				ZipFile zip = new ZipFile(zipFile);
				for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().toLowerCase().endsWith(".datapool")) {
						// use first data pool found
						ois = new ObjectInputStream(new BufferedInputStream(zip.getInputStream(entry)));
						break;
					}
				}
				// ensure zip flag is set
				zipDataPool = true;
				if (ois == null) {
					throw new IllegalArgumentException("A datapool with the key " + key + " not found in " + zipFile);
				}
			}
				
			IDataPool pool = (IDataPool) ois.readObject();
			ois.close();
			return pool;
		} catch (Exception e) {
			throw new StorageException("Error loading DataPool " + key + ": " + e, e);
		}
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#saveDataPool(de.xwic.cube.IDataPool)
	 */
	public void saveDataPool(IDataPool dataPool) throws StorageException {
		
		try {
			String filename = dataPool.getKey() + ".datapool";
			String originalFilename = filename;
			if (zipDataPool) {
				filename += ".zip";
			}
			log.info("Saving DataPool " + filename + " with cubes " + dataPool.getCubes() + "...");
			FileOutputStream fos = new FileOutputStream(new File(dataDir, filename));
			BufferedOutputStream bufOut = new BufferedOutputStream(fos);
			OutputStream os = bufOut;
			
			if (zipDataPool) {
				// zip data pool
				ZipEntry zipEntry = new ZipEntry(originalFilename);
				//zipEntry.setTime(System.currentTimeMillis());
				ZipOutputStream zos = new ZipOutputStream(bufOut);
				zos.putNextEntry(zipEntry);
				os = zos;
			}
			
			ObjectOutputStream oos = new ObjectOutputStream(os);
	
			oos.writeObject(dataPool);
			
			oos.flush();
			oos.close();
			bufOut.flush();
			bufOut.close();
			fos.close();

		} catch (Exception e) {
			throw new StorageException("Error storing DataPool " + dataPool.getKey() + ": " + e, e);
		}

	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#deleteDataPool(java.lang.String)
	 */
	public void deleteDataPool(String key) {
		// TODO Auto-generated method stub
		
	}

}
