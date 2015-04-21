package behaviours.flood;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

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
	
	@SuppressWarnings("unchecked")
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
					ArrayList<String> path = null;
					try {
						
						path = (ArrayList<String>) msg.getContentObject();
						ArrayList<String> path2 = agent.getMap().goTo(agent.getCurrentPosition(), path.get(path.size() - 1));
						if(path2 != null && !path2.isEmpty())
							path = path2;
						
						System.out.println(this.agent.getLocalName() + " vas prendre le tresor en " + path.get(path.size() - 1));
						
						System.out.println(agent.getLocalName() + " (C, q, T) = (" + flood.getAttribute("capacity") + ", " + flood.getAttribute("quantity") + ", " + flood.getAttribute("treasure") + ")");
					
						agent.setFollow(true);
						agent.setTreasure(true);
						agent.setStackMove(path);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
				}
				else{
					ArrayList<String> path;
					try {
						path = (ArrayList<String>) msg.getContentObject();
						ArrayList<String> path2 = agent.getMap().goTo(this.agent.getCurrentPosition(), this.agent.getFlood(protocol).getParentPos());
						for(int i = 1; i < path.size(); i++){
							path2.add(path.get(i));
						}
						final ACLMessage msgAccept = new ACLMessage(ACLMessage.REQUEST);
						msgAccept.setProtocol(this.protocol);
						msgAccept.setSender(this.agent.getAID());
										
						for(String child : flood.getChildren()){
							msgAccept.addReceiver(new AID(child, AID.ISLOCALNAME));
						}	
						msgAccept.setContentObject(path2);
						agent.sendMessage(msgAccept);
					} catch (UnreadableException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
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
