package com.cts.hotelservice.service;


import com.cts.hotelservice.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(String id);
    List<HotelDto> getHotelsByManagerId(String managerId);
    void deleteHotel(String id);
    HotelDto updateBasicInfo(String id, HotelBasicInfoUpdateDto hotelUpdateDto);
    HotelDto updateContactAndAddress(String id, HotelContactAddressUpdateDto hotelUpdateDto);
    HotelDto updateImagesAndMedia(String id, HotelImagesUpdateDto imagesUpdateDto);
    HotelDto updateFeaturesAndAmenities(String id, HotelFeaturesAmenitiesUpdateDto featuresAmenitiesUpdateDto);
    HotelDto updateHotelPolicies(String id, HotelPoliciesUpdateDto policiesUpdateDto);
    HotelDto updateRooms(String id, List<RoomUpdateDto> roomUpdates);
    List<HotelDto> searchHotels(String location, String type);
    List<HotelDto> getUniqueStaysCards();
    List<HotelDto> getTopDealsCards();
}
