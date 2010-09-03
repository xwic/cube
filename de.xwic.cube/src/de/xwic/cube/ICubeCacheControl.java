/**
 * 
 */
package de.xwic.cube;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author lippisch
 *
 */
public interface ICubeCacheControl {

	/**
	 * Print cache details to the stream. The details
	 * are a list of keys in the cache, including the hit count
	 * and leaf count. Format:
	 * 
	 * [score]; [hit count]; [leaf count]; [unused count]; [key]
	 * 
	 * @param out
	 */
	public abstract void printCacheProfile(PrintStream out);

	/**
	 * Refresh and compact the cache. Updates unused counters on cells
	 * and removes cells from the cache if the cache exceeds a certain 
	 * size. 
	 */
	public abstract void refreshCache();

	/**
	 * @return the maxCacheSize
	 */
	public abstract int getMaxCacheSize();

	/**
	 * @param maxCacheSize the maxCacheSize to set
	 */
	public abstract void setMaxCacheSize(int maxCacheSize);

	/**
	 * Rebuild the cache based on cache statistics.
	 * @param openStream
	 * @throws IOException 
	 */
	public abstract void buildCacheFromStats(InputStream openStream) throws IOException;
	
	/**
	 * Rebuild the cache based on another cube.
	 * @param cube 
	 */
	public abstract void buildCacheFromCube(ICube cube);
	
	/**
	 * @return current size of the cache
	 */
	public abstract int getCacheSize();
	
	/**
	 * Clears the cache.
	 */
	public void clearCache();

}