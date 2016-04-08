package TriangleGenome;

import java.util.ArrayList;

/**
 * 
 * We have the same method written three times 
 * (insert sorted) so for best practice we should 
 * probably put it in a utility class. Also if
 * there are any other duplicate implementations
 * we can add them here aswell. 
 *
 */
public class UtilityClass {

	
	  /**
	   * Insert the child genome into the tribe in a sorted manor. 
	   * @param genome
	   * @param genomes
	   */
	public void insertSorted(Genome genome, ArrayList<Genome> genomes)
	{
		  for(int i = 0; i < genomes.size(); i++)
		  {
			  if(genomes.get(i).getFitness() > genome.getFitness())continue;
			  
			  genomes.add(i,genome);
			  return;
		  }
 
		  genomes.add(genome);
		  	  
	}
	
	
}
