<h1>Task Scheduling</h1>

These are the repeated tasks that the Football-Core application has to run:

- Update the database with scores, player ratings, xG etc from the most recent played matches. Also it must update the kickoff times stored in the database so that we know which games to predict on. Games can commonly be reschedulled due to weather, covid, crowd issues.
- Make a prediction on upcoming games.

These tasks are scheduled via cron jobs. 
You can see the cron jobs that are running in production by looking at the GitHub Actions workflow - `.github/workflows/deploy.yaml`, where the crons are generated based on the jar name.

The cron is generated on each deploy, writing a jar with a unique filename to prevent overwriting a jar while a cron is running.