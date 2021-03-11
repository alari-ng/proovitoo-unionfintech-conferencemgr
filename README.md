# Backend REST API for conference management

This project is test task to implement restful web application backend.

Used technological parts:
- programming language: Java 11
- data store: H2
- application framework: Spring
- build tool: Gradle
- fast unit-tests: JUnit + Mockito
- REST API integration tests: Rest Assured + Spring Boot Test


## Building

```
git clone https://github.com/alari-ng/proovitoo-unionfintech-conferencemgr.git
cd proovitoo-unionfintech-conferencemgr/
./gradlew build
```

Note, that this gradle build also performs unit-tests and integration tests.
If you need to run separately REST integration tests only, then run:

```
./gradlew test --rerun-tasks --info --tests "*ConferenceControllerIT"
./gradlew test --rerun-tasks --info --tests "*ParticipantControllerIT"
```

## Run the REST API

```
./gradlew bootRun
```


## REST API endpoints

### Create new conference

Service uri: /conferencemgr/conference

Method: POST

Input payload content type: application-json
  
Input payload payload JSON structure:
  
  - "name" string - conference name (unique)
  
  - "seats" number - max seats in conference room

Result:
- HTTP 200 - success
- HTTP 400 - conference already exists

Example call:

```
curl -X POST -d "{\"name\":\"konverents123\",\"seats\":4}" -H "Content-Type: application/json" http://localhost:8080/conferencemgr/conference
```

### Cancel conference

Service uri: /conferencemgr/conference/cancel

Method: PUT

Request params:
- conferenceName - conference name to be cancelled

Input payload: N/A

Result:
- HTTP 200 - success
- HTTP 404 - conference with such name does not exist

Example call:

```
curl -X PUT http://localhost:8080/conferencemgr/conference/cancel?conferenceName=konverents123
```

### Check conference room availability

Service uri: /conferencemgr/conference/availability

Method: GET

Request params:
- conferenceName - conference name to be checked for availability

Input payload: N/A

Result:
- HTTP 200 - success with result payload as check status
- HTTP 404 - conference with such name does not exist

Result payload structure for success:
- "conferenceRoomAvailability" enum( AVAILABLE | FULL | CANCELLED | NO_MATCH )

Example call:

```
curl -X GET http://localhost:8080/conferencemgr/conference/availability?conferenceName=konverents123
```

### Add participant to conference

Service uri: /conferencemgr/participant

Method: POST

Request params:
- conferenceName - conference name where to add participant
- participantName - participant name to add (unique in conference)

Input payload: N/A

Result:
- HTTP 200 - success
- HTTP 404 - conference with such name does not exist
- HTTP 400 - participant with such name already exist

Example call:

```
curl -X POST http://localhost:8080/conferencemgr/participant?conferenceName=konverents123\&participantName=participant123
```

### Remove participant from conference

Service uri: /conferencemgr/participant

Method: DELETE

Request params:
- conferenceName - conference name from where to remove participant
- participantName - participant name to be removed

Input payload: N/A

Result:
- HTTP 200 - success
- HTTP 404 - conference with such name does not exist
- HTTP 404 - participant with such name does not exist

Example call:

```
curl -X DELETE http://localhost:8080/conferencemgr/participant?conferenceName=konverents123\&participantName=participant123
```


