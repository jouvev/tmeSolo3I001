public class TestAvion{

    private static final int nbRangs=4;
    private static final int nbSieges=5;

    public static void main(String args[]){
	Avion avion = new Avion(nbRangs,nbSieges);

	Thread tChef = new Thread(new ChefDeBord(avion));
	tChef.start();

	Thread[] passagers = new Thread[nbRangs*nbSieges];
	for(int i=0; i<passagers.length; i++){
	    passagers[i] = new Thread(new Passager(avion,(int)(i/nbSieges),i%nbSieges));
	    passagers[i].start();
	}

	try{
	    for(int i=0; i<passagers.length; i++){
		passagers[i].join();
	    }
	}catch(InterruptedException e){
	}

	tChef.interrupt();

	try{
	    tChef.join();
	}catch(InterruptedException e){}

	System.out.println("Fin du main");
    }

}
