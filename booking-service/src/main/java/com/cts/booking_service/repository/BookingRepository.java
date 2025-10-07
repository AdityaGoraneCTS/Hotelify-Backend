package com.cts.booking_service.repository;

import com.cts.booking_service.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserId(String userId);
    List<Booking> findByHotelIdIn(List<String> hotelIds);

    /**
     * Finds all bookings for a specific room that have an overlapping date range
     * with the given check-in and check-out dates.
     * The logic is: (StartA <= EndB) and (EndA >= StartB)
     * We also exclude bookings that are already cancelled.
     *
     * @param roomId The ID of the room to check.
     * @param checkInDate The requested check-in date.
     * @param checkOutDate The requested check-out date.
     * @return A list of overlapping, non-cancelled bookings.
     */
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId " +
            "AND b.status != com.cts.booking_service.models.BookingStatus.CANCELLED " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(@Param("roomId") String roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);
}
