package com.dnieln7.java.generic.request;


import com.dnieln7.java.generic.request.exception.BuilderException;
import com.dnieln7.java.generic.request.utils.RequestMethod;
import com.dnieln7.java.generic.request.utils.RequestProperties;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class for {@link GenericRequestSession}
 * <br/> <br/> This test runs with the Java-Spring-API project available on <a href="https://github.com/dnieln7/Java-Spring-API">Github</a>
 * <br/> <br/> This test is disabled, delete the {@link Disabled} annotation to enable.
 *
 * @author dnieln7
 */
@Disabled
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("When running GenericRequestSession")
public class GenericRequestSessionTest {

    //<editor-fold desc="Data classes">
    private static class Product {
        private int id;
        private String name;
        private String description;
        private int quantity;
        private double price;
        private Seller seller;

        public Product() {
        }

        public Product(int id, String name, String description, int quantity, double price, Seller seller) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.quantity = quantity;
            this.price = price;
            this.seller = seller;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public Seller getSeller() {
            return seller;
        }

        public void setSeller(Seller seller) {
            this.seller = seller;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", seller=" + seller +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Product product = (Product) o;
            return id == product.id &&
                    quantity == product.quantity &&
                    Double.compare(product.price, price) == 0 &&
                    name.equals(product.name) &&
                    description.equals(product.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, quantity, price);
        }
    }

    private static class Seller {
        private int id;
        private String name;
        private String address;
        private String phone;

        public Seller() {
        }

