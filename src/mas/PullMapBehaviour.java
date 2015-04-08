package mas;

import java.util.HashMap;
import java.util.List;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class PullMapBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -487112501153551789L;
	private boolean finished=false;
	private HunterAgent agent;
	
	public PullMapBehaviour(final HunterAgent myagent) {
		super(myagent);
		this.agent = myagent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			//MessageTemplate.and(
				//MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM),
				//MessageTemplate.and(
				//		MessageTemplate.MatchProtocol(MyOntology.PAXOS_QUIT_COALITION),
				//		MessageTemplate.and(
				//				MessageTemplate.MatchLanguage(MyOntology.LANGUAGE),
				//				MessageTemplate.MatchOntology(MyOntology.ONTOLOGY_NAME))
				//)
		

		final ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null) {
			HashMap<String, List<String>> info;
			try {
				info = (HashMap<String, List<String>>) msg.getContentObject();
				agent.getMap().merge(SerializationHelper.deserializeMapInfo(info));
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
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