package behaviours.flood;

import java.io.IOException;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class TransmitFloodBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = 791926463639889149L;
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	
	public TransmitFloodBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
	}

	@Override
	public void action() {
		final ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
		msg.setProtocol("flood");
		msg.setSender(this.agent.getAID());		
		for(String partner : this.agent.getPartners()){
			msg.addReceiver(new AID(partner, AID.ISLOCALNAME));
		}
		try {
			msg.setContentObject(agent.getFlood(protocol).transmitFlood(agent.getLocalName(), agent.getCurrentPosition()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.agent.sendMessage(msg);
		this.agent.addBehaviour(new RegisterChildrenBehaviour(agent, protocol));
		finished = true;
	}

	@Override
	public boolean done() {
		return finished;
	}

}
