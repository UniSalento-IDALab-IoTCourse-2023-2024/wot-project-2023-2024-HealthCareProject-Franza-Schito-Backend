package org.unisalento.iotproject.restcontrollers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.unisalento.iotproject.domain.*;
import org.unisalento.iotproject.dto.*;
import org.unisalento.iotproject.exceptions.*;
import org.unisalento.iotproject.repositories.*;
import org.unisalento.iotproject.security.JwtUtilities;


import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/parameters")
public class ParameterRestController {

    @Autowired
    private MqttConfig mqttConfig;

    public static final String Bearer_Token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyM1BUOFIiLCJzdWIiOiJDN1o3R1oiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd3BybyB3bnV0IHdzbGUgd2VjZyB3c29jIHdhY3Qgd294eSB3dGVtIHd3ZWkgd2lybiB3Y2Ygd3NldCB3bG9jIHdyZXMiLCJleHAiOjE3NTEyNzQ4NzksImlhdCI6MTc0ODY4Mjg3OX0.lhZnKBtM02fu9BSW84-gK-in4LsI3jEyrsGs9EEEtWw";

    @Autowired
    ParametersRepository parametersRepository;

    @Autowired
    Spo2Repository spo2Repository;

    @Autowired
    HeartRateRepository heartRateRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    WeightRepository weightRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    SleepRepository sleepRepository;

