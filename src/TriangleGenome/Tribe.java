package TriangleGenome;

import java.util.ArrayList;
/**
 * 
 * A Tribe contains anywhere between 2000 and 10000 genomes. 
 *
 */
public class Tribe {

	private ArrayList<Genome> genomes = new ArrayList<>();
	
	private int tribePopulation;
	
	Tribe(ArrayList<Genome> genomes)
	{
		this.genomes = genomes;
		this.tribePopulation = genomes.size();
	}
	
	public ArrayList<Genome> getGenomesInTribe()
	{
		return this.genomes;
	}
}
