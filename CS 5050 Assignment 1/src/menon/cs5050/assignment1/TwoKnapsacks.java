package menon.cs5050.assignment1;

public class TwoKnapsacks {
	
	//Knapsack variables
	private int objectSizes[]; //array containing object sizes 
	private int knapsack1Size; //Size of first knapsack
	private int knapsack2Size; //Size of second knapsack
	
	//Knapsack cache variables
	private boolean cache[][][];
	private boolean cacheValid[][][];
	
	//Constructor
	public TwoKnapsacks(int objectSizes[], int knapsack1Size, int knapsack2Size) {
		
		this.objectSizes = objectSizes;
		this.knapsack1Size = knapsack1Size;
		this.knapsack2Size = knapsack2Size;
		
		this.cache = new boolean[this.objectSizes.length + 1][knapsack1Size + 1][knapsack2Size + 1];
		this.cacheValid = new boolean[this.objectSizes.length + 1][knapsack1Size + 1][knapsack2Size + 1];
		
	}
	
	//Call a recursive method that checks if there exists a subset of objects that fill both knapsacks exactly
	public boolean knap() {	
		
		return willTheyExactlyFitRecursive(this.objectSizes.length - 1, this.knapsack1Size, this.knapsack2Size);
	}
	
	//Call a recursive with memo method that checks if there exists a subset of objects that fill both knapsacks exactly
	public boolean knapMemo() {	
		
		return willTheyExactlyFitRecursiveWithMemo(this.objectSizes.length - 1, this.knapsack1Size, this.knapsack2Size);
	}
	
	//Dynamic Programming based method that checks if there exists a subset of objects that fill both knapsacks exactly
	public boolean knapDP() {
		
		int dpCuboidHeight = this.objectSizes.length, dpCuboidWidth = this.knapsack1Size + 1, dpCuboidDepth = this.knapsack2Size + 1;
		
		//This array will already have default boolean false values for the cases where number of objects is zero
		boolean dpSolutionCubeoid[][][] = new boolean[dpCuboidHeight][dpCuboidWidth][dpCuboidDepth];
		

		//Fill in the default solution of true for cases where both knapsacks are of size zero
		for (int counter = 0; counter < dpCuboidHeight; ++counter) {
			dpSolutionCubeoid[counter][0][0] = true;
		}
		
		//Temporary variables to hold neighboring cube values
		boolean neighborBelow = false, neighborInKnapsack1 = false, neighborInKnapsack2 = false;	
		int testLength = 0;
		
		//Loop through all the entries in the cuboid and end in the far top corner
		for (int heightCounter = 1; heightCounter < dpCuboidHeight; ++heightCounter) {
			for (int widthCounter = 0; widthCounter < dpCuboidWidth; ++widthCounter) {
				for (int depthCounter = 0; depthCounter < dpCuboidDepth; ++depthCounter) {

					neighborBelow = dpSolutionCubeoid[heightCounter - 1][widthCounter][depthCounter];
					
					testLength = widthCounter - this.objectSizes[heightCounter];
					if (testLength < 0) {
						neighborInKnapsack1 = false;
					} else {
						neighborInKnapsack1 = dpSolutionCubeoid[heightCounter - 1][testLength][depthCounter];
					}
					
					testLength = depthCounter - this.objectSizes[heightCounter];
					if (testLength < 0) {
						neighborInKnapsack2 = false;
					} else {
						neighborInKnapsack2 = dpSolutionCubeoid[heightCounter - 1][widthCounter][testLength];
					}				
				
					dpSolutionCubeoid[heightCounter][widthCounter][depthCounter] = neighborBelow || neighborInKnapsack1 || neighborInKnapsack2;
					
				}
			}
		}
		
		return dpSolutionCubeoid[dpCuboidHeight - 1][dpCuboidWidth - 1][dpCuboidDepth - 1];
	}
	
