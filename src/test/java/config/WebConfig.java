package config;

import org.aeonbits.owner.Config;

@Config.Sources({

        "classpath:config/${env}.properties",
})

public interface WebConfig extends Config {

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("browserVersion")
    @DefaultValue("")
    String browserVersion();

    @Key("browserSize")
    @DefaultValue("")
    String browserSize();

    @Key("pageLoadStrategy")
    @DefaultValue("")
    String pageLoadStrategy();

    @Key("isRemote")
    @DefaultValue("false")
    boolean isRemote();

    @Key("remoteUrl")
    @DefaultValue("false")
    String remoteUrl();
}
