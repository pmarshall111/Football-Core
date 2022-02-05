package com.footballbettingcore.scrape.placeBet.unibet;

import com.footballbettingcore.scrape.ChromeDriverFactory;
import com.footballbettingcore.utils.ConvertOdds;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class AutomateBetUniBet {
    private static final String UNIBET_LINK = "https://www.unibet.co.uk/betting/sports/filter/football";

    public static void main(String[] args) {
        BetPlacedUniBet bpub = placeBet("Italy", "Serie A", "Bologna", "Juventus", 2, 0.1, 1.3);
        System.out.println("Odds: " + bpub.getOddsOffered());
        System.out.println("Stake: " + bpub.getStake());
        System.out.println("Success: " + bpub.isBetSuccessful());
        System.out.println("bal:" + bpub.getBalance());
    }

    //int result 0 = home, 1 = draw, 2 = away
    public static BetPlacedUniBet placeBet(String targetCountry, String leagueName, String homeTeam, String awayTeam, int result, double amount, double minOdds) {
        double stake = amount;
        BetPlacedUniBet bet = new BetPlacedUniBet(-1, stake,false, -1);
        WebDriver driver = ChromeDriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        try {
            driver.get(UNIBET_LINK);
            //close annoying cover page showing offers. enclosed in try because sometimes does not appear
            try {
                wait.until(presenceOfElementLocated(By.cssSelector("button[type='reset']")));
                WebElement closeBtn = driver.findElement(By.cssSelector("button[type='reset']"));
                Thread.sleep(2000); //site makes link unclickable for some time
                closeBtn.click();
            } catch (Exception e) {
                System.out.println("No annoying cover page. Continuing as normal");
            }

            //get rid of annoying cookies notice. should be done here as cookie overlay can block click events on the odds.
            boolean hasAcceptedCookies = false;
            try {
                wait.until(presenceOfElementLocated(By.cssSelector("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"))).click();
                hasAcceptedCookies = true;
            } catch (Exception e) {
                System.out.println("No need to accept cookies. Continuing as normal");
            }

            //login
            driver.findElement(By.cssSelector("[data-test-name=header-login-button]")).click();
            wait.until(presenceOfElementLocated(By.cssSelector("input[data-test-name='kaf-username-email-field']"))).sendKeys(Unibet_Secrets.USERNAME);
            driver.findElement(By.cssSelector("input[data-test-name='kaf-password-field']")).sendKeys(Unibet_Secrets.PASSWORD);
            WebElement loginBtn = wait.until(presenceOfElementLocated(By.cssSelector("button[data-test-name='kaf-submit-credentials-button']")));
            loginBtn.click();
            Thread.sleep(2000); //waiting for the page to reload otherwise we will find containers before page reload.

            if (!hasAcceptedCookies) {
                //get rid of annoying cookies notice. should be done here as cookie overlay can block click events on the odds.
                try {
                    wait.until(presenceOfElementLocated(By.cssSelector("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"))).click();
                } catch (Exception e) {
                    System.out.println("No need to accept cookies. Continuing as normal");
                }
            }

            //wait for balance to show
            wait.until(presenceOfElementLocated(By.cssSelector("span[data-test-name='balance-cash-amount']")));
            Thread.sleep(2000); //wait for balance to be updated
            double mainBalance = Double.parseDouble(driver.findElement(By.cssSelector("span[data-test-name='balance-cash-amount']")).getText().substring(1));
            double bonusBalance = Double.parseDouble(driver.findElement(By.cssSelector("span[data-test-name='balance-bonus-amount']")).getText().substring(1));
            double balance = mainBalance+bonusBalance;
            bet.setBalance(balance);
            if (balance < stake) {
                stake = balance;
                bet.setStake(stake);
            }

            //wait for market info
            wait.until(presenceOfElementLocated(By.cssSelector("[data-test-name=matches]")));
            ArrayList<WebElement> allCountries = (ArrayList<WebElement>) driver.findElements(By.cssSelector("[data-test-name=accordionLevel1]"));

            //look for correct league
            countryLoop:
            for (WebElement country: allCountries) {
                actions.moveToElement(country);
                actions.perform();
                String uniBetCountry = country.findElement(By.cssSelector("h3")).getText().trim(); //title should be first span in group
                if (uniBetCountry.equals(targetCountry)) {
                    country.click();
                    Thread.sleep(1500); //allowing webpage to load in leagues and games of country
                    ArrayList<WebElement> leaguesInCountry = (ArrayList<WebElement>) country.findElements(By.cssSelector("[data-test-name=accordionLevel2]"));
                    for (int i = 0; i<leaguesInCountry.size(); i++) {
                        WebElement leagueElem = leaguesInCountry.get(i);
                        if (leagueName.equals(leagueElem.getText().trim())) {
                            ArrayList<WebElement> games = (ArrayList<WebElement>) leagueElem.findElements(By.cssSelector("[data-test-name=event]"));
                            for (int g = 0; g < games.size(); g++) {
                                WebElement game = games.get(g);
                                ArrayList<WebElement> teamNames = (ArrayList<WebElement>) game.findElements(By.cssSelector("[data-test-name=teamName]"));
                                if (teamNames.get(0).getText().equals(homeTeam) || teamNames.get(1).getText().equals(awayTeam)) {
                                    ArrayList<WebElement> odds = (ArrayList<WebElement>) game.findElements(By.cssSelector("[data-test-id=odds]"));
                                    WebElement relevantOdds = odds.get(result);
                                    String oddsStr = relevantOdds.getText();
                                    if (oddsStr.equals("Evens")) oddsStr = "1/1";
                                    double oddsOffered = ConvertOdds.fromFractionToDecimal(oddsStr);
                                    bet.setOddsOffered(oddsOffered);
                                    if (oddsOffered >= minOdds) {
                                        actions.moveToElement(odds.get(0));
                                        actions.perform();
                                        relevantOdds.click();
                                        break countryLoop;
                                    } else {
                                        return bet;
                                    }
                                }
                                //scroll latest game into view so next game can be seen
//                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", game);
//                                Thread.sleep(200);
                            }
                        }
                    }
                }
                //scrolling latest country into view so the next element is not covered by the header
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", country);
//                Thread.sleep(200);
            };

            //filling out bet form and placing bet
            wait.until(presenceOfElementLocated(By.cssSelector(".mod-KambiBC-stake-input"))).sendKeys(bet.getStake()+"");
            driver.findElement(By.cssSelector(".mod-KambiBC-betslip__place-bet-btn")).click();
            wait.until(presenceOfElementLocated(By.cssSelector(".mod-KambiBC-betslip-receipt__close-button"))).click();
            bet.setBetSuccessful();
            bet.setBalance(bet.getBalance()-bet.getStake());

            //logging out
            WebElement accountBtn = driver.findElement(By.cssSelector(".account-box-button"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", accountBtn);
            accountBtn.click();
            Thread.sleep(1000);
            wait.until(presenceOfElementLocated(By.cssSelector(".logout-link"))).click();
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return bet;
    }
}
