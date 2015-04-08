package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RegisterChildrenBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -6476744038359331843L;
	private final long timeout = 100; //ms
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	private long begin;
	
	public RegisterChildrenBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
		this.begin = System.nanoTime();
	}

	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			String child = msg.getSender().getLocalName();
			this.agent.getFlood(this.protocol).addChild(child);
			
			final ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
			msgSend.setProtocol(this.protocol);
			msgSend.setSender(this.agent.getAID());
			msgSend.addReceiver(new AID(child, AID.ISLOCALNAME));
			agent.sendMessage(msgSend);
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
		if(finished && this.agent.getFlood(protocol).hasChild() && this.agent.getFlood(protocol).hasParent())
			this.agent.addBehaviour(new CatchEchoBehaviour(agent, protocol));
		
		else if(finished && !this.agent.getFlood(protocol).hasChild() && this.agent.getFlood(protocol).hasParent()){
			this.agent.getFlood(protocol).setUtility(this.agent.getCapacity() - this.agent.getBackPackFreeSpace());
			this.agent.addBehaviour(new TransmitEchoBehaviour(agent, protocol));
		}
		else if(finished & !this.agent.getFlood(protocol).hasChild() && !this.agent.getFlood(protocol).hasParent())
			this.agent.setStandBy(false);
		
		return finished;
	}

}
