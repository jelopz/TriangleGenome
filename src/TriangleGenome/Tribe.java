package TriangleGenome;

import java.util.ArrayList;

/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez
 * 
 * This class encapsulates the data of a tribe and provides some
 * functionality related to it. A tribe consists of 2,000-10,000 genomes
 * in our case the tribe populaiton stays at a constant 2,000 for each
 * tribe. To use this class you have to instanciate a Tribe object by
 * providing and ArrayList of genomes which should contain the tobe
 * members of the tribe (the genomes).
 *
 */
public class Tribe
{

  private ArrayList<Genome> genomes = new ArrayList<>();

  private int tribePopulation;
  /**
   * 
   * @param ArrayList<Genomes>
   *          - An array list containing the tribes tobe members (genomes).
   * 
   */
  Tribe(ArrayList<Genome> genomes)
  {
    this.genomes = genomes;
    this.tribePopulation = genomes.size();
  }
  /**
   * 
   * @return ArrayList<Genomes> - The Tribes current population. 
   */
  public ArrayList<Genome> getGenomesInTribe()
  {
    return this.genomes;
  }
  /**
   * 
   * @return int - The current population of the tribe. 
   */
  public int getTribePopulation()
  {
    return tribePopulation;
  }

  /**
   * Remove the least fit genome from the tribe. 
   */
  public void removeLeastFit()
  {
    genomes.remove(genomes.size() - 1);
  }
}
