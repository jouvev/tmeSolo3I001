// Donnez la réponse à la question 2 ici
/*On utilisera un ReentrantLock car on a plusieurs conditions d'attentes (enregistrement, autorisation et embarquement) ce qui optimisera le parrallélisme.
 *De plus comme il ya plusieurs passagers qui peuvent arriver dans n'importe quel ordre, pour l'attente sur autorisation on fait un tableau de condition, une pour chaque rangée.
 *Comme ça ils seront tous notifiés seulement quand cela aura un interet et donc n'utiliseront pas le cpu pour rien.
 */
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Avion{

    private int nbRangees;
    private int nbSieges;
    private int etat[];

    private ReentrantLock lock;
    private Condition enregistrement;
    private Condition autorisations[];
    private Condition embarquement;

    public Avion(int nbRangees, int nbSieges){
	lock=new ReentrantLock();
	enregistrement = lock.newCondition();
	embarquement = lock.newCondition();
	autorisations = new Condition[nbRangees];
	for(int i=0;i<nbRangees;i++){
	    autorisations[i]= lock.newCondition();
	}
        
	this.nbRangees=nbRangees;
	this.nbSieges=nbSieges;
	etat=new int[nbRangees];
	for(int i=0;i<nbRangees;i++){
	    etat[i]=-nbSieges-1;
	}	
    }

    public int getNbRangees(){
	return nbRangees;
    }

    public void attendreEnregistrement(int rangee) throws InterruptedException{
	lock.lock();
	try{
	    while(etat[rangee] < -1){
		enregistrement.await();
	    }
	}finally{
	    lock.unlock();
	}
    }

    public void enregistrerPassager(int rangee){
	lock.lock();
	etat[rangee]+=1;
	
	if(etat[rangee]==-1)
	    enregistrement.signalAll();

	lock.unlock();
    }

    public void attendreAutorisation(int rangee) throws InterruptedException{
        lock.lock();
	try{
	    while(etat[rangee]!=0){
		autorisations[rangee].await();
	    }
	}finally{
	    lock.unlock();
	}
    }

    public void autoriserEmbarquement(int rangee){
	lock.lock();
	etat[rangee]=0;
	autorisations[rangee].signalAll();
	lock.unlock();
    }

    public void attendreEmbarquement(int rangee) throws InterruptedException{
	lock.lock();
	try{
	    while(etat[rangee] < nbSieges){
		embarquement.await();
	    }
	}finally{
	    lock.unlock();
	}
    }

    public void terminerEmbarquement(int rangee){
	lock.lock();
	etat[rangee]+=1;

	if(etat[rangee]==nbSieges)
	    embarquement.signalAll();

	lock.unlock();
    }

}
