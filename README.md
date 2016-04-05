# TriangleGenome
**README**

**Hill Climbing Methodology:** 

**Type: Adaptive Hill climbing with some stochastic elements.** 

**Description of Algorithm:**

Firstly the algorithm is given a genome which will be used for hill climbing. With the given genome mutate a random gene to a random value within range of the respective gene (stochastic element). Following the mutation determine whether that mutation was an improvement in the genomes fitness. If the mutation results in an improvement obtain the direction of the random mutation and begin to adaptively mutate in that direction in incremental step sizes starting at one. If the mutation of the adaptive step size results in an improved fitness increment the step size while staying below its defined max, and perform a mutation with the new step size. Continue to perform mutations in the same direction on the same gene in the same triangle in incremental step sizes (caps out at defined max) until one of the mutations results in no improvement in the genomes fitness. Once this occurs find a new random gene mutation resulting in improvement and begin to adaptively mutate in the new direction with the new triangle and gene in an incremental fashion (step size starts at one again). This process repeats continuously during the hill climbing. 

**Reasoning behind Choices:**

The reason we added the stochastic element was because we noticed it helped limit how often/ time it took the hill climbing to get stuck or stall out in local optima. The stochastic element does not alter the fact that the hill climbing is really adaptive, for all mutations that are kept resulted from a mutation of an incremental step size in some direction with a gene in a triangle.  The stochastic element just helps find little paths or hills to climb. The reason we have a cap on the step size is eventually the step size gets so large that it can skip over a section of beneficial gene that may be surrounded by unbeneficial genes, which sometimes happens with the color ranges mostly. Also capping the step size does not negatively affect performance or any other elements of the hill climbing process. 

**GA (Cross Over) Methodology:**

There are two ways to cross over a genome, inter-tribal and then cross tribal, where inter-tribal cross over is crossing over two genomes in the same tribe, and cross tribal is crossing over genomes between two tribes. I assume that cross tribal will be more helpful for cross over because if it wasn’t there would be no purpose in even having threads. 

**Description of Algorithm:** 
 
**TODO**

**Reasoning behind Choices:**

**TODO**

**Methodology to combining Hill Climbing and our GA:**

**(NOTE):** This is the prospective methodology which has not been implemented yet. 

**Purpose of Hill Climbing:**

The hill climbing carries most of the load in the overall process and is responsible for improving the fitness through its mutations. Through the mutations the child stays close to the parent since it mutating single genes at a time.  

**Purpose of GA (Cross Over):** 

The purpose of the GA is not necessarily to improve the fitness through its mutations/breeding instead the purpose is to introduce diversity. The cross over acts as a explorative component to the overall process which cannot possible occur during the hill climbing. The hill climbing does not encompass the complete solution set as only single genes are being mutated at a time, through cross over answers not encompassed in the solution set of the hill climbing can be introduced which can help the hill climbing overcome local optima it may have gotten stuck on. 

**Implementation:** 

**(NOTE):** This still actually has to be implemented, this is just a prospective outlook/planning
As mentioned hill climbing carries most of the load in the overall process, and we start straight out of the gate with solely hill climbing for each tribes best fit genome. During this process we keep track of the improvement in fitness per second of each genome having hill climbing performed on it. Once that rate dips below a defined threshold (implying we are reaching the top of a local maxima or are at the top of a local maxima) then we introduce cross over to break out of that local maxima by introducing diversity. The cross over will be performed until a child is born that meets two conditions. The first condition is the fitness of the child is very close or better than the best genome in that tribe, next the child passes a diversity threshold (calculated via hamming distance) to make sure that the child is different enough from the best genome in the tribe. We want the diversity threshold since the whole point of the cross over is to introduce diversity. Overall the program starts almost solely hill climbing early on, and then later there is a mix of cross over and hill climbing. 

**Proof/Support through Graphs:**
We are going to have to provide graphical evidence supporting choices we made for the program, below are some of them.
**1.** Choice between single point and double point cross over.
**2.** Number of Tribes (Threads) –Most important he wants us to test it with multiple different numbers of threads. 
**3.** Amounts of cross-over/ hill climbing used
