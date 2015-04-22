package behaviours.automata;

import mas.HunterAgent;
import behaviours.flood.Flood;
import behaviours.flood.RiskFlood;
import behaviours.flood.TransmitFloodBehaviour;
import behaviours.flood.TreasureFlood;
import jade.core.behaviours.OneShotBehaviour;

public class RiskBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -2397540306361699336L;
	private HunterAgent agent;
	private int nextState;
	
	public RiskBehaviour(HunterAgent a){
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
		
		String protocolId = "Risk_"+agent.getLocalName()+"_"+agent.getCurrentPosition();
		int capacity = agent.getCapacity();
		int quantity = capacity - agent.getBackPackFreeSpace();
		Flood flood = new RiskFlood(protocolId);
		flood.setAttribute("capacity", capacity);
		flood.setAttribute("quantity", quantity);
		flood.setParentId(null);
		flood.setParentPos(null);
		agent.addFlood(protocolId, flood);
		//ajouter params a flood
		System.out.println("\n\n\n\n\n\n" + agent.getLocalName() + " lance le flood pour la prise de risques "+protocolId);
		System.out.println(agent.getLocalName() + " (" + quantity + "/" + capacity + ") in the flood "+protocolId);

		agent.addBehaviour(new TransmitFloodBehaviour(agent, protocolId));
		agent.setStandBy(true);
	}
	
	@Override
	public int onEnd(){
		return nextState;
	}

}
