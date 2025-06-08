package com.hotelsystemmanegment.Service.Interface;

import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);
}
