package nz.ac.auckland.se281;

import nz.ac.auckland.se281.Types.CateringType;

public class Catering extends Service {

  private CateringType cateringType;

  public Catering(String bookingReference, CateringType cateringType) {
    super(bookingReference, cateringType.getName());
    this.cateringType = cateringType;
  }

  private int cost;

  public int getCost() {
    cost = cateringType.getCostPerPerson();
    return cost;
  }
}
