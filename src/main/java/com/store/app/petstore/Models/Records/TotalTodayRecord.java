package com.store.app.petstore.Models.Records;

import com.store.app.petstore.Repositories.OrderDetailRepository;
import com.store.app.petstore.Repositories.OrderRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public record TotalTodayRecord(long total_price, int total_pet, String total_product) {}