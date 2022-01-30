<h1>Scrape</h1>

The project scrapes data from 3 different websites to store in the database:
- Sofascore
  - Goal, player ratings, shots, possession, historic odds data
- Understat
  - XG data
- OddsChecker
  - Odds

This presents a challenge in coordinating team names across websites (e.g. Atlético Madrid vs Atletico Madrid), and having to adapt the code when 
a website changes how it presents data.

<h3>Adding a new season</h3>

Every new season, the Sofascore ID for each league has to be added to the `classes/LeagueIdsAndData` class, and then `AddNewSeason` has to be run.
This new season ID for EPL can be found by going to the following link:
`https://api.sofascore.com/api/v1/unique-tournament/17/seasons`. For other leagues, replace `17` with the ID for the given league.

Something to note is there may be errors when scraping in a new season if the Understat and SofaScore sites have team names that aren't identical.
i.e. Atlético Madrid vs Atletico Madrid. This would cause an error.
Most teams have these conversions written already, but errors can happen with teams who have just been promoted, or if a site changes the name of a team. To fix this, add a new conversion to the `scrape/classes/Team` file.