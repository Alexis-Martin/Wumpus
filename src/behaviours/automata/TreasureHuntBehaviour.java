package behaviours.automata;


import behaviours.flood.Flood;
import behaviours.flood.TransmitFloodBehaviour;
import behaviours.flood.TreasureFlood;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class TreasureHuntBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -2397540306361699336L;
	private HunterAgent agent;
	
	public TreasureHuntBehaviour(HunterAgent a){
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		
		String protocolId = agent.getLocalName()+"_"+agent.getCurrentPosition();
		int treasure_cap = agent.getMap().getNode(agent.getCurrentPosition()).getAttribute("treasure#");
		int capacity = agent.getCapacity();
		int quantity = capacity - agent.getBackPackFreeSpace();
		Flood flood = new TreasureFlood(protocolId);
		flood.setAttribute("treasure", treasure_cap);
		flood.setAttribute("capacity", capacity);
		flood.setAttribute("quantity", quantity);
		flood.setParentId(null);
		flood.setParentPos(null);
		agent.addFlood(protocolId, flood);
		//ajouter params a flood
		System.out.println("\n\n\n\n\n\n" + agent.getLocalName() + " lance le flood");
		System.out.println(agent.getLocalName() + " (C, q, T) = (" + capacity + ", " + quantity + ", " + treasure_cap + ")");

		agent.addBehaviour(new TransmitFloodBehaviour(agent, protocolId));
		agent.setStandBy(true);
	}
}
