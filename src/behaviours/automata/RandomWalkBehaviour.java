package behaviours.automata;

import java.util.List;
import java.util.Random;

import mas.HunterAgent;

import org.graphstream.graph.Node;

import behaviours.flood.FloodBehaviour;
import env.Attribute;
import env.Environment;
import env.Environment.Couple;
import jade.core.behaviours.TickerBehaviour;

public class RandomWalkBehaviour extends TickerBehaviour {

	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;

	//private Environment realEnv;
	private HunterAgent agent;
	
	public RandomWalkBehaviour (final HunterAgent myagent,Environment realEnv) {
		super(myagent, 1000);
		//this.realEnv=realEnv;
		this.agent = myagent;
	}

	@Override
	protected void onTick() {
		String myPosition = agent.getCurrentPosition();
		String log = "";
		log += "\nPosition of agent "+agent.getLocalName()+" : "+myPosition;
		List<String> roomNotOpen;
		List<String> roadNotTaken;
		if (myPosition!=""){
			List<Couple<String,List<Attribute>>> lobs = agent.observe(myPosition);
			//Update current Room
			agent.getMap().getNode(myPosition).setAttribute("visited?", true);
			
			//Update Map
			for(Couple<String,List<Attribute>> c:lobs){
				String pos = c.getL();
				//Skip the current room
				if(pos.equals(myPosition)){
					Node n = agent.getMap().addRoom(pos, true, c.getR());
					agent.getDiff().addRoom(n);
					if (n.hasAttribute("treasure#") && (int) n.getAttribute("treasure#") > 0){
						agent.addBehaviour(new FloodBehaviour(agent));
						this.stop();
					}
					continue;
				}
				
				//Try to add a new room to the graph
				Node n = agent.getMap().addRoom(pos, false, c.getR());
				if(agent.getMap().addRoad(myPosition, pos)){
					agent.getDiff().addRoom(n);
					agent.getDiff().addRoad(agent.getMap().getEdge(agent.getMap().getEdgeId(myPosition, pos)));
				}
			}
			
			/*//Checking map completeness
			if(agent.map.checkMapCompleteness()){
				System.out.println(agent.getLocalName()+" discovered the map!");
				//TODO:Send "stop searching" message
				this.stop();
				return;
			}*/

			//Decide the next move
			Random r= new Random();
			String myDestination;
			int moveId;
			roomNotOpen = agent.getMap().roomNotOpen(lobs, myPosition);
			if(roomNotOpen.size() > 0){ //Choice 1 : Room Not Open
				moveId = r.nextInt(roomNotOpen.size());
				myDestination = roomNotOpen.get(moveId);
			}else{
				roadNotTaken = agent.getMap().roadNotTaken(lobs, myPosition);
				if(roadNotTaken.size() > 0){ //Choice 2 : Road Not Taken
					moveId = r.nextInt(roadNotTaken.size());
					myDestination = roadNotTaken.get(moveId);
				}else{	//Choice 3 : anywhere... (not cool => TODO:insert path-finding here)
					do{
						moveId = r.nextInt(lobs.size());
					}while(moveId == 0);
					myDestination = lobs.get(moveId).getL();
				}
			}
			
			//Move
			log += "\nAgent "+agent.getLocalName()+" is heading to position "+myDestination;
			int k=0; //pas jolie jolie
			while(!agent.move(myPosition, myDestination) && k<10){// If there is already an agent in the room, go anywhere
				log += "\nAgent "+agent.getLocalName()+" Cannot go into a full room";
				do{
					moveId = r.nextInt(lobs.size());
				}while(lobs.get(moveId).getL().equals(myPosition) || lobs.get(moveId).getL().equals(myDestination));
				log += "\nAgent "+agent.getLocalName()+" is heading to "+lobs.get(moveId).getL()+" instead";
				myDestination = lobs.get(moveId).getL();
				k++;
			}
		}
		//System.out.println(log);
	}
	
}
