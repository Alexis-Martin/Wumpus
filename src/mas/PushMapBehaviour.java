package mas;

import java.io.IOException;

import org.graphstream.graph.Node;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class PushMapBehaviour extends TickerBehaviour {
	private static final long serialVersionUID = -2009988805814997107L;
	private HunterAgent agent;
	private final int k = 5;
	private int i = 0;
	
	public PushMapBehaviour (final HunterAgent myagent) {
		
		super(myagent, 5000);
		//this.realEnv=realEnv;
		agent = myagent;
	}
	
	@Override
	protected void onTick() {
		if(((agent.getDiff().getEdgeSet().size() > k || i > 10) && agent.getDiff().getNodeSet().size() > k) || i > k*k){
			
			//update attributes
			for(Node n : agent.getDiff().getNodeSet()){
				Node node = agent.getMap().getNode(n.getId());
				for(String attr:node.getAttributeKeySet()){
					if(attr.contains("ui")){
						continue;
					}
					n.addAttribute(attr, node.getAttribute(attr));
				}
			}
			
			//Send graph update to team mates
			if(agent.hasPartners()){
				final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(agent.getAID());
				for(String aId:agent.getPartners()){
					msg.addReceiver(new AID(aId, AID.ISLOCALNAME));
				}
				try {
					msg.setContentObject(SerializationHelper.serializeMapInfo(agent.getDiff()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				agent.send(msg);
				agent.getDiff().clear();
				i++;
			}else{
				stop();
			}
		}

	}

}