/**
 * 
 */
package de.xwic.cube.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.xwic.cube.ICell;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IIdentifyable;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.Key;
import de.xwic.cube.StorageException;

/**
 * File based indexed storage.
 * @author lippisch
 */
public class FileIndexedDataTable extends IndexedDataTable {

	private final static int HEADER_SIZE = 4 * 4; // header has 4 x int
	private final static int MAX_LEAF_CACHE_SIZE = 10000;
	private final static int MAX_FETCH_BYTES = 8192; // 8k seems the optimal balance
	
	private String swapFileName;
	private File swapFile;
	private final DataPool dataPool;
	
	private boolean swapped = false;
	private RandomAccessFile fileAccess = null;

	private int maxSize = 0;
	private int recordSize = 0;
	private int totalReadCount = 0;
	private long myPos = 0;
	
	private IndexedData startId = null;
	
	private Map<Key, ICell> leafCache;
	
	/**
	 * @param dimensionCount
	 * @param measureCount
	 * @param swapFile 
	 * @param dataPool 
	 */
	public FileIndexedDataTable(int dimensionCount, int measureCount, String swapFileName, DataPool dataPool) {
		super(dimensionCount, measureCount);
		this.swapFileName = swapFileName;
		this.dataPool = dataPool;
		leafCache = new LinkedHashMap<Key, ICell>(1000, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<Key, ICell> eldest) {
				return size() > MAX_LEAF_CACHE_SIZE;
			}
		};
	}
	
	private void checkSwapFile() {
		if (swapFile == null) {
			File dataDir = dataPool.getDataPoolManager().getStorageProvider().getDataDir();
			if (dataDir == null) {
				throw new IllegalStateException("DataDir is NULL. This cube only works when a data directory is available.");
			}
			swapFile = new File(dataDir, swapFileName);
		}
	}


	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#get(de.xwic.cube.Key)
	 */
	@Override
	public ICell get(Key key) {
		if (swapped) {
			// since this method is called for leafs, check the cache
			ICell c = leafCache.get(key);
			if (c != null) {
				return c;
			}
			synchronized (this) {
				c = calcCell(key);
				leafCache.put(key.clone(), c);
				return c;
			}
		}
		return super.get(key);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#calcCell(de.xwic.cube.Key)
	 */
	@Override
	public ICell calcCell(Key key) {
		if (swapped) {
			// in swapped state, the access needs to be synchronized
			synchronized (this) {
				return super.calcCell(key);
			}
		}
		return super.calcCell(key);
	}
	
	/**
	 * Swap the data table to the disk and remove it from memory.
	 */
	public void storeToDisk() {
		
		if (swapped) {
			log.warn("storeToDisk skipped as already in swapped state");
			return; // data already swapped out 
		}
		
		long start = System.currentTimeMillis();
		checkSwapFile();
		if (swapFile.exists()) {
			if (!swapFile.delete()) { // delete the file first.
				throw new IllegalStateException("Can not delete swap file " + swapFile.getName());
			}
		}
		
		recordSize = (dimensionCount * maxDepth * 4)  // pointers (integer values)
			+ (dimensionCount * 4) // length Information.
			+ (dimensionCount * 4) // Key elements
			+ (measureCount * 8);
		
		try {
			BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(swapFile, false));
			DataOutputStream out = new DataOutputStream(buf);
			
			maxSize = indexData.size();
			out.writeInt(maxSize);
			out.writeInt(dimensionCount);
			out.writeInt(measureCount);
			out.writeInt(maxDepth);
			
			for (IndexedData id : indexData) {
				
				// write pointers
				for (int[] dp : id.getNextEntry()) {
					out.writeInt(dp.length);
					for (int i = 0; i < maxDepth; i++) {
						if (i < dp.length) {
							out.writeInt(dp[i]);
						} else {
							out.writeInt(0);
						}
					}
				}
				// write the key
				for (IDimensionElement de : id.getKey().getDimensionElements()) {
					out.writeInt(de.getObjectId());
				}
				// write the data
				ICell cell = id.getCell();
				for (int i = 0; i < measureCount; i++) {
					Double d = cell.getValue(i);
					if (d == null) {
						out.writeDouble(Double.NaN);
					} else {
						out.writeDouble(d);
					}
				}
				
			}
			out.flush();
			out.close();
			
			fileAccess = new RandomAccessFile(swapFile, "r"); // open the file.

		} catch (IOException e) {
			throw new RuntimeException("Error writing swap file ", e);
		}
		
		if (indexData.size() > 0) {
			startId = indexData.get(0);
		} else {
			startId = null;
		}
		
		indexData.clear();
		hashData.clear();
		
		swapped = true;
		
		long time = System.currentTimeMillis() - start;
		log.info("Time for dump: " + time + "ms for " + maxSize + " elements [rec-size=" + recordSize + "]; FileSize=" + swapFile.length());
		
	}
	
	/**
	 * This method releases the data loaded into memory if a swap file exists. This may be
	 * used if the whole data was loaded into memory (i.e. for cache rebuild) without modifying
	 * it. 
	 */
	public void releaseInMemoryData() {
		if (swapped) {
			log.warn("releaseInMemoryData skipped as already in swapped state");
			return; // data already swapped out 
		}
		
		checkSwapFile();
		if (!swapFile.exists()) {
			log.warn("releaseInMemoryData: No swap file exists - using storeToDisk instead");
			storeToDisk();
		} else {
			
			if (indexData.size() > 0) {
				startId = indexData.get(0);
			} else {
				startId = null;
			}
			
			indexData.clear();
			hashData.clear();
			
			swapped = true;
			log.debug("Data released, returning into swapped state (release only)");
			
		}

	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#getStartIndexData()
	 */
	@Override
	protected IndexedData getStartIndexData() {
		if (swapped) {
			return startId;
		}
		return super.getStartIndexData();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#getIndexDataSize()
	 */
	@Override
	protected int getIndexDataSize() {
		if (swapped) {
			return maxSize;
		}
		return super.getIndexDataSize();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#getLeafValue(de.xwic.cube.Key)
	 */
	@Override
	protected ICell getLeafValue(Key key) {
		if (!swapped) {
			return super.getLeafValue(key);
		}
		
		// perform a regular search as leafs are not stored/cached either
		
		if (indexDirty) {
			buildIndex(); // rebuild index
		}

		SearchContext ctx = new SearchContext();
		ctx.key = key;
		ctx.maxRow = getIndexDataSize();
		ctx.rowIdx = 0;
		ctx.cell = null;
		ctx.currId = getStartIndexData(); // start with the first one
		ctx.currIdx = ctx.currId == null ? -1 : 0;

		onBeginScan(ctx);
		// search the elements.
		scanElements(ctx, 0, ctx.maxRow);
		
		onFinishedScan(ctx);
		
		System.out.println("Entries touched: " + ctx.ibScan + " out of " + indexData.size());
		
		return ctx.cell;

	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		if (swapped) {
			if (fileAccess != null) {
				try {
					fileAccess.close();
				} catch (IOException e) {
					throw new RuntimeException("Unexpected error on clear " + e, e);
				}
				fileAccess = null;
			}
			swapped = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#onBeginScan()
	 */
	@Override
	protected void onBeginScan(SearchContext ctx) {
		super.onBeginScan(ctx);
		if (swapped) {
			try {
				if (fileAccess == null) { // open file for first time
					synchronized (this) {
						if (fileAccess == null) {
							checkSwapFile();
							fileAccess = new RandomAccessFile(swapFile, "r");
						
							maxSize = fileAccess.readInt();
							int dimCnt = fileAccess.readInt();
							int meaCnt = fileAccess.readInt();
				
							if (dimCnt != dimensionCount || meaCnt != measureCount) {
								throw new IllegalStateException("The cube swap file does not match the size of the actual cube.");
							}
		
							maxDepth = fileAccess.readInt();
		
							recordSize = (dimensionCount * maxDepth * 4)  // pointers (integer values)
									+ (dimensionCount * 4) // length Information.
									+ (dimensionCount * 4) // Key elements
									+ (measureCount * 8);
		
							myPos = fileAccess.getFilePointer();
						}
					}
	
				}
				int maxRecords = MAX_FETCH_BYTES / recordSize;
				ctx.buffer = new byte[Math.max(1, maxRecords) * recordSize];
				ctx.bufferIn = new ByteArrayInputStream(ctx.buffer);
				ctx.objIn = new DataInputStream(ctx.bufferIn);
				
			} catch (IOException e) {
				throw new IllegalStateException("Can not read data from swap file", e);
			}
			ctx.readCount = 0;
			ctx.seekCount = 0;
		}
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#onFinishedScan()
	 */
	@Override
	protected void onFinishedScan(SearchContext ctx) {
		totalReadCount += ctx.readCount;
//		System.out.println("Finished scanning, did read " + ctx.ibScan + " records (" + ctx.readCount + " physical reads) records (total=" + totalReadCount + "); seek= " + ctx.seekCount + " :: " + ctx.key);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#onScanElement(de.xwic.cube.impl.IndexedDataTable.SearchContext)
	 */
	@Override
	protected IndexedData onScanElement(SearchContext ctx) {
		if (swapped) {
			ctx.ibScan++;
			try {
				long pos = HEADER_SIZE + (ctx.rowIdx * recordSize);
				if ((ctx.bufferStart + ctx.bufferPos) != pos || ctx.bufferPos >= ctx.bufferMax) {
					
					if (pos != myPos) {
						ctx.seekCount++;
						fileAccess.seek(pos);
						myPos = pos; 
					}
					ctx.bufferStart = pos;
					myPos += fileAccess.read(ctx.buffer); // read the full buffer
					ctx.bufferMax = ctx.buffer.length;
					ctx.readCount++;
					ctx.bufferIn.reset(); // make sure to read from the start
					ctx.bufferPos = 0;
				}
				
				IndexedData id = restoreIndexedData(ctx.objIn);
				ctx.bufferPos += recordSize;
				return id;
				
			} catch (IOException e) {
				throw new RuntimeException("Error reading data from file " + e, e);
			}
			
		} 
		return super.onScanElement(ctx);
	}
	
	/**
	 * @param objIn2
	 * @return
	 * @throws IOException 
	 */
	private IndexedData restoreIndexedData(DataInputStream in) throws IOException {

		int[][] pointers = new int[dimensionCount][0];
		for (int dc = 0 ; dc < dimensionCount; dc++) {
			int ptSize = in.readInt();
			pointers[dc] = new int[ptSize];
			for (int dcI = 0; dcI < maxDepth; dcI++) {
				if (dcI < pointers[dc].length) {
					pointers[dc][dcI] = in.readInt();
				} else {
					in.readInt();
				}
			}
		}
		// read the key
		IDimensionElement[] keyElms = new IDimensionElement[dimensionCount];
		for (int dc = 0; dc < dimensionCount; dc++) {
			int objectId = in.readInt();
			IIdentifyable obj = dataPool.getObject(objectId);
			if (obj == null) {
				throw new IllegalStateException("Can not restore IDimensionElement with id " + objectId);
			}
			if (!(obj instanceof IDimensionElement)) {
				throw new IllegalStateException("Can not restore IDimensionElement with id " + objectId + "; found: " + obj.getClass().getName());
			}
			keyElms[dc] = (IDimensionElement) obj;
		}
		
		// write the data
		Cell cell = new Cell(measureCount);
		for (int mec = 0; mec < measureCount; mec++) {
			double d = in.readDouble();
			cell.setValue(mec, d);
		}

		Key key = new Key(keyElms);
		IndexedData id = new IndexedData(key, cell);
		id.setNextEntry(pointers);
		return id;
		
	}

	/**
	 * Read the data back from the disk.
	 */
	public void restoreFromDisk() {
		
		if (!swapped) {
			log.debug("Skipping restoreFromDisk as data is already in-memory");
			return; // do not restore if everything is already in-memory
		}
		
		checkSwapFile();
		if (!swapFile.exists()) {
			throw new IllegalStateException("The cube swap file does not exist.");
		}

		indexData.clear();
		hashData.clear();
		
		long start = System.currentTimeMillis();
		
		try {
			
			if (fileAccess != null) {
				fileAccess.close();
				fileAccess = null;
			}
			
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(swapFile));
			DataInputStream in = new DataInputStream(buf);
			try {
				
				maxSize = in.readInt();
				int dimCnt = in.readInt();
				int meaCnt = in.readInt();
	
				if (dimCnt != dimensionCount || meaCnt != measureCount) {
					throw new IllegalStateException("The cube swap file does not match the size of the actual cube.");
				}

				maxDepth = in.readInt();

				recordSize = (dimensionCount * maxDepth * 4)  // pointers (integer values)
						+ (dimensionCount * 4) // length Information.
						+ (dimensionCount * 4) // Key elements
						+ (measureCount * 8);

				
				indexData = new ArrayList<IndexedData>(maxSize);
				for (int i = 0; i < maxSize; i++) {
					// read pointers
					IndexedData id = restoreIndexedData(in);
					indexData.add(id);
					hashData.put(id.getKey(), id.getCell());
				}
				
			} catch (IOException e) {
				throw new RuntimeException("Error writing swap file ", e);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error closing file.");
		}

		swapped = false;
		long time = System.currentTimeMillis() - start;
		log.debug("RestoreFromDisk done in " + time + "ms");
		
	}

	/**
	 * Close all open files
	 * @throws StorageException 
	 */
	public void close() throws StorageException {
		if (fileAccess != null) {
			try {
				fileAccess.close();
			} catch (IOException e) {
				throw new StorageException("Error closing swap file", e);
			}
			fileAccess = null;
		}
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#serialize(java.io.ObjectOutput)
	 */
	@Override
	public void serialize(ObjectOutput out) throws IOException {
		out.writeInt(maxSize);
		out.writeInt(maxDepth);
		out.writeBoolean(swapped);
		out.writeObject(swapFileName);
		
		// serialize leaf cache
		out.writeInt(leafCache.size());
		for (Entry<Key, ICell> e: leafCache.entrySet()) {
			out.writeObject(e.getKey());
			out.writeObject(e.getValue());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#restore(java.io.ObjectInput, de.xwic.cube.IKeyProvider)
	 */
	@Override
	public void restore(ObjectInput in, IKeyProvider keyProvider) throws IOException, ClassNotFoundException {
		maxSize = in.readInt();
		maxDepth = in.readInt();
		swapped = in.readBoolean();
		swapFileName = (String)in.readObject();
		
		indexDirty = !swapped;
		
		int lcSize = in.readInt();
		leafCache = new LinkedHashMap<Key, ICell>(lcSize, 0.75f, true);
		for (int i = 0; i < lcSize; i++) {
			Key k = (Key)in.readObject();
			ICell cell = (ICell)in.readObject();
			leafCache.put(k, cell);
		}
		
	}

	/**
	 * @return the swapFile
	 */
	public File getSwapFile() {
		return swapFile;
	}

	/**
	 * @param swapFile the swapFile to set
	 */
	public void setSwapFile(File swapFile) {
		this.swapFile = swapFile;
	}

	/**
	 * @return the swapped
	 */
	public boolean isSwapped() {
		return swapped;
	}

	/**
	 * Returns the total number of records read due to scan operations.
	 * @return the totalReadCount
	 */
	public int getTotalReadCount() {
		return totalReadCount;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.IndexedDataTable#size()
	 */
	@Override
	public int size() {
		if (swapped) {
			return maxSize;
		}
		return super.size();
	}
	
}
