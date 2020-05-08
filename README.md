# Java Generic Request

_Java Library to make Http requests like GET, GET (ById), POST, PUT, DELETE, and cast the response into a desired type._

## Running the tests

To run the test you'll need:

1. An API with an employee endpoint _Check employee entity deatils below_.
2. A Data Source.
3. Delete the Disabled annotation at the HttpSessionTest class.

### The Employee entity

#### Attributes

* id - Integer
* name - String
* address - String
* phone - String

### Test's description

There are 5 test, one for every Http method, they'll execute in the next order.

1. testPost
2. testGet
3. testGetById
4. testPut
5. testDelete
