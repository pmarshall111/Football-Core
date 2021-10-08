package com.petermarshall.placeBet.unibet;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class AutomateBetUniBet {
    private static final String UNIBET_LINK = "https://www.unibet.co.uk/betting/sports/filter/football";

    public static void main(String[] args) {
        BetPlacedUniBet bpub = placeBet("Italy", "Serie A", "Bologna", "Juventus", 2, 0.1, 1.3);
        System.out.println("Odds: " + bpub.getOddsOffered());
        System.out.println("STake: " + bpub.getStake());
        System.out.println("Success: " + bpub.isBetSuccessful());
        System.out.println("bal:" + bpub.getBalance());
    }

    //int result 0 = home, 1 = draw, 2 = away
    public static BetPlacedUniBet placeBet(String targetCountry, String leagueName, String homeTeam, String awayTeam, int result, double amount, double minOdds) {
        double stake = amount;
        BetPlacedUniBet bet = new BetPlacedUniBet(-1, stake,false, -1);
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        try {
            driver.get(UNIBET_LINK);
            //close annoying cover page showing offers. enclosed in try because sometimes does not appear
            try {
                wait.until(presenceOfElementLocated(By.cssSelector("button[type='reset']")));
                WebElement closeBtn = driver.findElement(By.cssSelector("button[type='reset']"));
                Thread.sleep(2010); //site makes link unclickable for some time
                closeBtn.click();
            } catch (Exception e) {
                System.out.println("No annoying cover page. Continuing as normal");
            }
            //login
            wait.until(presenceOfElementLocated(By.cssSelector("input[data-test-name='field-username']"))).sendKeys(PrivateKeysUniBet.USERNAME);
            driver.findElement(By.cssSelector("input[data-test-name='field-password']")).sendKeys(PrivateKeysUniBet.PASSWORD);
            WebElement loginBtn = wait.until(presenceOfElementLocated(By.cssSelector("button[data-test-name='btn-login']")));
            loginBtn.click();
            Thread.sleep(5000); //waiting for the page to reload otherwise we will find containers before page reload.

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
            Thread.sleep(20000); //extra long wait as webpage can take a really long time to load in countries
            wait.until(presenceOfElementLocated(By.cssSelector(".KambiBC-collapsible-container")));
            Thread.sleep(5000); //letting the webpage load in all countries
            ArrayList<WebElement> allCountries = (ArrayList<WebElement>) driver.findElements(By.cssSelector(".KambiBC-mod-event-group-container"));
            //get rid of annoying cookies notice. should be done here as cookie overlay can block click events on the odds.
            wait.until(presenceOfElementLocated(By.cssSelector("#CybotCookiebotDialogBodyButtonAccept"))).click();
            //look for correct league
            countryLoop:
            for (WebElement country: allCountries) {
                String uniBetCountry = country.findElement(By.cssSelector("header span")).getText().trim(); //title should be first span in group
                if (uniBetCountry.equals(targetCountry)) {
                    if (!country.getAttribute("class").contains("KambiBC-expanded")) {
                        country.click();
                        Thread.sleep(1500); //allowing webpage to load in leagues and games of country
                    }
                    ArrayList<WebElement> leaguesInCountry = (ArrayList<WebElement>) country.findElements(By.cssSelector(".KambiBC-betoffer-labels__title"));
                    ArrayList<WebElement> gamesInLeagues = (ArrayList<WebElement>) country.findElements(By.cssSelector(".KambiBC-list-view__event-list"));
                    for (int i = 0; i<leaguesInCountry.size(); i++) {
                        String league = leaguesInCountry.get(i).getText().trim();
                        if (league.equals(leagueName)) {
                            ArrayList<WebElement> games = (ArrayList<WebElement>) gamesInLeagues.get(i).findElements(By.cssSelector(".KambiBC-event-item__event-wrapper"));
                            for (int g = 0; g < games.size(); g++) {
                                WebElement game = games.get(g);
                                ArrayList<WebElement> teamNames = (ArrayList<WebElement>) game.findElements(By.cssSelector(".KambiBC-event-participants__name"));
                                if (teamNames.get(0).getText().equals(homeTeam) || teamNames.get(1).getText().equals(awayTeam)) {
                                    ArrayList<WebElement> odds = (ArrayList<WebElement>) game.findElements(By.cssSelector("div[class*='onecrosstwo'] button"));
                                    WebElement relevantOdds = odds.get(result);
                                    String oddsStr = relevantOdds.findElements(By.cssSelector("div > div > div")).get(2).getText();
                                    double oddsOffered = Double.parseDouble(oddsStr);
                                    bet.setOddsOffered(oddsOffered);
                                    if (oddsOffered >= minOdds) {
                                        relevantOdds.click();
                                        break countryLoop;
                                    } else {
                                        return bet;
                                    }
                                }
                                //scroll latest game into view so next game can be seen
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", game);
                                Thread.sleep(200);
                            }
                        }
                    }
                }
                //scrolling latest country into view so the next element is not covered by the header
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", country);
                Thread.sleep(200);
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
