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
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		Flood flood = this.agent.getFlood(protocol);
		if(msg != null){
			flood.setChildUtility(msg.getSender().getLocalName(), Double.parseDouble(msg.getContent()));
			if(flood.hasAllUtilities())
				finished = true;
		}else
			block();

	}

	@Override
	public boolean done() {
		if(finished){
			Flood flood = this.agent.getFlood(protocol);
			String best = flood.getBestId();
			if(flood.getBestValue() == 0){
				best = null;
			}
			

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
			
			if(!flood.hasParent() && best == null){	
				if(flood.getBestValue() == 0){
					System.out.println("Best utility for flood "+ protocol+" is 0. Nobody is elected");
				}else{
					ArrayList<String> path = new ArrayList<String>();
					flood.setAttribute("path", path);
					this.agent.elected(protocol);
				}
				this.agent.setStandBy(false);
			}
			else if(!flood.hasParent() && best != null){
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
				this.agent.setStandBy(false);
				this.agent.removeFlood(protocol);
				agent.sendMessage(msgAccept);
			}
			else if(flood.hasParent()){
				this.agent.addBehaviour(new TransmitEchoBehaviour(agent, protocol));
			}
		}
		return finished;
	}

}
