package com.cts.hotelservice.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(length = 2000)
    private String description;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private HotelType type;

    @ElementCollection
    @CollectionTable(name = "hotel_features", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "feature")
    private List<String> features;

    @ElementCollection
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    private double rating;
    private int reviews;

    @ElementCollection
    @CollectionTable(name = "hotel_images", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "image_url")
    private List<String> images;

    private String primaryImage;
    private boolean isFullyRefundable;
    private boolean hasFreeBreakfast;
    private boolean reserveNowPayLater;

    @Embedded
    private HotelPolicies policies;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Room> rooms;

    @Embedded
    private Contact contact;

    private String availabilityMessage;
    private String managerId;

    @PrePersist
    @PreUpdate
    private void setHotelForRooms() {
        if (rooms != null) {
            rooms.forEach(room -> room.setHotel(this));
        }
    }
}
