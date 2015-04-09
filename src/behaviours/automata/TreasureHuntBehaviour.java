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
		Flood flood = new TreasureFlood(protocolId, agent.getBackPackFreeSpace());
		flood.setParentId(null);
		flood.setParentPos(null);
		agent.addFlood(protocolId, flood);
		//ajouter params a flood
		System.out.println(agent.getLocalName() + " lance le flood");
		agent.addBehaviour(new TransmitFloodBehaviour(agent, protocolId));
		agent.setStandBy(true);
	}
}
