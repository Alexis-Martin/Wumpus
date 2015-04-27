package behaviours;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour qui gère le comportement du suiveur.
 * <br/>
 * <br/>On attend le message de l'explorateur et on enregistre la prochaine case ou il va se déplacer. On lui envoie ensuite une confirmation
 * <br/>
 * <br/>Si on a pas de réponse c'est qu'il est mort
 */
public class FollowExplorerBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7046436630180209687L;
	private boolean finished=false;
	private HunterAgent agent;

	public FollowExplorerBehaviour(HunterAgent a) {
		super(a);
		agent = a;
	}

	@Override
	public void action() {
		//on attend le message de l'exploreur
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null){
			//si le message est "done, on arrête l'exploration
			if(msg.getContent().equals("done")){
				agent.onExploration(false);
				agent.setStandBy(false);
				finished = true;
				return;
			}

			
			String myPosition = agent.getCurrentPosition();
			String explorerId = agent.getFollowingId();
			String explorerPosition = agent.getFollowingRoom();
			//on se déplace jusqu'a l'ancienne position de l'exploreur

			agent.move(myPosition, explorerPosition);

			//on observe notre environement
			myPosition = agent.getCurrentPosition();
			List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);

			
			agent.getMap().getNode(myPosition).setAttribute("visited?", true);
			//On  met à jour notre représentation du monde
			for(Couple<String,List<Attribute>> c:lobs){
				String pos = c.getL();
				if(pos.equals(myPosition)){
					agent.getMap().updateLayout(agent.getMap().getNode(pos), true);
					continue;
				}
				
				Node n = agent.getMap().addRoom(pos, false, c.getR());
				agent.getDiff().addRoom(n);
			
				if(pos.equals(explorerPosition)){
					Node explorerNext = agent.getMap().addRoom(msg.getContent(), false);
					if(agent.getMap().addRoad(explorerPosition, explorerNext.getId()))
						agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(explorerPosition, explorerNext.getId())));
						
				}
				
				if(agent.getMap().addRoad(myPosition, pos)){
					agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
				}
				
			}
			//on met à jour la position de l'exploreur
			agent.setFollowing(explorerId, msg.getContent());
			//on confirme son déplacement
			final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
			reply.setSender(this.agent.getAID());	
			reply.addReceiver(new AID(explorerId,  AID.ISLOCALNAME));
			reply.setContent("OK let's go!");
			//send message
			System.out.println("send message OK let's go! to the explorer " + explorerId);
			agent.sendMessage(reply);
			
			
		}
		else{
			block();
		}

	}

	@Override
	public boolean done() {
		return finished;
	}

}
