package TriangleGenome;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
	
	public Genome(ArrayList<Triangle> DNA)
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
	
  public void saveGenome()
  {
    try
    {
      FileOutputStream outputStream = new FileOutputStream(System.currentTimeMillis()
          + "Genome.txt");
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

      bufferedWriter.write(DNA.get(0).valuesToString());
      for (int i = 1; i < DNA.size(); i++)
      {
        bufferedWriter.newLine();
        bufferedWriter.write(DNA.get(i).valuesToString());
      }

      bufferedWriter.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
