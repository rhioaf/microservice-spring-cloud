package com.xtremax.mcs.roomreservationservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/room-reservations")
public class RoomReservationWebService {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//  @Autowired
//  private RestTemplate restTemplate;
  @Autowired
  private RoomClient roomClient;
  @Autowired
  private GuestClient guestClient;
  @Autowired
  private ReservationClient reservationClient;

  @GetMapping
  public List<RoomReservation> getRoomReservations(@RequestParam(name = "date", required = false)String dateString) {
    Date currentDate = createDateFromString(dateString);
    List<Room> rooms = this.roomClient.getAllRooms();
    Map<Long, RoomReservation> roomReservations = new HashMap<>();

    rooms.forEach(room -> {
      RoomReservation roomReservation = new RoomReservation();
      roomReservation.setRoomId(room.getId());
      roomReservation.setRoomName(room.getName());
      roomReservation.setRoomNumber(room.getRoomNumber());
      roomReservations.put(room.getId(), roomReservation);
    });
    List<Reservation> reservations = this.reservationClient.getAllReservations(new java.sql.Date(currentDate.getTime()));
    reservations.forEach(reservation -> {
      RoomReservation roomReservation = roomReservations.get(reservation.getRoomId());
      roomReservation.setDate(currentDate);
      Guest guest = guestClient.getGuest(reservation.getGuestId());
      roomReservation.setGuestId(guest.getId());
      roomReservation.setFirstName(guest.getFirstName());
      roomReservation.setLastName(guest.getLastName());
    });
    return new ArrayList<>(roomReservations.values());
//    List<RoomReservation> roomReservations = new ArrayList<>();
//    rooms.forEach(room -> {
//      RoomReservation roomReservation = new RoomReservation();
//      roomReservation.setRoomNumber(room.getRoomNumber());
//      roomReservation.setRoomName(room.getName());
//      roomReservation.setRoomId(room.getId());
//      roomReservations.add(roomReservation);
//    });
//    return roomReservations;
  }

  private Date createDateFromString(String dateString) {
    Date result = null;
    if(dateString != null) {
      try {
        result = DATE_FORMAT.parse(dateString);
      } catch (ParseException e) {
        result = new Date();
      }
    } else {
      result = new Date();
    }
    return result;
  }

//  private List<Room> getAllRooms() {
////    HttpHeaders headers = new HttpHeaders();
////    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
////    HttpEntity<String> entity = new HttpEntity<String>(headers);
//    ResponseEntity<List<Room>> roomResponse = this.restTemplate.exchange("http://ROOMSERVICES/rooms", HttpMethod.GET, null, new ParameterizedTypeReference<List<Room>>() {
//    });
//    return roomResponse.getBody();
//  }
}
