<h1>Machine Learning</h1>

<h3>Generating Features</h3>

The app retrieves all data for a season from the database, and tracks averages for each team. For example, the average goals a team scores or conceedes.
This tracking is done in the `createData/CalcPastStats` file. Before updating, it generates features based on the teams performance prior to the game.
The feature lists can be seen in `createData/CreateFeatures`. These features have been chosen by plotting the feature compared to the goal difference 
of the game, calculating a R coefficient and then choosing the features that have the highest correlation to the final score.

<h3>Training a new model</h3>

Models have been trained using Octave. Features are generated and this is written to a CSV for the Octave code to read and calculate on.
Extra data has been created by using the XG of the game to simulate probabilities of each result. This can be seen in `createData/SimulateMatches`. 

We can then train a model, weighting the updates by the probability.
With this we can effectively train on 2x the raw data. The actual result has a probability of 1, and there's a total probability of 1 for the 3 results simulated
from the XG.

<h3>Predicting games</h3>

Predictions are done in the Octave code. This choice was made to prevent duplication of Machine Learning logic between this project
and Octave. To predict a game, a CSV is written of the features. Then the octave command is called via the command line. This outputs 
another CSV with the predictions and bet recommendations, and this is then stored in the Database.