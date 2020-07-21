import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.UUID;


public class AccountBalance {


    public static void main(String[] args) throws IOException {

        // Create a trust manager that does not validate certificate chains like the default
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }

       // String client_id= "4nTl2HGcvNBjoOYPyu2wgCkUB7vbb85tMaA_vJoGbXs=";
        //String client_secret ="Dg7nEe57LEH_hn44ITFoy4vzZqalvGuFTZPks1fGrIM=";
        String client_id= "KaJh6KZMfFnI8haZdEuLcBHUYk3tX97IC0AkYauEUMs=";
        String client_secret ="BhGyjuvM4hbpvCyJAzYXcRb4ITlkuEyf7wo21TkWBxc=";
        String redirect_uri="https://localhost:9090/redirect";
        String AccountId ="GB90NWBK50000012345602";
        //String AccountId ="d96151eb-466a-4e27-90e4-f19ad2682194";
        //Step-1 start
        String urlParameters = "grant_type=client_credentials&client_id="+ client_id + "&client_secret="+client_secret+"&scope=accounts";
        //String urlParameters = "grant_type=client_credentials&client_id=KaJh6KZMfFnI8haZdEuLcBHUYk3tX97IC0AkYauEUMs=&client_secret=BhGyjuvM4hbpvCyJAzYXcRb4ITlkuEyf7wo21TkWBxc=&scope=accounts";

        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        URL url = new URL("https://ob.natwest.useinfinite.io/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

        conn.setUseCaches(false);


        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
            int responseCode = conn.getResponseCode();

            System.out.println("responseCode= " + responseCode);
            wr.flush();

        }
        String access_token1;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {

            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {

                response.append(responseLine.trim());

            }
            System.out.println(response.toString());
            String jsonString1 = response.toString();
            access_token1 = jsonString1.substring(17, 968);


        }
/*
Step-1 END

 Step-2 START- domestic-account-consents API
*/


        URL url2 = new URL("https://ob.natwest.useinfinite.io/open-banking/v3.1/aisp/account-access-consents");
        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
        conn2.setDoOutput(true);
        //conn2.setDoInput(true);
        //conn2.setInstanceFollowRedirects(false);
        conn2.setRequestMethod("POST");
        conn2.setRequestProperty("Authorization", "Bearer " + access_token1);
        conn2.setRequestProperty("Content-Type", "application/json");
        String financialID = "0015800000jfwxXAAQ";
        conn2.setRequestProperty("x-fapi-financial-id", financialID);
        //UUID uuid = UUID.randomUUID();
        //System.out.println("uuid= " + uuid);
        //conn2.setRequestProperty("x-idempotency-key", uuid.toString());
        //conn2.setRequestProperty("x-jws-signature", "ignored");

        String jsonInputString2 = "{\n" +
                "  \"Data\": {\n" +
                "    \"Permissions\": [\n" +
                "      \"ReadAccountsDetail\",\n" +
                "      \"ReadBalances\",\n" +
                "      \"ReadTransactionsCredits\",\n" +
                "      \"ReadTransactionsDebits\",\n" +
                "      \"ReadTransactionsDetail\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"Risk\": {}\n" +
                "}";


        try (OutputStream os2 = conn2.getOutputStream()) {
            byte[] input = jsonInputString2.getBytes("utf-8");
            os2.write(input, 0, input.length);
        }

        //conn2.setUseCaches(false);
        String ConsentId;
        int responseCode2 = conn2.getResponseCode();

        System.out.println("responseCode2= " + responseCode2);

        try (BufferedReader br2 = new BufferedReader(
                new InputStreamReader(conn2.getInputStream(), "utf-8"))) {

            StringBuilder response2 = new StringBuilder();
            String responseLine2 = null;
            while ((responseLine2 = br2.readLine()) != null) {

                response2.append(responseLine2.trim());

            }
            System.out.println(response2.toString());

            String jsonString2 = response2.toString();
            ConsentId = jsonString2.substring(22, 58);


        }
        /* Step-2 END - domestic-account-consents API */
        /* Step-3 START - Authorize consent */

       String AuthURl="https://api.natwest.useinfinite.io/authorize?client_id="+ client_id + "&response_type=code id_token&scope=openid accounts&redirect_uri=" + redirect_uri +"&request="+ ConsentId + "&authorization_mode=AUTO_POSTMAN&authorization_result=APPROVED&authorization_username=123456789012@eac0144e-4778-4085-a933-4de014788c9b.example.org&authorization_accounts=*";
        System.out.println("AuthURl=" + AuthURl);

        URL url3 = new URL(AuthURl);
        HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
        conn3.setDoOutput(true);

        conn3.setRequestMethod("GET");

