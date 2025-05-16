package com.example.paksahara.dao;

import com.example.paksahara.model.Admin;
import java.util.List;

public interface AdminDAO {
    void addAdmin(Admin admin);
    Admin findAdminById(int userID);
    // existing methods…
    Admin findAdminByEmail(String email);

    List<Admin> findAllAdmins();
    void updateAdmin(Admin admin);
    void deleteAdmin(int userID);
}