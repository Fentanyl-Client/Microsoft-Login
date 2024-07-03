/**
 * Copyright (C) 2024, darraghd493
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tech.fentanyl.microsoftlogin.impl.login.easymc;

import lombok.SneakyThrows;
import tech.fentanyl.microsoftlogin.api.login.Login;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EasyMCLogin extends Login<EasyMCProfile> {
    private final String token;

    public EasyMCLogin(String token) {
        this.token = token;
    }

    @Override
    @SneakyThrows
    public EasyMCProfile login() {
        AtomicReference<EasyMCProfile> profile = new AtomicReference<>(null);
        this.login(profile::set);

        while (profile.get() == null) {
            Thread.sleep(100L);
        }

        return profile.get();
    }

    @SneakyThrows
    public void login(Consumer<EasyMCProfile> callback) {
        callback.accept(new EasyMCLogic().redeem(this.token));
    }
}
