package TriangleGenome;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * This class represents a genome (DNA).
 * The genome contains 200 triangles and
 * each triangle contains 10 genes for a total
 * of 2000 genes making up a genome.
 */
public class Genome {

	//Hold the 200 triangles that make up the genome. 
	private ArrayList<Triangle> DNA;
	//The fitness of the genome. 
	private double fitness;
	private ImageView img;
	
	Genome(ArrayList<Triangle> DNA)
	{
		this.DNA = DNA;
	}

	
	public ArrayList<Triangle> getDNA()
	{
		return this.DNA;
	}
	
	public void setDNA(ArrayList<Triangle> DNA)
	{
	  this.DNA = DNA;
	}
	
	public void setFitness(double fitness)
	{
		this.fitness = fitness;
	}
	
	public double getFitness()
	{
		return this.fitness;
	}
	
	
}
