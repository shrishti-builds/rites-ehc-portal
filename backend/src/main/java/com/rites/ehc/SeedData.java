package com.rites.ehc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SeedData {
    private SeedData() {
    }

    public static void seedIfNeeded() {
        try (Connection conn = Db.getConnection()) {
            if (!hasRows(conn, "ehc_cities")) {
                insertCity(conn, "Haryana", "Gurugram");
                insertCity(conn, "Haryana", "Faridabad");
                insertCity(conn, "Haryana", "Panchkula");
                insertCity(conn, "Delhi", "New Delhi");
                insertCity(conn, "Delhi", "North Delhi");
                insertCity(conn, "Delhi", "South Delhi");
                insertCity(conn, "Maharashtra", "Mumbai");
                insertCity(conn, "Maharashtra", "Pune");
                insertCity(conn, "Maharashtra", "Nagpur");
                insertCity(conn, "West Bengal", "Kolkata");
                insertCity(conn, "West Bengal", "Howrah");
                insertCity(conn, "West Bengal", "Durgapur");
            }
            if (!hasRows(conn, "ehc_employees")) {
                insertEmployee(conn, "10124", "ANJANI UPADHYAY", "Manager (IT)", "INFORMATION TECHNOLOGY", "9876543210", "0124-2818000", "1975-01-26", "Male");
                insertEmployee(conn, "10245", "RAHUL KUMAR", "Assistant Manager", "CIVIL ENGINEERING", "9988776655", "0124-2818111", "1982-05-15", "Male");
                insertEmployeeDependent(conn, "10124", "ANJANI UPADHYAY", "Self", "1975-01-26", "Male");
                insertEmployeeDependent(conn, "10124", "SWETA SHARMA", "Spouse", "1976-08-28", "Female");
                insertEmployeeDependent(conn, "10245", "RAHUL KUMAR", "Self", "1982-05-15", "Male");
                insertEmployeeDependent(conn, "10245", "PRIYA KUMARI", "Spouse", "1985-09-12", "Female");
                insertEmployeeDependent(conn, "10245", "RAMESH KUMAR", "Father", "1953-04-10", "Male");
            
                insertEmployee(conn, "10312", "SUNITA VERMA", "Deputy General Manager", "HUMAN RESOURCES", "9811223344", "0124-2818222", "1970-03-08", "Female");
                insertEmployee(conn, "10378", "ARUN SHARMA", "Executive Engineer", "ELECTRICAL ENGINEERING", "9700112233", "0124-2818333", "1978-09-22", "Male");
                insertEmployee(conn, "10445", "PREETI SINGH", "Senior Finance Officer", "FINANCE & ACCOUNTS", "9934556677", "0124-2818444", "1985-12-14", "Female");
                
                insertEmployeeDependent(conn, "10312", "SUNITA VERMA", "Self", "1970-03-08", "Female");
                insertEmployeeDependent(conn, "10312", "VIKRAM VERMA", "Spouse", "1968-07-14", "Male");
                insertEmployeeDependent(conn, "10312", "ANANYA VERMA", "Daughter", "2001-11-20", "Female");
                
                insertEmployeeDependent(conn, "10378", "ARUN SHARMA", "Self", "1978-09-22", "Male");
                insertEmployeeDependent(conn, "10378", "KAVITA SHARMA", "Spouse", "1980-04-05", "Female");
                insertEmployeeDependent(conn, "10378", "SURESH SHARMA", "Father", "1950-06-18", "Male");
                insertEmployeeDependent(conn, "10378", "KAMLA DEVI", "Mother", "1952-02-25", "Female");
                
                insertEmployeeDependent(conn, "10445", "PREETI SINGH", "Self", "1985-12-14", "Female");
                insertEmployeeDependent(conn, "10445", "MOHIT SINGH", "Spouse", "1983-08-30", "Male");
            }

            int count = 0;
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ehc_hospitals");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
            if (count < 40) {
                try (java.sql.Statement st = conn.createStatement()) { st.executeUpdate("DELETE FROM ehc_hospitals"); }
                insertHospital(conn, "HOSP001", "Medanta - The Medicity", "Sector 38", "Near NH-8", "Haryana", "Gurugram", "122001", "0124-4141414", "Dr. Sandeep Dewan", "Medical Director", "contact@medanta.org", "9999911111", "Sunita Rao", "Admin Coordinator", "sunita.r@medanta.org", "9888822222", 4500, 5200, "2027-12-31", "10% discount on additional tests", "Preferred hospital for executives");
                insertHospital(conn, "HOSP002", "Fortis Memorial Research Institute", "Sector 44", "Opposite HUDA City Centre", "Haryana", "Gurugram", "122002", "0124-4962200", "Dr. Ritu Garg", "Zonal Director", "info@fortis.com", "9911223344", "Amit Kumar", "Operations Head", "amit.k@fortis.com", "9822334455", 4800, 5500, "2027-06-30", "Free consultation follow-up", "High quality facilities");
                insertHospital(conn, "HOSP003", "Paras Hospital Gurugram", "C-1, Sushant Lok", "Phase-1", "Haryana", "Gurugram", "122002", "0124-4585555", "Dr. Nikhil Chandra", "CEO", "info@parashospitals.com", "9810101010", "Rohit Negi", "Client Relations", "rohit.n@parashospitals.com", "9810202020", 4200, 4900, "2027-09-30", "5% concession on lab tests", "ISO certified facilities");
                insertHospital(conn, "HOSP004", "Asian Institute of Medical Sciences", "Plot No.69, Sector 21-A", "Badkal Flyover Road", "Haryana", "Faridabad", "121001", "0129-4200000", "Dr. Amar Singh", "Medical Superintendent", "info@asianims.com", "9811234567", "Neha Dhawan", "Corporate Coordinator", "neha.d@asianims.com", "9812345678", 3800, 4400, "2027-12-31", "Free ECG for corporate clients", "NABH accredited");
                insertHospital(conn, "HOSP005", "Sarvodaya Hospital", "YMCA Road", "Sector 8", "Haryana", "Faridabad", "121006", "0129-4282222", "Dr. P.K. Aggarwal", "Director", "corporate@sarvodayahospital.com", "9899887766", "Sanjay Bhatia", "Billing Head", "sanjay.b@sarvodayahospital.com", "9898776655", 3600, 4100, "2027-06-30", "15% off on diagnostic packages", "Well-equipped diagnostic centre");
                insertHospital(conn, "HOSP006", "Metro Hospital Faridabad", "NIT, NH-2", "Sector 12", "Haryana", "Faridabad", "121007", "0129-3080100", "Dr. Vivek Pathak", "COO", "contact@metrohospitals.com", "9891234567", "Pooja Gupta", "Admin Manager", "pooja.g@metrohospitals.com", "9891235678", 3500, 4000, "2027-03-31", "Free blood sugar test", "Good emergency services");
                insertHospital(conn, "HOSP007", "Alchemist Hospital Panchkula", "Sector 21", "Near Sec-21 Bus Stand", "Haryana", "Panchkula", "134112", "0172-5088888", "Dr. Rohit Anand", "MD", "info@alchemisthospital.com", "9876512345", "Manpreet Kaur", "Corporate Desk", "manpreet.k@alchemisthospital.com", "9876623456", 3900, 4500, "2027-12-31", "10% concession on health packages", "Multi-speciality, Tri-city area");
                insertHospital(conn, "HOSP008", "Grecian Super Speciality Hospital", "Sector 69", "Phase 10, Mohali", "Haryana", "Panchkula", "160062", "0172-5088000", "Dr. Gurpreet Singh", "Director", "info@grecian.in", "9914001234", "Harjinder Pal", "Operations", "hp@grecian.in", "9914002345", 4000, 4700, "2027-09-30", "Free treadmill test included", "Serves Chandigarh tri-city area");
                insertHospital(conn, "HOSP009", "Max Super Speciality Hospital, Saket", "1-2, Press Enclave Road", "Saket", "Delhi", "New Delhi", "110017", "011-26515050", "Dr. Ajay Lall", "Director - Pulmonology", "contact.saket@maxhealthcare.com", "9876501234", "Rahul Malhotra", "Manager Operations", "rahul.m@maxhealthcare.com", "9876543210", 4200, 4900, "2027-12-31", "5% discount on pharmacy", "Located in South Delhi");
                insertHospital(conn, "HOSP010", "Indraprastha Apollo Hospital", "Sarita Vihar", "Delhi-Mathura Road", "Delhi", "New Delhi", "110076", "011-26925858", "Dr. Anupam Sibal", "Group Medical Director", "apollo.delhi@apollohospitals.com", "9988770011", "Vikram Shah", "Lead Coordinator", "vikram.s@apollohospitals.com", "9988223344", 4600, 5400, "2027-10-31", "Free dietitian consult", "Leading multi-specialty");
                insertHospital(conn, "HOSP011", "AIIMS Delhi", "Ansari Nagar East", "New Delhi", "Delhi", "New Delhi", "110029", "011-26588500", "Dr. M. Srinivas", "Director", "corporate@aiims.edu", "9810056789", "Ravi Shankar", "Admin Officer", "ravi.s@aiims.edu", "9810067890", 2800, 3200, "2028-03-31", "Government rates applicable", "Premier government hospital");
                insertHospital(conn, "HOSP012", "GTB Hospital (Govt. Corporate Wing)", "Dilshad Garden", "Shahdara", "Delhi", "North Delhi", "110095", "011-22582222", "Dr. Suresh Yadav", "Medical Director", "corporate@gtbhospital.gov.in", "9971234567", "Anita Sharma", "Billing Executive", "anita.s@gtbhospital.gov.in", "9971345678", 2500, 2900, "2028-03-31", "Government panel rates", "North Delhi government hospital");
                insertHospital(conn, "HOSP013", "Max Hospital Shalimar Bagh", "FC-50, Shalimar Bagh", "Near Ring Road", "Delhi", "North Delhi", "110088", "011-45055050", "Dr. Prashant Sharma", "Medical Director", "shalimarbagh@maxhealthcare.com", "9810198765", "Vishal Arora", "Corporate Desk", "vishal.a@maxhealthcare.com", "9810287654", 4000, 4700, "2027-12-31", "10% off on comprehensive package", "Well known in North Delhi");
                insertHospital(conn, "HOSP014", "BLK-Max Super Speciality Hospital", "5, Pusa Road", "Rajinder Nagar", "Delhi", "North Delhi", "110005", "011-30403040", "Dr. Deepak Govil", "Chairman", "corporate@blkmax.com", "9958112233", "Namita Sood", "Corporate Relations", "namita.s@blkmax.com", "9958223344", 4400, 5100, "2027-09-30", "Free follow-up consultation", "Renowned multi-specialty");
                insertHospital(conn, "HOSP015", "Fortis Escorts Heart Institute", "Okhla Road", "Near Sukhdev Vihar Metro", "Delhi", "South Delhi", "110025", "011-47135000", "Dr. Ashok Seth", "Chairman", "fehi@fortishealthcare.com", "9810345678", "Divya Jain", "Corporate Relations", "divya.j@fortishealthcare.com", "9810456789", 5000, 5800, "2027-12-31", "Free Holter monitoring", "Best cardiac care in Delhi");
                insertHospital(conn, "HOSP016", "Safdarjung Hospital (Corporate Block)", "Ansari Nagar West", "Ring Road", "Delhi", "South Delhi", "110029", "011-26707444", "Dr. Balvinder Kumar", "Medical Superintendent", "corporate@safdarjung.gov.in", "9868567890", "Rekha Singh", "Accounts", "rekha.s@safdarjung.gov.in", "9868678901", 2600, 3000, "2028-03-31", "Government empanelled", "Government hospital with corporate ward");
                insertHospital(conn, "HOSP017", "Kokilaben Dhirubhai Ambani Hospital", "Rao Saheb Achutrao Patwardhan Marg", "Four Bungalows, Andheri West", "Maharashtra", "Mumbai", "400053", "022-30999999", "Dr. Santosh Shetty", "CEO", "corporate@kokilabenhospital.com", "9820012345", "Anjali Desai", "Corporate Manager", "anjali.d@kokilabenhospital.com", "9820123456", 5500, 6200, "2027-12-31", "Priority appointment for RITES employees", "Premium corporate packages available");
                insertHospital(conn, "HOSP018", "Hinduja Hospital", "Veer Savarkar Marg", "Mahim", "Maharashtra", "Mumbai", "400016", "022-24447000", "Dr. Joachim Dsouza", "Medical Director", "corporate@hindujahospital.com", "9821234567", "Grace Pinto", "Corporate Billing", "grace.p@hindujahospital.com", "9821345678", 5200, 5900, "2027-09-30", "Cashless facility", "Well established hospital");
                insertHospital(conn, "HOSP019", "Lilavati Hospital", "A-791, Bandra Reclamation", "Bandra West", "Maharashtra", "Mumbai", "400050", "022-26751000", "Dr. Shashank Joshi", "Director", "corporate@lilavatihospital.com", "9819012345", "Sneha Nair", "Admin Head", "sneha.n@lilavatihospital.com", "9819123456", 4900, 5600, "2027-12-31", "Free nutritionist session", "Trusted by Mumbai corporates");
                insertHospital(conn, "HOSP020", "Ruby Hall Clinic", "40, Sassoon Road", "Camp", "Maharashtra", "Pune", "411001", "020-66455100", "Dr. Bomi Bhote", "CEO", "corporate@rubyhall.com", "9822023456", "Nilima Kale", "Corporate Desk", "nilima.k@rubyhall.com", "9822134567", 4300, 5000, "2027-12-31", "Free X-ray with package", "Most preferred in Pune");
                insertHospital(conn, "HOSP021", "Jehangir Hospital", "32, Sassoon Road", "Opposite Pune Station", "Maharashtra", "Pune", "411001", "020-66810000", "Dr. Lancelot Pinto", "Medical Director", "corporate@jehangirhospital.com", "9822245678", "Priya Karandikar", "Operations", "priya.k@jehangirhospital.com", "9822356789", 4100, 4800, "2027-09-30", "10% off on annual checkup", "Historic hospital with modern facilities");
                insertHospital(conn, "HOSP022", "Sahyadri Hospital Pune", "30-C, Erandwane", "Karve Road", "Maharashtra", "Pune", "411004", "020-67210000", "Dr. Charudutt Apte", "Chairman", "corporate@sahyadrihospital.com", "9823456789", "Swapnil Deshpande", "Billing", "swapnil.d@sahyadrihospital.com", "9823567890", 4000, 4600, "2027-12-31", "Cashless & priority service", "Expanding chain across Maharashtra");
                insertHospital(conn, "HOSP023", "KIMS Kingsway Hospital", "44, Kingsway", "Nagpur", "Maharashtra", "Nagpur", "440001", "0712-6600500", "Dr. Ravi Wankhede", "Director", "corporate@kimshospital.com", "9890112233", "Sonal Bhatt", "Corporate Billing", "sonal.b@kimshospital.com", "9890223344", 3700, 4300, "2027-12-31", "Free cardiac screening", "Central India's leading hospital");
                insertHospital(conn, "HOSP024", "Alexis Hospital", "Mankapur Square", "Ring Road", "Maharashtra", "Nagpur", "440015", "0712-3028888", "Dr. Swapnil Trivedi", "MD", "contact@alexismultispeciality.com", "9881234567", "Manisha Joshi", "Admin", "manisha.j@alexismultispeciality.com", "9881345678", 3500, 4100, "2027-09-30", "Free vision test", "Growing in Nagpur region");
                insertHospital(conn, "HOSP025", "Apollo Gleneagles Hospital", "58, Canal Circular Road", "Kadapara", "West Bengal", "Kolkata", "700054", "033-23202122", "Dr. Sabyasachi Bal", "Director", "corporate.kolkata@apollohospitals.com", "9830123456", "Subrata Das", "Corporate Manager", "subrata.d@apollohospitals.com", "9830234567", 4400, 5100, "2027-12-31", "Priority queue for RITES employees", "Premier hospital in East India");
                insertHospital(conn, "HOSP026", "AMRI Hospital Salt Lake", "JC-16&17, Sector III", "Salt Lake", "West Bengal", "Kolkata", "700098", "033-66000000", "Dr. Rupak Barua", "Group CEO", "corporate@amrihospitals.in", "9831234567", "Debjani Mitra", "Corporate Relations", "debjani.m@amrihospitals.in", "9831345678", 4100, 4700, "2027-09-30", "10% on comprehensive packages", "NABH accredited");
                insertHospital(conn, "HOSP027", "Fortis Hospital Anandapur", "730, Anandapur", "EM Bypass", "West Bengal", "Kolkata", "700107", "033-66284444", "Dr. Subhash Datta", "Facility Director", "kolkata@fortishealthcare.com", "9832345678", "Aruna Sinha", "Billing Manager", "aruna.s@fortishealthcare.com", "9832456789", 4300, 5000, "2027-12-31", "Cashless facility available", "Modern multi-specialty");
                insertHospital(conn, "HOSP028", "Narayana Multispeciality Hospital Howrah", "78, Jessore Road South", "Kolkata", "West Bengal", "Howrah", "700074", "033-40506050", "Dr. Devi Shetty", "Chairman", "howrah@narayanahospitals.com", "9800111222", "Abhijit Roy", "Operations Head", "abhijit.r@narayanahospitals.com", "9800222333", 3900, 4500, "2027-12-31", "Free cardiac consultation", "Affordable super-speciality");
                insertHospital(conn, "HOSP029", "RN Tagore International Institute", "124, EM Bypass", "Mukundapur", "West Bengal", "Howrah", "700099", "033-66000000", "Dr. K.M. Mandana", "Director", "corporate@rtiics.org", "9801122334", "Priyanka Bose", "Admin", "priyanka.b@rtiics.org", "9801233445", 4200, 4900, "2027-09-30", "Free consultation with referral", "Specialised cardiac care");
                insertHospital(conn, "HOSP030", "Mission Hospital Durgapur", "219, PO: Dishergarh", "Dist. Paschim Bardhaman", "West Bengal", "Durgapur", "713305", "0343-2500100", "Dr. Soumitra Ghosh", "Medical Director", "corporate@missionhospital.org", "9832567890", "Tuhin Majumdar", "Admin Officer", "tuhin.m@missionhospital.org", "9832678901", 3400, 4000, "2027-12-31", "10% off on RITES employees", "Trusted in industrial belt");
                insertHospital(conn, "HOSP031", "ILS Hospital Durgapur", "Nazrul Sarani, City Centre", "Durgapur", "West Bengal", "Durgapur", "713216", "0343-6600300", "Dr. Arindam Kar", "Director", "durgapur@ilshospitals.com", "9832789012", "Sukanta Paul", "Billing", "sukanta.p@ilshospitals.com", "9832890123", 3200, 3800, "2027-09-30", "Free blood group test", "Good outpatient services");
                insertHospital(conn, "HOSP032", "Medanta Hospital Lucknow", "Sector A, Pocket 1", "Amar Shaheed Path, Sushant Golf City", "Uttar Pradesh", "Lucknow", "226030", "0522-4506000", "Dr. Rakesh Kapoor", "Director", "lucknow@medanta.org", "9415001234", "Prerna Gupta", "Corporate Relations", "prerna.g@medanta.org", "9415112345", 4000, 4700, "2027-12-31", "Priority booking for RITES employees", "State-of-the-art facilities");
                insertHospital(conn, "HOSP033", "Sahara Hospital Lucknow", "Viraj Khand", "Gomti Nagar", "Uttar Pradesh", "Lucknow", "226010", "0522-6780000", "Dr. Anurag Tandon", "MD", "corporate@saharahospital.com", "9415223456", "Alka Shukla", "Admin", "alka.s@saharahospital.com", "9415334567", 3700, 4300, "2027-09-30", "15% discount on packages", "Well known in Gomti Nagar area");
                insertHospital(conn, "HOSP034", "Fortis Hospital Noida", "B-22, Sector 62", "Noida", "Uttar Pradesh", "Noida", "201301", "0120-4677777", "Dr. Deepak Gupta", "Facility Director", "noida@fortishealthcare.com", "9810512345", "Kavita Verma", "Corporate Desk", "kavita.v@fortishealthcare.com", "9810623456", 4100, 4800, "2027-12-31", "Cashless claim support", "Premium facility in NCR");
                insertHospital(conn, "HOSP035", "Kailash Hospital Noida", "Sector 27", "Near Noida City Centre", "Uttar Pradesh", "Noida", "201301", "0120-4444000", "Dr. H.S. Bhatia", "Director", "corporate@kailashhealthcare.com", "9811712345", "Geeta Rawat", "Billing", "geeta.r@kailashhealthcare.com", "9811823456", 3800, 4400, "2027-09-30", "5% off on all packages", "Affordable & quality care");
                insertHospital(conn, "HOSP036", "Manipal Hospital Old Airport Road", "98, HAL Airport Road", "Bangalore", "Karnataka", "Bengaluru", "560017", "080-25023344", "Dr. H. Sudarshan Ballal", "Chairman", "corporate.blr@manipalhospitals.com", "9845012345", "Shruti Rao", "Corporate Manager", "shruti.r@manipalhospitals.com", "9845123456", 4700, 5400, "2027-12-31", "Priority scheduling", "Leading hospital in Bangalore");
                insertHospital(conn, "HOSP037", "Narayana Health City Bengaluru", "258/A, Bommasandra", "Anekal Taluk", "Karnataka", "Bengaluru", "560099", "080-71222222", "Dr. Devi Prasad Shetty", "Founder", "corporate.hc@narayanahospitals.com", "9845234567", "Mohan Das", "Admin", "mohan.d@narayanahospitals.com", "9845345678", 4200, 4900, "2027-09-30", "Affordable world-class care", "Largest healthcare city in world");
                insertHospital(conn, "HOSP038", "Fortis Hospital Bangalore", "14, Cunningham Road", "Vasanth Nagar", "Karnataka", "Bengaluru", "560052", "080-66214444", "Dr. Ashutosh Raghuvanshi", "MD", "bangalore@fortishealthcare.com", "9845456789", "Deepika Nair", "Corporate Relations", "deepika.n@fortishealthcare.com", "9845567890", 4500, 5200, "2027-12-31", "10% off comprehensive package", "Top-rated in Bangalore");
                insertHospital(conn, "HOSP039", "Apollo Hospital Chennai", "21, Greams Lane", "Off Greams Road", "Tamil Nadu", "Chennai", "600006", "044-28290200", "Dr. Prathap Reddy", "Founder Chairman", "corporate.chennai@apollohospitals.com", "9841012345", "Surekha Krishnan", "Corporate Billing", "surekha.k@apollohospitals.com", "9841123456", 4800, 5500, "2027-12-31", "Comprehensive package discounts", "Flagship hospital of Apollo");
                insertHospital(conn, "HOSP040", "Fortis Malar Hospital", "52, 1st Main Road", "Gandhi Nagar, Adyar", "Tamil Nadu", "Chennai", "600020", "044-42892222", "Dr. A.K. Bansal", "Director", "malar@fortishealthcare.com", "9841234567", "Meenakshi R", "Admin Manager", "meenakshi.r@fortishealthcare.com", "9841345678", 4500, 5100, "2027-09-30", "Free follow-up visit", "Leading in Adyar belt");
                insertHospital(conn, "HOSP041", "Apollo Hospital Hyderabad", "Jubilee Hills", "Road No. 72", "Telangana", "Hyderabad", "500033", "040-23607777", "Dr. K. Hari Prasad", "President - Apollo Hospitals", "corporate.hyd@apollohospitals.com", "9848012345", "Padmaja Rao", "Corporate Manager", "padmaja.r@apollohospitals.com", "9848123456", 4600, 5300, "2027-12-31", "Priority OPD for RITES employees", "Top hospital in Hyderabad");
                insertHospital(conn, "HOSP042", "Yashoda Hospital Somajiguda", "Rajbhavan Road", "Somajiguda", "Telangana", "Hyderabad", "500082", "040-45670000", "Dr. G. Venkateswar Rao", "MD", "corporate@yashodahospitals.com", "9849234567", "Kavitha Reddy", "Corporate Relations", "kavitha.r@yashodahospitals.com", "9849345678", 4300, 5000, "2027-09-30", "10% off diagnostic package", "Well known in twin cities");
                insertHospital(conn, "HOSP043", "KIMS Hospital Secunderabad", "1-8-31/1, Minister Road", "Secunderabad", "Telangana", "Secunderabad", "500003", "040-44885000", "Dr. Bhaskara Rao", "Director", "corporate.sec@kimshospitals.com", "9848567890", "Srinivas Kumar", "Admin", "srinivas.k@kimshospitals.com", "9848678901", 4000, 4700, "2027-12-31", "Free ECG & X-ray with package", "NABH accredited");
            }
            int reqCount = 0;
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ehc_requests");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) reqCount = rs.getInt(1);
            }
            if (reqCount < 10) {
                try (java.sql.Statement st = conn.createStatement()) { 
                    st.executeUpdate("DELETE FROM ehc_request_dependents");
                    st.executeUpdate("DELETE FROM ehc_requests"); 
                }
                String r1 = "{\"empNo\":\"10124\",\"empName\":\"ANJANI UPADHYAY\",\"designation\":\"Manager (IT)\",\"division\":\"INFORMATION TECHNOLOGY\",\"mobile\":\"9876543210\",\"landline\":\"0124-2818000\",\"puHead\":\"S K Sharma\",\"stateName\":\"Haryana\",\"cityName\":\"Gurugram\",\"hospitalName\":\"Medanta - The Medicity\",\"submissionDate\":\"2026-07-01\",\"dependents\":[{\"name\":\"ANJANI UPADHYAY\",\"relation\":\"Self\",\"dob\":\"1975-01-26\",\"gender\":\"Male\"}]}";
                String id1 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r1), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestStatus(id1, "Submitted", "");
                
                String r2 = "{\"empNo\":\"10245\",\"empName\":\"RAHUL KUMAR\",\"designation\":\"Assistant Manager\",\"division\":\"CIVIL ENGINEERING\",\"mobile\":\"9988776655\",\"landline\":\"0124-2818111\",\"puHead\":\"M N Singh\",\"stateName\":\"Delhi\",\"cityName\":\"New Delhi\",\"hospitalName\":\"Indraprastha Apollo Hospital\",\"submissionDate\":\"2026-07-02\",\"dependents\":[{\"name\":\"RAHUL KUMAR\",\"relation\":\"Self\",\"dob\":\"1982-05-15\",\"gender\":\"Male\"},{\"name\":\"PRIYA KUMARI\",\"relation\":\"Spouse\",\"dob\":\"1985-09-12\",\"gender\":\"Female\"}]}";
                String id2 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r2), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestStatus(id2, "HR_APPROVED", "Approved by HR");
                
                String r3 = "{\"empNo\":\"10312\",\"empName\":\"SUNITA VERMA\",\"designation\":\"Deputy General Manager\",\"division\":\"HUMAN RESOURCES\",\"mobile\":\"9811223344\",\"landline\":\"0124-2818222\",\"puHead\":\"Self\",\"stateName\":\"Maharashtra\",\"cityName\":\"Mumbai\",\"hospitalName\":\"Hinduja Hospital\",\"submissionDate\":\"2026-07-03\",\"dependents\":[{\"name\":\"SUNITA VERMA\",\"relation\":\"Self\",\"dob\":\"1970-03-08\",\"gender\":\"Female\"}]}";
                String id3 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r3), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestStatus(id3, "SBU_APPROVED", "SBU Approved");
                
                String r4 = "{\"empNo\":\"10378\",\"empName\":\"ARUN SHARMA\",\"designation\":\"Executive Engineer\",\"division\":\"ELECTRICAL ENGINEERING\",\"mobile\":\"9700112233\",\"landline\":\"0124-2818333\",\"puHead\":\"R K Gupta\",\"stateName\":\"West Bengal\",\"cityName\":\"Kolkata\",\"hospitalName\":\"Apollo Gleneagles Hospital\",\"submissionDate\":\"2026-07-04\",\"dependents\":[{\"name\":\"SURESH SHARMA\",\"relation\":\"Father\",\"dob\":\"1950-06-18\",\"gender\":\"Male\"},{\"name\":\"KAMLA DEVI\",\"relation\":\"Mother\",\"dob\":\"1952-02-25\",\"gender\":\"Female\"}]}";
                String id4 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r4), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestStatus(id4, "HR_REJECTED", "Missing documents");
                
                String r5 = "{\"empNo\":\"10445\",\"empName\":\"PREETI SINGH\",\"designation\":\"Senior Finance Officer\",\"division\":\"FINANCE & ACCOUNTS\",\"mobile\":\"9934556677\",\"landline\":\"0124-2818444\",\"puHead\":\"V P Jain\",\"stateName\":\"Haryana\",\"cityName\":\"Faridabad\",\"hospitalName\":\"Sarvodaya Hospital\",\"submissionDate\":\"2026-07-05\",\"dependents\":[{\"name\":\"PREETI SINGH\",\"relation\":\"Self\",\"dob\":\"1985-12-14\",\"gender\":\"Female\"}]}";
                String id5 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r5), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestBill(id5, "{\"invoiceNumber\":\"INV-1001\",\"invoiceDate\":\"2026-07-06\",\"invoiceAmount\":4500,\"documentUrl\":\"mock_bill.pdf\"}");
                
                String r6 = "{\"empNo\":\"10124\",\"empName\":\"ANJANI UPADHYAY\",\"designation\":\"Manager (IT)\",\"division\":\"INFORMATION TECHNOLOGY\",\"mobile\":\"9876543210\",\"landline\":\"0124-2818000\",\"puHead\":\"S K Sharma\",\"stateName\":\"Haryana\",\"cityName\":\"Gurugram\",\"hospitalName\":\"Fortis Memorial Research Institute\",\"submissionDate\":\"2026-07-06\",\"dependents\":[{\"name\":\"SWETA SHARMA\",\"relation\":\"Spouse\",\"dob\":\"1976-08-28\",\"gender\":\"Female\"}]}";
                String id6 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r6), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestBill(id6, "{\"invoiceNumber\":\"INV-1002\",\"invoiceDate\":\"2026-07-07\",\"invoiceAmount\":5500,\"documentUrl\":\"mock_bill2.pdf\"}");
                com.rites.ehc.JdbcRepository.updateRequestFinanceAction(id6, "Bill Approved", "Looks good");
                
                String r7 = "{\"empNo\":\"10245\",\"empName\":\"RAHUL KUMAR\",\"designation\":\"Assistant Manager\",\"division\":\"CIVIL ENGINEERING\",\"mobile\":\"9988776655\",\"landline\":\"0124-2818111\",\"puHead\":\"M N Singh\",\"stateName\":\"Delhi\",\"cityName\":\"New Delhi\",\"hospitalName\":\"AIIMS Delhi\",\"submissionDate\":\"2026-07-07\",\"dependents\":[{\"name\":\"RAMESH KUMAR\",\"relation\":\"Father\",\"dob\":\"1953-04-10\",\"gender\":\"Male\"}]}";
                String id7 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r7), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestBill(id7, "{\"invoiceNumber\":\"INV-1003\",\"invoiceDate\":\"2026-07-08\",\"invoiceAmount\":3200,\"documentUrl\":\"mock_bill3.pdf\"}");
                com.rites.ehc.JdbcRepository.updateRequestFinanceAction(id7, "Bill Rejected", "Amount mismatch");

                String r8 = "{\"empNo\":\"10312\",\"empName\":\"SUNITA VERMA\",\"designation\":\"Deputy General Manager\",\"division\":\"HUMAN RESOURCES\",\"mobile\":\"9811223344\",\"landline\":\"0124-2818222\",\"puHead\":\"Self\",\"stateName\":\"Maharashtra\",\"cityName\":\"Mumbai\",\"hospitalName\":\"Lilavati Hospital\",\"submissionDate\":\"2026-07-08\",\"dependents\":[{\"name\":\"VIKRAM VERMA\",\"relation\":\"Spouse\",\"dob\":\"1968-07-14\",\"gender\":\"Male\"}]}";
                String id8 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r8), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestBill(id8, "{\"invoiceNumber\":\"INV-1004\",\"invoiceDate\":\"2026-07-09\",\"invoiceAmount\":5600,\"documentUrl\":\"mock_bill4.pdf\"}");
                com.rites.ehc.JdbcRepository.updateRequestFinanceAction(id8, "Bill Approved", "Ok");
                com.rites.ehc.JdbcRepository.updateRequestDisbursement(id8, "{\"paymentRef\":\"PAY-9876543\",\"paymentDate\":\"2026-07-10\",\"disbursedAmount\":5600}");

                String r9 = "{\"empNo\":\"10378\",\"empName\":\"ARUN SHARMA\",\"designation\":\"Executive Engineer\",\"division\":\"ELECTRICAL ENGINEERING\",\"mobile\":\"9700112233\",\"landline\":\"0124-2818333\",\"puHead\":\"R K Gupta\",\"stateName\":\"West Bengal\",\"cityName\":\"Kolkata\",\"hospitalName\":\"AMRI Hospital Salt Lake\",\"submissionDate\":\"2026-07-09\",\"dependents\":[{\"name\":\"ARUN SHARMA\",\"relation\":\"Self\",\"dob\":\"1978-09-22\",\"gender\":\"Male\"}]}";
                String id9 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r9), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestStatus(id9, "SBU_REJECTED", "Not eligible");

                String r10 = "{\"empNo\":\"10445\",\"empName\":\"PREETI SINGH\",\"designation\":\"Senior Finance Officer\",\"division\":\"FINANCE & ACCOUNTS\",\"mobile\":\"9934556677\",\"landline\":\"0124-2818444\",\"puHead\":\"V P Jain\",\"stateName\":\"Uttar Pradesh\",\"cityName\":\"Noida\",\"hospitalName\":\"Fortis Hospital Noida\",\"submissionDate\":\"2026-07-10\",\"dependents\":[{\"name\":\"MOHIT SINGH\",\"relation\":\"Spouse\",\"dob\":\"1983-08-30\",\"gender\":\"Male\"}]}";
                String id10 = com.rites.ehc.JsonUtil.jsonValue(com.rites.ehc.JdbcRepository.createRequest(r10), "ehcId").orElse("");
                com.rites.ehc.JdbcRepository.updateRequestBill(id10, "{\"invoiceNumber\":\"INV-1005\",\"invoiceDate\":\"2026-07-11\",\"invoiceAmount\":4800,\"documentUrl\":\"mock_bill5.pdf\"}");
                com.rites.ehc.JdbcRepository.updateRequestFinanceAction(id10, "Bill Approved", "Approved for disbursement");
                com.rites.ehc.JdbcRepository.updateRequestDisbursement(id10, "{\"paymentRef\":\"PAY-1234567\",\"paymentDate\":\"2026-07-12\",\"disbursedAmount\":4800}");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed base data", e);
        }
    }

    private static boolean hasRows(Connection conn, String table) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 1 FROM " + table);
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    private static void insertCity(Connection conn, String state, String city) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ehc_cities(state_name, city_name) VALUES (?, ?)")) {
            ps.setString(1, state);
            ps.setString(2, city);
            ps.executeUpdate();
        }
    }

    private static void insertEmployee(Connection conn, String empNo, String name, String designation, String division, String mobile, String landline, String dob, String gender) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ehc_employees(emp_no, emp_name, designation, division, mobile, landline, dob, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, empNo);
            ps.setString(2, name);
            ps.setString(3, designation);
            ps.setString(4, division);
            ps.setString(5, mobile);
            ps.setString(6, landline);
            ps.setString(7, dob);
            ps.setString(8, gender);
            ps.executeUpdate();
        }
    }

    private static void insertEmployeeDependent(Connection conn, String empNo, String name, String relation, String dob, String gender) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ehc_employee_dependents(emp_no, dependent_name, relation, dob, gender) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, empNo);
            ps.setString(2, name);
            ps.setString(3, relation);
            ps.setString(4, dob);
            ps.setString(5, gender);
            ps.executeUpdate();
        }
    }

    private static void insertHospital(Connection conn, String vendorCode, String hospitalName, String address1, String address2, String state, String city, String pincode, String phoneL, String contactPerson, String contactDesignation, String contactEmail, String contactM, String altContactPerson, String altContactDesignation, String altContactEmail, String altContactM, double rateMale, double rateFemale, String validUpto, String concessionInfo, String remarks) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ehc_hospitals(vendor_code, hospital_name, address1, address2, state_name, city_name, pincode, phone_l, contact_person, contact_designation, contact_email, contact_m, alt_contact_person, alt_contact_designation, alt_contact_email, alt_contact_m, rate_male, rate_female, valid_upto, concession_info, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, vendorCode);
            ps.setString(2, hospitalName);
            ps.setString(3, address1);
            ps.setString(4, address2);
            ps.setString(5, state);
            ps.setString(6, city);
            ps.setString(7, pincode);
            ps.setString(8, phoneL);
            ps.setString(9, contactPerson);
            ps.setString(10, contactDesignation);
            ps.setString(11, contactEmail);
            ps.setString(12, contactM);
            ps.setString(13, altContactPerson);
            ps.setString(14, altContactDesignation);
            ps.setString(15, altContactEmail);
            ps.setString(16, altContactM);
            ps.setDouble(17, rateMale);
            ps.setDouble(18, rateFemale);
            ps.setString(19, validUpto);
            ps.setString(20, concessionInfo);
            ps.setString(21, remarks);
            ps.executeUpdate();
        }
    }
}