    @Autowired
    DailyMealRepository dailyMealRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilities jwtUtilities;


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/spo2/create",method=RequestMethod.POST)
    public Spo2DTO postSpo2(@RequestParam String date) {

            Spo2 spo2 = new Spo2();

            String fitbitUrl = "https://api.fitbit.com/1/user/-/spo2/date/" + date + ".json";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + Bearer_Token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> response = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> performanceData = response.getBody();
            Map<String, Object> valueData = (Map<String, Object>) performanceData.get("value");

            Object spo2Media = valueData.get("avg");
            Object spo2Min = valueData.get("min");
            Object spo2Max = valueData.get("max");
            Object spo2Date = performanceData.get("dateTime");
            String Stringspo2Date = (String) spo2Date;
            double doublespo2Media = (Double) spo2Media;
            double doublespo2Min = (Double) spo2Min;
            double doublespo2Max = (Double) spo2Max;


            spo2.setDate(Stringspo2Date);
            spo2.setMax(doublespo2Max);
            spo2.setMin(doublespo2Min);
            spo2.setMedia(doublespo2Media);

            spo2 = spo2Repository.save(spo2);


            return null;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/spo2/", method = RequestMethod.GET)
    public Spo2ListDTO getAllSpo2() {

        Spo2ListDTO spo2ListDTO = new Spo2ListDTO();
        ArrayList<Spo2DTO> list = new ArrayList<>();
        spo2ListDTO.setList(list);

        List<Spo2> spo2s = spo2Repository.findAll();

        for (Spo2 spo2 : spo2s) {
            Spo2DTO spo2DTO = new Spo2DTO();
            spo2DTO.setId(spo2.getId());
            spo2DTO.setDate(spo2.getDate());
            spo2DTO.setAvg(spo2.getMedia());
            spo2DTO.setMin(spo2.getMin());
            spo2DTO.setMax(spo2.getMax());

            list.add(spo2DTO);
        }

        return spo2ListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/spo2/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) throws UserNotFoundException {
        if (spo2Repository.findById(id).isEmpty()) {
            throw new UserNotFoundException();
        }
        spo2Repository.deleteById(id);
    }


    @PreAuthorize("hasRole('ROLE_Doctor')")
    @RequestMapping(value = "/spo2/searchByDate", method = RequestMethod.GET)
    public Spo2ListDTO searchSpo2ByDate(@RequestParam String date) throws UserNotFoundException {
        Spo2ListDTO spo2ListDTO = new Spo2ListDTO();
        ArrayList<Spo2DTO> list = new ArrayList<>();
        spo2ListDTO.setList(list);

        List<Spo2> spo2s = spo2Repository.findByDate(date);
        if (spo2s.isEmpty()) {
            throw new UserNotFoundException();
        }

        for (Spo2 spo2 : spo2s) {
            Spo2DTO spo2DTO = new Spo2DTO();
            spo2DTO.setId(spo2.getId());
            spo2DTO.setDate(spo2.getDate());
            spo2DTO.setAvg(spo2.getMedia());
            spo2DTO.setMin(spo2.getMin());
            spo2DTO.setMax(spo2.getMax());

            list.add(spo2DTO);
        }

        return spo2ListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/create",method=RequestMethod.POST)
    public HeartRateDTO postHeartRate(@RequestParam String date) {

            HeartRate heartRate = new HeartRate();

            String fitbitUrl = "https://api.fitbit.com/1/user/-/activities/heart/date/" + date + "/1d/5min/time/00:00/23:59.json";      //INSERIRE TODAY COME DATA

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + Bearer_Token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> response = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> performanceData = response.getBody();

            Map<String, Object> activitiesHeartIntraday = (Map<String, Object>) performanceData.get("activities-heart-intraday");
            List<Map<String, Object>> dataset = (List<Map<String, Object>>) activitiesHeartIntraday.get("dataset");

            Map<Integer, Object> heartRateMap = new HashMap<>();

            int[] heartRateValues = new int[dataset.size()];

            for (int i = 0; i < dataset.size(); i++) {
                heartRateValues[i] = (int) dataset.get(i).get("value");
            }


            heartRate.setRate(heartRateValues);
            heartRate.setDate(date);

            heartRate = heartRateRepository.save(heartRate);
            HeartRateDTO heartRateDTO = new HeartRateDTO();
            heartRateDTO.setId(heartRate.getId());
            heartRateDTO.setRate(heartRate.getRate());
            heartRateDTO.setDate(heartRate.getDate());

            return heartRateDTO;

    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/",method=RequestMethod.GET)
    public HeartRateListDTO getAllHeartRate() {
        HeartRateListDTO heartRateListDTO = new HeartRateListDTO();
        ArrayList<HeartRateDTO> list = new ArrayList<>();
        heartRateListDTO.setList(list);

        List<HeartRate> heartRates = heartRateRepository.findAll();

        for (HeartRate heartRate : heartRates) {
            HeartRateDTO heartRateDTO = new HeartRateDTO();
            heartRateDTO.setId(heartRate.getId());
            heartRateDTO.setRate(heartRate.getRate());
            heartRateDTO.setDate(heartRate.getDate());

            list.add(heartRateDTO);
        }

        return heartRateListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/{id}", method = RequestMethod.GET)
    public HeartRateDTO getHeartRateById(@PathVariable String id) throws HeartRateNotFoundException {
        Optional<HeartRate> heartRateOptional = heartRateRepository.findById(id);
        if (heartRateOptional.isEmpty()) {
            throw new HeartRateNotFoundException();
        }
        HeartRate heartRate = heartRateOptional.get();
        HeartRateDTO heartRateDTO = new HeartRateDTO();
        heartRateDTO.setId(heartRate.getId());
        heartRateDTO.setRate(heartRate.getRate());
        heartRateDTO.setDate(heartRate.getDate());
        return heartRateDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/searchByDate", method = RequestMethod.GET)
    public HeartRateListDTO searchHeartRateByDate(@RequestParam String date) {
        HeartRateListDTO heartRateListDTO = new HeartRateListDTO();
        ArrayList<HeartRateDTO> list = new ArrayList<>();
        heartRateListDTO.setList(list);

        List<HeartRate> heartRates = heartRateRepository.findByDate(date);

        for (HeartRate heartRate : heartRates) {
            HeartRateDTO heartRateDTO = new HeartRateDTO();
            heartRateDTO.setId(heartRate.getId());
            heartRateDTO.setRate(heartRate.getRate());
            heartRateDTO.setDate(heartRate.getDate());

            list.add(heartRateDTO);
        }

        return heartRateListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/delete/{id}", method = RequestMethod.DELETE)
    public void deleteHeartRateById(@PathVariable String id) throws HeartRateNotFoundException {
        if (heartRateRepository.findById(id).isEmpty()) {
            throw new HeartRateNotFoundException();
        }
        heartRateRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/delete/all", method = RequestMethod.DELETE)
    public void deleteHeartRate() throws HeartRateNotFoundException {
        heartRateRepository.deleteAll();
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/create",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WeightDTO postWeight(@RequestBody WeightDTO weightDTO) throws WeightNotFoundException{

            double weight = weightDTO.getWeight();

            Weight weightEntity = new Weight();

            String currentDate = LocalDate.now().toString();

            String fitbitUrl = "https://api.fitbit.com/1/user/-/body/log/weight.json?weight=" + weight + "&date=" + currentDate;

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + Bearer_Token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(fitbitUrl, HttpMethod.POST, entity, String.class);

            fitbitUrl = "https://api.fitbit.com/1/user/-/profile.json";
            RestTemplate restTemplate2 = new RestTemplate();

            headers.set("Authorization", "Bearer " + Bearer_Token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> response2 = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode heightNode = null;

            try {
                JsonNode rootNode = objectMapper.readTree(response2.getBody());
                heightNode = rootNode.path("user").path("height");
                if (heightNode.isMissingNode()) {
                    throw new WeightNotFoundException();
                }
            } catch (JsonProcessingException e) {
                throw new WeightNotFoundException();
            }


            weightEntity.setHeight(heightNode.asDouble());
            weightEntity.setWeight(weight);
            weightEntity.setDate(currentDate);

            weightEntity = weightRepository.save(weightEntity);
            WeightDTO weightDTO2 = new WeightDTO();
            weightDTO2.setId(weightEntity.getId());
            weightDTO2.setWeight(weightEntity.getWeight());
            weightDTO2.setDate(weightEntity.getDate());
            weightDTO2.setHeight(weightEntity.getHeight());

            return weightDTO2;
    }




    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/", method = RequestMethod.GET)
    public WeightListDTO getAllWeight() {
        WeightListDTO weightListDTO = new WeightListDTO();
        ArrayList<WeightDTO> list = new ArrayList<>();
        weightListDTO.setList(list);

        List<Weight> weights = weightRepository.findAll();

        for (Weight weight : weights) {
            WeightDTO weightDTO = new WeightDTO();
            weightDTO.setId(weight.getId());
            weightDTO.setWeight(weight.getWeight());
            weightDTO.setDate(weight.getDate());
            weightDTO.setHeight(weight.getHeight());

            list.add(weightDTO);
        }


        return weightListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/{id}", method = RequestMethod.GET)
    public WeightDTO getByIdWeight(@PathVariable String id) throws WeightNotFoundException {
        Optional<Weight> weightOptional = weightRepository.findById(id);
        if (weightOptional.isEmpty()) {
            throw new WeightNotFoundException();
        }
        Weight weight = weightOptional.get();
        WeightDTO weightDTO = new WeightDTO();
        weightDTO.setId(weight.getId());
        weightDTO.setWeight(weight.getWeight());
        weightDTO.setDate(weight.getDate());
        weightDTO.setHeight(weight.getHeight());

        return weightDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/searchByDate", method = RequestMethod.GET)
    public WeightListDTO searchByDateWeight(@RequestParam String date) {
        WeightListDTO weightListDTO = new WeightListDTO();
        ArrayList<WeightDTO> list = new ArrayList<>();
        weightListDTO.setList(list);

        List<Weight> weights = weightRepository.findByDate(date);

        for (Weight weight : weights) {
            WeightDTO weightDTO = new WeightDTO();
            weightDTO.setId(weight.getId());
            weightDTO.setWeight(weight.getWeight());
            weightDTO.setDate(weight.getDate());
            weightDTO.setHeight(weight.getHeight());

            list.add(weightDTO);
        }

        return weightListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/delete/{id}", method = RequestMethod.DELETE)
    public void deleteByIdWeight(@PathVariable String id) throws WeightNotFoundException {
        if (weightRepository.findById(id).isEmpty()) {
            throw new WeightNotFoundException();
        }
        weightRepository.deleteById(id);
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/create",method=RequestMethod.POST)
    public ActivityDTO postActivity() {

            Activity activity = new Activity();

            String currentDate = LocalDate.now().toString();

            String fitbitUrl = "https://api.fitbit.com/1/user/-/activities/date/" + currentDate + ".json";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + Bearer_Token);
            //headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> response = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            Map<String, Object> summary = (Map<String, Object>) responseBody.get("summary");
            List<Map<String, Object>> distances = (List<Map<String, Object>>) summary.get("distances");


            activity.setCaloriesOut((int) summary.get("caloriesOut"));
            activity.setStep((int) summary.get("steps"));
            activity.setSedentaryMinutes((int) summary.get("sedentaryMinutes"));


            for (Map<String, Object> distance : distances) {
                if ("tracker".equals(distance.get("activity"))) {
                    activity.setDistances((double) distance.get("distance"));
                    break;
                }
            }

            activity.setDate(currentDate);
            activity = activityRepository.save(activity);

            ActivityDTO activityDTO = new ActivityDTO();

            activityDTO.setCaloriesOut(activity.getCaloriesOut());
            activityDTO.setStep(activity.getStep());
            activityDTO.setDistances(activity.getDistances());
            activityDTO.setSedentaryMinutes(activity.getSedentaryMinutes());
            activityDTO.setDate(activity.getDate());
            activityDTO.setId(activity.getId());

            return activityDTO;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/", method = RequestMethod.GET)
    public ActivityListDTO getAllActivity() {
        ActivityListDTO activityListDTO = new ActivityListDTO();
        ArrayList<ActivityDTO> list = new ArrayList<>();
        activityListDTO.setList(list);

        List<Activity> activities = activityRepository.findAll();

        for (Activity activity : activities) {
            ActivityDTO activityDTO = new ActivityDTO();
            activityDTO.setId(activity.getId());
            activityDTO.setCaloriesOut(activity.getCaloriesOut());
            activityDTO.setStep(activity.getStep());
            activityDTO.setSedentaryMinutes(activity.getSedentaryMinutes());
            activityDTO.setDistances(activity.getDistances());
            activityDTO.setDate(activity.getDate());

            list.add(activityDTO);
        }

        return activityListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/{id}", method = RequestMethod.GET)
    public ActivityDTO getByIdActivity(@PathVariable String id) throws ActivityNotFoundException {
        Optional<Activity> activityOptional = activityRepository.findById(id);
        if (activityOptional.isEmpty()) {
            throw new ActivityNotFoundException();
        }
        Activity activity = activityOptional.get();
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(activity.getId());
        activityDTO.setCaloriesOut(activity.getCaloriesOut());
        activityDTO.setStep(activity.getStep());
        activityDTO.setSedentaryMinutes(activity.getSedentaryMinutes());
        activityDTO.setDistances(activity.getDistances());
        activityDTO.setDate(activity.getDate());
        return activityDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/searchByDate", method = RequestMethod.GET)
    public ActivityListDTO searchByDateActivity(@RequestParam String date) {
        ActivityListDTO activityListDTO = new ActivityListDTO();
        ArrayList<ActivityDTO> list = new ArrayList<>();
        activityListDTO.setList(list);

        List<Activity> activities = activityRepository.findByDate(date);

        for (Activity activity : activities) {
            ActivityDTO activityDTO = new ActivityDTO();
            activityDTO.setId(activity.getId());
            activityDTO.setCaloriesOut(activity.getCaloriesOut());
            activityDTO.setStep(activity.getStep());
            activityDTO.setSedentaryMinutes(activity.getSedentaryMinutes());
            activityDTO.setDistances(activity.getDistances());
            activityDTO.setDate(activity.getDate());

            list.add(activityDTO);
        }

        return activityListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/delete/{id}", method = RequestMethod.DELETE)
    public void deleteByIdActivity(@PathVariable String id) throws ActivityNotFoundException {
        if (activityRepository.findById(id).isEmpty()) {
            throw new ActivityNotFoundException();
        }
        activityRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/delete/all", method = RequestMethod.DELETE)
    public void deleteAllActivity()  {
        activityRepository.deleteAll();
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/create",method=RequestMethod.POST)
    public SleepDTO postSleep(@RequestParam String date) {

            Sleep sleep = new Sleep();

            String fitbitUrl = "https://api.fitbit.com/1.2/user/-/sleep/date/" + date + ".json";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + Bearer_Token);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> response = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> sleepList = (List<Map<String, Object>>) responseBody.get("sleep");
            Map<String, Object> firstSleep = sleepList.get(0);

            int num = (int) firstSleep.get("duration") / 60000;
            int minuti = num % 60;
            int ore = num / 60;
            String duration = ore + "h " + minuti + "m";
            int efficiency = (int) firstSleep.get("efficiency");

            Map<String, Object> levels = (Map<String, Object>) firstSleep.get("levels");
            Map<String, Object> summary = (Map<String, Object>) levels.get("summary");

            Map<String, Object> deepMap = (Map<String, Object>) summary.get("deep");
            Map<String, Object> lightMap = (Map<String, Object>) summary.get("light");
            Map<String, Object> remMap = (Map<String, Object>) summary.get("rem");
            Map<String, Object> wakeMap = (Map<String, Object>) summary.get("wake");

            int deep = (int) deepMap.get("minutes");
            int light = (int) lightMap.get("minutes");
            int rem = (int) remMap.get("minutes");
            int wake = (int) wakeMap.get("minutes");

            int[] sleepStages = {deep, light, rem, wake};

            sleep.setDuration(duration);
            sleep.setEfficiency(efficiency);
            sleep.setStages(sleepStages);
            sleep.setDate(date);

            sleep = sleepRepository.save(sleep);

            SleepDTO sleepDTO = new SleepDTO();
            sleepDTO.setDuration(sleep.getDuration());
            sleepDTO.setEfficiency(sleep.getEfficiency());
            sleepDTO.setStages(sleep.getStages());
            sleepDTO.setDate(sleep.getDate());
            sleepDTO.setId(sleep.getId());

            return sleepDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/", method = RequestMethod.GET)
    public List<SleepDTO> getAllSleep() {
        List<Sleep> sleeps = sleepRepository.findAll();
        List<SleepDTO> sleepDTOs = new ArrayList<>();

        for (Sleep sleep : sleeps) {
            SleepDTO sleepDTO = new SleepDTO();
            sleepDTO.setId(sleep.getId());
            sleepDTO.setDuration(sleep.getDuration());
            sleepDTO.setEfficiency(sleep.getEfficiency());
            sleepDTO.setStages(sleep.getStages());
            sleepDTO.setDate(sleep.getDate());
            sleepDTOs.add(sleepDTO);
        }


        return sleepDTOs;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/{id}", method = RequestMethod.GET)
    public SleepDTO getByIdSleep(@PathVariable String id) throws ActivityNotFoundException {
        Optional<Sleep> sleepOptional = sleepRepository.findById(id);
        if (sleepOptional.isEmpty()) {
            throw new ActivityNotFoundException();
        }
        Sleep sleep = sleepOptional.get();
        SleepDTO sleepDTO = new SleepDTO();
        sleepDTO.setId(sleep.getId());
        sleepDTO.setDuration(sleep.getDuration());
        sleepDTO.setEfficiency(sleep.getEfficiency());
        sleepDTO.setStages(sleep.getStages());
        sleepDTO.setDate(sleep.getDate());
        return sleepDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/searchByDate", method = RequestMethod.GET)
    public List<SleepDTO> searchByDateSleep(@RequestParam String date) {
        List<Sleep> sleeps = sleepRepository.findByDate(date);
        List<SleepDTO> sleepDTOs = new ArrayList<>();

        for (Sleep sleep : sleeps) {
            SleepDTO sleepDTO = new SleepDTO();
            sleepDTO.setId(sleep.getId());
            sleepDTO.setDuration(sleep.getDuration());
            sleepDTO.setEfficiency(sleep.getEfficiency());
            sleepDTO.setStages(sleep.getStages());
            sleepDTO.setDate(sleep.getDate());
            sleepDTOs.add(sleepDTO);
        }

        return sleepDTOs;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/delete/{id}", method = RequestMethod.DELETE)
    public void deleteByIdSleep(@PathVariable String id) throws SleepNotFoundException {
        if (sleepRepository.findById(id).isEmpty()) {
            throw new SleepNotFoundException();
        }
        sleepRepository.deleteById(id);
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/create",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public DailyMealDTO postFood(@RequestBody FoodDTO foodDTO, @RequestParam String date) {

        Food food = new Food();

        String fitbitUrl = "https://api.fitbit.com/1/user/-/foods/log.json?foodName=" + foodDTO.getFoodName() +
                "&mealTypeId=" + foodDTO.getMealTypeId() + "&unitId=147&amount=1&date=" + date +
                "&calories=" + foodDTO.getCalories();

        System.out.println(fitbitUrl);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + Bearer_Token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> responsePost = restTemplate.postForEntity(fitbitUrl, entity, String.class);


        ObjectMapper objectMapper = new ObjectMapper();
        long logId = 0;
        try {
            JsonNode rootNode = objectMapper.readTree(responsePost.getBody());
            JsonNode foodLogNode = rootNode.path("foodLog");
            logId = foodLogNode.path("logId").asLong();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        fitbitUrl = "https://api.fitbit.com/1/user/-/foods/log/date/" + date + ".json";

        restTemplate = new RestTemplate();

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + Bearer_Token);

        entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<Map> responseGet = restTemplate.exchange(fitbitUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> responseBody = responseGet.getBody();
        List<Map<String, Object>> foods = (List<Map<String, Object>>) responseBody.get("foods");
        Map<String, Object> loggedFood = null;

        String logDate = null;

        for (Map<String, Object> foodEntry : foods) {
            if (logId == ((Number) foodEntry.get("logId")).longValue()) {
                loggedFood = (Map<String, Object>) foodEntry.get("loggedFood");
                logDate = (String) foodEntry.get("logDate");
                break;
            }
        }

        String name = (String) loggedFood.get("name");
        int calories = (int) loggedFood.get("calories");
        int mealTypeId = (int) loggedFood.get("mealTypeId");

        food.setFoodName(name);
        food.setMealTypeId(mealTypeId);
        food.setCalories(calories);
        food.setFoodId(UUID.randomUUID().toString());

        Optional<DailyMeal> dailyMealOptional = dailyMealRepository.findByDate(logDate);
        DailyMeal dailyMeal;
        if (dailyMealOptional.isEmpty()) {
            dailyMeal = new DailyMeal();
            dailyMeal.setDate(logDate);
            dailyMeal.setFoods(new ArrayList<>());
        } else {
            dailyMeal = dailyMealOptional.get();
        }

        dailyMeal.getFoods().add(food);
        dailyMeal = dailyMealRepository.save(dailyMeal);

        DailyMealDTO dailyMealDTO = new DailyMealDTO();
        dailyMealDTO.setId(dailyMeal.getId());
        dailyMealDTO.setDate(dailyMeal.getDate());

        FoodDTO lastFoodDTO = new FoodDTO();
        lastFoodDTO.setFoodName(food.getFoodName());
        lastFoodDTO.setMealTypeId(food.getMealTypeId());
        lastFoodDTO.setCalories(food.getCalories());
        lastFoodDTO.setFoodId(food.getFoodId());

        dailyMealDTO.setFoods(Collections.singletonList(lastFoodDTO));

        return dailyMealDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/", method = RequestMethod.GET)
    public List<DailyMealDTO> getAllFood() {
        List<DailyMeal> dailyMeals = dailyMealRepository.findAll();
        List<DailyMealDTO> dailyMealDTOs = dailyMeals.stream().map(dailyMeal -> {
            DailyMealDTO dailyMealDTO = new DailyMealDTO();
            dailyMealDTO.setId(dailyMeal.getId());
            dailyMealDTO.setDate(dailyMeal.getDate());
            dailyMealDTO.setFoods(dailyMeal.getFoods().stream().map(food -> {
                FoodDTO foodDTO = new FoodDTO();
                foodDTO.setFoodName(food.getFoodName());
                foodDTO.setCalories(food.getCalories());
                foodDTO.setMealTypeId(food.getMealTypeId());
                foodDTO.setFoodId(food.getFoodId());
                return foodDTO;
            }).collect(Collectors.toList()));
            return dailyMealDTO;
        }).collect(Collectors.toList());
        return dailyMealDTOs;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/dailyMeal/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DailyMealDTO getByIdFood(@PathVariable String id) throws DailyMealNotFoundException {
        Optional<DailyMeal> dailyMealOptional = dailyMealRepository.findById(id);
        if (dailyMealOptional.isEmpty()) {
            throw new DailyMealNotFoundException();
        }

        DailyMeal dailyMeal = dailyMealOptional.get();
        DailyMealDTO dailyMealDTO = new DailyMealDTO();
        dailyMealDTO.setId(dailyMeal.getId());
        dailyMealDTO.setDate(dailyMeal.getDate());
        dailyMealDTO.setFoods(dailyMeal.getFoods().stream().map(f -> {
            FoodDTO dto = new FoodDTO();
            dto.setFoodName(f.getFoodName());
            dto.setMealTypeId(f.getMealTypeId());
            dto.setCalories(f.getCalories());
            dto.setFoodId(f.getFoodId());
            return dto;
        }).collect(Collectors.toList()));

        return dailyMealDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/{foodId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FoodDTO getByFoodId(@PathVariable String foodId) throws FoodNotFoundException {
        List<DailyMeal> dailyMeals = dailyMealRepository.findAll();
        for (DailyMeal dailyMeal : dailyMeals) {
            for (Food food : dailyMeal.getFoods()) {
                if (food.getFoodId().equals(foodId)) {
                    FoodDTO foodDTO = new FoodDTO();
                    foodDTO.setFoodName(food.getFoodName());
                    foodDTO.setMealTypeId(food.getMealTypeId());
                    foodDTO.setCalories(food.getCalories());
                    foodDTO.setFoodId(food.getFoodId());
                    return foodDTO;
                }
            }
        }
        throw new FoodNotFoundException();
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/searchByDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DailyMealDTO> getByDateFood(@RequestParam String date) {
        Optional<DailyMeal> dailyMeals = dailyMealRepository.findByDate(date);
        List<DailyMealDTO> dailyMealDTOs = dailyMeals.stream().map(dailyMeal -> {
            DailyMealDTO dailyMealDTO = new DailyMealDTO();
            dailyMealDTO.setId(dailyMeal.getId());
            dailyMealDTO.setDate(dailyMeal.getDate());
            dailyMealDTO.setFoods(dailyMeal.getFoods().stream().map(food -> {
                FoodDTO foodDTO = new FoodDTO();
                foodDTO.setFoodName(food.getFoodName());
                foodDTO.setCalories(food.getCalories());
                foodDTO.setMealTypeId(food.getMealTypeId());
                foodDTO.setFoodId(food.getFoodId());
                return foodDTO;
            }).collect(Collectors.toList()));
            return dailyMealDTO;
        }).collect(Collectors.toList());
        return dailyMealDTOs;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/delete/{foodId}", method = RequestMethod.DELETE)
    public void deleteByFoodId(@PathVariable String foodId) throws FoodNotFoundException {
        List<DailyMeal> dailyMeals = dailyMealRepository.findAll();
        boolean foodFound = false;

        for (DailyMeal dailyMeal : dailyMeals) {
            Iterator<Food> iterator = dailyMeal.getFoods().iterator();
            while (iterator.hasNext()) {
                Food food = iterator.next();
                if (food.getFoodId().equals(foodId)) {
                    iterator.remove();
                    dailyMealRepository.save(dailyMeal);
                    foodFound = true;
                    break;
                }
            }
            if (foodFound) {
                break;
            }
        }

        if (!foodFound) {
            throw new FoodNotFoundException();
        }
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/deleteByDate", method = RequestMethod.DELETE)
    public void deleteByDateFood(@RequestParam String date) throws DailyMealNotFoundException {
        Optional<DailyMeal> dailyMealOptional = dailyMealRepository.findByDate(date);
        if (dailyMealOptional.isEmpty()) {
            throw new DailyMealNotFoundException();
        }
        dailyMealRepository.delete(dailyMealOptional.get());
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/food/deficit/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public int checkCaloriesDeficit(@PathVariable String idCaregiver, @RequestParam String date , @RequestHeader("Authorization") String token) throws FoodNotFoundException {

        String currentDate = LocalDate.now().toString();

        int calories_in = 0;
        int calories_out = 0;
        int deficit = 0;
        List<DailyMeal> dailyMeals = dailyMealRepository.findAll();

        boolean foodFound = false;

        Optional<User> user = userRepository.findById(idCaregiver);

        if (user.isEmpty()) {
            throw new FoodNotFoundException();
        }
        String idDoctor = user.get().getLinkedUserId();

        for (DailyMeal dailyMeal : dailyMeals) {
            if (dailyMeal.getDate().equals(date)) {
                Iterator<Food> iterator = dailyMeal.getFoods().iterator();
                while (iterator.hasNext()) {
                    Food food = iterator.next();
                    calories_in = calories_in + food.getCalories();
                    foodFound = true;
                }
                if (foodFound) {
                    break;
                }
            }
        }

        Message message = new Message();
        message.setDate(currentDate);
        message.setTopic("food/deficit");
        message.setDestinatarioId(idDoctor);
        message.setMittenteId(idCaregiver);

        if (foodFound == false) {
            message.setMessage("In data " + date + " non ha assunto calorie");
            mqttConfig.publishMessage("food/deficit", message);
            return 0;
        }

        if (userRepository.findById(idCaregiver).isEmpty()) {
            throw new FoodNotFoundException();
        }

        List<Activity> activityList = activityRepository.findAll();
        for (Activity activity : activityList) {
            if (activity.getDate().equals(date)) {
                calories_out = activity.getCaloriesOut();
            }
        }

        deficit = calories_out - calories_in;

        Optional<User> existingUserOptional = userRepository.findById(idCaregiver);
        int caloriesThreshold = Integer.parseInt(existingUserOptional.get().getCaloriesThreshold());

        if (deficit > caloriesThreshold) {
            message.setMessage("Il paziente ha avuto un plus di " + deficit + " Kcal in data " + date);
            mqttConfig.publishMessage("food/deficit", message);
        } else if (deficit < (caloriesThreshold * -1) ) {
            message.setMessage("Il paziente ha avuto un deficit di " + deficit + " Kcal in data " + date);
            mqttConfig.publishMessage("food/deficit", message);
        }

        return deficit;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/activity/step/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public int checkSteps(@PathVariable String idCaregiver, @RequestParam String date , @RequestHeader("Authorization") String token) throws MessageNotFoundException {

        String currentDate = LocalDate.now().toString();
        token = token.substring(7);

        int step = 0, caloriesOut = 0;

        Optional<User> caregiverOptional = userRepository.findById(idCaregiver);
        if (caregiverOptional.isEmpty()) {
            throw new MessageNotFoundException();
        }

        String idDoctor = caregiverOptional.get().getLinkedUserId();

        List<Activity> activityList = activityRepository.findAll();
        for (Activity activity : activityList) {
            if (activity.getDate().equals(date)) {
                step = activity.getStep();
                caloriesOut = activity.getCaloriesOut();
            }
        }

        Message message = new Message();
        message.setDate(currentDate);
        message.setTopic("activity/step");
        message.setDestinatarioId(idDoctor);
        message.setMittenteId(idCaregiver);


        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("step", step);
        jsonNode.put("caloriesOut", caloriesOut);

        try{
            System.out.println(objectMapper.writeValueAsString(jsonNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Errore durante la serializzazione del JSON.");
            throw new MessageNotFoundException();
        }


        String modelUrl = "http://54.208.243.32:8088/model";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonNode.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(modelUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // Analizzare il JSON
                ObjectMapper objectMapper2 = new ObjectMapper();
                JsonNode rootNode = objectMapper2.readTree(response.getBody());

                // Estrarre il valore di "message"
                message.setMessage(rootNode.get("message").asText());
                mqttConfig.publishMessage("activity/step", message);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Errore durante l'analisi del JSON.");
                throw new MessageNotFoundException();
            }
        } else {
            System.out.println("Errore nella chiamata API: " + response.getStatusCode());
            throw new MessageNotFoundException();
        }

        return step;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/heartRate/check/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HeartRateDTO2 checkHeartRateThreshold(@PathVariable String idCaregiver, @RequestParam String date, @RequestHeader("Authorization") String token) throws HeartRateNotFoundException {

        token = token.substring(7);

        Optional<User> caregiverOptional = userRepository.findById(idCaregiver);
        if (caregiverOptional.isEmpty()) {
            throw new HeartRateNotFoundException();
        }

        String idDoctor = caregiverOptional.get().getLinkedUserId();

        User caregiver = caregiverOptional.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(caregiver.getBirthdate(), formatter);
        LocalDate currentDate = LocalDate.now();

        int maxHeartRateThreshold = (220 - Period.between(birthDate, currentDate).getYears());
        int minHeartRateThreshold = 50;
        boolean found = false;

        List<HeartRate> heartRateList = heartRateRepository.findAll();
        List<Integer> maxRates = new ArrayList<>();
        List<Integer> minRates = new ArrayList<>();
        for (HeartRate heartRate : heartRateList) {
            if (heartRate.getDate().equals(date)) {
                found = true;
                for (Integer rate : heartRate.getRate()) {
                    if (rate > maxHeartRateThreshold) {
                        maxRates.add(rate);
                    } else if (rate < minHeartRateThreshold) {
                        minRates.add(rate);
                    }
                }
            }
            if (found) {
                break;
            }
        }

        if (!found) {
            throw new HeartRateNotFoundException();
        }

        System.out.println("Max Rates: " + maxRates);
        System.out.println("Min Rates: " + minRates);

        if (!maxRates.isEmpty()) {
            Message message = new Message();
            message.setDate(LocalDate.now().toString());
            message.setTopic("heartRate/check");
            message.setDestinatarioId(idDoctor);
            message.setMittenteId(idCaregiver);
            message.setMessage("Il paziente ha superato la soglia di frequenza cardiaca (" + maxHeartRateThreshold + ") in data " + date);
            mqttConfig.publishMessage("heartRate/check", message);
        }

        if (!minRates.isEmpty()) {
            Message message = new Message();
            message.setDate(LocalDate.now().toString());
            message.setTopic("heartRate/check");
            message.setDestinatarioId(idDoctor);
            message.setMittenteId(idCaregiver);
            message.setMessage("Il paziente non ha raggiunto la soglia di frequenza cardiaca (" + minHeartRateThreshold + ") in data " + date);
            mqttConfig.publishMessage("heartRate/check", message);
        }

        HeartRateDTO2 heartRateDTO2 = new HeartRateDTO2();
        heartRateDTO2.setIdCaregiver(idCaregiver);
        heartRateDTO2.setDate(date);
        heartRateDTO2.setMaxRates(maxRates);
        heartRateDTO2.setMinRates(minRates);

        return heartRateDTO2;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/sleep/check/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public int checkSleep(@PathVariable String idCaregiver, @RequestParam String date , @RequestHeader("Authorization") String token) throws FoodNotFoundException {

        String currentDate = LocalDate.now().toString();
        token = token.substring(7);

        String durata;

        Optional<User> caregiverOptional = userRepository.findById(idCaregiver);
        if (caregiverOptional.isEmpty()) {
            throw new FoodNotFoundException();
        }

        String idDoctor = caregiverOptional.get().getLinkedUserId();

        int hours = 0;
        int minutes = 0;

        List<Sleep> sleepList = sleepRepository.findAll();
        for (Sleep sleep : sleepList) {
            if (sleep.getDate().equals(date)) {
                durata = sleep.getDuration();
                hours = Integer.parseInt(durata.split("h")[0].trim());
                minutes = Integer.parseInt(durata.split("h")[1].replace("m", "").trim());

            }
        }

        Message message = new Message();
        message.setDate(currentDate);
        message.setTopic("sleep/check");
        message.setDestinatarioId(idDoctor);
        message.setMittenteId(idCaregiver);


        if (hours < 6) {
            message.setMessage("Il paziente in data " + date + " ha dormito meno delle ore consigliate: " + hours + " ore e " + minutes + " minuti");
            mqttConfig.publishMessage("sleep/check", message);
        } else if (hours > 9) {
            message.setMessage("Il paziente in data " + date + " ha dormito più delle ore consigliate: " + hours + " ore e " + minutes + " minuti");
            mqttConfig.publishMessage("sleep/check", message);
        }


        return hours;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/spo2/check/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public double checkSpo2(@PathVariable String idCaregiver, @RequestParam String date , @RequestHeader("Authorization") String token) throws FoodNotFoundException {

        String currentDate = LocalDate.now().toString();
        token = token.substring(7);

        Optional<User> caregiverOptional = userRepository.findById(idCaregiver);
        if (caregiverOptional.isEmpty()) {
            throw new FoodNotFoundException();
        }

        String idDoctor = caregiverOptional.get().getLinkedUserId();

        double min = 0;

        List<Spo2> spo2List = spo2Repository.findAll();
        for (Spo2 spo2 : spo2List) {
            if (spo2.getDate().equals(date)) {
                min = spo2.getMin();
            }
        }

        Message message = new Message();
        message.setDate(currentDate);
        message.setTopic("spo2/check");
        message.setDestinatarioId(idDoctor);
        message.setMittenteId(idCaregiver);


        if ( (min < 92) && (min > 90) ) {
            message.setMessage("Il paziente ha un basso valore di ossigeno nel sangue: " + min + "% in data " + date);
            mqttConfig.publishMessage("spo2/check", message);
        } else if (min <= 90) {
            message.setMessage("Il paziente ha un valore estremamente basso di ossigeno nel sangue (" + min + "%) in data " + date +". Necessario controllo medico immediato");
            mqttConfig.publishMessage("spo2/check", message);
        }

        return min;
    }



    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/create",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MessageDTO postMessage(@RequestBody MessageDTO messageDTO) throws UserNotFoundException {

        Message messageEntity = new Message();

        String currentDate = LocalDate.now().toString();

        messageEntity.setMessage(messageDTO.getMessage());
        messageEntity.setTopic(messageDTO.getTopic());
        messageEntity.setDate(currentDate);

        if (userRepository.findById(messageDTO.getDestinatarioId()).isEmpty()) {
            throw new UserNotFoundException();
        }
        if (userRepository.findById(messageDTO.getMittenteId()).isEmpty()) {
            throw new UserNotFoundException();
        }

        String destinatarioId = messageDTO.getDestinatarioId();
        String mittenteId = messageDTO.getMittenteId();

        messageEntity.setMittenteId(mittenteId);
        messageEntity.setDestinatarioId(destinatarioId);

        messageEntity = messageRepository.save(messageEntity);

        MessageDTO messageDTO1 = new MessageDTO();

        messageDTO1.setMessage(messageEntity.getMessage());
        messageDTO1.setDate(messageEntity.getDate());
        messageDTO1.setId(messageEntity.getId());
        messageDTO1.setTopic(messageEntity.getTopic());
        messageDTO1.setMittenteId(messageEntity.getMittenteId());
        messageDTO1.setDestinatarioId(messageEntity.getDestinatarioId());

        return messageDTO1;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/weight/check/{idCaregiver}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public double checkWeight(@PathVariable String idCaregiver, @RequestParam String date , @RequestHeader("Authorization") String token) throws FoodNotFoundException {

        String currentDate = LocalDate.now().toString();

        if (userRepository.findById(idCaregiver).isEmpty()) {
            throw new FoodNotFoundException();
        }

        Optional<User> user = userRepository.findById(idCaregiver);
        String idDoctor = user.get().getLinkedUserId();

        double doubleWeight = 0;
        double doubleHeight = 0;

        List<Weight> weightList = weightRepository.findAll();
        for (Weight weight : weightList) {
            if (weight.getDate().equals(date)) {
                doubleWeight = weight.getWeight();
                doubleHeight = weight.getHeight();
            }
        }

        Message message = new Message();
        message.setDate(currentDate);
        message.setTopic("weight/check");
        message.setDestinatarioId(idDoctor);
        message.setMittenteId(idCaregiver);

        double bodyMassIndex = 0;

        bodyMassIndex = (1.3 * doubleWeight) / ((doubleHeight/100) * 2.5);

        if (bodyMassIndex < 16.5) {
            message.setMessage("Il paziente è in condizione di GRAVE MAGREZZA in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if ( (bodyMassIndex >= 16.5) && (bodyMassIndex <= 18.5) ) {
            message.setMessage("Il paziente è in condizione di SOTTOPESO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if ((bodyMassIndex > 18.5) && (bodyMassIndex <= 24.99)) {
            message.setMessage("Il paziente è in condizione di NORMOPESO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if ((bodyMassIndex >= 25) && (bodyMassIndex <= 29.99)) {
            message.setMessage("Il paziente è in condizione di SOVRAPPESO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if ((bodyMassIndex >= 30) && (bodyMassIndex <= 34.99)) {
            message.setMessage("Il paziente è in condizione di OBESITA' di I GRADO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if ((bodyMassIndex >= 35) && (bodyMassIndex <= 39.99)) {
            message.setMessage("Il paziente è in condizione di OBESITA' di II GRADO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        } else if (bodyMassIndex >= 40) {
            message.setMessage("Il paziente è in condizione di OBESITA' di III GRADO in data " + date);
            mqttConfig.publishMessage("weight/check", message);
        }


        return bodyMassIndex;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/",method=RequestMethod.GET)
    public MessageListDTO getAllMessage() {
        MessageListDTO messageListDTO = new MessageListDTO();
        ArrayList<MessageDTO> list = new ArrayList<>();
        messageListDTO.setList(list);

        List<Message> messages = messageRepository.findAll();

        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setMessage(message.getMessage());
            messageDTO.setDate(message.getDate());
            messageDTO.setTopic(message.getTopic());
            messageDTO.setMittenteId(message.getMittenteId());
            messageDTO.setDestinatarioId(message.getDestinatarioId());

            list.add(messageDTO);
        }

        return messageListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
    public MessageDTO getByIdMessage(@PathVariable String id) throws MessageNotFoundException {

        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new MessageNotFoundException();
        }
        Message message = messageOptional.get();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setTopic(message.getTopic());
        messageDTO.setDate(message.getDate());
        messageDTO.setMessage(message.getMessage());
        messageDTO.setDestinatarioId(message.getDestinatarioId());
        messageDTO.setMittenteId(message.getMittenteId());

        return messageDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/destinatario/{id}", method = RequestMethod.GET)
    public MessageListDTO getByDestinatarioId(@PathVariable String id)  {

        MessageListDTO messageListDTO = new MessageListDTO();
        ArrayList<MessageDTO> list = new ArrayList<>();
        messageListDTO.setList(list);

        List<Message> messages = messageRepository.findByDestinatarioId(id);

        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setMessage(message.getMessage());
            messageDTO.setDate(message.getDate());
            messageDTO.setTopic(message.getTopic());
            messageDTO.setMittenteId(message.getMittenteId());
            messageDTO.setDestinatarioId(message.getDestinatarioId());

            list.add(messageDTO);
        }

        return messageListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/mittente/{id}", method = RequestMethod.GET)
    public MessageListDTO getByMittenteId(@PathVariable String id)  {

        MessageListDTO messageListDTO = new MessageListDTO();
        ArrayList<MessageDTO> list = new ArrayList<>();
        messageListDTO.setList(list);

        List<Message> messages = messageRepository.findByMittenteId(id);

        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setMessage(message.getMessage());
            messageDTO.setDate(message.getDate());
            messageDTO.setTopic(message.getTopic());
            messageDTO.setMittenteId(message.getMittenteId());
            messageDTO.setDestinatarioId(message.getDestinatarioId());

            list.add(messageDTO);
        }

        return messageListDTO;
    }



    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/searchByDate", method = RequestMethod.GET)
    public MessageListDTO searchMessageByDate(@RequestParam String date) {
        MessageListDTO messageListDTO = new MessageListDTO();
        ArrayList<MessageDTO> list = new ArrayList<>();
        messageListDTO.setList(list);

        List<Message> messages =  messageRepository.findByDate(date);

        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setDate(message.getDate());
            messageDTO.setMessage(message.getMessage());
            messageDTO.setTopic(message.getTopic());
            messageDTO.setMittenteId(message.getMittenteId());
            messageDTO.setDestinatarioId(message.getDestinatarioId());

            list.add(messageDTO);
        }

        return messageListDTO;
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/searchByTopic", method = RequestMethod.GET)
    public MessageListDTO searchMessageByTopic(@RequestParam String topic) {
        MessageListDTO messageListDTO = new MessageListDTO();
        ArrayList<MessageDTO> list = new ArrayList<>();
        messageListDTO.setList(list);

        List<Message> messages =  messageRepository.findByTopic(topic);

        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setDate(message.getDate());
            messageDTO.setMessage(message.getMessage());
            messageDTO.setTopic(message.getTopic());
            messageDTO.setMittenteId(message.getMittenteId());
            messageDTO.setDestinatarioId(message.getDestinatarioId());

            list.add(messageDTO);
        }

        return messageListDTO;
    }


    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/delete/{idMessage}",method=RequestMethod.DELETE)
    public void deleteByIdMessage(@PathVariable String idMessage) throws UserNotFoundException {
        if (messageRepository.findById(idMessage).isEmpty()) {
            throw new UserNotFoundException();
        }
        messageRepository.deleteById(idMessage);
    }

    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    @RequestMapping(value = "/message/deleteByDate", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMessagesFromDate(@RequestParam String date) {
        try {
            List<Message> messages = messageRepository.findAll().stream()
                    .filter(message -> LocalDate.parse(message.getDate()).isAfter(LocalDate.parse(date).minusDays(1)))
                    .collect(Collectors.toList());

            if (messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nessun messaggio trovato da eliminare.");
            }

            messageRepository.deleteAll(messages);

            return ResponseEntity.ok("Messaggi eliminati con successo.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'eliminazione dei messaggi: " + e.getMessage());
        }
    }


    @RequestMapping(value = "/update/caloriesThreshold/{idCaregiver}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    public ResponseEntity<UserDTO> updateCaloriesThreshold(@PathVariable String idCaregiver, @RequestBody UserDTO userDTO) {

        Optional<User> existingUserOptional = userRepository.findById(idCaregiver);

        if (!existingUserOptional.isPresent()) {

            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOptional.get();

        existingUser.setCaloriesThreshold(userDTO.getCaloriesThreshold());

        User updatedUser = userRepository.save(existingUser);

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(updatedUser.getId());
        updatedUserDTO.setNome(updatedUser.getNome());
        updatedUserDTO.setCognome(updatedUser.getCognome());
        updatedUserDTO.setEmail(updatedUser.getEmail());
        updatedUserDTO.setRole(updatedUser.getRole());
        updatedUserDTO.setCity(updatedUser.getCity());
        updatedUserDTO.setTelephoneNumber(updatedUser.getTelephoneNumber());
        updatedUserDTO.setBirthdate(updatedUser.getBirthdate());
        updatedUserDTO.setSex(updatedUser.getSex());
        updatedUserDTO.setAddress(updatedUser.getAddress());
        updatedUserDTO.setCaloriesThreshold(updatedUser.getCaloriesThreshold());

        return ResponseEntity.ok(updatedUserDTO);
    }


    @RequestMapping(value = "/update/stepsThreshold/{idCaregiver}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_Doctor') or hasRole('ROLE_Caregiver')")
    public ResponseEntity<UserDTO> updateStepsThreshold(@PathVariable String idCaregiver, @RequestBody UserDTO userDTO) {

        Optional<User> existingUserOptional = userRepository.findById(idCaregiver);

        if (!existingUserOptional.isPresent()) {

            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOptional.get();

        User updatedUser = userRepository.save(existingUser);

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(updatedUser.getId());
        updatedUserDTO.setNome(updatedUser.getNome());
        updatedUserDTO.setCognome(updatedUser.getCognome());
        updatedUserDTO.setEmail(updatedUser.getEmail());
        updatedUserDTO.setRole(updatedUser.getRole());
        updatedUserDTO.setCity(updatedUser.getCity());
        updatedUserDTO.setTelephoneNumber(updatedUser.getTelephoneNumber());
        updatedUserDTO.setBirthdate(updatedUser.getBirthdate());
        updatedUserDTO.setSex(updatedUser.getSex());
        updatedUserDTO.setAddress(updatedUser.getAddress());
        updatedUserDTO.setCaloriesThreshold(updatedUser.getCaloriesThreshold());

        return ResponseEntity.ok(updatedUserDTO);
    }

}