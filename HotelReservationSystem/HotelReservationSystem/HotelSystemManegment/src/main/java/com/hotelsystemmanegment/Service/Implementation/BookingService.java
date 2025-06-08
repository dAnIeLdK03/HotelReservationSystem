package com.hotelsystemmanegment.Service.Implementation;

import com.hotelsystemmanegment.DTO.BookingDTO;
import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.Booking;
import com.hotelsystemmanegment.Entity.Room;
import com.hotelsystemmanegment.Entity.User;
import com.hotelsystemmanegment.Exception.OurException;
import com.hotelsystemmanegment.Repositories.BookingRepository;
import com.hotelsystemmanegment.Repositories.RoomRepository;
import com.hotelsystemmanegment.Repositories.UserRepository;
import com.hotelsystemmanegment.Service.Interface.IBookingService;
import com.hotelsystemmanegment.Utils.Utilis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check-out date should be after check-in date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            List<Booking> existingBookings = room.getBookings();

            if (!isRoomAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Room is not available for that date range");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utilis.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("Booking successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving booking: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new OurException("Booking not found"));
            BookingDTO bookingDTO = Utilis.mapBookingEntityToBookingDTO(booking);
            response.setStatusCode(200);
            response.setMessage("Booking found successfully");
            response.setBooking(bookingDTO);
            response.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error finding booking: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utilis.mapBookingEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("Bookings retrieved successfully");
            response.setBookingList(bookingDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving bookings: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new OurException("Booking does not exist"));
            bookingRepository.delete(booking);
            response.setStatusCode(200);
            response.setMessage("Booking cancelled successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling booking: " + e.getMessage());
        }
        return response;
    }

    private boolean isRoomAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                                || bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                || bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate())
                );
    }
}