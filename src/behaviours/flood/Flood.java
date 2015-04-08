package behaviours.flood;

import java.io.Serializable;
import java.util.List;

public interface Flood extends Serializable {
	public String getMessage();
	
	public String getParentId();
	
	public String getParentPos();
	
	public List<String> getChildren();
	
	public String getId();
	
	public String getBestChild();
	
	public Flood transmitFlood(String parentId, String parentPos);

	public void setParent(String parent);
	
	public Object getAttribute(String attr);
	
	public void setAttribute(String attr, Object obj);

	public void addChild(String child);
}
