package TriangleGenome;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Christian Seely
 * @author Jesus Lopez 
 * This class performs both single and double point cross over muations.
 * To use this class you have to create an instance of the class which
 * initializes the variables/objects, and then from that point all you have to
 * do is call the invokeCrossOverMuation method to perform the cross over
 * mutation. You must provide the a mother and father genome and the tribe
 * the child will be assigned to. 
 *
 */
public class CrossOverMutation {
	private Tribe tribe;
	private Random random = new Random();
	//Hold the DNA of the mother/father. 
	private ArrayList<Triangle> mother;
	private ArrayList<Triangle> father;
	//Way to hold the genes in a simple structure to make
	//cross over easier. 
	private ArrayList<Integer> mothersGeneList;
	private ArrayList<Integer> fathersGeneList;
	private ArrayList<Integer> childsGeneList;
	private boolean crossOverMode; //Let true represent single point and false represent double point. 
	private int hammingDistance;
	private FitnessFunction fitnessTest;
	private Renderer imageRenderer;
	private UtilityClass util;
	private int IMAGE_WIDTH;
	private int IMAGE_HEIGHT;
	private double lastChildsFit;
	private boolean CLONE_PREVENTION = false;
	
	/**
	 * 
	 * @param fitnessTest Object used to calculate the childs fitness prior
	 * to adding him to the tribe. 
	 * @param imageRenderer Object also used to calculate the childs fitness.
	 * @param IMAGE_WIDTH Image width 
	 * @param IMAGE_HEIGHT Image height 
	 */
	CrossOverMutation(FitnessFunction fitnessTest, Renderer imageRenderer, int IMAGE_WIDTH, int IMAGE_HEIGHT)
	{
		this.fitnessTest = fitnessTest;
		this.imageRenderer = imageRenderer;
		this.IMAGE_WIDTH = IMAGE_WIDTH;
		this.IMAGE_HEIGHT = IMAGE_HEIGHT;
		this.util = new UtilityClass();	
	}
	
	
	/**
	 * Instead of making an object for each cross over a single object can be made
	 * and then to perform a cross over mutation all you have to do is call this
	 * method with the correct parameters using the instantiation of the cross over
	 * object where ever that may be. 
	 */
	public void invokeCrossOverMutation(Tribe tribe, Genome mother, Genome father, boolean crossOverMode)
	{
		this.tribe = tribe;
		this.mother = mother.getDNA();
		this.father = father.getDNA();
		this.crossOverMode = crossOverMode;
		this.hammingDistance = 0;
		this.mothersGeneList = new ArrayList<>();
		this.fathersGeneList = new ArrayList<>();
		this.childsGeneList = new ArrayList<>();
		//Create the parents gene lists and calculate the hamming distance.
		createGeneListsAndCalculateHammingDistance();
		if(!CLONE_PREVENTION)
		{
		//Depending on the mode perform a single or double point cross over.
		if(crossOverMode) //If true single point. 
		{
			singlePointCrossOver();
		}
		else
		{
			doublePointCrossOver(); //If false double point. 
		}
		//Add the child to the appropriate tribe. 
		addChildToTribe();
		}
		else
		{
			CLONE_PREVENTION=false;
		}
	}
	
