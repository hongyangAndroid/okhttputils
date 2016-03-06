package com.zhy.http.okhttp.cookie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SimpleCookieJar implements CookieJar
{
    private final Set<Cookie> allCookies = new HashSet<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        List<Cookie> copy = new ArrayList<>(allCookies);
        allCookies.addAll(cookies);
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie cookie : cookies)
        {
//            Log.e("===saveFromResponse", cookie.name() + " , " + cookie.value());
            for (Cookie old : copy)
            {
                if (old.name().equals(cookie.name()))
                {
                    needRemove.add(old);
                }
            }
        }
        allCookies.removeAll(needRemove);
    }


    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url)
    {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies)
        {
            if (cookie.matches(url))
            {
//                Log.e("===loadForRequest", cookie.name() + " , " + cookie.value());
                result.add(cookie);
            }
        }
        return result;
    }
}
