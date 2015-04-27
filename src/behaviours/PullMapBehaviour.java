package behaviours;

import java.util.HashMap;
import java.util.List;

import mas.HunterAgent;
import mas.Map;
import mas.SerializationHelper;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Récupération des messages de représentation du monde des autres HunterAgent.
 *<br/>
 *<br/> Fait parti des comportements de Base du HunterAgent
 *<br/>
 *<br/>Cette messagerie est réveillé à la reception d'un message et traite les messages de type INFORM. 
 */
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
			agent.setStandBy(false); //test
			HashMap<String, List<String>> info;
			try {
				info = (HashMap<String, List<String>>) msg.getContentObject();
				Map msgMap = SerializationHelper.deserializeMapInfo(info);
				agent.getMap().merge(msgMap);
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