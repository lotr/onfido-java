package com.onfido;

import com.onfido.api.Config;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.time.Duration;

/** The main class used for accessing instances of the manager classes. */
public final class Onfido {
  private static final OkHttpClient CLIENT = new OkHttpClient();

  private static final String EU_API_URL = "https://api.eu.onfido.com/v3.5/";
  private static final String US_API_URL = "https://api.us.onfido.com/v3.5/";
  private static final String CA_API_URL = "https://api.ca.onfido.com/v3.5/";

  /** The Configuration for the instance. */
  public final Config config;

  /** The manager class for the Applicant resource. */
  public final ApplicantManager applicant;
  /** The manager class for the Document resource. */
  public final DocumentManager document;
  /** The manager class for the Check resource. */
  public final CheckManager check;
  /** The manager class for the Report resource. */
  public final ReportManager report;
  /** The manager class for the Live photo resource. */
  public final LivePhotoManager livePhoto;
  /** The manager class for the Live video resource. */
  public final LiveVideoManager liveVideo;
  /** The manager class for the Address resource */
  public final AddressManager address;
  /** The manager class for the Sdk token resource. */
  public final SdkTokenManager sdkToken;
  /** The manager class for the Webhook resource. */
  public final WebhookManager webhook;
  /** The manager class for the Extraction resource. */
  public final ExtractionManager extraction;

  private Onfido(Builder builder) {
    config = new Config(builder);
    OkHttpClient.Builder clientBuilder = CLIENT.newBuilder();

    if (builder.clientInterceptor != null) {
      clientBuilder.addInterceptor(builder.clientInterceptor);
    }

    if (builder.httpClientReadTimeout != null) {
      clientBuilder.readTimeout(builder.httpClientReadTimeout);
    }

    if (builder.httpClientProxy != null) {
      clientBuilder.proxy(builder.httpClientProxy);
    }

    final OkHttpClient client = clientBuilder.build();
    applicant = new ApplicantManager(this.config, client);
    document = new DocumentManager(this.config, client);
    check = new CheckManager(this.config, client);
    report = new ReportManager(this.config, client);
    livePhoto = new LivePhotoManager(this.config, client);
    liveVideo = new LiveVideoManager(this.config, client);
    address = new AddressManager(this.config, client);
    sdkToken = new SdkTokenManager(this.config, client);
    webhook = new WebhookManager(this.config, client);
    extraction = new ExtractionManager(this.config, client);
  }

  /** The Builder for the Onfido object. */
  public static final class Builder {
    /** The Api token. */
    public String apiToken = "";
    /** The Api url. */
    public String apiUrl = "";
    /** The HTTP client interceptor. */
    private Interceptor clientInterceptor;
    /** Read timeout duration, defaults to 30 seconds. */
    private Duration httpClientReadTimeout = Duration.ofSeconds(30);
    /** HttpClient Proxy */
    private Proxy httpClientProxy;

    private Builder() {}

    /**
     * Builds the Onfido object instance.
     *
     * @return the Onfido
     */
    public Onfido build() {
      if (apiToken == null || apiToken.isEmpty()) {
        throw new RuntimeException("Please provide an apiToken");
      }

      if (apiUrl == null || apiUrl.isEmpty()) {
        throw new RuntimeException(
          "Please specify a region with .regionEU(), .regionUS(), or .regionCA(). " +
          "We previously defaulted to the EU region, so if you previously didn’t set a region or " +
          "used api.onfido.com, please set your region using .regionEU()"
        );
      }

      return new Onfido(this);
    }

    /**
     * Api token attribute.
     *
     * @param apiToken the api token
     * @return the builder
     */
    public Builder apiToken(String apiToken) {
      this.apiToken = apiToken;
      return this;
    }

    /**
     * Interceptor attribute.
     *
     * @param interceptor the HTTP interceptor
     * @return the builder
     */
    public Builder clientInterceptor(Interceptor interceptor) {
      this.clientInterceptor = interceptor;
      return this;
    }

    /**
     * The read timeout duration that will be used to configure the HttpClient
     * @param readTimeout the readTimeout
     * @return the builder
     */
    public Builder clientReadTimeout(Duration readTimeout) {
      this.httpClientReadTimeout = readTimeout;
      return this;
    }

    /**
     * The proxy that will be used to configure the HttpClient with
     * @param proxy the proxy
     * @return the proxy
     */
    public Builder clientProxy(Proxy proxy) {
      this.httpClientProxy = proxy;
      return this;
    }

    /**
     * Sets the object to use the EU region base URL.
     *
     * @return the builder
     */
    public Builder regionEU() {
      this.apiUrl = EU_API_URL;
      return this;
    }

    /**
     * Sets the object to use the US region base URL.
     *
     * @return the builder
     */
    public Builder regionUS() {
      this.apiUrl = US_API_URL;
      return this;
    }

    /**
     * Sets the object to use the Canada region base URL.
     *
     * @return the builder
     */
    public Builder regionCA() {
      this.apiUrl = CA_API_URL;
      return this;
    }

    /**
     * Sets the object to use the specified URL for the api. Used for testing.
     *
     * @param url the url
     * @return the builder
     */
    public Builder unknownApiUrl(String url) {
      this.apiUrl = url;
      return this;
    }
  }

  /**
   * Gives access to the builder for the object.
   *
   * @return the builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Shuts down the client across all Onfido instances, causing all future API calls to be rejected.
   */
  public static void shutdown() {
    CLIENT.dispatcher().executorService().shutdown();
    CLIENT.connectionPool().evictAll();
  }
}
