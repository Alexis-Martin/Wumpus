package behaviours;

import java.util.List;

import org.graphstream.graph.Node;

import env.Attribute;
import env.Environment.Couple;
import mas.HunterAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour qui gère le comportement du suiveur.
 * <br/>
 * <br/>On attend le message de l'explorateur et on enregistre la prochaine case ou il va se déplacer. On lui envoie ensuite une confirmation
 * <br/>
 * <br/>Si on a pas de réponse c'est qu'il est mort
 */
public class FollowExplorerBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 7046436630180209687L;
	private boolean finished=false;
	private HunterAgent agent;
	private long max_time = 2000;
	private long start;
	private String nextMove;

	public FollowExplorerBehaviour(HunterAgent a) {
		super(a);
		agent = a;
		start = System.currentTimeMillis();
		nextMove = agent.getFollowingRoom();
	}

	@Override
	public void action() {
		//on attend le message de l'exploreur
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
		ACLMessage msg = agent.receive(msgTemplate);
		if (msg != null){
			
			
			agent.setFollow(false);
			//si le message est "done, on arrête l'exploration
			System.out.println("le suiveur " + agent.getLocalName() + " recoit le message " + msg.getContent());
			if(msg.getContent().equals("done")){
				System.out.println("c'est terminé je m'en vais");
				agent.onExploration(false);
				agent.setStandBy(false);
				finished = true;
				return;
			}

			
			String myPosition = agent.getCurrentPosition();
			String explorerId = agent.getFollowingId();
			String explorerPosition = agent.getFollowingRoom();
			
			//on se déplace jusqu'a l'ancienne position de l'exploreur
			System.out.println("le suiveur " + agent.getLocalName() + "se déplace en " + explorerPosition);
			if(!agent.move(myPosition, nextMove)){
				
				System.out.println("le follower n'a pas reussi a se déplacer");
				List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
				boolean near_me = false;
				for(Couple<String, List<Attribute>> attr : lobs){
					if(attr.getL().equals(explorerPosition)){
						near_me = true;
						break;
					}
					
				}
				if(!near_me){
					final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
					reply.setSender(this.agent.getAID());	
					reply.addReceiver(new AID(explorerId,  AID.ISLOCALNAME));
					reply.setContent("done");
					agent.sendMessage(reply);
					agent.onExploration(false);
					agent.setStandBy(false);
					finished = true;
					return;

				}
			}
			else{
				nextMove = explorerPosition;
			}

			//on observe notre environement
			myPosition = agent.getCurrentPosition();
			List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);

			
			agent.getMap().getNode(myPosition).setAttribute("visited?", true);
			agent.getMap().getNode(myPosition).setAttribute("treasure#", 0);
			//On  met à jour notre représentation du monde
			for(Couple<String,List<Attribute>> c:lobs){
				String pos = c.getL();
				if(pos.equals(myPosition)){
					Node n = agent.getMap().addRoom(pos, true, c.getR());
					if(n.hasAttribute("treasure#") && (int) n.getAttribute("treasure#") > 0 && agent.getBackPackFreeSpace() > 0){
						agent.pick();
					}
					/*agent.getMap().updateLayout(agent.getMap().getNode(pos), true);
					for(Attribute att : c.getR()){
						if(att.getName().equals("Treasure") && agent.getBackPackFreeSpace() > 0){
							agent.pick();
						}
					}
					*/
					continue;
				}
				
				Node n = agent.getMap().addRoom(pos, false, c.getR());
				agent.getDiff().addRoom(n);
			
				if(pos.equals(explorerPosition)){
					Node explorerNext = agent.getMap().addRoom(msg.getContent(), false);
					agent.getDiff().addRoom(explorerNext);
					if(agent.getMap().addRoad(explorerPosition, explorerNext.getId()))
						agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(explorerPosition, explorerNext.getId())));
						
				}
				
				if(agent.getMap().addRoad(myPosition, pos)){
					agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
				}
				
			}
			agent.getMap().updateWell(myPosition, lobs);
			
			//on met à jour la position de l'exploreur
			agent.setFollowing(explorerId, msg.getContent());
			//on confirme son déplacement
			final ACLMessage reply = new ACLMessage(ACLMessage.INFORM_IF);
			reply.setSender(this.agent.getAID());	
			reply.addReceiver(new AID(explorerId,  AID.ISLOCALNAME));
			reply.setContent("OK let's go!");
			start = System.currentTimeMillis();
			//send message
			System.out.println("send message OK let's go! to the explorer " + explorerId);
			agent.sendMessage(reply);
			
			
		}
		else{
			long stop = System.currentTimeMillis();
			if(stop - start < max_time)
				block(stop - start);
			else if(agent.isFollowing()){
				System.out.println("il n'est plus là");
				agent.onExploration(false);
				agent.setStandBy(false);
				agent.setFollow(false);
				finished = true;
				return;
			}
			else{
				System.out.println("he's dead");
				agent.getMap().well(agent.getFollowingRoom(), true);
				agent.setAgentDead(agent.getFollowingId());
				agent.setPushMap(10);
				agent.getDiff().addRoom(agent.getMap().getNode(agent.getFollowingRoom()));
				agent.addBehaviour(new ReportDeathBehaviour(agent));
				agent.pushLoss(10);
				agent.onExploration(false);
				agent.setStandBy(false);
				finished = true;
				return;
			}
		}

	}

	@Override
	public boolean done() {
		return finished;
	}

}
