package com.cab404.libph.util;

import com.cab404.libph.pages.TabunPage;
import com.cab404.libph.requests.LoginRequest;
import com.cab404.moonlight.framework.AccessProfile;
import org.apache.http.Header;
import org.json.simple.JSONObject;

import static com.cab404.libph.requests.LSRequest.LS_KEY_ENTRY;
import static com.cab404.libph.requests.LSRequest.LS_KEY_LEN;

/**
 * @author cab404
 */
public class PonyhawksProfile extends AccessProfile {

    MessageListener messageListener = new MessageListener() {
        @Override
        public void show(JSONObject parsed) {
        }
    };

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public PonyhawksProfile() {
        super("ponyhawks.ru", 80);
        cookies.put("CHECK", "0");
        cookies.put("path", "/");
    }

    @Override
    public synchronized void handleCookies(Header[] headers) {
        String ls_key_cached = cookies.get(LS_KEY_ENTRY);
        super.handleCookies(headers);
        String ls_key_new = cookies.get(LS_KEY_ENTRY);

        if (ls_key_new != null)
            if (ls_key_new.length() != LS_KEY_LEN) {
                System.out.println("LSKEY невалиден. SAY HELLO TO THE RANDOM WITH " + ls_key_new);

                if (ls_key_cached == null)
                    cookies.remove(LS_KEY_ENTRY);
                else
                    cookies.put(LS_KEY_ENTRY, ls_key_cached);
            }
    }

    public boolean login(String name, String password) {
        TabunPage page = new TabunPage();
        page.fetch(this);
        return new LoginRequest(name, password).exec(this).success();
    }

    public static PonyhawksProfile parseString(String s) {
        PonyhawksProfile _return = new PonyhawksProfile();
        _return.setUpFromString(s);
        return _return;
    }

    @Override
    public String userAgentName() {
        return "sweetieBot";
    }
}