        int responseCode3 = conn3.getResponseCode();
        System.out.println("responseCode3= " + responseCode3);
        String AuthCode;
        try (BufferedReader br3 = new BufferedReader(
                new InputStreamReader(conn3.getInputStream(), "utf-8"))) {

            StringBuilder response3 = new StringBuilder();
            String responseLine3 = null;
            while ((responseLine3 = br3.readLine()) != null) {

                response3.append(responseLine3.trim());

            }
            System.out.println(response3.toString());
            String jsonString3 = response3.toString();
            AuthCode = jsonString3.substring(jsonString3.lastIndexOf("code=") + 5, jsonString3.indexOf("&id_token"));

            System.out.println("AuthCode=" + AuthCode);
        }
            /* Step-3 END - Authorize consent */
            /* Step-4 START - Exchange code for access token */

            String urlParameters4 = "client_id="+client_id+"&client_secret="+ client_secret + "&redirect_uri="+redirect_uri+"&grant_type=authorization_code&code="+AuthCode;

            byte[] postData4 = urlParameters4.getBytes(StandardCharsets.UTF_8);
            int postDataLength4 = postData4.length;

            URL url4 = new URL("https://ob.natwest.useinfinite.io/token");
            HttpURLConnection conn4 = (HttpURLConnection) url4.openConnection();
            conn4.setDoOutput(true);
            conn4.setDoInput(true);
            conn4.setInstanceFollowRedirects(false);
            conn4.setRequestMethod("POST");
            conn4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn4.setRequestProperty("charset", "utf-8");
            conn4.setRequestProperty("Content-Length", Integer.toString(postDataLength4));

            conn4.setUseCaches(false);


            try (DataOutputStream wr4 = new DataOutputStream(conn4.getOutputStream())) {
                wr4.write(postData);
                int responseCode4 = conn4.getResponseCode();

                System.out.println("responseCode4= " + responseCode4);
                wr4.flush();

            }
            String access_token4;
            try (BufferedReader br4 = new BufferedReader(
                    new InputStreamReader(conn4.getInputStream(), "utf-8"))) {

                StringBuilder response4 = new StringBuilder();
                String responseLine4 = null;
                while ((responseLine4 = br4.readLine()) != null) {

                    response4.append(responseLine4.trim());

                }
                System.out.println(response4.toString());
                String jsonString4 = response4.toString();

               // access_token4 = jsonString4.substring(17, 968);
                access_token4 = jsonString4.substring(jsonString4.lastIndexOf("access_token")+15,968);
                System.out.println("access_token4= "+ access_token4);


            }

        /* Step-4 END - Exchange code for access token */
        /* Step-5 END - Get Account Balance */

        String balanceURL = "https://ob.natwest.useinfinite.io/open-banking/v3.1/aisp/accounts/"+AccountId+"/balances";


        URL url5 = new URL(balanceURL);

        HttpURLConnection conn5 = (HttpURLConnection) url5.openConnection();
        conn5.setDoOutput(true);
       conn5.setDoInput(true);
        conn5.setInstanceFollowRedirects(false);
        conn5.setRequestProperty("Authorization", "Bearer " + access_token4);

        conn5.setRequestProperty("x-fapi-financial-id", "0015800000jfwxXAAQ");
        conn5.setRequestProperty("Host", "ob.natwest.useinfinite.io");


        conn5.setRequestMethod("GET");

        int responseCode5 = conn5.getResponseCode();
        //conn5.getErrorStream();

        System.out.println("responseCode5= " + responseCode5);
        InputStream a;
        a = conn5.getErrorStream();
        System.out.println("Error Stream : "+a);

        /*try (DataOutputStream wr5 = new DataOutputStream(conn5.getOutputStream())) {
            //wr5.write(postData);
            int responseCode5 = conn5.getResponseCode();

            System.out.println("responseCode5= " + responseCode5);
            wr5.flush();

        }*/

        try (BufferedReader br5 = new BufferedReader(
                new InputStreamReader(conn5.getInputStream(), "utf-8"))) {

            StringBuilder response5 = new StringBuilder();
            String responseLine5 = null;
            while ((responseLine5 = br5.readLine()) != null) {

                response5.append(responseLine5.trim());

            }
            System.out.println(response5.toString());
            String jsonString5 = response5.toString();


        }


/*

        URL url5 = new URL("https://ob.natwest.useinfinite.io/open-banking/v3.1/aisp/accounts");
        HttpURLConnection conn5 = (HttpURLConnection) url5.openConnection();
        conn5.setDoOutput(true);
        conn5.setDoInput(true);
        conn5.setInstanceFollowRedirects(false);
        conn5.setRequestProperty("Authorization", "Bearer " + access_token4);
        conn5.setRequestProperty("x-fapi-financial-id", "0015800000jfwxXAAQ");
        conn5.setRequestMethod("GET");
        int responseCode5 = conn5.getResponseCode();

        System.out.println("responseCode5= " + responseCode5);
 */
    }
}
