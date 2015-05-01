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
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
		ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null){
			//si le message est positif
			if(msg.getContent().equals("OK let's go!")){
				String myPosition = agent.getCurrentPosition();
				//on se déplace
				if(!agent.move(myPosition, agent.getNextMove())){
					System.out.println("Explorer "+agent.getLocalName()+" failded to move from "+ myPosition +" to "+agent.getNextMove());
				}

				List<Couple<String,List<Attribute>>> lobs;
				try{
					myPosition = agent.getCurrentPosition();
					lobs = agent.observe(myPosition);
				}
				catch(NullPointerException e){
					System.out.println("je suis mort");
					finished = true;
					return;
				}
				System.out.println(this.agent.getLocalName()+" is now in " + myPosition);
				
				
				agent.getMap().getNode(myPosition).setAttribute("visited?", true);
				agent.getMap().setWell(myPosition);
				//agent.getMap().getNode(myPosition).setAttribute("well#", 2);
				for(Couple<String,List<Attribute>> c:lobs){
					String pos = c.getL();
					if(pos.equals(myPosition)){
						Node n = agent.getMap().addRoom(pos, true, c.getR());
						agent.getMap().well(pos, false);
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
					Node n = agent.getMap().getNode(c.getL());
					if(n.hasAttribute("well#") && (int)n.getAttribute("well#") == 3 && (!n.hasAttribute("well?") || !(boolean)n.getAttribute("well?"))){
						room = c.getL();
						break;
					}
				}
				
				final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
				reply.setSender(this.agent.getAID());	
				reply.addReceiver(msg.getSender());
				//si il n'y a pas de pièce comme ça, on arrête l'exploration et on le signale à notre suiveur
				if(room.equals("")){
					System.out.println(agent.getLocalName()+" envoie done à " + msg.getSender().getLocalName());
					reply.setContent("done");
					agent.setStandBy(false);
					agent.onExploration(false);
					agent.setPushMap(10);
					finished = true;
				}
				//sinon on transmet la prochaine case et on attend à nouveau sa réponse
				else{
					System.out.println(agent.getLocalName()+" envoie le prochain pas " + room + " à " + msg.getSender().getLocalName());					
					reply.setContent(room);
					agent.setNextMove(room);
					agent.setPushMap(1);
				}
				
				agent.sendMessage(reply);
				
				
				
			//	block(1500);
					
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
