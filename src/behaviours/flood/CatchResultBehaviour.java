package behaviours.flood;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CatchResultBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -2417706979155315948L;
	private boolean finished = false;
	private HunterAgent agent;
	private String protocol;
	
	public CatchResultBehaviour(HunterAgent agent, String protocol){
		super(agent);
		this.agent = agent;
		this.protocol = protocol;
	}
	
	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			Flood flood = agent.getFlood(protocol);
			if(msg.getContent().equals("dismiss")){
				final ACLMessage msgDismiss = new ACLMessage(ACLMessage.REQUEST);
				msgDismiss.setProtocol(this.protocol);
				msgDismiss.setSender(this.agent.getAID());
								
				for(String child : flood.getChildren()){
					msgDismiss.addReceiver(new AID(child, AID.ISLOCALNAME));
				}	
				msgDismiss.setContent("dismiss");
				agent.sendMessage(msgDismiss);
			}
			else{
				if(!flood.hasChild()){
					String lieu = msg.getContent().split("_")[1];
					System.out.println("je vais prendre le tresor en " + lieu);
					
					
				}
				else{
					final ACLMessage msgAccept = new ACLMessage(ACLMessage.REQUEST);
					msgAccept.setProtocol(this.protocol);
					msgAccept.setSender(this.agent.getAID());
									
					for(String child : flood.getChildren()){
						msgAccept.addReceiver(new AID(child, AID.ISLOCALNAME));
					}	
					msgAccept.setContent(msg.getContent());
					agent.sendMessage(msgAccept);
				}
			}
			agent.removeFlood(protocol);
			agent.setStandBy(false);
			finished = true;
		}else
			block();

	}

	@Override
	public boolean done() {
		return finished;
	}

}
