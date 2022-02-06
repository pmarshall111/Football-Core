package com.footballbettingcore.scrape.placeBet.bet365;

import com.footballbettingcore.scrape.WebDriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class AutomateBet {
    final private static String BET_365_LINK_ALL_LEAGUES = "https://www.bet365.com/#/AC/B1/C1/D13/E0/F2/J0/Q1/F^24/";

    final private static String EPL = "https://www.bet365.com/#/AC/B1/C1/D13/E49359350/F2/";
    final private static String LA_LIGA = "https://www.bet365.com/#/AC/B1/C1/D13/E49462827/F2/";
    final private static String BUNDESLIGA = "https://www.bet365.com/#/AC/B1/C1/D13/E48779875/F2/";
    final private static String SERIE_A = "https://www.bet365.com/#/AC/B1/C1/D13/E49487629/F2/";
    final private static String LIGUE_1 = "https://www.bet365.com/#/AC/B1/C1/D13/E0/F2/J0/Q1/F^24/"; //TODO: needs to be found. As season cancelled could not confirm
    final private static String RUSSIA = "https://www.bet365.com/#/AC/B1/C1/D13/E49078161/F2/";


    public static void main(String[] args) {
        BetPlaced bp = placeBet("EPL", "Burnley", "Watford", Winner.AWAY.getSetting(), 0.1, 1.7);
        System.out.println("Odds: " + bp.getOddsOffered());
        System.out.println("Stake: " + bp.getStake());
        System.out.println("Success: " + bp.isBetSuccessful());
        System.out.println("Balance: " + bp.getBalance());
    }

    public static BetPlaced placeBet(String leagueName, String homeTeam, String awayTeam, int result, double amount, double minOdds) {
        double stake = amount;
        BetPlaced bet = new BetPlaced(-1,stake,false, -1);
        WebDriver driver = WebDriverFactory.getFirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String url = getUrlFromLeagueName(leagueName);
        try {
            driver.get(url);
//            try {
//                wait.until(presenceOfElementLocated(By.cssSelector("div[class*='MarketFixtureDetailsLabel']")));
//            } catch (Exception e) {}
            //login
            WebElement loginBtn = wait.until(presenceOfElementLocated(By.cssSelector(".hm-MainHeaderRHSLoggedOutWide_Login")));
            loginBtn.click();
            driver.manage().deleteAllCookies();
            driver.findElement(By.cssSelector(".lms-StandardLogin_Username")).sendKeys(Bet365_Secrets.USER);
            driver.findElement(By.cssSelector(".lms-StandardLogin_Password")).sendKeys(Bet365_Secrets.PASSWORD);
            driver.findElement(By.cssSelector(".lms-LoginButton")).click();
            //wait for market info
            Thread.sleep(5000);
            wait.until(presenceOfElementLocated(By.cssSelector("div[class*='MarketFixtureDetailsLabel']")));
            //getting balance
            double balance = Double.parseDouble(driver.findElement(By.cssSelector("div[class*='hm-Balance']")).getText().substring(1));
            bet.setBalance(balance);
            if (stake > balance) {
                stake = balance;
                bet.setStake(stake);
            }

            ArrayList<WebElement> games = (ArrayList<WebElement>) driver.findElements(By.cssSelector("div[class*='ParticipantFixtureDetails_TeamNames']"));
            ArrayList<WebElement> odds = (ArrayList<WebElement>) driver.findElements(By.cssSelector("div[class*='MarketOddsExpand']"));
            for (int i = 0; i<games.size(); i++) {
                WebElement game = games.get(i);
                //scrolling into view
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", game);
                Thread.sleep(200);
                ArrayList<WebElement> teamNames = (ArrayList<WebElement>) game.findElements(By.cssSelector("div[class$='ParticipantFixtureDetails_Team']"));
                if (teamNames.size() == 0) {
                    //have to use ending wildcard here as same class is used with TeamNames or TeamWrapper on the end.
                    teamNames = (ArrayList<WebElement>) game.findElements(By.cssSelector("div[class$='ParticipantFixtureDetails_Team ']"));
                }
                if (teamNames.get(0).getText().equals(homeTeam) || teamNames.get(1).getText().equals(awayTeam)) {
                    WebElement oddsColumn = odds.get(result);
                    WebElement oddsVal = oddsColumn.findElements(By.cssSelector("span[class*='ParticipantOddsOnly80_Odds']")).get(i);
                    double oddsOffered = Double.parseDouble(oddsVal.getText());
                    bet.setOddsOffered(oddsOffered);
                    if (oddsOffered >= minOdds) {
                        oddsVal.click();
                        break;
                    } else {
                        return bet;
                    }
                }
            };
            //filling out bet form and placing bet
            wait.until(presenceOfElementLocated(By.cssSelector(".bss-StakeBox_StakeValueInput"))).sendKeys(bet.getStake()+"");
            driver.findElement(By.cssSelector(".bss-PlaceBetButton")).click();
            bet.setBetSuccessful();
            bet.setBalance(bet.getBalance() - bet.getStake());
            wait.until(presenceOfElementLocated(By.cssSelector(".bs-ReceiptContent_Done"))).click();

            //logging out
            WebElement memberIcon = driver.findElement(By.cssSelector(".hm-MainHeaderMembersWide_MembersMenuIcon"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", memberIcon);
            Thread.sleep(200);
            memberIcon.click();
            wait.until(presenceOfElementLocated(By.cssSelector(".um-MainMenu")));
            ArrayList<WebElement> menuLinks = (ArrayList<WebElement>) driver.findElements(By.cssSelector(".um-MembersLinkRow"));
            menuLinks.get(menuLinks.size()-1).click();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return bet;
    }

    private static String getUrlFromLeagueName(String leagueName) {
        switch (leagueName) {
            case "EPL":
                return EPL;
            case "LA_LIGA":
                return LA_LIGA;
            case "BUNDESLIGA":
                return BUNDESLIGA;
            case "SERIE_A":
                return SERIE_A;
            case "LIGUE_1":
                return LIGUE_1;
            case "RUSSIA":
                return RUSSIA;
            default:
                return BET_365_LINK_ALL_LEAGUES;
        }
    }
}
