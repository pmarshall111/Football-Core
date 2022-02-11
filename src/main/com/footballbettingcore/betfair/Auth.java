package com.footballbettingcore.betfair;

import com.jbetfairng.BetfairClient;
import com.jbetfairng.enums.Exchange;
import com.jbetfairng.exceptions.LoginException;

public class Auth {
    public final static String BETFAIR_P12_PATH = System.getenv("BETFAIR_P12_PATH");
    public final static String BETFAIR_P12_PASS = System.getenv("BETFAIR_P12_PASS");
    public final static String BETFAIR_ACC_USER = System.getenv("BETFAIR_ACC_USER");
    public final static String BETFAIR_ACC_PASS = System.getenv("BETFAIR_ACC_PASS");
    public final static String BETFAIR_APP_KEY = System.getenv("BETFAIR_APP_KEY");

    private Auth() {}

    public static BetfairClient login() throws LoginException {
        BetfairClient client = new BetfairClient(Exchange.UK, BETFAIR_APP_KEY);
        client.login(BETFAIR_P12_PATH,
                BETFAIR_P12_PASS,
                BETFAIR_ACC_USER,
                BETFAIR_ACC_PASS);
        return client;
    }
}
