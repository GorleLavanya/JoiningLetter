package Joining.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAppointmentLetterWithPdf(String toEmail, String subject, String employeeName,
            String position, String employeeId, String startDate,
            String department, String workLocation, String salary,
            String annualSalary, String probationPeriod, String trainingPeriod,
            String address, String phone, String reportingManager,
            MultipartFile pdfFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("hamsinitech@gmail.com", "Hamsini Tech Solutions");
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String emailContent = buildEmailContent(toEmail, employeeName, position, employeeId, startDate,
                    department, workLocation, salary, annualSalary,
                    probationPeriod, trainingPeriod, address, phone, reportingManager);
            helper.setText(emailContent, true);

            // Add PDF attachment
            helper.addAttachment("Appointment_Letter_" + employeeName.replace(" ", "_") + ".pdf",
                    new ByteArrayResource(pdfFile.getBytes()));

            mailSender.send(message);
            System.out.println("Email with PDF sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email with PDF: " + e.getMessage());
            throw new RuntimeException("Failed to send email with PDF: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to send email with PDF: " + e.getMessage());
            throw new RuntimeException("Failed to send email with PDF: " + e.getMessage());
        }
    }

    private String buildEmailContent(String toEmail, String employeeName, String position, String employeeId,
            String startDate,
            String department, String workLocation, String salary, String annualSalary,
            String probationPeriod, String trainingPeriod, String address, String phone,
            String reportingManager) {

        // Format current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = dateFormat.format(new Date());

        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <style>
                                /* Your existing styles */
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <div class="company-name">Hamsini Tech Solutions</div>
                                    <h2>APPOINTMENT LETTER</h2>
                                </div>

                                <div class="content">
                                    <div style="margin-bottom: 20px;">
                                        <p><strong>Date:</strong> %s</p>
                                        <p><strong>Name:</strong> %s</p>
                                        <p><strong>Employee ID:</strong> %s</p>
                                        <p><strong>Mobile No:</strong> %s</p>
                                        <p><strong>Email:</strong> %s</p>
                                        <p><strong>Address:</strong> %s</p>
                                    </div>

                                    <div class="clause">
                                        <p>Dear <strong>%s</strong>,</p>
                                        <p>Welcome to Hamsini Tech Solutions!</p>
                                        <p>Congratulations — we are delighted to have you on board.</p>
                                        <p>Following your application and subsequent personal interview, we are pleased to offer you the position of <strong>%s</strong> in the <strong>%s</strong> department.</p>
                                    </div>

                                    <div style="margin: 20px 0;">
                                        <p><strong>Work Location:</strong> %s</p>
                                        <p><strong>Date of Joining:</strong> %s</p>
                                    </div>

                                    <div class="clause">
                                        <div class="clause-title">Compensation & Benefits:</div>
                                        <p>You will receive a stipend of <strong>₹%s/-</strong> per month during your initial <strong>%s</strong> of training and probation.</p>
                                        <p>Upon successful completion of this period, you will be entitled to a Cost to Company (CTC) of <strong>₹%s/-</strong> per annum.</p>
                                        <p>Your probation period will be <strong>%s</strong>.</p>
                                    </div>

                                    <div class="clause">
                                        <p><strong>Reporting Manager:</strong> %s</p>
                                        <p>Please refer to the attached PDF document for complete details of your appointment, including all terms and conditions, policies, and annexures.</p>
                                    </div>
                                </div>

                                <div class="footer">
                                    <p><strong>HAMSINI TECH SOLUTIONS</strong></p>
                                    <p>Flat no:502, Annapoorna Block, Aditya Enclave, Ameerpet, Hyderabad, Telangana 500085</p>
                                    <p>Phone: +91 9515345553 | Email: hamsinitechsolutions@gmail.com | Website: www.hamsinitechsolutions.com</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                currentDate, employeeName, employeeId, phone, toEmail, address,
                employeeName, position, department, workLocation, startDate,
                salary, trainingPeriod, annualSalary, probationPeriod, reportingManager);
    }
}