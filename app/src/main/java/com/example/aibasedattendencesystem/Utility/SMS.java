package com.example.aibasedattendencesystem.Utility;

import java.util.Collections;
import java.util.List;

import ClickSend.*;
import ClickSend.Api.SmsApi;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;


public class SMS {

    public static void setUpServer(String mobileNo, String message) {

        ApiClient defaultClient = new ApiClient();
        defaultClient.setUsername("atulpg7@student.in");
        defaultClient.setPassword("Atul@@123");
        SmsApi apiInstance = new SmsApi(defaultClient);

        SmsMessage smsMessage = new SmsMessage();
        smsMessage.body(message);
        smsMessage.to(mobileNo);
        smsMessage.source("java");

        List<SmsMessage> smsMessageList = Collections.singletonList(smsMessage);
        SmsMessageCollection smsMessages = new SmsMessageCollection();
        smsMessages.messages(smsMessageList);
        try {
            String result = apiInstance.smsSendPost(smsMessages);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SmsApi#smsSendPost");
            e.printStackTrace();
        }
    }
}