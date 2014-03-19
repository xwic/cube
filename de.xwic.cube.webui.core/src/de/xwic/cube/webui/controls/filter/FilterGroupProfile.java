package de.xwic.cube.webui.controls.filter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	
	public static List<FilterGroupProfile> deserialize(String jsonSerialization) throws JSONException{
		List<FilterGroupProfile> profiles = new ArrayList<FilterGroupProfile>();
		JSONArray array = new JSONArray(jsonSerialization);
		for(int i=0;i<array.length();i++){
			JSONObject o = array.getJSONObject(i);
			String name = o.getString("name");
			String profile = o.getString("profile");
			profiles.add(new FilterGroupProfile(name, profile));
		}
		return profiles;
	}
	
	public static String serialize(List<FilterGroupProfile> groups) throws JSONException{
		JSONArray object = new JSONArray();
		for(FilterGroupProfile group : groups){
			JSONObject o = new JSONObject();
			o.put("name", group.getName());
			o.put("profile", group.getProfile());
			object.put(o);
		}
		return object.toString();
	}
	
	
	

}
