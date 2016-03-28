package TriangleGenome;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * Class for crossOver mutations. To perform a cross over mutation
 * you must provide a mother and father genome and the that you want
 * the child to be added too. Sometimes cross over is within the same
 * tribe so obviously the child will be added to that tribe but we can
 * also have cross over mutation between genomes of different tribes
 * so in that case you have to send over the tribe that you want the
 * child to be added to. 
 * 
 * One thing I have noticed so far is the hamming  distance is usually
 * really high which makes sense, but it results in cross overs where
 * the child's genes are predominantly one of its parents.  
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
	CrossOverMutation(Tribe tribe, Genome mother, Genome father, boolean crossOverMode)
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
			    	  mothersGeneList.add(mothersTriangle.getP1().x);
			    	  fathersGeneList.add(fathersTriangle.getP1().x);
			    	  if(mothersTriangle.getP1().x!=fathersTriangle.getP1().x)++hammingDistance;
			      break;
			      case 6:
			    	  mothersGeneList.add(mothersTriangle.getP1().y);
			    	  fathersGeneList.add(fathersTriangle.getP1().y);
			    	  if(mothersTriangle.getP1().y!=fathersTriangle.getP1().y)++hammingDistance;
			      break;
			      case 7:
			    	  mothersGeneList.add(mothersTriangle.getP2().x);
			    	  fathersGeneList.add(fathersTriangle.getP2().x);
			    	  if(mothersTriangle.getP2().x!=fathersTriangle.getP2().x)++hammingDistance;
			      break;
			      case 8:
			    	  mothersGeneList.add(mothersTriangle.getP2().y);
			    	  fathersGeneList.add(fathersTriangle.getP2().y);
			    	  if(mothersTriangle.getP2().y!=fathersTriangle.getP2().y)++hammingDistance;
			      break;
			      case 9:
			    	  mothersGeneList.add(mothersTriangle.getP3().x);
			    	  fathersGeneList.add(fathersTriangle.getP3().x);
			    	  if(mothersTriangle.getP3().x!=fathersTriangle.getP3().x)++hammingDistance;
			      break;
			      case 10:
			    	  mothersGeneList.add(mothersTriangle.getP3().y);
			    	  fathersGeneList.add(fathersTriangle.getP3().y);
			    	  if(mothersTriangle.getP3().y!=fathersTriangle.getP3().y)++hammingDistance;
			      break;
			      default:
			      break;
			      
			}
			}
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
		//Supposedly the only constraint for a  double point cross over is
		//x has to be less than y but I we might want to check on having a 
		//min splice range to prevent small splice cross overs but joel
		//does not mention it in his slides. (In this case a small splice
		//would of say 10 would lead a childs genes being 1990 of their mother
		//and 10 of their father, but again I am not sure if that is a negative
		//thing.)  
		if(x>y)
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
			    	  //wait for y to set full point. 
			      break;
			      case 6:
			    	  childTriangle.setP1(new Point(childsGeneList.get(childGeneIndex-1),childsGeneList.get(childGeneIndex)));
			      break;
			      case 7:
			    	  //wait for y to set full point. 
			      break;
			      case 8:
			    	  childTriangle.setP2(new Point(childsGeneList.get(childGeneIndex-1),childsGeneList.get(childGeneIndex)));
			      break;
			      case 9:
			    	  //wait for y to set full point. 
			      break;
			      case 10:
			    	  childTriangle.setP3(new Point(childsGeneList.get(childGeneIndex-1),childsGeneList.get(childGeneIndex)));
			      break;
			      default:
			      break;
				}
				
			}
			//Update triangle after changes made. 
			childTriangle.updateTriangle();
			childsDNA.add(childTriangle);
		}
		
		//I assume we should calculate the child's fitness before adding
		//them to the tribe but I might be wrong. So possibly at this point
		//we should calculate the child's fitness. 
		
		tribe.addGenome(new Genome(childsDNA));
	}
	
	
}
