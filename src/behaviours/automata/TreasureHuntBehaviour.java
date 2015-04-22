package behaviours.automata;


import behaviours.flood.Flood;
import behaviours.flood.TransmitFloodBehaviour;
import behaviours.flood.TreasureFlood;
import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class TreasureHuntBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -2397540306361699336L;
	private HunterAgent agent;
	private int nextState;
	
	public TreasureHuntBehaviour(HunterAgent a){
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		nextState = 0;
		if(agent.isInAFlood()){
			nextState = 1;
			return;
		}
		
		String protocolId = agent.getLocalName()+"_"+agent.getCurrentPosition();
		int treasure_cap = agent.getMap().getNode(agent.getCurrentPosition()).getAttribute("treasure#");
		int capacity = agent.getCapacity();
		int quantity = capacity - agent.getBackPackFreeSpace();
		Flood flood = new TreasureFlood(protocolId, Flood.TreasureHunt);
		flood.setAttribute("treasure", treasure_cap);
		flood.setAttribute("capacity", capacity);
		flood.setAttribute("quantity", quantity);
		flood.setParentId(null);
		flood.setParentPos(null);
		agent.addFlood(protocolId, flood);
		//ajouter params a flood
		System.out.println("\n\n\n\n\n\n" + agent.getLocalName() + " lance le flood pour la chasse au trésor "+protocolId);
		System.out.println(agent.getLocalName() + " (C, q, T) = (" + capacity + ", " + quantity + ", " + treasure_cap + ") in the flood "+protocolId);

		agent.addBehaviour(new TransmitFloodBehaviour(agent, protocolId));
		agent.setStandBy(true);
	}
	
	@Override
	public int onEnd(){
		return nextState;
	}
}
