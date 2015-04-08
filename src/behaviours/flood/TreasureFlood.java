package behaviours.flood;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TreasureFlood extends AbstractFlood {
	private static final long serialVersionUID = 660947212581865645L;

	
	public TreasureFlood(String id, double myUtility){
		super(id, myUtility);
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getBestChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flood transmitFlood(String parentId, String parentPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParent(String parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAttribute(String attr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String attr, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(String child) {
		this.children.add(child);
	}

	@Override
	public boolean hasChild() {
		return !children.isEmpty();
	}

	@Override
	public boolean hasParent() {
		
		return (this.parent == null)?false:true;
	}

	@Override
	public void removeAll(Set<String> removeChildren) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBestId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAllUtilities() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUtility(String localName, int parseInt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUtility(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Flood clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
