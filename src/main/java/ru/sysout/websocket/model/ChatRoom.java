package ru.sysout.websocket.model;

import java.util.Objects;

public class ChatRoom {
  private String firstPerson;
  private String secondPerson;

  public ChatRoom(String firstPerson, String secondPerson) {
    this.firstPerson = firstPerson;
    this.secondPerson = secondPerson;
  }

  public String getFirstPerson() {
    return firstPerson;
  }

  public void setFirstPerson(String firstPerson) {
    this.firstPerson = firstPerson;
  }

  public String getSecondPerson() {
    return secondPerson;
  }

  public void setSecondPerson(String secondPerson) {
    this.secondPerson = secondPerson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatRoom chatRoom = (ChatRoom) o;
    return Objects.equals(getFirstPerson(), chatRoom.getFirstPerson()) && Objects
        .equals(getSecondPerson(), chatRoom.getSecondPerson());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFirstPerson(), getSecondPerson());
  }

  @Override
  public String toString() {
    return "ChatRoom{" +
        "firstPerson='" + firstPerson + '\'' +
        ", secondPerson='" + secondPerson + '\'' +
        '}';
  }
}
