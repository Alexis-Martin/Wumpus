package behaviours.flood;

import java.io.IOException;
import java.util.ArrayList;

import mas.HunterAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Behaviour qui attend le résultat du flood et traite ces résultats
 */
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
		//lit les messages de type REQUEST avec comme protocol "protocol"
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchProtocol(this.protocol));
		final ACLMessage msg = agent.receive(msgTemplate);
		
		if(msg != null){
			
			Flood flood = agent.getFlood(protocol);
			//si on recoit dismiss
			if(msg.getContent().equals("dismiss")){
				//on dismiss nos fils
				final ACLMessage msgDismiss = new ACLMessage(ACLMessage.REQUEST);
				msgDismiss.setProtocol(this.protocol);
				msgDismiss.setSender(this.agent.getAID());
								
				for(String child : flood.getChildren()){
					msgDismiss.addReceiver(new AID(child, AID.ISLOCALNAME));
				}	
				msgDismiss.setContent("dismiss");
				agent.sendMessage(msgDismiss);
				//on part
				agent.removeFlood(protocol);
				agent.setStandBy(false);
			}
			//si on a une réponse positive
			else{
				//si on a pas de fils c'est qu'on est le meilleur
				if(!flood.hasChild()){
					ArrayList<String> path = null;
					try {
						//on récupère le chemin jusqu'à la case importante
						path = (ArrayList<String>) msg.getContentObject();
						//on essaie de se faire un chemin depuis notre environement jusqu'a la case importante
						ArrayList<String> path2 = agent.getMap().goTo(agent.getCurrentPosition(), path.get(path.size() - 1));
						
						if(path2 != null && !path2.isEmpty())
							path = path2;
						//si il existe pas on suit le chemin proposé par le flood
						else{
							ArrayList<String> path_to_father = agent.getMap().goTo(this.agent.getCurrentPosition(), this.agent.getFlood(protocol).getParentPos());
							for(int i = 0; i < path.size()-1; i++){
								path_to_father.add(path.get(i));
							}
							path = path_to_father;
						}
						
						flood.setAttribute("path", path);
						//on lance la méthode de l'agent qui va dire quoi faire en fonction du type de flood
						agent.elected(protocol);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
				}
				//si on a un fils c'est que c'est lui le meilleur
				else{
					//on récupère le chemin actuel et on ajoute le chemin jusqu'à notre père et on envoi ce chemin à notre fils
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
					//on retire le flood
					agent.removeFlood(protocol);
					agent.setStandBy(false);
				}
			}
			finished = true;
		}else
			block();

	}

	@Override
	public boolean done() {
		return finished;
	}

}
