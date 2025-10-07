package com.cts.hotelservice.service;

import com.cts.hotelservice.dto.*;
import com.cts.hotelservice.exception.HotelNotFoundException;
import com.cts.hotelservice.exception.InvalidHotelDataException;
import com.cts.hotelservice.models.Hotel;
import com.cts.hotelservice.models.HotelType;
import com.cts.hotelservice.models.Room;
import com.cts.hotelservice.repository.HotelRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository, ModelMapper modelMapper) {
        this.hotelRepository = hotelRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        if (hotelDto == null) {
            throw new InvalidHotelDataException("Hotel data cannot be null.");
        }

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);

        if (hotel.getRooms() != null) {
            hotel.getRooms().forEach(room -> room.setHotel(hotel));
        }

        return modelMapper.map(hotelRepository.save(hotel), HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(String id) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }
        return modelMapper.map(hotelOptional.get(), HotelDto.class);
    }

    @Override
    public List<HotelDto> getHotelsByManagerId(String managerId) {
        List<Hotel> hotels = hotelRepository.findByManagerId(managerId);
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteHotel(String id) {
        if (!hotelRepository.existsById(id)) {
            throw new HotelNotFoundException(id);
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HotelDto updateBasicInfo(String id, HotelBasicInfoUpdateDto hotelUpdateDto) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        existingHotel.setName(hotelUpdateDto.getName());
        existingHotel.setDescription(hotelUpdateDto.getDescription());

        try {
            HotelType type = HotelType.valueOf(hotelUpdateDto.getType().toUpperCase());
            existingHotel.setType(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidHotelDataException("Invalid hotel type: " + hotelUpdateDto.getType());
        }

        return modelMapper.map(hotelRepository.save(existingHotel), HotelDto.class);
    }

    @Override
    @Transactional
    public HotelDto updateContactAndAddress(String id, HotelContactAddressUpdateDto hotelUpdateDto) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        modelMapper.map(hotelUpdateDto.getAddress(), existingHotel.getAddress());
        modelMapper.map(hotelUpdateDto.getContact(), existingHotel.getContact());

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDto.class);
    }

    @Override
    @Transactional
    public HotelDto updateImagesAndMedia(String id, HotelImagesUpdateDto imagesUpdateDto) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        existingHotel.setImages(imagesUpdateDto.getImages());

        // If the primary image is not provided, set the first image from the list as the primary one
        if (!StringUtils.hasText(imagesUpdateDto.getPrimaryImage())) {
            existingHotel.setPrimaryImage(imagesUpdateDto.getImages().getFirst());
        } else {
            existingHotel.setPrimaryImage(imagesUpdateDto.getPrimaryImage());
        }

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDto.class);
    }

    @Override
    @Transactional
    public HotelDto updateFeaturesAndAmenities(String id, HotelFeaturesAmenitiesUpdateDto featuresAmenitiesUpdateDto) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        existingHotel.setFeatures(featuresAmenitiesUpdateDto.getFeatures());
        existingHotel.setAmenities(featuresAmenitiesUpdateDto.getAmenities());

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelPolicies(String id, HotelPoliciesUpdateDto policiesUpdateDto) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        // Update the policies DTO fields
        modelMapper.map(policiesUpdateDto.getPolicies(), existingHotel.getPolicies());

        // Update the boolean fields
        existingHotel.setFullyRefundable(policiesUpdateDto.getIsFullyRefundable());
        existingHotel.setHasFreeBreakfast(policiesUpdateDto.getHasFreeBreakfast());
        existingHotel.setReserveNowPayLater(policiesUpdateDto.getReserveNowPayLater());

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDto.class);
    }

    @Override
    public HotelDto updateRooms(String id, List<RoomUpdateDto> roomUpdates) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);

        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException(id);
        }

        Hotel existingHotel = hotelOptional.get();

        for (RoomUpdateDto roomUpdateDto : roomUpdates) {
            switch (roomUpdateDto.getStatus()) {
                case CREATE:
                    Room newRoom = modelMapper.map(roomUpdateDto, Room.class);
                    newRoom.setHotel(existingHotel);
                    existingHotel.getRooms().add(newRoom);
                    break;
                case UPDATE:
                    existingHotel.getRooms().stream()
                            .filter(room -> room.getId().equals(roomUpdateDto.getId()))
                            .findFirst()
                            .ifPresent(roomToUpdate -> {
                                modelMapper.map(roomUpdateDto, roomToUpdate);
                                roomToUpdate.setHotel(existingHotel);
                            });
                    break;
                case DELETE:
                    existingHotel.getRooms().removeIf(room -> room.getId().equals(roomUpdateDto.getId()));
                    break;
                default:
                    throw new InvalidHotelDataException("Invalid room update status: " + roomUpdateDto.getStatus());
            }
        }

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDto.class);
    }
}

