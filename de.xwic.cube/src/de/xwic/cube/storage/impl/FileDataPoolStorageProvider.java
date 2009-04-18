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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
		return file.exists();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#listDataPools()
	 */
	public List<String> listDataPools() throws StorageException {
		File[] files = dataDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase().endsWith(".datapool");
			}
		});
		List<String> keys = new ArrayList<String>();
		for (File f : files) {
			String name = f.getName();
			name = name.substring(0, name.length() - ".datapool".length());
			keys.add(name);
		}
		return keys;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#loadDataPool(java.lang.String)
	 */
	public IDataPool loadDataPool(String key) throws StorageException {
		
		try {
			String filename = key + ".datapool";
			File file = new File(dataDir, filename);
			if (!file.exists()) {
				throw new IllegalArgumentException("A datapool with the key " + key + " does not exist.");
			}
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bip = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bip);
	
			IDataPool pool = (IDataPool) ois.readObject();
			ois.close();
			bip.close();
			fis.close();
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
			log.info("Saving DataPool " + filename + " with cubes " + dataPool.getCubes() + "...");
			FileOutputStream fos = new FileOutputStream(new File(dataDir, filename));
			BufferedOutputStream bufOut = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bufOut);
	
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
