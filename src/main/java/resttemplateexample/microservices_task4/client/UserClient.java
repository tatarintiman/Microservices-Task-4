package resttemplateexample.microservices_task4.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import resttemplateexample.microservices_task4.model.User;

import java.util.List;

public class UserClient {
    private static final String BASE_URL = "http://94.198.50.185:7081/api/users";
    private RestTemplate restTemplate = new RestTemplate();
    private String sessionId;

    public static void main(String[] args) {
        UserClient client = new UserClient();
        client.run();
    }

    public void run(){

        ResponseEntity<User[]> response = restTemplate.getForEntity(BASE_URL, User[].class);
        createHeaders(response);

        User user = new User(3L, "James", "Brown", (byte) 25);
        String firstRequestString = addUser(user);

        user.setName("Thomas");
        user.setLastName("Shelby");
        String secondRequestString = updateUser(user);

        String thirdRequestString = deleteUser(user.getId());

        String finalAnswer = firstRequestString + secondRequestString + thirdRequestString;
        System.out.println(finalAnswer);
    }

    public void createHeaders(ResponseEntity<?> response){
        HttpHeaders headers = response.getHeaders();
        List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
        if (cookies != null && !cookies.isEmpty()){
            sessionId = cookies.get(0).split(";")[0];
            System.out.println("Session ID: " + sessionId);
        }
    }

    public String addUser(User user){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionId);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

        return response.getBody();
    }

    public String updateUser(User user){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionId);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.PUT, request, String.class);

        return response.getBody();
    }

    public String deleteUser(long id){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionId);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/" + id, HttpMethod.DELETE, request, String.class);

        return response.getBody();
    }

}
