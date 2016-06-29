package com.example.pato.tacheando.Classes;
import com.squareup.otto.Bus;

public class SharedData {

  private static Bus bus;
  public static Bus bus() {
    if (bus == null) {
      bus = new Bus();
    }
    return bus;
  }

}
