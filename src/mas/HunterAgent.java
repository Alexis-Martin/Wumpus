package mas;

import jade.core.behaviours.FSMBehaviour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import behaviours.CheckDeathBehaviour;
import behaviours.PullMapBehaviour;
import behaviours.PushMapBehaviour;
import behaviours.automata.DecideMoveBehaviour;
import behaviours.automata.ExploreBehaviour;
import behaviours.automata.FetchTreasureBehaviour;
import behaviours.automata.FollowerBehaviour;
import behaviours.automata.MoveBehaviour;
import behaviours.automata.ObserveBehaviour;
import behaviours.automata.RiskBehaviour;
import behaviours.automata.SearchFollowerBehaviour;
import behaviours.automata.StandByBehaviour;
import behaviours.automata.SynchronizeAgentBehaviour;
import behaviours.automata.TreasureHuntBehaviour;
import behaviours.flood.CatchFloodBehaviour;
import behaviours.flood.Flood;
import env.Environment;

/**
 *Un HunterAgent est un agent jade spécialisé dans la recherche et la récupération de trésor en coopération dans un environnement hostile.
 *<br/>
 *<br/>Chaque HunterAgent a un nom de code (Alpha, Bravo, ...) afin de masquer leurs identités!
 *<br/>Le comportement de base d'un HunterAgent lorsqu'il arrive dans un nouvel environnement est de commencer par scanner celui ci. Si les alentours sont trop dangeureux, il attend une transmition amicale, sinon la quête commence!
 *<br/>
 *<br/>Un HunterAgent a un comportement de base et des comportements alternatifs qui sont lancés lors d'évènement particulier (trésors, wumpus, ...)
 *<br/>
 *<br/>Le comportement de base:
 *<br/>Durant sa recherche le HunterAgent
 *<br/>- se déplace
 *<br/>- observe
 *<br/>- met à jour ça représentation du monde avec ce qu'il a observé
 *<br/>- envoi son observation aux autres HunterAgents
 *<br/>- met à jour sa représentation du monde avec les informations envoyer par ces collègues.
 *<br/>
 *<br/>Si Le HunterAgent rencontre un trésor:
 *<br/>- il lance un flood pour savoir qui est autour de lui.
 *<br/>- si c'est lui le meilleur il ramasse sinon il envoi un message au meilleur HunterAgent pour ramasser le trésor.
 *<br/>
 *<br/>Si le HunterAgent est choisi pour ramasser le trésor:
 *<br/>- il construit son chemin pour aller jusqu'au trésor
 *<br/>- il ramasse le trésor
 *<br/>
 *<br/>Si le HunterAgent a visité toute la carte et que tous les trésors sont ramassé, il peut vouloir lancer une prise de risque pour découvrir les zones dangeureuses.
 *<br/>- il lance un flood pour savoir qui est autour de lui.
 *<br/>- si c'est lui le meilleur il rentre dans un autre comportement décrit ci-après sinon il envoi un message au mieux placer pour prendre des risque.
 *<br/>
 *<br/>Si un HunterAgent est choisi pour prendre des risques:
 *<br/>- il va jusqu'à la case critique et lance un flood pour que quelqu'un le couvre.
 *<br/>- si personne ne répond ou que c'est lui le meilleur, il part.
 *<br/>- sinon il attend son collègue. 
 *<br/>
 *<br/>Si le HunterAgent est choisi pour suivre un explorateur:
 *<br/>- il va jusqu'a la case critique et envoi un message à son collègue.
 *<br/>
 *<br/>Les deux hunteragents continue de s'envoyer des message pendant toute la prise de risque.
 *
 */

public class HunterAgent extends abstractAgent {
	

	private static final long serialVersionUID = -7792554715666014751L;
	public static final int STAND_BY = 2; //etat stand_by de l'automate
	private HashMap<String, Boolean> teamMates; //collegues (nom, vivant?)
	private Map map; //représentation du monde
	private Map diff; //ce qui a été découvert depuis le dernier transmit
	private String nextMove; //prochain déplacement
	private List<String> stackMove; //liste des mouvements à réaliser
	protected boolean randomWalk = false; //déplacement aléatoire, si on est bloqué par exemple
	protected boolean standBy = false; //on fait une pause, pour participer à un flood par exemple
	private boolean catchTreasure = false; //résultat du flood, true si on a été élu pour rammasser le trésor
	private boolean risk = false; //résultat du flood, true si on a été élu pour explorer les zones dangereuses
	private boolean follow = false; //résultat du flood, true si on a été élu pour suivre l'explorateur
	private String followingId= null; //nom de l'explorateur
	private String followingRoom= null; //piece dans laquelle l'explorateur va
	private int nbStep; //aucune idée
	