        public Seller(int id, String name, String address, String phone) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.phone = phone;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "Seller{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", phone='" + phone + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Seller seller = (Seller) o;
            return id == seller.id &&
                    name.equals(seller.name) &&
                    address.equals(seller.address) &&
                    phone.equals(seller.phone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, address, phone);
        }
    }
    //</editor-fold>

    private TestInfo testInfo;
    private TestReporter testReporter;
    private GenericRequestSession.Builder<Seller> sellerSessionBuilder;
    private GenericRequestSession.Builder<Seller[]> sellersSessionBuilder;

    @BeforeEach
    public void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        this.sellerSessionBuilder = new GenericRequestSession.Builder<Seller>()
                .ofType(Seller.class)
                .withRequestProperties(RequestProperties.JSON_PROPERTIES);

        this.sellersSessionBuilder = new GenericRequestSession.Builder<Seller[]>()
                .to("http://localhost:8080/sellers")
                .ofType(Seller[].class)
                .withMethod(RequestMethod.GET)
                .withResponseCode(200)
                .withOutput(false)
                .withRequestProperties(RequestProperties.JSON_PROPERTIES);

        this.testReporter.publishEntry("Start");
    }

    @Test
    @Order(1)
    @Tag("POST")
    @DisplayName("When sending HTTP POST request")
    void testPost() throws BuilderException {
        Logger.getLogger(GenericRequestSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        Seller seller1 = new Seller(
                1,
                "Seller 1",
                "Address 1",
                "Phone 1"
        );

        Seller seller2 = new Seller(
                1,
                "Seller 2",
                "Address 2",
                "Phone 2"
        );

        GenericRequestSession<Seller> session1 = this.sellerSessionBuilder
                .to("http://localhost:8080/sellers")
                .withMethod(RequestMethod.POST)
                .withOutput(true)
                .withResponseCode(200)
                .build();


        Seller result1 = Assertions.assertDoesNotThrow(
                () -> session1.sendRequestWithArgs(seller1),
                () -> "Should not throw any exceptions"
        );

        GenericRequestSession<Seller> session2 = this.sellerSessionBuilder
                .to("http://localhost:8080/sellers")
                .withMethod(RequestMethod.POST)
                .withOutput(true)
                .withResponseCode(200)
                .build();

        Seller result2 = Assertions.assertDoesNotThrow(
                () -> session2.sendRequestWithArgs(seller2),
                () -> "Should not throw any exceptions"
        );

        Assertions.assertNotNull(result1, () -> "The result should not be null");
        Assertions.assertNotNull(result2, () -> "The result should not be null");
    }

    @Test
    @Order(2)
    @Tag("GET")
    @DisplayName("When sending HTTP GET request")
    void testGet() throws BuilderException {
        Logger.getLogger(GenericRequestSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        GenericRequestSession<Seller[]> session = sellersSessionBuilder.build();

        List<Seller> sellers = Assertions.assertDoesNotThrow(
                () -> Arrays.asList(session.sendRequest().clone()),
                () -> "Should not throw any exceptions"
        );

        Assertions.assertFalse(sellers.isEmpty(), () -> "should not return empty list");
    }

    @Test
    @Order(3)
    @Tag("GET")
    @DisplayName("When sending HTTP GET request with a valid id")
    void testGetById() throws BuilderException {
        Logger.getLogger(GenericRequestSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        // Get sellers
        GenericRequestSession<Seller[]> s = sellersSessionBuilder.build();

        List<Seller> sellers = Assertions.assertDoesNotThrow(
                () -> Arrays.asList(s.sendRequest().clone()),
                () -> "Should not throw any exceptions"
        );

        assert !sellers.isEmpty();

        // Get the first seller on the list
        GenericRequestSession<Seller> session = this.sellerSessionBuilder
                .to("http://localhost:8080/sellers/" + sellers.get(0).getId())
                .withMethod(RequestMethod.GET)
                .withResponseCode(200)
                .build();

        Seller seller = Assertions.assertDoesNotThrow(
                session::sendRequest,
                () -> "Should not throw any exceptions"
        );

        Assertions.assertNotNull(
                seller,
                () -> "The returned object should not be null"
        );
    }

    @Test
    @Order(4)
    @Tag("PUT")
    @DisplayName("When sending HTTP PUT request with an valid id and a valid object")
    void testPut() throws BuilderException {
        Logger.getLogger(GenericRequestSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        // Get sellers
        GenericRequestSession<Seller[]> s = sellersSessionBuilder.build();

        List<Seller> sellers = Assertions.assertDoesNotThrow(
                () -> Arrays.asList(s.sendRequest().clone()),
                () -> "Should not throw any exceptions"
        );

        assert !sellers.isEmpty();

        // Update the first seller on the list
        Seller sellerToSend = new Seller(
                sellers.get(0).getId(),
                "Seller Updated",
                "Address Updated",
                "Phone Updated"
        );

        GenericRequestSession<Seller> session = sellerSessionBuilder
                .to("http://localhost:8080/sellers/" + sellerToSend.getId())
                .withMethod(RequestMethod.PUT)
                .withResponseCode(200)
                .withOutput(true)
                .build();

        Seller sellerToGet = Assertions.assertDoesNotThrow(
                () -> session.sendRequestWithArgs(sellerToSend),
                () -> "Should not throw any exceptions"
        );

        Assertions.assertEquals(
                sellerToSend,
                sellerToGet,
                () -> "The returned object should be equal to the one sent"
        );
    }

    @Test
    @Order(5)
    @Tag("DELETE")
    @DisplayName("When sending HTTP DELETE request with an valid id")
    void testDelete() throws BuilderException {
        Logger.getLogger(GenericRequestSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );


        // Get sellers
        GenericRequestSession<Seller[]> s = sellersSessionBuilder.build();

        List<Seller> sellers = Assertions.assertDoesNotThrow(
                () -> Arrays.asList(s.sendRequest().clone()),
                () -> "Should not throw any exceptions"
        );

        assert !sellers.isEmpty();

        // Delete the first seller on the list
        GenericRequestSession<Seller> session = sellerSessionBuilder
                .to("http://localhost:8080/sellers/" + sellers.get(0).getId())
                .withMethod(RequestMethod.DELETE)
                .withResponseCode(200)
                .withOutput(false)
                .build();

        Seller deletedSeller = Assertions.assertDoesNotThrow(
                session::sendRequest,
                () -> "Should not throw any exceptions"
        );
    }

    @AfterEach
    public void tearDown() {
        this.testReporter.publishEntry("Finished");
    }
}
