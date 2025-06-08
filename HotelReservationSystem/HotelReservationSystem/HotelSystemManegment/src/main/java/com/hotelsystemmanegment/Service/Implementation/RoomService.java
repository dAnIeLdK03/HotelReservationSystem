package com.hotelsystemmanegment.Service.Implementation;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.DTO.RoomDTO;
import com.hotelsystemmanegment.Exception.OurException;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Repositories.RoomRepository;
import com.hotelsystemmanegment.Repositories.RoomImageRepository;
import com.hotelsystemmanegment.Service.Interface.IRoomService;
import com.hotelsystemmanegment.Utils.Utilis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hotelsystemmanegment.Entity.Room;
import com.hotelsystemmanegment.Entity.RoomImage;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try {
            Room room = new Room();
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice.toString());
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);

            if (photo != null && !photo.isEmpty()) {
                RoomImage roomImage = new RoomImage();
                roomImage.setRoom(savedRoom);
                roomImage.setPhoto(photo.getBytes());
                roomImage.setImageName(photo.getOriginalFilename());
                roomImageRepository.save(roomImage);
            }
            RoomDTO roomDTO = Utilis.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("Room added successfully");
            response.setRoom(roomDTO);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utilis.mapRoomEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoomList(roomDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting rooms " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRooms(Long roomId) {
        Response response = new Response();
        try {
            roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Room deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRooms(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice.toString());
            if (description != null) room.setRoomDescription(description);
            Room updatedRoom = roomRepository.save(room);

            if (photo != null && !photo.isEmpty()) {
                RoomImage roomImage = new RoomImage();
                roomImage.setRoom(updatedRoom);
                roomImage.setPhoto(photo.getBytes());
                roomImage.setImageName(photo.getOriginalFilename());
                roomImageRepository.save(roomImage);
            }
            RoomDTO roomDTO = Utilis.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Room updated successfully");
            response.setRoom(roomDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            RoomDTO roomDTO = Utilis.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoom(roomDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();
        try {
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTyoes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utilis.mapRoomEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoomList(roomDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utilis.mapRoomEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successfully");
            response.setRoomList(roomDTOList);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting available rooms " + e.getMessage());
        }
        return response;
    }
}
