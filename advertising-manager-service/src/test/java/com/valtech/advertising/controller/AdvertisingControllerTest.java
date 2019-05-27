package com.valtech.advertising.controller;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.dto.Gender;
import com.valtech.advertising.dto.Segmentation;
import com.valtech.advertising.service.domain.AdvertisingEntity;
import com.valtech.advertising.service.domain.SegmentationEntity;
import com.valtech.advertising.service.mapper.AdvertisingMapper;
import com.valtech.advertising.service.repository.AdvertisingRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdvertisingControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected AdvertisingRepository repository;

    @Value("${baseUrl}")
    private String BASE_URL;

    @Value("${message.listSize}")
    private String LIST_SIZE_MESSAGE;

    @Value("${message.notThrown}")
    private String NOT_THROWN_MESSAGE;

    @Value("${message.responseStatus}")
    private String RESPONSE_STATUS_MESSAGE;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    private ResponseEntity<Advertising> postAdvertising(CreateAdvertisingRequest createAdvertisingRequest) {
        return restTemplate.postForEntity(String.format(BASE_URL, port),
                createAdvertisingRequest, Advertising.class);
    }

    private ResponseEntity<List<Advertising>> getAdvertisingList(String country, Integer age, Gender gender) {
        String url = String.format(BASE_URL, port);
        if (country != null || age != null || gender != null) {
            url += "?";
            if (country != null) {
                url = url.concat("country=").concat(country).concat("&");
            }
            if (age != null) {
                url = url.concat("age=").concat(age.toString()).concat("&");
            }
            if (gender != null) {
                url = url.concat("gender=").concat(gender.name()).concat("&");
            }
            url = url.substring(0, url.length()-1);
        }
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Advertising>>(){}, country, age, gender);
    }

    @Test
    public void getPort() {
        Assert.assertThat("Should get a random port greater than zero!", port, greaterThan(0));
    }

    @Test
    public void createAdvertising() {
        CreateAdvertisingRequest advertisingRequest = buildDummyAdvertisingRequest();
        ResponseEntity<Advertising> response = postAdvertising(advertisingRequest);

        Assert.assertEquals(RESPONSE_STATUS_MESSAGE, HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createAdvertisingNullValuesRequest() {
        CreateAdvertisingRequest advertisingRequest = CreateAdvertisingRequest
                .builder()
                .build();
        try {
            postAdvertising(advertisingRequest);
            Assert.fail(NOT_THROWN_MESSAGE);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(String.format("Custom constrain validation: %s", RESPONSE_STATUS_MESSAGE),
                    HttpStatus.BAD_REQUEST, e.getStatusCode());
            Assert.assertTrue("printCost is required and was not present",
                    e.getResponseBodyAsString().contains("print cost is required"));
            Assert.assertTrue("maxCost is required and was not present",
                    e.getResponseBodyAsString().contains("max cost is required"));
            Assert.assertTrue("endDate is required and was not present",
                    e.getResponseBodyAsString().contains("endDate is required"));
            Assert.assertTrue("title is required and was not present",
                    e.getResponseBodyAsString().contains("title is required"));
            Assert.assertTrue("description is required and was not present",
                    e.getResponseBodyAsString().contains("description is required"));
        }
    }

    @Test
    public void createAdvertisingInvalidValuesRequest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date oldDate = cal.getTime();
        Segmentation invalidSegmentation = Segmentation.builder().age(-5).build();
        CreateAdvertisingRequest advertisingRequest = CreateAdvertisingRequest
                .builder()
                .printCost(-2)
                .maxCost(-3)
                .endDate(oldDate)
                .title("some title")
                .description("some desc")
                .segmentation(invalidSegmentation)
                .build();
        try {
            postAdvertising(advertisingRequest);
            Assert.fail(NOT_THROWN_MESSAGE);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(String.format("Custom constrain validation: %s", RESPONSE_STATUS_MESSAGE),
                    HttpStatus.BAD_REQUEST, e.getStatusCode());
            Assert.assertTrue("Print cost was too low",
                    e.getResponseBodyAsString().contains("print cost should be greater than 0"));
            Assert.assertTrue("Max cost was too low",
                    e.getResponseBodyAsString().contains("max cost should be greater than 0"));
            Assert.assertTrue("end date cost was too soon",
                    e.getResponseBodyAsString().contains("endDate should be later than today"));
            Assert.assertTrue("age was invalid",
                    e.getResponseBodyAsString().contains("age should be greater than 0"));
        }
    }

    @Test
    public void getAdvertisingListFilteredByGender() {
        loadRepository();
        ResponseEntity<List<Advertising>> response =  getAdvertisingList(null, null, Gender.Male);
        Assert.assertEquals(RESPONSE_STATUS_MESSAGE, HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LIST_SIZE_MESSAGE,
                3, response.getBody().size());
        for(Advertising ad : response.getBody()) {
            Assert.assertEquals("Advertising gender doesn't match",
                    Gender.Male, ad.getSegmentation().getGender());
        }
    }

    @Test
    public void getAdvertisingListFilteredByAge() {
        loadRepository();
        ResponseEntity<List<Advertising>> response =  getAdvertisingList(null, 26, null);

        Assert.assertEquals(RESPONSE_STATUS_MESSAGE, HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LIST_SIZE_MESSAGE,
                2, response.getBody().size());
        for(Advertising ad : response.getBody()) {
            Assert.assertEquals("Advertising age doesn't match",
                    new Integer(26), ad.getSegmentation().getAge());
        }
    }

    @Test
    public void getAdvertisingListFilteredByCountry() {
        loadRepository();
        ResponseEntity<List<Advertising>> response =  getAdvertisingList("USA", null, null);

        Assert.assertEquals(RESPONSE_STATUS_MESSAGE, HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LIST_SIZE_MESSAGE,
                3, response.getBody().size());
        for(Advertising ad : response.getBody()) {
            Assert.assertEquals("Advertising country doesn't match",
                    "USA", ad.getSegmentation().getCountry());
        }
    }

    @Test
    public void getAdvertisingNotFound() {
        try {
            getAdvertisingList(null, 50, null);
            Assert.fail(NOT_THROWN_MESSAGE);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(RESPONSE_STATUS_MESSAGE, HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    private CreateAdvertisingRequest buildDummyAdvertisingRequest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        Segmentation segmentation = Segmentation
                .builder()
                .age(26)
                .country("Argentine")
                .gender(Gender.Male)
                .build();
        return CreateAdvertisingRequest
                .builder()
                .printCost(5)
                .maxCost(100)
                .endDate(endDate)
                .title("Advertise")
                .description("Test advertise")
                .segmentation(segmentation)
                .build();
    }

    private void loadRepository() {
        AdvertisingEntity entity = AdvertisingMapper.requestToEntity(buildDummyAdvertisingRequest());
        repository.save(entity);

        SegmentationEntity segmentation = SegmentationEntity
                .builder()
                .age(26)
                .country("USA")
                .gender(Gender.Male)
                .build();
        saveEntityForNewSegmentation(entity, segmentation);

        segmentation = SegmentationEntity.builder()
                .age(29)
                .country("USA")
                .gender(Gender.Female)
                .build();
        saveEntityForNewSegmentation(entity, segmentation);

        segmentation = SegmentationEntity.builder()
                .age(29)
                .country("Argentine")
                .gender(Gender.Female)
                .build();
        saveEntityForNewSegmentation(entity, segmentation);

        segmentation = SegmentationEntity.builder()
                .age(30)
                .country("USA")
                .gender(Gender.Male)
                .build();
        saveEntityForNewSegmentation(entity, segmentation);
    }

    private void saveEntityForNewSegmentation(AdvertisingEntity entity, SegmentationEntity newSegmentation) {
        entity.setAdvertisingId(null);
        entity.setSegmentation(newSegmentation);
        repository.save(entity);
    }

}
