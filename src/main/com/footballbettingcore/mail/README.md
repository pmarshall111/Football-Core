## Gmail

To make the Gmail flow work in production, you need to obtain the credentials.json file from the Google Cloud GUI by creating a user.
Then you need to run the `GetToken` file, which will obtain a token that can be used in production, which is downloaded and stored in the `tokens` directory. This token must be added as a GitHub Actions secret.

### Adding the GitHub Actions Secrets

The StoredCredential token is not uploaded to GitHub as it would allow anyone to send an email from our email address. The GitHub Actions `deploy.yaml` workflow adds the tokens to the codebase before a jar is created. The following secrets must be added:

`GMAIL_CREDENTIAL_JSON` -> This is the credentials.json file downloaded for the Google Cloud GUI

`GMAIL_STORED_CREDENTIAL` -> This is the binary token that can be obtained by running the `GetToken` file manually. IMPORTANT: This must be converted to base64 before being added as a GitHub Secret. Do this using: `cat StoredCredential | base64 --wrap=0 > secret.b64`, where `secret.b64` is what you paste into GitHub Actions.


### Running the Project locally

If running this project locally, the application will require the `tokens` directory to be in the current working directory, otherwise you'll be required to re-authenticate when running the app. The `tokens` directory is created upon sending an email for the first time.