	private HashMap<String, Flood> floods; //floods en cours
	private int capacity; //taille du sac
	private int pushMap = 0;
	private int pushLoss = 0;
	private boolean waitFollower = false; //a été élu pour prendre un risque et attend son suiveur
	private boolean explorate = false; //en phase d'exploration, vrai si on est exploreur ou suiveur
	
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
		this.teamMates = new HashMap<String, Boolean>();
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
			this.teamMates = (HashMap<String, Boolean>) args[1];
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
		dispach_behaviour.registerState(new FetchTreasureBehaviour(this), "FetchTreasure");
		dispach_behaviour.registerState(new RiskBehaviour(this), "Risk");
		dispach_behaviour.registerState(new ExploreBehaviour(this), "Explore");
		dispach_behaviour.registerState(new SearchFollowerBehaviour(this), "SearchFollower");
		dispach_behaviour.registerState(new FollowerBehaviour(this), "Follow");
		
		dispach_behaviour.registerLastState(new MessageBehaviour(this), "End");

		dispach_behaviour.registerDefaultTransition("Sync", "Observe");
		dispach_behaviour.registerTransition("Observe", "Decide", 0);
		dispach_behaviour.registerTransition("Observe", "TreasureHunt", 1);
		dispach_behaviour.registerTransition("Observe", "Risk", 3);
		dispach_behaviour.registerTransition("Observe", "End", 10);
		dispach_behaviour.registerTransition("Observe", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("Decide", "Move", 0);
		dispach_behaviour.registerTransition("Decide", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("Move", "Observe", 0);
		dispach_behaviour.registerTransition("Move", "Decide", 1);
		dispach_behaviour.registerTransition("Move", "StandBy", STAND_BY);
		dispach_behaviour.registerTransition("StandBy", "StandBy", 0);
		dispach_behaviour.registerTransition("StandBy", "Decide", 4);
		dispach_behaviour.registerTransition("StandBy", "FetchTreasure", Flood.TreasureHunt);
		dispach_behaviour.registerTransition("StandBy", "Explore", Flood.Risk);
		dispach_behaviour.registerTransition("StandBy", "Follow", Flood.Follow);
		
		dispach_behaviour.registerTransition("TreasureHunt", "StandBy", 0);
		dispach_behaviour.registerTransition("TreasureHunt", "Decide", 1);
		dispach_behaviour.registerTransition("Risk", "StandBy", 0);
		dispach_behaviour.registerTransition("Risk", "Decide", 1);
		dispach_behaviour.registerTransition("FetchTreasure", "FetchTreasure", 0);
		dispach_behaviour.registerTransition("FetchTreasure", "Observe", 1);
		dispach_behaviour.registerTransition("Explore", "Explore", 0);
		dispach_behaviour.registerTransition("Explore", "SearchFollower", 1);
		dispach_behaviour.registerTransition("Explore", "Observe", 2);
		dispach_behaviour.registerTransition("SearchFollower", "StandBy", 0);
		dispach_behaviour.registerTransition("SearchFollower", "Decide", 1);
		dispach_behaviour.registerTransition("Follow", "Follow", 0);
		dispach_behaviour.registerTransition("Follow", "StandBy", 1);
		dispach_behaviour.registerTransition("Follow", "Observe", 2);

		addBehaviour(dispach_behaviour);
		addBehaviour(new PushMapBehaviour(this));
		addBehaviour(new PullMapBehaviour(this));
		addBehaviour(new CatchFloodBehaviour(this));
		addBehaviour(new CheckDeathBehaviour(this));
		nbStep = 0;
		
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
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
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
		nbStep++;
		return move;
	}
	
	/**
	 * Fonction déclenchée lorsque l'on est élu dans un flood
	 * elle permet de switcher de comportement en fonction du flood
	 * 
	 * @param protocol le protocole du flood
	 */
	@SuppressWarnings("unchecked") 
	public void elected(String protocol){
		Flood f = this.getFlood(protocol);
		if(f.getType() == Flood.TreasureHunt){
			List<String> path = (List<String>) f.getAttribute("path");
			System.out.println(this.getLocalName() + " elected best for the flood "+protocol);
			if(!this.isStackMoveEmpty()){
				System.out.println(this.getLocalName()+" is already heading to "+ this.getStackMove().get(this.getStackMove().size()-1));
				this.removeFlood(protocol);
				return;
			}else{
				if(path.isEmpty()){
					System.out.println(this.getLocalName()+" is going to pick up the treasure");
				}else{
					System.out.println(this.getLocalName() + " vas prendre le tresor en " + path.get(path.size() - 1));
					System.out.println(path);
				}
				this.setTreasure(true);
				this.setStackMove(path);
			}
		}
		//si on a été élu pour prendre un risque
		else if (f.getType() == Flood.Risk){
			List<String> path = (List<String>) f.getAttribute("path");
			if(!this.isStackMoveEmpty() || this.isWaitingFollower() || this.isOnExploration()){
				System.out.println(this.getLocalName()+" has something to do first!");
				this.removeFlood(protocol);
				return;
			}
			if(f.hasParent() && path.size() > 1){
				System.out.println(this.getLocalName()+" is going on an adventure ! at "+ path.get(path.size() - 1));
			}else{
				System.out.println(this.getLocalName()+" is going on an adventure ! ");
			}
			//on met risque à true
			this.setRisk(true);
			//on ajoute la liste des mouvements a effectué pour aller jusqu'à la case ou prendre un risque
			this.setStackMove(path);
		}
		else if (f.getType() == Flood.Follow){
			if(!this.isStackMoveEmpty() || this.isOnExploration()){
				System.out.println(this.getLocalName()+" has something to do first!");
				this.removeFlood(protocol);
				return;
			}
			
			List<String> path = (List<String>) f.getAttribute("path");
			this.setFollowing((String)f.getAttribute("explorerId"), (String)f.getAttribute("explorerRoom"));
			if(f.hasParent()){
				path.remove(path.size() - 1);
				if(path.size() > 1)
					System.out.println(this.getLocalName()+" is going to follow ! at "+ path.get(path.size() - 1));
			}else{
				System.out.println(this.getLocalName()+" is the best to follow and going on adventure ! It's a PROBLEM!!");
			}
			this.setFollow(true);
			this.setStackMove(path);
		}
		this.standBy = false;
		this.removeFlood(protocol);
	}

	/**
	 * renvoi le prochain mouvement du HunterAgent
	 * 
	 * @return le prochain mouvement
	 */
	public String getNextMove() {
		return nextMove;
	}
	
	/**
	 * met à jour le prochain mouvement du HunterAgent
	 * @param nextMove le prochain mouvement
	 */

	public void setNextMove(String nextMove) {
		this.nextMove = nextMove;
	}
	
	/**
	 * Ajoute un flood
	 * @param protocolId le nom du protocole
	 * @param flood 
	 */
	public void addFlood(String protocolId, Flood flood) {
		this.floods.put(protocolId, flood);

	}

	/**
	 * 
	 * @param protocol le nom du flood
	 * @return le flood correspondant au nom protocol
	 */
	public Flood getFlood(String protocol) {
		return floods.get(protocol);
	}

	/**
	 * 
	 * @param id le nom du flood
	 * @return true si le HunterAgent est dans le flood id
	 */
	public boolean containsFlood(String id) {
		return floods.containsKey(id);
	}
	
	/**
	 * 
	 * @return true si on est en marche aléatoire
	 */
	public boolean isRandomWalk() {
		return randomWalk;
	}

	/**
	 * 
	 * @param randomWalk true si on veut entrer dans une marche aléatoire
	 */
	public void setRandomWalk(boolean randomWalk) {
		this.randomWalk = randomWalk;
	}

	/**
	 * 
	 * @return true si on est en attente. 
	 */
	public boolean isStandBy() {
		return standBy;
	}

	/**
	 * 
	 * @param standBy true si on veut se mettre en pause
	 */
	public void setStandBy(boolean standBy) {
		this.standBy = standBy;
	}

	/**
	 * 
	 * @return la représentation du monde du HunterAgent
	 */
	public Map getMap() {
		return map;
	}
	
	
	/**
	 * 
	 * @return la représentation du monde parcouru depuis le dernier envoie
	 */
	public Map getDiff() {
		return diff;
	}
	
	/**
	 * clear la map envoyer en prenant soins de laisser toutes les pièces où on a pris un risque
	 */
	public void clearDiff() {
		diff.clear();
		if(this.pushMap > 0){
			for(Node n : map.getWells()){
				diff.addRoom(n);
			}
			for(Node n : map.getWrongWells()){
				diff.addRoom(n);
			}
		}
	}
	
	public void pushLoss(int i) {
		pushLoss = i;
	}
	public int getPushLoss(){
		return pushLoss;
	}
	
	public void decreasePushLoss(){
		pushLoss--;
	}
	
	/**
	 * 
	 * @return la liste des collègues vivants du HunterAgent
	 */
	public Set<String> getPartners(){
		Set<String> retour = new HashSet<String>();
		for(String partner : teamMates.keySet()){
			if(teamMates.get(partner)){
				retour.add(partner);
			}
		}
		return retour;
		
	}
	
	/**
	 * Enregistre la mort de l'agent name
	 * @param name le nom de l'agent qui est mort
	 */
	public void setAgentDead(String name){
		teamMates.put(name, false);
	}

	/**
	 * 
	 * @return true si on a des collègues avec nous dans le monde
	 */
	public boolean hasPartners() {
		for(String partner : teamMates.keySet()){
			if(teamMates.get(partner))
				return true;
		}
		return false;
	}
	
	public void setPartner(String partner, Boolean death) {
		teamMates.put(partner, death);
	}


	public HashMap<String, Boolean> getAllPartners() {
		return teamMates;
	}


	/**
	 * Supprime le flood qui a pour nom protocol
	 * @param protocol le nom du flood
	 */
	public void removeFlood(String protocol) {
		this.floods.remove(protocol);
	}

	/**
	 * 
	 * @return la capacité de notre sac
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * 
	 * @return true si on doit ramasser le trésor
	 */
	public boolean isTreasure() {
		return catchTreasure;
	}

	/**
	 * 
	 * @param b true si on doit aller ramasser un trésor
	 */
	public void setTreasure(boolean b) {
		catchTreasure = b;
	}

	/**
	 * 
	 * @return true si on est élu pour devenir explorateur
	 */
	public boolean isRisk() {
		return risk;
	}

	/**
	 * 
	 * @param b true si on est élu pour devenir explorateur
	 */
	public void setRisk(boolean b) {
		risk = b;
	}

	/**
	 * 
	 * @return true si on est choisi comme suiveur
	 */
	public boolean isFollowing() {
		return follow;
	}
	
	/**
	 * 
	 * @param b true si on est choisi comme suiveur
	 */
	public void setFollow(boolean b) {
		follow = b;
	}
	
	/**
	 * Met à jour le nom et la position du HunterAgent qu'on suit
	 * @param aId nom du HunterAgent
	 * @param aRoom position du HunterAgent
	 */
	public void setFollowing(String aId, String aRoom){
		this.followingId = aId;
		this.followingRoom = aRoom;
	}
	
	
	/**
	 * 
	 * @return le nom du collègue que l'on couvre
	 */
	public String getFollowingId(){
		return this.followingId;
	}

	/**
	 * 
	 * @return pièce du HunterAgent que l'on couvre
	 */
	public String getFollowingRoom(){
		return this.followingRoom;
	}
	
	/**
	 * 
	 * @param stackMove liste des prochains mouvement que l'on doit faire. L'objectif est d'arriver dans une pièce précise
	 */
	public void setStackMove(List<String> stackMove){
		this.stackMove = stackMove;
	}
	
	/**
	 * 
	 * @return supprime le premier élément de la liste et le renvoi si il existe, renvoi null sinon
	 */
	public String popStackMove(){
		if(!stackMove.isEmpty()){
			String ret = stackMove.get(0);
			stackMove.remove(0);
			return ret;
		}
		return null;
	}
	
	/**
	 * 
	 * @return la liste des prochains mouvement à faire
	 */
	public List<String> getStackMove(){
		return this.stackMove;
	}
	
	/**
	 * 
	 * @return true si la liste des mouvements suivants est vide.
	 */
	public boolean isStackMoveEmpty(){
		return stackMove.isEmpty();
	}

	/**
	 * 
	 * @param capacity la capacité de notre sac
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	/**
	 * 
	 * @return true si on est dans un flood
	 */
	public boolean isInAFlood(){
		return !this.floods.isEmpty();
	}
	
	/**
	 * 
	 * @return aucune idée....
	 */
	public int getNbStep(){
		return nbStep;
	}

	/**
	 * 
	 * @return le nombre de fois qu'on envoie la map sans condition
	 */
	public int pushMap() {
		return pushMap ;
	}

	/**
	 * 
	 * @param nombre de fois qu'on envoi la map sans condition
	 */
	public void setPushMap(int nb_times) {
		pushMap = nb_times;
	}
	
	public void decreasePushMap() {
		pushMap--;
	}

	/**
	 * 
	 * @param b true si l'explorateur attend son suiveur
	 */
	public void setWaitFollower(boolean b) {
		waitFollower = b;
	}

	
	/**
	 * 
	 * @return true si on attend notre suiveur
	 */
	public boolean isWaitingFollower() {
		return waitFollower;
	}

	/**
	 * 
	 * @param b true si on est le suiveur ou l'explorateur
	 */
	public void onExploration(boolean b) {
		explorate = b;
	}

	/**
	 * 
	 * @return true si on est le suiveur ou l'explorateur
	 */
	public boolean isOnExploration() {
		return explorate;
	}

	public void reset() {
		standBy = false;
		risk = false;
		follow = false;
		catchTreasure = false;
		explorate = false;
		waitFollower = false;
		randomWalk = false;
		floods.clear();
		
	}







}