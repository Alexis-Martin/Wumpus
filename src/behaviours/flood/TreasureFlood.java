package behaviours.flood;

import java.util.List;

public class TreasureFlood implements Flood {
	private static final long serialVersionUID = 660947212581865645L;
	private String id;
	private List<String> children;
	private String parent;
	
	public TreasureFlood(String id){
		this.id = id;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
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

}
