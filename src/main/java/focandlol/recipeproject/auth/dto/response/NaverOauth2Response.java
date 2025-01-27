package focandlol.recipeproject.auth.dto.response;

import java.util.Map;

/**
 * 네이버용 response
 */
public class NaverOauth2Response implements Oauth2Response {

  private final String provider;
  private final Map<String, Object> attribute;

  public NaverOauth2Response(Map<String, Object> attribute, String provider) {
    this.attribute = (Map<String, Object>) attribute.get("response");
    this.provider = provider;
  }

  @Override
  public String getProvider() {
    return provider;
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    return attribute.get("email").toString();
  }

  @Override
  public String getName() {
    return attribute.get("name").toString();
  }
}
