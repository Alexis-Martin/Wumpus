package behaviours.flood;

import mas.HunterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.MessageTemplate;

public class CatchEchoBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = -8002056159166717857L;
	private boolean finished = false;
	private HunterAgent agent;
	private MessageTemplate msgTemplate;
	
	public CatchEchoBehaviour(HunterAgent agent, MessageTemplate msgTemplate){
		super(agent);
		this.agent = agent;
		this.msgTemplate = msgTemplate;
	}
	
	@Override
	public void action() {
		//reception de l'echo des enfants
				//	stock l'utilite de l'enfant
		
		//finished = true
	}

	@Override
	public boolean done() {
		//si racine:
			//elit le meilleur
			//dismiss le reste
			//lance le resultat avec info sur le meilleur
		
		//si pas racine:
			//decide du meilleur entre fils et soi meme
			//envoit un dismiss aux fils rejetes
			//met a jour l'objet flood
			//lance transmitEcho
		return finished;
	}

}
