package focandlol.recipeproject.auth.dto.response;

import java.util.Map;

/**
 * 구글용 response
 */
public class GoogleOauth2Response implements Oauth2Response {

  private final String provider;
  private final Map<String, Object> attribute;

  public GoogleOauth2Response(Map<String, Object> attribute, String provider) {
    this.attribute = attribute;
    this.provider = provider;
  }

  @Override
  public String getProvider() {
    return provider;
  }

  @Override
  public String getProviderId() {
    return attribute.get("sub").toString();
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
