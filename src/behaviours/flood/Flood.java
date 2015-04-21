package behaviours.flood;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public interface Flood extends Serializable {
	public String getMessage();
	
	public String getParentId();
	
	public String getParentPos();
	
	public Set<String> getChildren();
	
	public String getId();
	
	public String getBestChild();
	
	public Flood transmitFlood(String parentId, String parentPos);
	
	public String transmitUtility();

	public void setParentId(String parent);
	
	public Object getAttribute(String attr);
	
	public void setAttribute(String attr, Object obj);

	public void addChild(String child);

	public boolean hasChild();

	public boolean hasParent();

	public void removeAll(Set<String> removeChildren);

	public String getBestId();

	public boolean hasAllUtilities();
	
	public HashMap<String, Object> getAttributes();
	
	public void setChildUtility(String localName, double utility);
	
	public double getMyUtility();
	
	void setParentPos(String parentPos);

	public void removeAllChild();

	public Flood clone();

}
