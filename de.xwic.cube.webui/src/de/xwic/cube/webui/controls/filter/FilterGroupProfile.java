package de.xwic.cube.webui.controls.filter;

/**
 * @author bogdanpandia
 *
 */
public class FilterGroupProfile {
	private final String name;
	private final String profile;
	
	/**
	 * @param name
	 * @param profile
	 */
	public FilterGroupProfile(String name, String profile) {
		super();
		this.name = name;
		this.profile = profile;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return
	 */
	public String getProfile() {
		return profile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterGroupProfile other = (FilterGroupProfile) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
	
	
	

}
