package com.valtech.advertising.controller;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.service.repository.AdvertisingRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;

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

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    private ResponseEntity<Advertising> postAdvertising(CreateAdvertisingRequest createAdvertisingRequest) {
        return restTemplate.postForEntity(String.format("http://localhost:%s/v1/advertisings", port),
                createAdvertisingRequest, Advertising.class);
    }

    private ResponseEntity<Advertising> getAdvertising(String advertisingId) {
        return restTemplate.getForEntity(String.format("http://localhost:%s/v1/advertisings/{advertisingId}", port),
                Advertising.class, advertisingId);
    }

    @Test
    public void getPort() {
        Assert.assertThat("Should get a random port greater than zero!", port, greaterThan(0));
    }

    @Test
    public void createAdvertising() {
        CreateAdvertisingRequest advertisingRequest = buildDummyAdvertisingRequest();
        ResponseEntity<Advertising> response = postAdvertising(advertisingRequest);

        Assert.assertEquals("Response status doesn't match", HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createAdvertisingNullValuesRequest() {
        CreateAdvertisingRequest advertisingRequest = CreateAdvertisingRequest
                .builder()
                .build();
        try {
            postAdvertising(advertisingRequest);
            Assert.fail("Did not throw");
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(String.format("Custom constrain validation: %s", "Response status don't match"),
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
        CreateAdvertisingRequest advertisingRequest = CreateAdvertisingRequest
                .builder()
                .printCost(-2)
                .maxCost(-3)
                .endDate(oldDate)
                .title("")
                .description("")
                .build();
        try {
            postAdvertising(advertisingRequest);
            Assert.fail("Did not throw");
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(String.format("Custom constrain validation: %s", "Response status don't match"),
                    HttpStatus.BAD_REQUEST, e.getStatusCode());
            Assert.assertTrue("Print cost was too low",
                    e.getResponseBodyAsString().contains("print cost should be greater than 0"));
            Assert.assertTrue("Max cost was too low",
                    e.getResponseBodyAsString().contains("max cost should be greater than 0"));
            Assert.assertTrue("end date cost was too soon",
                    e.getResponseBodyAsString().contains("endDate should be later than today"));
            Assert.assertTrue("title was blank",
                    e.getResponseBodyAsString().contains("title is required"));
            Assert.assertTrue("description was blank",
                    e.getResponseBodyAsString().contains("description is required"));
        }
    }

    @Test
    public void getAdvertising() {
        CreateAdvertisingRequest advertisingRequest = buildDummyAdvertisingRequest();
        Advertising advertising = postAdvertising(advertisingRequest).getBody();

        ResponseEntity<Advertising> getResponse =  getAdvertising(advertising.getAdvertisingId());
        Assert.assertEquals("Response status doesn't match", HttpStatus.OK, getResponse.getStatusCode());
        Assert.assertEquals("Advertising print cost doesn't match",
                advertisingRequest.getPrintCost(), getResponse.getBody().getPrintCost());
    }

    @Test
    public void getAdvertisingNotFound() {
        try {
            getAdvertising("invalid_id");
            Assert.fail("Did not throw");
        } catch (HttpClientErrorException e) {
            Assert.assertEquals("Response status doesn't match", HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    private CreateAdvertisingRequest buildDummyAdvertisingRequest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();
        return CreateAdvertisingRequest
                .builder()
                .printCost(5)
                .maxCost(100)
                .endDate(endDate)
                .title("Advertise")
                .description("Test advertise")
                .build();
    }

}
