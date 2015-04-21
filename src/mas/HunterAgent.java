package mas;



import jade.core.behaviours.FSMBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;

import behaviours.automata.CatchTreasureBehaviour;
import behaviours.automata.DecideMoveBehaviour;
import behaviours.automata.GoToBehaviour;
import behaviours.automata.MoveBehaviour;
import behaviours.automata.ObserveBehaviour;
import behaviours.automata.ObserveTreasureBehaviour;
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
	private List<String> stackMove;
	protected boolean randomWalk = false;
	protected boolean standBy = false;
	private boolean catchTreasure = false;
	private boolean riskWell = false;
	private boolean follow = false;
	
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
		this.stackMove = new ArrayList<String>();
		//Map UI settings
		this.map = new Map(this.getLocalName(), false);
		String stylesheet = "node {text-style:bold;fill-color:white;stroke-mode:plain;stroke-color:black;size:20;}"
				 + "edge.taken{fill-color:green;}"
				 + "edge.taken2{fill-color:red;}"
				 + "node.marker{fill-color:blue;text-color:white;}"
				 + "node.open{fill-color:grey;}"
				 + "node.treasure{stroke-mode:plain; stroke-color:yellow; stroke-width:3;}"
				 + "node.well1{stroke-mode:plain; stroke-color:green; stroke-width:3;}"
				 + "node.well2{stroke-mode:plain; stroke-color:yellow; stroke-width:3;}"
				 + "node.well3{stroke-mode:plain; stroke-color:orange; stroke-width:3;}"
		         + "node.well4{stroke-mode:plain; stroke-color:red; stroke-width:3;}";
		map.addAttribute("ui.stylesheet", stylesheet);
		if(this.getLocalName().equals("Alpha"))
			map.display();
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
		dispach_behaviour.registerState(new ObserveTreasureBehaviour(this), "ObserveTreasure");
		dispach_behaviour.registerState(new GoToBehaviour(this), "goToTreasure");
		dispach_behaviour.registerState(new MoveBehaviour(this), "moveTreasure");
		dispach_behaviour.registerState(new CatchTreasureBehaviour(this), "catchTreasure");

		dispach_behaviour.registerLastState(new MessageBehaviour(this), "End");

		dispach_behaviour.registerDefaultTransition("Sync", "Observe");
		dispach_behaviour.registerDefaultTransition("TreasureHunt", "StandBy");
		dispach_behaviour.registerTransition("Observe", "Decide", 0);
		dispach_behaviour.registerTransition("Observe", "TreasureHunt", 1);
		dispach_behaviour.registerTransition("Observe", "End", 10);
		dispach_behaviour.registerTransition("Observe", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("Decide", "Move", 0);
		dispach_behaviour.registerTransition("Decide", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("Move", "Observe", 0);
		dispach_behaviour.registerTransition("Move", "Decide", 1);
		dispach_behaviour.registerTransition("Move", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("StandBy", "StandBy", 0);
		dispach_behaviour.registerTransition("StandBy", "Decide", 3);
		dispach_behaviour.registerTransition("StandBy", "ObserveTreasure", 1);
		dispach_behaviour.registerTransition("ObserveTreasure", "goToTreasure", 0);
		dispach_behaviour.registerTransition("ObserveTreasure", "catchTreasure", 4);
		dispach_behaviour.registerDefaultTransition("goToTreasure", "moveTreasure");
		dispach_behaviour.registerDefaultTransition("moveTreasure", "ObserveTreasure");
		dispach_behaviour.registerDefaultTransition("catchTreasure", "Decide");

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

	}

	public Flood getFlood(String protocole) {
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
		this.floods.remove(protocol);
	}

	public int getCapacity() {
		return capacity;
	}

	public boolean isTreasure() {
		return catchTreasure;
	}

	public void setTreasure(boolean b) {
		catchTreasure = b;
	}

	public boolean isRiskWell() {
		return riskWell;
	}

	public void setRiskWell(boolean b) {
		riskWell = b;
	}

	public boolean isFollow() {
		return follow;
	}
	
	public void setStackMove(List<String> stackMove){
		this.stackMove = stackMove;
	}
	
	public String popStackMove(){
		if(!stackMove.isEmpty()){
			String ret = stackMove.get(stackMove.size() - 1);
			stackMove.remove(stackMove.size() - 1);
			return ret;
		}
		return null;
	}
	public boolean stackMoveEmpty(){
		return stackMove.isEmpty();
	}

	public void setFollow(boolean b) {
		follow = b;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}