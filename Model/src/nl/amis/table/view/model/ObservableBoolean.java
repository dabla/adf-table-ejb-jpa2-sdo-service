package nl.amis.table.view.model;

import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// http://stackoverflow.com/questions/2743852/constantly-check-for-change-in-java
public class ObservableBoolean {
  // "CopyOnWrite" to avoid concurrent modification exceptions in loop below.
  private final ChangeListener listener;

  public ObservableBoolean(final ChangeListener listener) {
    this.listener = listener;
  }

  private boolean value;

  public boolean booleanValue() {
    return value;
  }

  public synchronized void setValue(final boolean value) {
    System.out.println("setValue: " + value);
    this.value = value;
    listener.stateChanged(new ChangeEvent(this));
  }
  
  public String toString() {
    return String.valueOf(value);
  }
}