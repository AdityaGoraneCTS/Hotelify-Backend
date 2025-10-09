package com.cts.hotelservice.repository;


import com.cts.hotelservice.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {
    List<Hotel> findByManagerId(String managerId);
    @Query("SELECT h FROM Hotel h WHERE " +
           "(:location IS NULL OR :location = '' OR " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(h.address.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(h.address.area) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(h.address.state) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:type IS NULL OR :type = '' OR LOWER(CAST(h.type AS string)) LIKE LOWER(CONCAT('%', :type, '%')))")
    List<Hotel> searchHotelsByLocationAndType(@Param("location") String location, @Param("type") String type);

    @Query("SELECT h FROM Hotel h WHERE h.type IN ('COTTAGE', 'APARTMENT', 'VILLA') AND h.rooms IS NOT EMPTY")
    List<Hotel> findUniqueStaysHotels();

    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.rooms r WHERE r.discountedPrice < r.originalPrice")
    List<Hotel> findTopDealsHotels();
}
