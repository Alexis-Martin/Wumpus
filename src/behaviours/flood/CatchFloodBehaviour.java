package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CatchFloodBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -583348112306128893L;
	private boolean finished = false;
	private HunterAgent agent;
	
	public CatchFloodBehaviour(HunterAgent agent){
		super(agent);
		this.agent = agent;
	}

	@Override
	public void action() {
		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), MessageTemplate.MatchProtocol("flood"));
		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null) {
			this.agent.setStandBy(true); 
			System.out.println(this.agent.getLocalName() + " receptionne le message de flood");
			Flood flood;
			try {
				flood = (Flood) msg.getContentObject();
				flood.setUtility(agent.getBackPackFreeSpace());
				if(!this.agent.containsFlood(flood.getId()) && !agent.getMap().goTo(agent.getCurrentPosition(), flood.getParentPos()).isEmpty()){
					this.agent.addFlood(flood.getId(), flood);
					System.out.println(this.agent.getLocalName() + " ajoute le flood");
					
				}else{
					block();
					return;
				}
				
				System.out.println(this.agent.getLocalName() + " envoi le ack");

				final ACLMessage msgSend = new ACLMessage(ACLMessage.AGREE);
				msgSend.setProtocol(flood.getId());
				msgSend.setSender(this.agent.getAID());
				msgSend.addReceiver(new AID(flood.getParentId(), AID.ISLOCALNAME));
				agent.sendMessage(msgSend);
				
				agent.addBehaviour(new RegisterParentBehaviour(agent, flood.getId()));
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
