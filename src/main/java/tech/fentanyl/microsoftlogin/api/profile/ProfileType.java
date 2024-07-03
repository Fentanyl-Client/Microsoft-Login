package tech.fentanyl.microsoftlogin.api.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.fentanyl.microsoftlogin.impl.login.cracked.CrackedProfile;
import tech.fentanyl.microsoftlogin.impl.login.easymc.EasyMCProfile;
import tech.fentanyl.microsoftlogin.impl.login.web.WebProfile;

@Getter
@AllArgsConstructor
public enum ProfileType {
    CRACKED(CrackedProfile.class),
    EASY_MC(EasyMCProfile.class),
    WEB(WebProfile.class);

    private final Class<? extends Profile> profileClass;
}
