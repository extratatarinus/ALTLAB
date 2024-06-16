package com.example.demo.Validatoin;

import jakarta.validation.constraints.*;

public class OrderDto {

    @NotBlank(message = "Имя не должно быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Имя должно содержать только буквы")
    private String firstName;

    @NotBlank(message = "Фамилия не должна быть пустой")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Фамилия должна содержать только буквы")
    private String lastName;

    @NotBlank(message = "Номер телефона не должен быть пустым")
    @PhoneNumber
    private String phone;

    @NotBlank(message = "Адрес не должен быть пустым")
    private String address;

    @NotBlank(message = "Город не должен быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Город должен содержать только буквы")
    private String city;

    @NotBlank(message = "Регион не должен быть пустым")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Регион должен содержать только буквы")
    private String region;

    @NotBlank(message = "Индекс не должен быть пустым")
    @Pattern(regexp = "^[0-9]+$", message = "Индекс должен содержать только цифры")
    private String zipCode;

    @NotBlank(message = "Способ оплаты не должен быть пустым")
    private String paymentMethod;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // геттеры и сеттеры
}
