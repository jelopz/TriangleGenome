package TriangleGenome;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * @author Christian Seely
 * @author Jesus Lopez 
 * This class represents a genome (DNA). The genome contains 200 triangles and
 * each triangle contains 10 genes for a total of 2000 genes making up a genome.
 */
public class Genome
{

  // Hold the 200 triangles that make up the genome.
  private ArrayList<Triangle> DNA;
  // The fitness of the genome.
  private double fitness;
  private ImageView img;
  /**
   * 
   * @param DNA/Genome 
   * This is the constructor for a genome object. 
   */
  public Genome(ArrayList<Triangle> DNA)
  {
    this.DNA = DNA;
  }
  /**
   * 
   * @return The DNA/Genome as a list of triangles. 
   */
  public ArrayList<Triangle> getDNA()
  {
    return this.DNA;
  }
  /**
   * 
   * @param DNA Array List of 200 triangles
   * Set the genomes DNA.
   */
  public void setDNA(ArrayList<Triangle> DNA)
  {
    this.DNA = DNA;
  }
  /**
   * 
   * @param Set the genomes fitness. 
   */
  public void setFitness(double fitness)
  {
    this.fitness = fitness;
  }
  /**
   * 
   * @return The genomes fitness. 
   */
  public double getFitness()
  {
    return this.fitness;
  }
  /**
   * Save the Genome to a text file. 
   */
  public void saveGenome()
  {
    try
    {
      FileWriter writer = new FileWriter(
          System.currentTimeMillis() + "Genome.txt");
      BufferedWriter bufferedWriter = new BufferedWriter(writer);

      bufferedWriter.write(DNA.get(0).valuesToString());
      for (int i = 1; i < DNA.size(); i++)
      {
        bufferedWriter.newLine();
        bufferedWriter.write(DNA.get(i).valuesToString());
      }

      bufferedWriter.close();
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
