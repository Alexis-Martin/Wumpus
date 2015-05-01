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
 * Behaviour qui va permettre d'attendre le suiveur.
 */
public class WaitFollowerBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = -1173919798274334761L;
	private boolean finished = false;
	private HunterAgent agent;
	private long max_wait = 10000; //ms
	private long start;

	public WaitFollowerBehaviour(HunterAgent a) {
		super(a);
		agent = a;
		start = System.currentTimeMillis();
	}

	@Override
	public void action() {
		//on attend les messages de type INFORM_IF
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null && msg.getContent().equals("I'm here!")) {
			System.out.println("my follower " + msg.getSender().getLocalName() + " is here");
			List<Couple<String,List<Attribute>>> observe = agent.observe(agent.getCurrentPosition());
			String room = "";
			//on prend la première pice à 3 dans notre entourage
			for(Couple<String,List<Attribute>> c : observe){
				Node n = agent.getMap().getNode(c.getL());
				if(n.hasAttribute("well#") && (int)n.getAttribute("well#") == 3 && (!n.hasAttribute("well?") || !(boolean)n.getAttribute("well?"))){
					room = c.getL();
					break;
				}
			}
			//on envoie cette pièce à notre suiveur, sauf si il n'y en a aucune à trois, dans ce cas on arrête l'exploration
			final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
			reply.setSender(this.agent.getAID());	
			reply.addReceiver(msg.getSender());
			if(room.equals("")){
				reply.setContent("done");
				finished = true;
				agent.setWaitFollower(false);
				agent.setStandBy(false);
				return;
			}
			else{
				reply.setContent(room);
				agent.setNextMove(room);
			}
			agent.sendMessage(reply);
			finished = true;
			agent.setWaitFollower(false);
			agent.onExploration(true);
			agent.addBehaviour(new ExplorerBehaviour(agent));
		}
		else{
			long end = System.currentTimeMillis();
			if(end - start < max_wait)
				block(end-start);
			else{
				System.out.println(agent.getLocalName()+" stoped wainting for follower");
				finished = true;
				agent.setWaitFollower(false);
				agent.setStandBy(false);
				return;
			}
		}
		
		
	}

	@Override
	public boolean done() {
		return finished;
	}

}