	/**
	 * Create gene list for mother and father of 2000 genes each
	 * by going through each triangle in their genome and each of the 
	 * genes in the triangle to add them to their respective lists. 
	 * In the list the genes of each triangle are added in the order
	 * argbx1y1x2y2x3y3...(and so on)
	 */
	private void createGeneListsAndCalculateHammingDistance()
	{
		Triangle mothersTriangle;
		Triangle fathersTriangle;
		for(int i = 0; i <200; i++)
		{
			mothersTriangle = mother.get(i);
			fathersTriangle = father.get(i);
			for(int j = 1; j <= 10; j++)
			{
				switch(j)
				 {
			      case 1:
			    	  mothersGeneList.add(mothersTriangle.getAlpha());
			    	  fathersGeneList.add(fathersTriangle.getAlpha());
			    	  if(mothersTriangle.getAlpha()!=fathersTriangle.getAlpha())++hammingDistance;
			      break;
			      case 2:
			    	  mothersGeneList.add(mothersTriangle.getRed());
			    	  fathersGeneList.add(fathersTriangle.getRed());
			    	  if(mothersTriangle.getRed()!=fathersTriangle.getRed())++hammingDistance;
			      break;
			      case 3:
			    	  mothersGeneList.add(mothersTriangle.getGreen());
			    	  fathersGeneList.add(fathersTriangle.getGreen());
			    	  if(mothersTriangle.getGreen()!=fathersTriangle.getGreen())++hammingDistance;
			      break;
			      case 4:
			    	  mothersGeneList.add(mothersTriangle.getBlue());
			    	  fathersGeneList.add(fathersTriangle.getBlue());
			    	  if(mothersTriangle.getBlue()!=fathersTriangle.getBlue())++hammingDistance;
			      break;
			      case 5:
			    	  mothersGeneList.add(mothersTriangle.getP1x());
			    	  fathersGeneList.add(fathersTriangle.getP1x());
			    	  if(mothersTriangle.getP1x()!=fathersTriangle.getP1x())++hammingDistance;
			      break;
			      case 6:
			    	  mothersGeneList.add(mothersTriangle.getP1y());
			    	  fathersGeneList.add(fathersTriangle.getP1y());
			    	  if(mothersTriangle.getP1y()!=fathersTriangle.getP1y())++hammingDistance;
			      break;
			      case 7:
			    	  mothersGeneList.add(mothersTriangle.getP2x());
			    	  fathersGeneList.add(fathersTriangle.getP2x());
			    	  if(mothersTriangle.getP2x()!=fathersTriangle.getP2x())++hammingDistance;
			      break;
			      case 8:
			    	  mothersGeneList.add(mothersTriangle.getP2y());
			    	  fathersGeneList.add(fathersTriangle.getP2y());
			    	  if(mothersTriangle.getP2y()!=fathersTriangle.getP2y())++hammingDistance;
			      break;
			      case 9:
			    	  mothersGeneList.add(mothersTriangle.getP3x());
			    	  fathersGeneList.add(fathersTriangle.getP3x());
			    	  if(mothersTriangle.getP3x()!=fathersTriangle.getP3x())++hammingDistance;
			      break;
			      case 10:
			    	  mothersGeneList.add(mothersTriangle.getP3y());
			    	  fathersGeneList.add(fathersTriangle.getP3y());
			    	  if(mothersTriangle.getP3y()!=fathersTriangle.getP3y())++hammingDistance;
			      break;
			      default:
			      break;
			    
			}
			
				 // System.out.println("ham: " + hammingDistance);
			}
		}
		System.out.println("hamming dist: " + hammingDistance);
		if(hammingDistance <=2)
		{
			CLONE_PREVENTION = true;
		}
	}
	/**
	 * Perform a single point cross over, the cross over point
	 * is at a random point between 1 and the hamming distance-1.
	 * Up to the cross over point copy the mothers genes, then after
	 * the cross over point copy over the fathers genes. 
	 */
	private void singlePointCrossOver()
	{
		int x = 1 + random.nextInt(hammingDistance-1);
		System.out.println("single point");
		//inclusive x;
		for(int i = 0; i <= x; i++)
		{
			childsGeneList.add(mothersGeneList.get(i));
		}
		for(int j = (x+1); j <2000; j++)
		{
			childsGeneList.add(fathersGeneList.get(j));
		}
	
	}
	/**
	 * Perform a double point cross over, the two cross over points
	 * are at points random points between 1 and the hamming distance-1, 
	 * such that the first point x is less than the second point y. 
	 */
	private void doublePointCrossOver()
	{
		int x = 1 + random.nextInt(hammingDistance-1);
		int y = 1 + random.nextInt(hammingDistance-1);

		while(x>y)
		{
			 x = 1 + random.nextInt(hammingDistance-1);
			 y = 1 + random.nextInt(hammingDistance-1);
		}

		
		for(int i = 0; i <= x; i++)
		{
			childsGeneList.add(mothersGeneList.get(i));
		}
		for(int j = (x+1); j <=y; j++)
		{
			childsGeneList.add(fathersGeneList.get(j));
		}
		for(int k = (y+1); k <2000; k++)
		{
			childsGeneList.add(mothersGeneList.get(k));
		}
		
		
	}
	
