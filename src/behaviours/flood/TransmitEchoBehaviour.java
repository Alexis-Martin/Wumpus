package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
		String message = this.agent.getFlood(protocol).transmitUtility();

		final ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM_REF);
		msgSend.setProtocol(this.protocol);
		msgSend.setSender(this.agent.getAID());
		msgSend.addReceiver(new AID(this.agent.getFlood(protocol).getParentId(), AID.ISLOCALNAME));
		msgSend.setContent(message);
		agent.sendMessage(msgSend);
		agent.addBehaviour(new CatchResultBehaviour(agent, protocol));
		this.finished = true;
	}

	@Override
	public boolean done() {
		return finished;
	}

}
