package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Attend les utilités de tous ces fils. Dès qu'il les a toutes il va transmettre à son tour l'echo et dismiss tous les fils qui sont mauvais
 */
public class CatchEchoBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -8002056159166717857L;
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	
	public CatchEchoBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
	}
	
	@Override
	public void action() {
		//catch les messages de type INFORM_REF avec le protocol "protocol"
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		Flood flood = this.agent.getFlood(protocol);
		if(msg != null){
			//met a jour l'utilité du fils
			flood.setChildUtility(msg.getSender().getLocalName(), Double.parseDouble(msg.getContent()));
			//si on a toutes les utilités on termine
			if(flood.hasAllUtilities())
				finished = true;
		}else
			block();

	}

	@Override
	public boolean done() {
		
		if(finished){
			//si aucun agent n'est bon pour faire cette tache (dans tous nos fils et nous même on le note
			Flood flood = this.agent.getFlood(protocol);
			String best = flood.getBestId();
			if(flood.getBestValue() == 0){
				best = null;
			}
			
			//on dismiss tous les mauvais
			final ACLMessage msgDismiss = new ACLMessage(ACLMessage.REQUEST);
			msgDismiss.setProtocol(this.protocol);
			msgDismiss.setSender(this.agent.getAID());
			
			Set<String> removeChildren = new HashSet<String>();
			
			for(String child : flood.getChildren()){
				if(!child.equals(best)){
					msgDismiss.addReceiver(new AID(child, AID.ISLOCALNAME));
					removeChildren.add(child);
				}
			}	
			flood.removeAll(removeChildren);
			msgDismiss.setContent("dismiss");
			agent.sendMessage(msgDismiss);
			
			//si on a pas de parents et que best est null
			if(!flood.hasParent() && best == null){
				//si il n'y a pas de meilleur (personne n'est élu) on le signale
				if(flood.getBestValue() == 0){
					agent.reset();

					System.out.println("Best utility for flood "+ protocol+" is 0. Nobody is elected");
				}
				//sinon on est le meilleur (on est élu)
				else{
					ArrayList<String> path = new ArrayList<String>();
					flood.setAttribute("path", path);
					this.agent.elected(protocol);
				}
				this.agent.setStandBy(false);
			}
			//si on n'a pas de parent et que best n'est pas égale a null
			else if(!flood.hasParent() && best != null){
				//on envoi un message pour transmettre la victoire du meilleur agent et le chemin qu'il doit prendre pour rejoindre la case importante
				final ACLMessage msgAccept = new ACLMessage(ACLMessage.REQUEST);
				msgAccept.setProtocol(this.protocol);
				msgAccept.setSender(this.agent.getAID());
				msgAccept.addReceiver(new AID(best, AID.ISLOCALNAME));
				ArrayList<String> path = new ArrayList<String>();
				path.add(this.agent.getCurrentPosition());
				try {
					msgAccept.setContentObject(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//on a fini notre travail, on part et on supprime le flood
				this.agent.setStandBy(false);
				this.agent.removeFlood(protocol);
				agent.sendMessage(msgAccept);
			}
			//si on a un parent
			else if(flood.hasParent()){
				//on transmet l'echo
				this.agent.addBehaviour(new TransmitEchoBehaviour(agent, protocol));
			}
		}
		return finished;
	}

}
