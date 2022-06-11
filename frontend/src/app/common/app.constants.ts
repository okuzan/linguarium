export class AppConstants {
  private static API_BASE_URL = 'http://localhost:8080/';
  private static OAUTH2_URL = AppConstants.API_BASE_URL + 'oauth2/authorization/';
  private static REDIRECT_URL = '?redirect_uri=http://localhost:8081/login';
  public static API_URL = AppConstants.API_BASE_URL + 'api/';
  public static AUTH_API = AppConstants.API_URL + 'auth/';
  public static FRIEND_API = AppConstants.API_URL + 'friend/';
  public static LANG_API = AppConstants.API_URL + 'lang/';
  public static GOOGLE_AUTH_URL = AppConstants.OAUTH2_URL + 'google' + AppConstants.REDIRECT_URL;
  public static FACEBOOK_AUTH_URL = AppConstants.OAUTH2_URL + 'facebook' + AppConstants.REDIRECT_URL;
  public static GITHUB_AUTH_URL = AppConstants.OAUTH2_URL + 'github' + AppConstants.REDIRECT_URL;
  public static LINKEDIN_AUTH_URL = AppConstants.OAUTH2_URL + 'linkedin' + AppConstants.REDIRECT_URL;
  public static FD_BASE_URL = 'https://api.dictionaryapi.dev/api/v2';
  public static FD_ENDPOINT = '/entries';
  public static FD_LANG_CODE = '/en/';
}
