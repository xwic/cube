/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.Map;

import de.xwic.cube.IDimension;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;
import de.xwic.cube.StorageException;
import de.xwic.cube.IDataPool.CubeType;

/**
 * This implementation stores the data elements of the indexed cube in 
 * the file system. It is only loaded into memory during updating.
 * @author lippisch
 */
public class CubeSwapIndexed extends CubeIndexed {

	private static final long serialVersionUID = -8944690909580629023L;

	private FileIndexedDataTable dataTable;
	
	/**
	 * 
	 */
	public CubeSwapIndexed() {

	}

	/**
	 * @param dataPool
	 * @param key
	 * @param dimensions
	 * @param measures
	 */
	public CubeSwapIndexed(DataPool dataPool, String key, IDimension[] dimensions, IMeasure[] measures) {
		super(dataPool, key, dimensions, measures);

	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#createCellStore()
	 */
	@Override
	protected ICellStore createCellStore() {
		dataTable = new FileIndexedDataTable(dimensionMap.size(), measureMap.size(), dataPool.getKey() + "_" + getKey() + ".cube", dataPool);
		return dataTable;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#close()
	 */
	@Override
	public void close() throws StorageException {
		dataTable.close();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#batchRefreshCache(java.util.Map)
	 */
	@Override
	protected void batchRefreshCache(Map<Key, CachedCell> cellMap) {
		
		if(massUpdateMode) {
			throw new IllegalStateException("batchRefreshCache should not be done while in massUpdateMode!!!");
		}
		
		// temporary load the data into memory to make loading (much) faster..
		dataTable.restoreFromDisk();
		super.batchRefreshCache(cellMap);
		dataTable.releaseInMemoryData();
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#beginMassUpdate()
	 */
	@Override
	public void beginMassUpdate() {
		dataTable.restoreFromDisk();
		super.beginMassUpdate();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#massUpdateFinished()
	 */
	@Override
	public void massUpdateFinished() {
		super.massUpdateFinished();
		//dump to disk and release memory
		dataTable.storeToDisk();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#splashAndWriteValue(int, de.xwic.cube.Key, int, java.lang.Double)
	 */
	@Override
	protected int splashAndWriteValue(int idx, Key key, int measureIndex, Double value) {
		if (!massUpdateMode) {
			throw new IllegalStateException("This cube does only allow updates in massUpdate mode. use beginMassUpdate() and massUpdateFinished()");
		}
		return super.splashAndWriteValue(idx, key, measureIndex, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		// make sure data is swapped
		if (!dataTable.isSwapped()) {
			dataTable.storeToDisk();
		}
		super.writeExternal(out);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.CubeIndexed#printStats(java.io.PrintStream)
	 */
	@Override
	public void printStats(PrintStream out) {
		super.printStats(out);
		out.println("Total Reads: " + dataTable.getTotalReadCount());
		out.println("Swapped:     " + dataTable.isSwapped());
	}
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getCubeType()
	 */
	public CubeType getCubeType() {
		return CubeType.INDEXED_SWAP;
	}
	
}
