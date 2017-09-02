[DancingLinks.java](src/DancingLinks.java) is an implementation of the [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links) algorithm by [Dr Donald Knuth](https://en.wikipedia.org/wiki/Donald_Knuth), used to solve [pentomino puzzles](http://puzzler.sourceforge.net/docs/pentominoes.html).


The input files are to be constructed as follows:

Line 1: ``<Rows> <Columns>``

Line 2: ``<Number of Pentomino pieces>``

Line 3+: ``<x1> <y1>	<x2> <y2>	...``

Rows = the number of rows this pentomino puzzle is

Columns = the number of columns this pentomino puzzle is

Number of Pentomino Pieces = the number of pieces we will be using to solve this puzzle

x = the offset in a 5 by 5 matrix that should be filled in for this pentomino piece

y = the offset in a 5 by 5 matrix that should be filled in for this pentomino piece


Note: Pentomino pieces can be rotated, and flipped to give more valid orientations to be placed on the board.

[DancingLinks.java](src/DancingLinks.java) and [BruteForce.java](src/BruteForce.java) both solve for all solutions, but if you want to solve for the first found solution, simply replace the ``return depth - 1;`` with ``return -1;`` in the conditionals for solutions being found [here](https://github.com/DDews/DancingLinks/blob/72c93c2f0eab3f0dedb4124e71fd579abd5fbb98/src/DancingLinks.java#L353) and [here](https://github.com/DDews/DancingLinks/blob/72c93c2f0eab3f0dedb4124e71fd579abd5fbb98/src/DancingLinks.java#L360). This will backtrack to the first level of recursion. 





To learn as much as possible, I did some things that are not very conventional:
- I used binary numbers to represent pentomino pieces. I did this to get more familiar with bitwise operations and binary number processing. Doing this made it more complex for no good reason other than to learn.
- I never actually read Dr. Knuth's paper, but instead read the wikipedia articles on Dancing Links and Algorithm X. The rest was inferred through trial and error.


I have inefficiencies in the BruteForce.java class. I am going to fix these eventually. For now, even an algorithm not using Algorithm X might be better execution time than BruteForce.java.
The brute force implementation is extremely slow because it checks an arraylist of ignored rows and columns to see if it should skip them or not. I am going to refactor the bruteforce later to make it more efficient.


The DancingLinks.java solves the same problem in an order of magnitude faster than BruteForce.java. On my laptop, it solves all solutions (including rotations and mirrors) for 3by20.txt in 4 seconds, and solves the same coverage of solutions for 4by15.txt in about 4 minutes. For contrast, the BruteForce.java solves the same solution coverage for 3by20.txt in about 5 minutes, and the 4by15.txt in about 45 minutes.


TODO:
- Add a GUI for selecting options including: Show attempts graphically, Play/Pause, Find all solutions, Find first solution, List found solutions graphically
- Add ability to make custom "boards" to fill with pentomino puzzles rather than a standard rectangle
