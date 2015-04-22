package behaviours.automata;

import mas.HunterAgent;
import jade.core.behaviours.OneShotBehaviour;

public class CatchTreasureBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = -8262609675222792477L;
	private HunterAgent agent;
	
	public CatchTreasureBehaviour(HunterAgent a) {
		super(a);
		this.agent = a;
	}

	@Override
	public void action() {
		System.out.println("catch treasure");
		this.agent.pick();
		/*
		String myPosition = this.agent.getCurrentPosition();
		List<Couple<String,List<Attribute>>> observe = this.agent.observe(myPosition);
		for(Couple<String, List<Attribute>> couple : observe){
			String pos = couple.getL();
			if(pos.equals(myPosition)){
				boolean treasure = false;
				for(Attribute attr : couple.getR()){
					if(attr.getName().equals("Treasure")){
						this.agent.getMap().setAttribute("treasure#", attr.getValue());
						treasure = true;
						break;
					}
				}
				if(!treasure){
					this.agent.getMap().removeAttribute("treasure#");
				}
				break;	
			}
		}
		*/
		
	}

}
