package com.example.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @description:
 * @author: azurewang
 * @date: 2026/8/30 周日 21:05
 * @Version 1.0
 **/
@Service
public class TicketService {




    public String bookTicket(   String userName ,String ticketNumber) {

        return userName + "预定车票成功" +"预定号:" + ticketNumber ;
    }







}
