package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * Attend les réponses des enfant pendant 100 ms et envoi un ack à chaque réponse
 */
public class RegisterChildrenBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -6476744038359331843L;
	private final long timeout = 500; //ms
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	private long begin;
	
	public RegisterChildrenBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
		this.begin = System.currentTimeMillis();
	}

	@Override
	public void action() {
		//lit les messages qui sont des acceptations pour le protocole "protocol"
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			//enregistre le fils
			String child = msg.getSender().getLocalName();
			this.agent.getFlood(this.protocol).addChild(child);
			//envoie un ack de confirmation
			final ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
			msgSend.setProtocol(this.protocol);
			msgSend.setSender(this.agent.getAID());
			msgSend.addReceiver(new AID(child, AID.ISLOCALNAME));
			agent.sendMessage(msgSend);
		}else{
			long t = (this.timeout + this.begin) - System.currentTimeMillis();
			if(t > 0){
				block(t);
			}else{
				this.finished = true;
			}
		}
	}

	@Override
	public boolean done() {
		//si on a terminer d'attendre et qu'on a des enfants on attend l'echo
		if(finished && this.agent.getFlood(protocol).hasChild())
			this.agent.addBehaviour(new CatchEchoBehaviour(agent, protocol));
		
		//si on a terminer et qu'on a pas d'enfant mais un parent, on transmet notre utilité 
		else if(finished && !this.agent.getFlood(protocol).hasChild() && this.agent.getFlood(protocol).hasParent()){
			Flood flood = this.agent.getFlood(protocol);
			int capacity = agent.getCapacity();
			int quantity = capacity - agent.getBackPackFreeSpace();
			flood.setAttribute("capacity", capacity);
			flood.setAttribute("quantity", quantity);
			this.agent.addBehaviour(new TransmitEchoBehaviour(agent, protocol));
		}
		//si on a terminer et qu'on a ni enfant, ni parent (on est la racine et tout seul) on ne fait rien, on part
		else if(finished && !this.agent.getFlood(protocol).hasChild() && !this.agent.getFlood(protocol).hasParent()){
			System.out.println("Personnes n'a répondu. "+ agent.getFlood(protocol).getId() +" finished.");
			this.agent.setStandBy(false);
			this.agent.removeFlood(protocol);
		}
		return finished;
	}

}
