DancingLinks.java is an implementation of the Dancing Links algorithm by Dr Knuth, used to solve pentomino puzzles.

To learn as much as possible, I did some things that are not very conventional:
- I used binary numbers to represent pentomino pieces. I did this to get more familiar with bitwise operations and binary number processing. Doing this made it more complex for no good reason other than to learn.
- I never actually read Dr. Knuth's paper, but instead read the wikipedia articles on Dancing Links and Algorithm X. The rest was inferred through trial and error.


I have inefficiencies in the BruteForce.java class. I am going to fix these eventually. For now, even an algorithm not using Algorithm X might be better execution time than BruteForce.java.
The brute force implementation is extremely slow because it checks an arraylist of ignored rows and columns to see if it should skip them or not. I am going to refactor the bruteforce later to make it more efficient.


The DancingLinks.java solves the same problem in an order of magnitude faster than BruteForce.java. On my laptop, it solves all solutions (including rotations and mirrors) for 3by20.txt in 4 seconds, and solves the same coverage of solutions for 4by15.txt in about 4 minutes. For contrast, the BruteForce.java solves the same solution coverage for 3by20.txt in about 5 minutes, and the 4by15.txt in about 45 minutes.
