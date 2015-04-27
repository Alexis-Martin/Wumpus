package behaviours;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour qui gère le comportement d'un explorateur.
 * <br/>
 * <br/>On attend un message du follower, si le message est positif, on avance dans la case suivante et on envoi un message en disant qu'on est pas mort et avec le prochain déplacement
 */
public class ExplorerBehaviour extends SimpleBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1168642339376120222L;
	private boolean finished=false;
	private HunterAgent agent;


	public ExplorerBehaviour(HunterAgent a) {
		super(a);
		agent = a;
	}

	@Override
	public void action() {
		//on attend un message de type INFORM_IF
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null){
			//si le message est positif
			if(msg.equals("OK let's go!")){
				String myPosition = agent.getCurrentPosition();
				//on se déplace
				agent.move(myPosition, agent.getNextMove());
				//si on a plus de position (on est mort)
				if(agent.getCurrentPosition() == null){
					System.out.println("je suis mort!!");
					finished = true;
					return;
				}
				
				//sinon on observe l'environement
				myPosition = agent.getCurrentPosition();
				List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
				
				agent.getMap().getNode(myPosition).setAttribute("visited?", true);
				for(Couple<String,List<Attribute>> c:lobs){
					String pos = c.getL();
					if(pos.equals(myPosition)){
						Node n = agent.getMap().addRoom(pos, true, c.getR());
						agent.getDiff().addRoom(n);
						agent.getMap().updateLayout(n, true);
						continue;
					}

					Node n = agent.getMap().addRoom(pos, false, c.getR());
					agent.getDiff().addRoom(n);
					if(agent.getMap().addRoad(myPosition, pos)){
						agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
					}
				}
				//on met à jour la détection des puits
				agent.getMap().updateWell(myPosition, lobs);
				
				String room = "";
				//si il y a une pièce qui a une force 3 de puits on y va
				for(Couple<String,List<Attribute>> c : lobs){
					if(agent.getMap().getNode(c.getL()).hasAttribute("well#") && (int)agent.getMap().getNode(c.getL()).getAttribute("well#") == 3){
						room = c.getL();
						break;
					}
				}
				
				final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
				reply.setSender(this.agent.getAID());	
				reply.addReceiver(msg.getSender());
				//si il n'y a pas de pièce comme ça, on arrête l'exploration et on le signale à notre suiveur
				if(room.equals("")){
					reply.setContent("done");
					agent.setStandBy(false);
					agent.onExploration(false);
					finished = true;
				}
				//sinon on transmet la prochaine case et on attend à nouveau sa réponse
				else{
					reply.setContent(room);
					agent.setNextMove(room);
				}
				agent.sendMessage(reply);
				
				
				
				block(1500);
					
			}
			else{
				agent.setStandBy(false);
				agent.onExploration(false);
				finished = true;
			}
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
