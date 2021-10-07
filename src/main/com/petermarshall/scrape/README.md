<h1>Scrape</h1>
<h2>Adding a new season</h2>

Every new season, the Sofascore ID for each league has to be added to LeagueIdsAndData, and then `taskScheduling/AddNewSeason` has to be run.
This new season ID for EPL can be found by going to the following link:
`https://api.sofascore.com/api/v1/unique-tournament/17/seasons`. For other leagues, replace `17` with the league ID.

Additionally, there may be errors when scraping in a new season if the `Understat` and `SofaScore` sites have team names that aren't identical between the sites. 
i.e. Sofascore has a team name of Atlético Madrid, but Understat uses Atletico Madrid. This would cause an error.
Most teams have these conversions written already, but errors can happen with teams who have just been promoted. To fix this, add a new conversion to the `scrape/classes/Team` file.