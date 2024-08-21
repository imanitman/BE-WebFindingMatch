package com.example.demo.service;


import org.springframework.stereotype.Service;

@Service
public class SmsService {
    public String generateOtp(){
        int randomPin =  (int)(Math.random()*90000)+10000;
        return String.valueOf(randomPin);
    }
    // public void sendMessage(String userPhoneNumber, String otp){
    //     Twilio.init(account_sid, auth_token);
    //     Message.creator(
    //         new PhoneNumber(userPhoneNumber),
    //         new PhoneNumber(my_phonenumber),
    //         "Your otp is" + otp).create();
    // }
    // public String convertPhoneNumber(String userPhone){
    //     char[] phone = userPhone.toCharArray();
    //     String twilio_number = "+84" + new String(phone,1,phone.length-1);
    //     return twilio_number;
    // }
}
