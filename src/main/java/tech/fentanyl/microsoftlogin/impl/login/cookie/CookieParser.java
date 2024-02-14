/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.login.cookie;

import okhttp3.Cookie;
import tech.fentanyl.microsoftlogin.impl.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CookieParser {
    public static List<Cookie> parse(String content) {
        ArrayList<Cookie> cookies = new ArrayList<>();

        List<String> list = Arrays.stream(content.split("\n")).collect(Collectors.toList());

        for (String line : list) {
            String[] options = line.split("\t");
            String name = options[5];

            if (!StringUtil.contains(name, "JSH", "JSHP", "MSPSoftVis", "__Host-MSAAUTHP", "__Host-MSAAUTH", "ClientId")) continue;
            String domain = options[0];

            if (domain.startsWith(".")) {
                domain = domain.substring(1);
            }

            if (cookies.stream().anyMatch(cookie -> cookie.name().equals(name))) continue;
            Cookie cookie = new Cookie.Builder().domain(domain).name(name).value(options[6]).build();
            cookies.add(cookie);
        }

        return cookies;
    }

    public static List<Cookie> parseFromFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        return parse(sb.toString());
    }

    public static String formatCookies(List<Cookie> list) {
        StringBuilder sb = new StringBuilder();

        for (Cookie cookie : list) {
            sb.append(cookie.name()).append("=").append(cookie.value()).append("; ");
        }

        String string = sb.toString();
        return string.substring(0, string.length() - 2);
    }
}
