package behaviours.automata;

import mas.HunterAgent;
import behaviours.flood.Flood;
import behaviours.flood.RiskFlood;
import behaviours.flood.SearchFollowerFlood;
import behaviours.flood.TransmitFloodBehaviour;
import behaviours.flood.TreasureFlood;
import jade.core.behaviours.OneShotBehaviour;
/**
 * Etat de l'automate qui lance le flood afin de trouver le meilleur agent pour suivre
 */
public class SearchFollowerBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -2397540306361699336L;
	private HunterAgent agent;
	private int nextState;
	
	public SearchFollowerBehaviour(HunterAgent a){
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		nextState = 0;
		if(agent.isInAFlood()){
			nextState = 1;
			block(1000);
			return;
		}
		
		String protocolId = "Follower_"+agent.getLocalName()+"_"+agent.getCurrentPosition();

		Flood flood = new SearchFollowerFlood(protocolId);
		//mise à zero comme ça aucun risque d'être choisi!
		flood.setAttribute("capacity", 0);
		flood.setAttribute("quantity", 0);
		flood.setAttribute("explorerId", agent.getLocalName());
		flood.setAttribute("explorerRoom", agent.getCurrentPosition());
		flood.setParentId(null);
		flood.setParentPos(null);
		agent.addFlood(protocolId, flood);
		//ajouter params a flood
		System.out.println("\n\n\n\n\n\n" + agent.getLocalName() + " lance le flood pour la recherche d'un suiveur "+protocolId);
		System.out.println(agent.getLocalName() + " (" + 0 + "/" + 0 + ") in the flood "+protocolId);
		
		//on lance le flood
		agent.addBehaviour(new TransmitFloodBehaviour(agent, protocolId));
		
		//on se met en attente de notre collègue
		agent.setStandBy(true);
		agent.setWaitFollower(true);
	}
	
	@Override
	public int onEnd(){
		return nextState;
	}

}
