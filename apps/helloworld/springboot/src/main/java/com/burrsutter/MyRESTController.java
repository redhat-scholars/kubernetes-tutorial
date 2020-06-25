package com.burrsutter;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.env.Environment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@RestController
public class MyRESTController {
     @Autowired
     private Environment environment;

     final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
     String greeting;

     private int count = 0; // simple counter to see lifecycle
     boolean behave = true;
     boolean dead = false;

     RestTemplate restTemplate = new RestTemplate();

   @RequestMapping("/appendgreetingfile")
   public ResponseEntity<String> appendGreetingToFile() throws IOException {
     
       try(final FileWriter fileWriter = new FileWriter("/tmp/demo/greeting.txt", true)) {
          fileWriter.append(environment.getProperty("GREETING","Jambo"));
          fileWriter.close();
       }
       return ResponseEntity.status(HttpStatus.CREATED).build();
   } 


   @RequestMapping("readgreetingfile")
   public String readGreetingFile() throws IOException {
        return new String(Files.readAllBytes(Paths.get("/tmp/demo/greeting.txt")));
   }

   @RequestMapping("/")
   public String sayHello() {
       greeting = environment.getProperty("GREETING","Jambo");
       count++;
       System.out.println(greeting + " from " + hostname + " " + count);
       return greeting + " from Spring Boot! " + count + " on " + hostname + "\n";
   }

   @RequestMapping("/sysresources") 
   public String getSystemResources() {
        long memory = Runtime.getRuntime().maxMemory();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("/sysresources " + hostname);
        return 
            " Memory: " + (memory / 1024 / 1024) +
            " Cores: " + cores + "\n";
   }

   @RequestMapping("/consume") 
   public String consumeSome() {
        System.out.println("/consume " + hostname);

        Runtime rt = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder();
        long maxMemory = rt.maxMemory();
        long usedMemory = 0;
        // while usedMemory is less than 80% of Max
        while (((float) usedMemory / maxMemory) < 0.80) {
            sb.append(System.nanoTime() + sb.toString());
            usedMemory = rt.totalMemory();
        }
        String msg = "Allocated about 80% (" + humanReadableByteCount(usedMemory, false) + ") of the max allowed JVM memory size ("
            + humanReadableByteCount(maxMemory, false) + ")";
        System.out.println(msg);
        return msg + "\n";
   }

   @RequestMapping(method = RequestMethod.GET, value = "/health")   
   public ResponseEntity<String> health() {               
        if (behave) {
          return ResponseEntity.status(HttpStatus.OK)
          .body("I am fine, thank you\n");     
        } else {             
          return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Bad");          
        }
   }

   @RequestMapping(method = RequestMethod.GET, value = "/misbehave")   
   public ResponseEntity<String> misbehave() {
        behave = false;
        return ResponseEntity.status(HttpStatus.OK).body("Misbehaving");
   }

   @RequestMapping(method = RequestMethod.GET, value = "/behave")   
   public ResponseEntity<String> behave() {
        behave = true;
        return ResponseEntity.status(HttpStatus.OK).body("Ain't Misbehaving");
   }

   @RequestMapping(method = RequestMethod.GET, value = "/shot")   
   public ResponseEntity<String> shot() {
        dead = true;
        return ResponseEntity.status(HttpStatus.OK).body("I have been shot in the head");
        // https://www.quora.com/Why-can-zombies-only-die-by-being-shot-in-the-head-Why-can-they-survive-all-the-blood-loss-and-still-live-If-zombies-were-real-anyway
   }

   @RequestMapping(method = RequestMethod.GET, value = "/alive")   
   public ResponseEntity<String> alive() {
    if (!dead) {
      return ResponseEntity.status(HttpStatus.OK)
      .body("It's Alive! (Frankenstein)\n");     
    } else {             
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All dead, not mostly dead (Princess Bride)");
    }
}



   @RequestMapping("/configure")
   public String configure() {
        String databaseConn = environment.getProperty("DBCONN","Default");
        String msgBroker = environment.getProperty("MSGBROKER","Default");
        greeting = environment.getProperty("GREETING","Default");
        String love = environment.getProperty("LOVE","Default");
        return "Configuration for : " + hostname + "\n" 
            + "databaseConn=" + databaseConn + "\n"
            + "msgBroker=" + msgBroker + "\n"
            + "greeting=" + greeting + "\n"
            + "love=" + love + "\n";
   }

   @RequestMapping("/callinganother")
   public String callinganother() {
        
        // <servicename>.<namespace>.svc.cluster.local
        String url = "http://mynode.yourspace.svc.cluster.local:8000/";

        ResponseEntity<String> response
        = restTemplate.getForEntity(url, String.class);
    
        String responseBody =  response.getBody();
        System.out.println(responseBody);

        return responseBody;
   }

   public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}