	//This is the recursive method called from method knap
	private boolean willTheyExactlyFitRecursive(int numberOfObjects, int knapsack1Size, int knapsack2Size) {
		
		//Both knapsacks are empty, then it means that they are filled
		if (knapsack1Size == 0 && knapsack2Size == 0) {
			return true;
		}
		
		//If there are no more objects left, since the knapsacks are both not empty, it means that the objects do not
		//fill the knapsacks exactly
		if (numberOfObjects == 0) {
			return false;
		}
		
		//The knapsacks should not have a negative length
		if (knapsack1Size < 0 || knapsack2Size < 0) {
			return false;
		}
		
		return willTheyExactlyFitRecursive(numberOfObjects - 1, knapsack1Size, knapsack2Size) || //discard the object
			   willTheyExactlyFitRecursive(numberOfObjects - 1, knapsack1Size - this.objectSizes[numberOfObjects], knapsack2Size) || //put the object in knapsack1
		       willTheyExactlyFitRecursive(numberOfObjects - 1, knapsack1Size, knapsack2Size - this.objectSizes[numberOfObjects]); //put the object in knapsack2

	}

	
	//This is the recursive method called from method knapMemo
	private boolean willTheyExactlyFitRecursiveWithMemo(int numberOfObjects, int knapsack1Size, int knapsack2Size) {
		
		//Both knapsacks are empty, then it means that they are filled
		if (knapsack1Size == 0 && knapsack2Size == 0) {
			return true;
		}
		
		//If there are no more objects left, since the knapsacks are both not empty, it means that the objects do not
		//fill the knapsacks exactly
		if (numberOfObjects == 0) {
			return false;
		}
		
		//The knapsacks should not have a negative length
		if (knapsack1Size < 0 || knapsack2Size < 0) {
			return false;
		}
		
		//Case for object being discarded
		boolean willItFitWithObjectDiscarded = false;
		if (this.cacheValid[numberOfObjects - 1][knapsack1Size][knapsack2Size]) {
			willItFitWithObjectDiscarded = this.cache[numberOfObjects - 1][knapsack1Size][knapsack2Size];
		} else {
			willItFitWithObjectDiscarded = willTheyExactlyFitRecursiveWithMemo(numberOfObjects - 1, knapsack1Size, knapsack2Size);
			this.cache[numberOfObjects - 1][knapsack1Size][knapsack2Size] = willItFitWithObjectDiscarded;
			this.cacheValid[numberOfObjects - 1][knapsack1Size][knapsack2Size] = true;
		}
		
		int testKnapsackSize = 0;

		//Case for object being put in knapsack 1
		boolean willItFitWithObjectInKnapsack1 = false;
		testKnapsackSize = knapsack1Size - this.objectSizes[numberOfObjects];
		if (testKnapsackSize >= 0) {
			if (this.cacheValid[numberOfObjects - 1][testKnapsackSize][knapsack2Size]) {
				willItFitWithObjectInKnapsack1 = this.cache[numberOfObjects - 1][testKnapsackSize][knapsack2Size];
			} else {
				willItFitWithObjectInKnapsack1 = willTheyExactlyFitRecursiveWithMemo(numberOfObjects - 1, testKnapsackSize, knapsack2Size);
				this.cache[numberOfObjects - 1][testKnapsackSize][knapsack2Size] = willItFitWithObjectInKnapsack1;
				this.cacheValid[numberOfObjects - 1][testKnapsackSize][knapsack2Size] = true;
			}
		}
		
		//Case for object being put in knapsack 2
		boolean willItFitWithObjectInKnapsack2 = false;
		testKnapsackSize = knapsack2Size - this.objectSizes[numberOfObjects];
		if (testKnapsackSize >= 0) {
			if (this.cacheValid[numberOfObjects - 1][knapsack1Size][testKnapsackSize]) {
				willItFitWithObjectInKnapsack2 = this.cache[numberOfObjects - 1][knapsack1Size][testKnapsackSize];
			} else {
				willItFitWithObjectInKnapsack2 = willTheyExactlyFitRecursiveWithMemo(numberOfObjects - 1, knapsack1Size, testKnapsackSize);
				this.cache[numberOfObjects - 1][knapsack1Size][testKnapsackSize] = willItFitWithObjectInKnapsack2;
				this.cacheValid[numberOfObjects - 1][knapsack1Size][testKnapsackSize] = true;
			}
		}
		return willItFitWithObjectDiscarded || willItFitWithObjectInKnapsack1 || willItFitWithObjectInKnapsack2;

	}

}