	/**
	 * Build the child's genome from its genes and then add the 
	 * child's genome to the tribe. 
	 */
	private void addChildToTribe()
	{
		//To hold the childs genome. 
		ArrayList<Triangle> childsDNA = new ArrayList<Triangle>();
		Triangle childTriangle;
		int childGeneIndex = 0;
		for(int i = 0; i < 200; i++)
		{
			childTriangle = new Triangle();
			for(int j = 1; j <=10; j++, childGeneIndex++)
			{
				switch(j)
				{
				 case 1:
					 childTriangle.setAlpha(childsGeneList.get(childGeneIndex));
			      break;
			      case 2:
			    	  childTriangle.setRed(childsGeneList.get(childGeneIndex));
			      break;
			      case 3:
			    	  childTriangle.setGreen(childsGeneList.get(childGeneIndex));
			      break;
			      case 4:
			    	  childTriangle.setBlue(childsGeneList.get(childGeneIndex));
			      break;
			      case 5:	
			    	  childTriangle.setP1x(childsGeneList.get(childGeneIndex));
			      break;
			      case 6:
			    	  childTriangle.setP1y(childsGeneList.get(childGeneIndex));
			      break;
			      case 7:
			    	  childTriangle.setP2x(childsGeneList.get(childGeneIndex));
			      break;
			      case 8:
			    	  childTriangle.setP2y(childsGeneList.get(childGeneIndex));
			      break;
			      case 9:
			    	  childTriangle.setP3x(childsGeneList.get(childGeneIndex));
			      break;
			      case 10:
			    	  childTriangle.setP3y(childsGeneList.get(childGeneIndex));
			      break;
			      default:
			      break;
				}
				
			}
			//Update triangle after changes made. 
			childTriangle.updateTriangle();
			childsDNA.add(childTriangle);
		}	
		Genome childGenome = new Genome(childsDNA);
		for(int i = 0; i< 5; i++)
		{
			int rand1 = random.nextInt(200);
			int rand2 = random.nextInt(10)+1;
			geneMutation(rand1,rand2,childGenome);
		}
		imageRenderer.render(childGenome.getDNA());
		//Check new fitness.		

		fitnessTest.calculateFitness(imageRenderer.getBuff());
		lastChildsFit = fitnessTest.getFitness();
		System.out.println("childs fit: " + fitnessTest.getFitness());
		childGenome.setFitness(fitnessTest.getFitness());	
		util.insertSorted(childGenome, tribe.getGenomesInTribe());
		deleteWeakest();

	}
	/**
	 * 
	 * @param rand1 Random Triangle 
	 * @param rand2 Random gene
	 * @param childGenome The child that will have mutations done to it. 
	 * This class performs gene mutations which are a part of our 
	 * cross over methodology as a type of forced diversity. 
	 */
	private void geneMutation(int rand1, int rand2, Genome childGenome)
	{
		Triangle triangle = childGenome.getDNA().get(rand1);
		switch (rand2)
		{
		 case 1:
		        triangle.setAlpha(random.nextInt(255));
		        break;
		      case 2:

		        triangle.setRed(random.nextInt(255));
		        break;
		      case 3:

		        triangle.setGreen(random.nextInt(255));
		        break;
		      case 4:

		        triangle.setBlue(random.nextInt(255));
		        break;
		      case 5:

		        triangle.setP1x(random.nextInt(IMAGE_WIDTH));
		        break;
		      case 6:
	
		        triangle.setP1y(random.nextInt(IMAGE_HEIGHT));
		        break;
		      case 7:

		        triangle.setP2x(random.nextInt(IMAGE_WIDTH));
		        break;
		      case 8:

		        triangle.setP2y(random.nextInt(IMAGE_HEIGHT));
		        break;
		      case 9:

		        triangle.setP3x(random.nextInt(IMAGE_WIDTH));
		        break;
		      case 10:

		        triangle.setP3y(random.nextInt(IMAGE_HEIGHT));
		        break;
		      default:
		        break; // Should never reach here.
		    }
		  triangle.updateTriangle();
	}

	/**
	 * 
	 * @return The fitness of the last child. 
	 */
	public double getLastChildFitness()
	{
		return lastChildsFit;
	}
	
	
	  /**
	   * Delete the weakest trible member (the member at the end of the
	   * list that has the lowest fitness). 
	   */
	  private void deleteWeakest()
	  {
		  tribe.getGenomesInTribe().remove(tribe.getGenomesInTribe().size()-1);
 
	  }
	
}
