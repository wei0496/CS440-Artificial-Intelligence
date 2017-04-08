# CS440-Artificial-Intelligence
There were 34 projects I have built during my AI Courses.<br />
Below are some desciprtions for my projects.

## Atropos-Game:
• We implemented a strategy (alpha-beta pruning / Minimax) to play this game intelligently (as opposed to randomly or via brute-force search), which is a static evaluator with [] adversial algorithm for Atropos game. Alpha-beta search is a variant of minimax search which seeks to prune branches of the search space aggressively.</br>

## Descriptions of the Atropos Game:
• Atropos is a two-player game played on a triangle-shaped board. It is convenient to visualize each position on this board as a circle rather than a square.</br>
There is no "regulation" size for the board. You and your opponent agree on the board size for each game. Atropos gets interesting when the board size is at least 6.</br>
The boundaries of the board are pre-colored before the game starts. The "bottom" side of the triangle is always colored with the red-blue pattern. The left side is always colored with the red-green pattern, and the right side with the green-blue pattern.</br>
Each move (play) consists of filling an uncolored position (circle) on the board with one of the three colors (red, green, or blue).</br>
Restrictions on the placement of the next move are as follows. The very first move may be anywhere on the board. For all subsequent moves, if the previous move (play) was adjacent to some uncolored circles, the current move must select one of these uncolored circles. Otherwise, if there are no adjacent uncolored circles, the current player may play anywhere on the board.</br>
The game is over when a player (call her player A) colors a circle in such a way that it completes a three-colored triangle (i.e., a configuration of red, green, and blue circles all adjacent to each other). When this happens, player A loses the game and player B wins. Note that player A can lose either due to carelessness or because player B forces player A to make the losing move (see rule 5).</br>

## Hand-Gesture-Recogition
• Implement Background differencing algorithms and skin color detection that delineate hand shapes or gestures, and create a graphical display that responds to the recognition of the hand shapes or gestures.

## Neural-Net
• Build a neural network model using stochastic gradient descent and the back propagation algorithm to train the weights of a feed-forward neural network for binary and multiclass classification. Train the model with various gradient descent parameters and hidden layers to explore their impacts on accuracy of the neural net.

## Natural Language Processing
• A distributed set of procedures that implement a distributed distance vector
routing with Split Horizon with Poison Reverse.
