package com.dnieln7.java.generic.request;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class for {@link HttpSession}
 * <br/> <br/> To run this test you need an API and a DataBase with an employee Endpoint and a DataSource.
 * <br/> <br/> Delete the {@link Disabled} annotation if want to perform a test.
 *
 * @author dnieln7
 */
@Disabled
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("When running HttpSession")
public class HttpSessionTest {

    //<editor-fold desc="Class Employee">
    private static class Employee {
        private int id;
        private String name;
        private String address;
        private String phone;

        public Employee() {
        }

        public Employee(int id, String name, String address, String phone) {
            super();
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
            return "Employee{"
                    + "id=" + id
                    + ", name='" + name + '\''
                    + ", address='" + address + '\''
                    + ", phone='" + phone + '\''
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return id == employee.id &&
                    name.equals(employee.name) &&
                    address.equals(employee.address) &&
                    phone.equals(employee.phone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, address, phone);
        }
    }
    //</editor-fold>

    private TestInfo testInfo;
    private TestReporter testReporter;
    private HttpSession<Employee> session;

    @BeforeEach
    public void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        this.session = new HttpSession<>("http://localhost:8081/employees");

        this.testReporter.publishEntry("Start");
    }

    @Test
    @Order(1)
    @Tag("POST")
    @DisplayName("When sending HTTP POST request")
    public void testPost() {
        Logger.getLogger(HttpSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        Employee employee = new Employee(
                0,
                "Test Name",
                "Test Address",
                "Test Phone"
        );

        Employee result = Assertions.assertDoesNotThrow(() -> session.post(employee, Employee.class), () -> "Should not throw any exceptions");

        Assertions.assertNotNull(result, () -> "The result should not be null");
    }

    @Test
    @Order(2)
    @Tag("GET")
    @DisplayName("When sending HTTP GET request")
    public void testGet() {
        Logger.getLogger(HttpSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        List<Employee> employees = Assertions.assertDoesNotThrow(() -> session.get(Employee[].class), () -> "Should not throw any exceptions");
        Assertions.assertFalse(employees.isEmpty(), () -> "should not return empty list");
    }

    @Test
    @Order(3)
    @Tag("GET")
    @DisplayName("When sending HTTP GET request with an valid id")
    public void testGetById() {
        Logger.getLogger(HttpSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        List<Employee> employees = session.get(Employee[].class);

        assert !employees.isEmpty();

        Assertions.assertEquals(
                employees.get(0),
                session.getById(String.valueOf(employees.get(0).getId()), Employee.class),
                () -> "The returned object should be equal to the one whose id was send"
        );
    }

    @Test
    @Order(4)
    @Tag("PUT")
    @DisplayName("When sending HTTP PUT request with an valid id and a valid object")
    public void testPut() {
        Logger.getLogger(HttpSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        List<Employee> employees = session.get(Employee[].class);

        assert !employees.isEmpty();

        Employee employee = employees.get(0);

        employee.setName(new Date().toString());

        Assertions.assertEquals(
                employee,
                session.put(String.valueOf(employee.getId()), employee, Employee.class),
                () -> "The returned object should be equal to the one sent"
        );
    }

    @Test
    @Order(5)
    @Tag("DELETE")
    @DisplayName("When sending HTTP DELETE request with an valid id")
    public void testDelete() {
        Logger.getLogger(HttpSessionTest.class.getName()).log(
                Level.INFO,
                "Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags()
        );

        List<Employee> employees = session.get(Employee[].class);

        assert !employees.isEmpty();

        Assertions.assertEquals(
                1,
                session.delete(String.valueOf(employees.get(0).getId())).getSuccess(),
                () -> "The property success should be equal to 1"
        );
    }

    @AfterEach
    public void tearDown() {
        this.testReporter.publishEntry("Finished");
    }
}
