package com.xtremax.mcs.reservationservices;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
  Iterable<Reservation> findAllByDate(Date date);
}
