package com.hotelsystemmanegment.Entity;

import jakarta.persistence.*;

@Entity
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Lob  // За големи двоични обекти (BLOB)
    private byte[] imageData;

    private String imageName;
}
