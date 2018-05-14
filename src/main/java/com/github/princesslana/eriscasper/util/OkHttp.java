package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperInfo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkHttp {
  private static final Logger LOG = LoggerFactory.getLogger(OkHttp.class);

  private OkHttp() {}

  /**
   * Creates a OkHttpClient for use within ErisCasper. Ensures we set the user agent correctly on
   * all requests.
   *
   * @see <a href="https://discordapp.com/developers/docs/reference#user-agent">
   *     https://discordapp.com/developers/docs/reference#user-agent</a>
   */
  public static OkHttpClient newHttpClient() {
    ErisCasperInfo info = ErisCasperInfo.load();

    String userAgent = String.format("DiscordBot (%s, %s)", info.getUrl(), info.getVersion());

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(LOG::trace);
    logging.setLevel(Level.BODY);

    return new OkHttpClient.Builder()
        .addInterceptor(
            c -> c.proceed(c.request().newBuilder().header("User-Agent", userAgent).build()))
        .addInterceptor(logging)
        .build();
  }
}
