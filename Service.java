package nz.ac.auckland.se281;

public abstract class Service {

  private String bookingReference;
  private String serviceTypeName;

  public Service(String bookingReference, String serviceTypeName) {
    this.bookingReference = bookingReference;
    this.serviceTypeName = serviceTypeName;
  }

  // getter methods for Service entities
  public String getBookingReference() {
    return bookingReference;
  }

  public String getserviceTypeName() {
    return serviceTypeName;
  }

  public abstract int getCost();
}
