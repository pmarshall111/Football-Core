<h1>Task Scheduling</h1>

These are the repeated tasks that the Football-Core application has to run:

- Update the database with data from the most recent played matches
- Predict upcoming games

These tasks are scheduled via cron jobs. 
You can see the cron jobs that are running in production by looking at the GitHub Actions workflow - `.github/workflows/deploy.yaml`, where the crons are generated based on the jar name.

The cron is generated on each deploy, calling a jar with a different filename, to prevent overwriting a jar while a cron is running.