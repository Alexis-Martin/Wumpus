package mas;



import jade.core.behaviours.FSMBehaviour;

import java.util.HashMap;
import java.util.Set;

import org.graphstream.graph.Edge;

import behaviours.automata.DecideMoveBehaviour;
import behaviours.automata.MoveBehaviour;
import behaviours.automata.ObserveBehaviour;
import behaviours.automata.StandByBehaviour;
import behaviours.automata.TreasureHuntBehaviour;
import behaviours.flood.CatchFloodBehaviour;
import behaviours.flood.Flood;
import env.Environment;

public class HunterAgent extends abstractAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7792554715666014751L;
	public static final int STAND_BY = 2; 
	private HashMap<String, String> teamMates;
	private Map map;
	private Map diff;
	private String nextMove;
	protected boolean randomWalk = false;
	protected boolean standBy = false;
	private HashMap<String, Flood> floods;
	private int capacity;
	
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1 set the agent attributes 
	 *	 		2 add the behaviours
	 *          
	 */
	@SuppressWarnings("unchecked")
	protected void setup(){

		super.setup();
		this.teamMates = new HashMap<String, String>();
		this.floods = new HashMap<String, Flood>();
		//Map UI settings
		this.map = new Map(this.getLocalName(), true);
		String stylesheet = "node {text-style:bold;fill-color:white;stroke-mode:plain;stroke-color:black;size:20;}"
				 + "edge.taken{fill-color:green;}"
				 + "edge.taken2{fill-color:red;}"
				 + "node.marker{fill-color:blue;text-color:white;}"
				 + "node.open{fill-color:grey;}"
				 + "node.treasure{stroke-mode:plain; stroke-color:yellow; stroke-width:3;}";
		map.addAttribute("ui.stylesheet", stylesheet);
//		this.capacity = getBackPackFreeSpace();
		diff = new Map(this.getLocalName()+"_diff", false);
		//diff.addAttribute("ui.stylesheet", stylesheet);
		
		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			realEnv = (Environment) args[0];
			realEnv.deployAgent(this.getLocalName());

		}else{
			System.out.println("Erreur lors du tranfert des parametres");
		}
		if(args[1]!=null){
			this.teamMates = (HashMap<String, String>) args[1];
		}
		
		map.addNode(this.getCurrentPosition());
		map.getNode(this.getCurrentPosition()).addAttribute("visited?", true);
		map.getNode(this.getCurrentPosition()).addAttribute("ui.label", this.getCurrentPosition());

		
		//Add the behaviours
		FSMBehaviour dispach_behaviour = new FSMBehaviour(this);
		dispach_behaviour.registerFirstState(new SynchronizeAgentBehaviour(this), "Sync");
		dispach_behaviour.registerState(new ObserveBehaviour(this), "Observe");
		dispach_behaviour.registerState(new DecideMoveBehaviour(this), "Decide");
		dispach_behaviour.registerState(new MoveBehaviour(this), "Move");
		dispach_behaviour.registerState(new TreasureHuntBehaviour(this), "TreasureHunt");
		dispach_behaviour.registerState(new StandByBehaviour(this), "StandBy");
		dispach_behaviour.registerLastState(new MessageBehaviour(this), "End");

		dispach_behaviour.registerDefaultTransition("Sync", "Observe");
		dispach_behaviour.registerDefaultTransition("Decide", "Move");
		dispach_behaviour.registerDefaultTransition("TreasureHunt", "StandBy");
		dispach_behaviour.registerTransition("Observe", "Decide", 0);
		dispach_behaviour.registerTransition("Observe", "TreasureHunt", 1);
		dispach_behaviour.registerTransition("Observe", "End", 10);
		dispach_behaviour.registerTransition("Observe", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("Move", "Observe", 0);
		dispach_behaviour.registerTransition("Move", "Decide", 1);
		dispach_behaviour.registerTransition("Move", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("StandBy", "StandBy", 0);
		dispach_behaviour.registerTransition("StandBy", "Decide", 1);
		
		addBehaviour(dispach_behaviour);
		addBehaviour(new PushMapBehaviour(this));
		addBehaviour(new PullMapBehaviour(this));
		addBehaviour(new CatchFloodBehaviour(this));
		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	@Override
	protected void takeDown(){
		System.out.println("Agent "+this.getLocalName()+" is DOWN!");
	}
	
	@Override
	public boolean move(String myPosition, String myDestination){
		boolean move = super.move(myPosition, myDestination);
		if(!move){
			return move;
		}
		Edge e = map.getEdge(this.map.getEdgeId(myPosition, myDestination));
		
		//update destination's visited? attribute
		map.getNode(myDestination).addAttribute("visited?", true);
		
		//update edge's taken attribute
		int taken = (Integer) e.getAttribute("taken#") + 1;
		e.addAttribute("taken#", taken);
		
		//update layout (position color of nodes + edge)
		this.map.updateUIMarkers(myPosition, myDestination);
		return move;
	}

	public String getNextMove() {
		return nextMove;
	}

	public void setNextMove(String nextMove) {
		this.nextMove = nextMove;
	}

	public void addFlood(String protocolId, Flood flood) {
		this.floods.put(protocolId, flood);
		System.out.println(getLocalName() + " ajoute le flood " + protocolId + " dans la HashMap");
		System.out.println("Le flood est : " + floods.get(protocolId));
	}

	public Flood getFlood(String protocole) {
		System.out.println(getLocalName() + " renvoi le flood " + floods.get(protocole));
		return floods.get(protocole);
	}

	public boolean containsFlood(String id) {
		return floods.containsKey(id);
	}
	
	public boolean isRandomWalk() {
		return randomWalk;
	}

	public void setRandomWalk(boolean randomWalk) {
		this.randomWalk = randomWalk;
	}

	public boolean isStandBy() {
		return standBy;
	}

	public void setStandBy(boolean standBy) {
		this.standBy = standBy;
	}

	public Map getMap() {
		return map;
	}

	public Map getDiff() {
		return diff;
	}
	
	public Set<String> getPartners(){
		return teamMates.keySet();
	}

	public boolean hasPartners() {
		
		return !teamMates.isEmpty();
	}

	public void removeFlood(String protocol) {
		System.out.println(getLocalName() + " supprime le flood " + protocol);
		this.floods.remove(protocol);
	}

	public int getCapacity() {
		return capacity;
	}

}