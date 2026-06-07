package view;
import model.Admin;
import java.util.List;

public class AdminView {
    public void showAdmins(List<Admin> adminList) {
        System.out.println("=== Daftar Admin ===");
        for (Admin admin : adminList) {
            System.out.println("Name: " + admin.getName() + ", Username: " + admin.getUsername());
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}