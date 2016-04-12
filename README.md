**README**

**IMPORTANT NOTE FOR RUNNING PROGRAM: The initial population when starting the jar takes around 5-15 minutes depending 
on your system because every member of the tribe is being rendered and their fitness being calculated. So when 
clicking on the jar and or program please note that it does work it just takes a little while to calculate the initial populaiton.**

**Entry Point:**

The entry point exists in the NewMain.java class

**How to use the GUI**

The application starts up paused with a default target image selected and the intiial population already set.

Most features are only available when the GA is paused/the stop button has been pressed. The only feature available while the GA is running are the display options. From here you can choose from displaying the number 1 most fit genome from all tribes, the best fit genome from a specific tribe, or you can browse through every genome within a tribe. When you select a specific genome from a specific tribe, the dropdown box becomes available to browse through whichever genome you may want. Once here you can also view each specific triangle from a genome as well.

Once paused/stopped you can save the stats, which compiles a list of timestamps to a text file. This was mostly for testing purposes. 

Most features open up when you select "Specific Genome from Tribe x", where x is one of the tribes. While here you can choose which genome to display in the dropdown menu right below. These genomes are ordered from most to least fit, so genome 0 is the most fit in the tribe, and the final genome is the least fit. Also, the box below the genome selector allows you to choose which triangles to display from that specific genome. You can select "Show Complete Genome" to show all 200 triangles, or choose 1 to display the triangle in position 1, or 144 to show the triangle in position 144.

Still in "Specific Genome from Tribe x" AND ALSO while paused, you can now upload a genome. This genome you upload will be placed in this same specific tribe. It also replaces the least fit tribe in the process.

Finally, while paused and in "specific Genome from Tribe x," the value editor opens up on the bottom right of the window. The top combobox allows you to choose which genome in the tribe to edit. These values correspond to the same genomes when browsing through all the genomes upon selecting "Specific Genome from Tribe x," and also the table. The combobox right below selects which of the 10 genes to edit, then the text field at the bottom lets you enter what numeric value. RGBA values can only be 0-255. If you edit genome 0's p1x, you will see the top left most value change to what you entered.

**Hill Climbing Methodology:**

**Type: Adaptive Hill climbing with some stochastic elements.**

**Description of Algorithm:**

Firstly the algorithm is given a genome which will be used for hill climbing. With the given genome mutate a random gene to a random value within range of the respective gene (stochastic element). Following the mutation determine whether that mutation was an improvement in the genomes fitness. If the mutation results in an improvement obtain the direction of the random mutation and begin to adaptively mutate in that direction in incremental step sizes starting at one. If the mutation of the adaptive step size results in an improved fitness increment the step size while staying below its defined max, and perform a mutation with the new step size. Continue to perform mutations in the same direction on the same gene in the same triangle in incremental step sizes (caps out at defined max) until one of the mutations results in no improvement in the genomes fitness. Once this occurs find a new random gene mutation resulting in improvement and begin to adaptively mutate in the new direction with the new triangle and gene in an incremental fashion (step size starts at one again). This process repeats continuously during the hill climbing.

**Reasoning behind Choices:**

The reason we added the stochastic element was because we noticed it helped limit how often/ time it took the hill climbing to get stuck or stall out in local optima. The stochastic element does not alter the fact that the hill climbing is really adaptive, for all mutations that are kept resulted from a mutation of an incremental step size in some direction with a gene in a triangle. The stochastic element just helps find little paths or hills to climb. The reason we have a cap on the step size is eventually the step size gets so large that it can skip over a section of beneficial gene that may be surrounded by unbeneficial genes, which sometimes happens with the color ranges mostly. Also capping the step size does not negatively affect performance or any other elements of the hill climbing process.


**GA (Cross Over) Methodology:**


**Description of Algorithm:**
The cross over is triggered once every 1500 generations and is a combination of both cross tribal cross over and inner tribal cross over. Once the cross over is triggered the threads are momentarily paused (pause is nanoseconds long and not noticeable). When the threads are paused each thread/tribe gathers genomes from the other tribes in an array list called globalPool. Each tribe will end up getting its own unique global pool and these are recreated every time the cross over is triggered pooling the latest genomes from the tribal populations. Each global pool consists of 5(NumThreads-1) +100 genomes, to increase probability of cross over occurring with the other tribes best genomes the global pool is filled with 5 copies of each of the other tribes best genome (except for the tribe who the pool is for). After that the rest of the genomes in each of the pools are obtained by first randomly selecting a tribe and then randomly selecting one of the genomes to add to the pool. After each tribe/thread has their global pool created the pools are set into the threads GA objects and then the threads are un paused and then cross over starts. Using the global pool given to each tribe the tribe performs cross over with each member of the global pool and a random member of their own tribe (cross tribal cross over). There currently is an equally weighted chance of single or double point cross over occurring. Immediately after the cross tribal cross over inner tribal cross over occurs where the tribe performs cross over 100 times (again with an equally weighted chance of either single point or double point cross over) with random members of the tribes population. As for the actual cross over itself, we added the chance of mutation occurring during cross over. There is a **0.0025% chance a gene gets mutated during crossover**.

**Reasoning behind Choices:**
We choose to do cross tribal and then inner tribal in that order because we wanted to introduce genes from the other tribes into the current tribes population before doing inner tribal cross over to increase the dispersion of genes from other tribes through the population of the current tribe. Also there is noticeably less cross over generations than hill climbing generations, because the purpose of the cross over is really to introduce diversity and overcome local optima. Also we haven’t found much of a different between single and double point cross over, hence the equal probability of both. Then finally the reason we introduced mutation to the cross over is because we are using cross over to introduce diversity and this is a forced way to keep diversity amongst the population of the tribes during cross over. 
**Methodology to combining Hill Climbing and our GA:**


**Purpose of Hill Climbing:**

The hill climbing carries most of the load in the overall process and is responsible for improving the fitness through its mutations. Through the mutations the child stays close to the parent since it mutating single genes at a time.

**Purpose of GA (Cross Over):**

The purpose of the GA is not necessarily to improve the fitness through its mutations/breeding instead the purpose is to introduce diversity. The cross over acts as a explorative component to the overall process which cannot possible occur during the hill climbing. The hill climbing does not encompass the complete solution set as only single genes are being mutated at a time, through cross over answers not encompassed in the solution set of hill climbing can be introduced which can help the hill climbing overcome local optima it may have gotten stuck on.

**Implementation:**
The hill climbing and cross over work together as follows; Hill climbing is performed once every 1500 run calls of the thread controlling the tribe (about once every 1500 generations). After that cross over is triggered leading to cross tribal cross over and then immediately after inner tribal cross over. After that the program goes right back to hill climbing and this process is repeated infinitely. We also added one more component to aid in getting some of the children from cross over to be hill climbed on when the best genome from a tribe gets stuck. What happens is every thread keeps track of each time over one second there was 0 changes in the fitness score and increments a counter. Once the counter hits 200 then a method called getUnstuck is called. What this method does is take the best genome from the tribe (the one that hill climbing is stuck on), and inject the genes with random mutated values and inserts it back into the tribe. Since the tribe is ordered by fitness the genome gets sent back in the list and the previous genome at the 1th index moves to the 0th index as the best genome and hill climbing starts on that genome. Thanks to the cross over the genome in the 1th index is very close to the one in the 0th index but is genetically different so most of the time this allows us to break out of the local optima the hill climbing was stuck on. 


