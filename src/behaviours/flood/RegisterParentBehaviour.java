package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RegisterParentBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -5673786944992246974L;
	private final long timeout = 100; //ms
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	private long begin;
	private boolean receive;
	
	public RegisterParentBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
		this.begin = System.nanoTime();
		this.receive = false;
	}

	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			agent.addBehaviour(new TransmitFloodBehaviour(agent, protocol));
			finished = true;
			receive = true;
		}else{
			long t = (this.timeout*1000 + this.begin) - System.nanoTime();
			if(t > 0){
				block(t);
			}else{
				this.finished = true;
			}
		}
	}

	@Override
	public boolean done() {
		if(!receive){
			this.agent.removeFlood(protocol);
			this.agent.setStandBy(false);
		}
		return finished;
	}

}
