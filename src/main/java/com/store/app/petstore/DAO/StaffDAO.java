package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Staff;
import java.util.List;

public interface StaffDAO extends BaseDAO<Staff> {
    Staff getStaffByEmail(String email);
    Staff getStaffByPhone(String phone);
    List<Staff> searchStaff(String keyword);
} 