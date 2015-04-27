package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
/**
 * Transmet l'echo, envoi notre utilité à notre père et attend sa réponse dans la behaviour CatchResult 
 */
public class TransmitEchoBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -6013275079546582660L;
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	
	public TransmitEchoBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
	}
	
	@Override
	public void action() {
		//on récupère notre utilité
		String message = this.agent.getFlood(protocol).transmitUtility();
		//on l'envoi
		final ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM_REF);
		msgSend.setProtocol(this.protocol);
		msgSend.setSender(this.agent.getAID());
		msgSend.addReceiver(new AID(this.agent.getFlood(protocol).getParentId(), AID.ISLOCALNAME));
		msgSend.setContent(message);
		agent.sendMessage(msgSend);
		//on attend sa réponse
		agent.addBehaviour(new CatchResultBehaviour(agent, protocol));
		this.finished = true;
	}

	@Override
	public boolean done() {
		return finished;
	}

}
