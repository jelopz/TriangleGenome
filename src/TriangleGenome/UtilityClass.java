package TriangleGenome;

import java.util.ArrayList;

/**
 * @author Christian Seely
 * @author Jesus Lopez
 * 
 * This is a class class contianing a commmonly used method in multiple
 * classes. By incapsualing it inside this Utility class it prevents
 * repetitive code.
 *
 */
public class UtilityClass
{
  /**
   * Insert the child genome into the tribe in a sorted manor.
   * 
   * @param Genome genome - genome/dna/combination of 200 triangle.
   * @param ArrayList<Genomes> genomes- list of genomes in a tribe.
   *           
   * The members of tribe (the genomes) are held in the arrayList
   * genomes. The ordering of the members of the tribe is from the
   * most fit (index 0) to least fit (index size-1). This method 
   * allows a new genome to be inserted in
   * the correct location in the tribe. It loops through the arrayList
   * untill it comes across the index where the new genomes fitness is
   * greater than that of the genome at that index and inserts it
   * there. To use this class you must create an instansiation
   * of the class and then you can call the insert sorted method
   * by providing the genome you want to insert and the tribes 
   * members held within an ArrayList. 
   */
  public void insertSorted(Genome genome, ArrayList<Genome> genomes)
  {
    for (int i = 0; i < genomes.size(); i++)
    {
      // Continue skips to the next iteration of the for loop
      // ignoring the rest of the code inside the loop.
      if (genomes.get(i).getFitness() > genome.getFitness())
        continue;

      genomes.add(i, genome);
      // Genome has been inserted, so now
      // we can leave the method.
      return;
    }
    // At end of list, add normally.
    genomes.add(genome);

  }

}
