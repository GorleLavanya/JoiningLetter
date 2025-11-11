package Joining.example.controller;

import Joining.example.model.Employee;
import Joining.example.service.EmailService;
import Joining.example.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmailService emailService;

    @Autowired
    private EmployeeService employeeService;

    public EmployeeController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Test endpoint to verify backend is running
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Backend is running successfully!");
        response.put("status", "OK");
        response.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            System.out.println("📝 Received employee data for: " + employee.getFullName());
            Employee savedEmployee = employeeService.saveEmployee(employee);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Employee saved successfully");
            response.put("employee", savedEmployee);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error saving employee: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to save employee: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/send-appointment-letter-with-pdf")
    public ResponseEntity<?> sendAppointmentLetterWithPdf(
            @RequestParam("employeeData") String employeeDataJson,
            @RequestParam("pdfFile") MultipartFile pdfFile) {

        try {
            System.out.println("📧 Starting email sending process...");

            // Validate PDF file
            if (pdfFile == null || pdfFile.isEmpty()) {
                throw new RuntimeException("PDF file is empty or null");
            }

            // Check file size (max 10MB)
            long maxFileSize = 10 * 1024 * 1024; // 10MB
            if (pdfFile.getSize() > maxFileSize) {
                throw new RuntimeException(
                        "PDF file is too large. Maximum size is 10MB. Current size: " + pdfFile.getSize() + " bytes");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> employeeData = objectMapper.readValue(employeeDataJson,
                    new TypeReference<Map<String, String>>() {
                    });

            String email = employeeData.get("email");
            String fullName = employeeData.get("fullName");

            // Enhanced logging
            System.out.println("=== 📧 EMAIL SENDING DETAILS ===");
            System.out.println("👤 Employee: " + fullName);
            System.out.println("📧 Email: " + email);
            System.out.println("📄 PDF File: " + pdfFile.getOriginalFilename());
            System.out.println("📊 PDF Size: " + pdfFile.getSize() + " bytes");

            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Recipient email is required");
            }

            emailService.sendAppointmentLetterWithPdf(
                    email,
                    "Appointment Letter - Hamsini Tech Solutions",
                    fullName,
                    employeeData.get("position"),
                    employeeData.get("employeeId"),
                    employeeData.get("startDate"),
                    employeeData.get("department"),
                    employeeData.get("workLocation"),
                    employeeData.get("salary"),
                    employeeData.get("annualSalary"),
                    employeeData.get("probationPeriod"),
                    employeeData.get("trainingPeriod"),
                    employeeData.get("address"),
                    employeeData.get("phone"),
                    employeeData.get("reportingManager"),
                    pdfFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Complete appointment letter sent successfully to " + email);
            response.put("status", "success");
            response.put("pdfSize", String.valueOf(pdfFile.getSize()));
            response.put("timestamp", new java.util.Date().toString());

            System.out.println("✅ Email sent successfully to: " + email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Failed to send email with PDF: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send appointment letter: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend is running!");
        response.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(response);
    }
}