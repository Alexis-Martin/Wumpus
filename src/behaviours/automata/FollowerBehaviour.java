package behaviours.automata;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Etat de l'automate qui correspond au comportement déclenché par l'élection du FollowFlood
 * 
 * l'objectif de cette Behaviour est de se déplacer jusqu'à retrouver l'exploreur et lui envoie un message pour le prévenir..
 */

public class FollowerBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -8435309021907373359L;
	private HunterAgent agent;
	private int nextState;
	
	public FollowerBehaviour (HunterAgent a){
		super(a);
		this.agent = a;
	}
	
	
	@Override
	public void action() {
		
		String myPosition = agent.getCurrentPosition();
		String nextMove = agent.popStackMove();
		
		//observe l'environement
		List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
		boolean canMove = false;
		
		nextState = 0;
		
		//Si on est arrivé on envoi un message
		if(nextMove == null){
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM_IF);
			msg.setSender(this.agent.getAID());	
			System.out.println("l'agent " + agent.getLocalName() + " send to the explorer " + agent.getFollowingId() + " \"I'm here!\"");
			msg.addReceiver(new AID(agent.getFollowingId(), AID.ISLOCALNAME));
			
			msg.setContent("I'm here!");
			this.agent.sendMessage(msg);
			
			
			agent.setFollow(false);
			agent.onExploration(true);
			agent.setStandBy(true);
			
			nextState = 1;
			return;
		}
		
		//on marque notre case comme visité
		agent.getMap().getNode(myPosition).setAttribute("visited?", true);
		
		//on met à jour notre environement
		for(Couple<String,List<Attribute>> c:lobs){
			String pos = c.getL();
			if(pos.equals(myPosition)){
				Node n = agent.getMap().addRoom(pos, true, c.getR());
				agent.getDiff().addRoom(n);
				agent.getMap().updateLayout(n, true);
				continue;
			}
			if(pos.equals(nextMove)){
				canMove = true;
			}
			Node n = agent.getMap().addRoom(pos, false, c.getR());
			agent.getDiff().addRoom(n);
			if(agent.getMap().addRoad(myPosition, pos)){
				agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
			}
		}
		
		
		if(canMove){
			if(!agent.move(myPosition, nextMove)){
				System.out.println(agent.getLocalName()+" waiting for room "+nextMove+" to be released");
				agent.getStackMove().add(0, nextMove);
			}
		}
		//si on ne peut pas se déplacer on retourne dans le comportement de base
		else{
			System.out.println("Error : room " + nextMove+" is not in the neighborhood of agent "+agent.getLocalName()+ " ("+agent.getCurrentPosition()+")");
			agent.setFollow(false);
			nextState = 2;
		}
		block(1500);

	}

	@Override
	public int onEnd() {
		return nextState;
	}
}
