package com.hotelsystemmanegment.Utils;

import com.hotelsystemmanegment.DTO.BookingDTO;
import com.hotelsystemmanegment.DTO.RoomDTO;
import com.hotelsystemmanegment.DTO.UserDTO;
import com.hotelsystemmanegment.Entity.Booking;
import com.hotelsystemmanegment.Entity.Room;
import com.hotelsystemmanegment.Entity.User;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class Utilis {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public static UserDTO mapUserEntityToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomPhotoUrl(room.getRoomPhotoUrl());
        roomDTO.setRoomDescription(room.getRoomDescription());
        return roomDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumOfAdults(booking.getNumOfAdults());
        bookingDTO.setNumOfChildren(booking.getNumOfChildren());
        bookingDTO.setTotalNumberOfGuests(booking.getTotalNumberOfGuests());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        return bookingDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTOPlusBookings(Room room) {
        RoomDTO roomDTO = mapRoomEntityToRoomDTO(room);

        if(room.getBookings() != null) {
            roomDTO.setBookings(room.getBookings().stream()
                    .map(Utilis::mapBookingEntityToBookingDTO)
                    .collect(Collectors.toList()));
        }

        return roomDTO;
    }

    public static UserDTO mapUserEntityToUserDTOPlusUserBookingsAndRoom(User user) {
        UserDTO userDTO = mapUserEntityToUserDTO(user);

        if(user.getBookings() != null && !user.getBookings().isEmpty()) {
            userDTO.setBookings(user.getBookings().stream()
                    .map(booking -> mapBookingEntityToBookingDTOPlusBookedRooms(booking, false))
                    .collect(Collectors.toList()));
        }

        return userDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTOPlusBookedRooms(Booking booking, boolean mapUser) {
        BookingDTO bookingDTO = mapBookingEntityToBookingDTO(booking);

        if(mapUser && booking.getUser() != null) {
            bookingDTO.setUser(mapUserEntityToUserDTO(booking.getUser()));
        }

        if(booking.getRoom() != null) {
            bookingDTO.setRoom(mapRoomEntityToRoomDTO(booking.getRoom()));
        }

        return bookingDTO;
    }

    public static List<RoomDTO> mapRoomEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream()
                .map(Utilis::mapRoomEntityToRoomDTO)
                .collect(Collectors.toList());
    }

    public static List<UserDTO> mapUserEntityToUserListDTO(List<User> userList) {
        return userList.stream()
                .map(Utilis::mapUserEntityToUserDTO)
                .collect(Collectors.toList());
    }

    public static List<BookingDTO> mapBookingEntityToBookingListDTO(List<Booking> bookingList) {
        return bookingList.stream()
                .map(Utilis::mapBookingEntityToBookingDTO)
                .collect(Collectors.toList());
    }
}