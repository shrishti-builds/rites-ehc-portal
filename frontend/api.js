// API Client and Mock Data Store for RITES EHC Portal

const CONFIG_KEY    = 'rites_ehc_config';
const HOSPITALS_KEY = 'rites_ehc_hospitals';
const REQUESTS_KEY  = 'rites_ehc_requests';
const CITIES_KEY    = 'rites_ehc_cities';
const DATA_VERSION  = 'rites_ehc_data_v4'; // bump this string to force-refresh seed data

// Default config
const defaultConfig = {
    mode: 'demo', // 'demo' or 'live'
    baseUrl: 'http://localhost:8080/api',
    headers: {}
};

// Initial Mock Employees
const mockEmployees = {
    "10124": {
        empNo: "10124",
        name: "ANJANI UPADHYAY",
        designation: "Manager (IT)",
        division: "INFORMATION TECHNOLOGY",
        mobile: "9876543210",
        landline: "0124-2818000",
        dob: "26-01-1975",
        gender: "Male",
        dependents: [
            { name: "ANJANI UPADHYAY", relation: "Self", dob: "1975-01-26", gender: "Male" },
            { name: "SWETA SHARMA", relation: "Spouse", dob: "1976-08-28", gender: "Female" }
        ]
    },
    "10245": {
        empNo: "10245",
        name: "RAHUL KUMAR",
        designation: "Assistant Manager",
        division: "CIVIL ENGINEERING",
        mobile: "9988776655",
        landline: "0124-2818111",
        dob: "15-05-1982",
        gender: "Male",
        dependents: [
            { name: "RAHUL KUMAR", relation: "Self", dob: "1982-05-15", gender: "Male" },
            { name: "PRIYA KUMARI", relation: "Spouse", dob: "1985-09-12", gender: "Female" },
            { name: "RAMESH KUMAR", relation: "Father", dob: "1953-04-10", gender: "Male" }
        ]
    },
    "10312": {
        empNo: "10312",
        name: "SUNITA VERMA",
        designation: "Deputy General Manager",
        division: "HUMAN RESOURCES",
        mobile: "9811223344",
        landline: "0124-2818222",
        dob: "08-03-1970",
        gender: "Female",
        dependents: [
            { name: "SUNITA VERMA", relation: "Self", dob: "1970-03-08", gender: "Female" },
            { name: "VIKRAM VERMA", relation: "Spouse", dob: "1968-07-14", gender: "Male" },
            { name: "ANANYA VERMA", relation: "Daughter", dob: "2001-11-20", gender: "Female" }
        ]
    },
    "10378": {
        empNo: "10378",
        name: "ARUN SHARMA",
        designation: "Executive Engineer",
        division: "ELECTRICAL ENGINEERING",
        mobile: "9700112233",
        landline: "0124-2818333",
        dob: "22-09-1978",
        gender: "Male",
        dependents: [
            { name: "ARUN SHARMA", relation: "Self", dob: "1978-09-22", gender: "Male" },
            { name: "KAVITA SHARMA", relation: "Spouse", dob: "1980-04-05", gender: "Female" },
            { name: "SURESH SHARMA", relation: "Father", dob: "1950-06-18", gender: "Male" },
            { name: "KAMLA DEVI", relation: "Mother", dob: "1952-02-25", gender: "Female" }
        ]
    },
    "10445": {
        empNo: "10445",
        name: "PREETI SINGH",
        designation: "Senior Finance Officer",
        division: "FINANCE & ACCOUNTS",
        mobile: "9934556677",
        landline: "0124-2818444",
        dob: "14-12-1985",
        gender: "Female",
        dependents: [
            { name: "PREETI SINGH", relation: "Self", dob: "1985-12-14", gender: "Female" },
            { name: "MOHIT SINGH", relation: "Spouse", dob: "1983-08-30", gender: "Male" }
        ]
    }
};

// Initial Mock States & Cities
const defaultStatesCities = [
    { state: "Haryana",     cities: ["Gurugram", "Faridabad", "Panchkula"] },
    { state: "Delhi",       cities: ["New Delhi", "North Delhi", "South Delhi"] },
    { state: "Maharashtra", cities: ["Mumbai", "Pune", "Nagpur"] },
    { state: "West Bengal", cities: ["Kolkata", "Howrah", "Durgapur"] },
    { state: "Uttar Pradesh", cities: ["Lucknow", "Noida", "Agra"] },
    { state: "Karnataka",   cities: ["Bengaluru", "Mysuru", "Mangaluru"] },
    { state: "Tamil Nadu",  cities: ["Chennai", "Coimbatore", "Madurai"] },
    { state: "Telangana",   cities: ["Hyderabad", "Secunderabad", "Warangal"] }
];

// Initial Mock Hospitals  (2-3 per city)
const defaultHospitals = [

    // ── HARYANA : GURUGRAM ──────────────────────────────────────────────────
    {
        vendorCode: "HOSP001", name: "Medanta - The Medicity",
        address1: "Sector 38", address2: "Near NH-8",
        state: "Haryana", city: "Gurugram", pincode: "122001",
        phoneL: "0124-4141414",
        contactPerson: "Dr. Sandeep Dewan", contactDesignation: "Medical Director",
        contactEmail: "contact@medanta.org", contactM: "9999911111",
        altContactPerson: "Sunita Rao", altContactDesignation: "Admin Coordinator",
        altContactEmail: "sunita.r@medanta.org", altContactM: "9888822222",
        rateMale: "4500", rateFemale: "5200", validUpto: "2027-12-31",
        concessionInfo: "10% discount on additional tests",
        remarks: "Preferred hospital for executives"
    },
    {
        vendorCode: "HOSP002", name: "Fortis Memorial Research Institute",
        address1: "Sector 44", address2: "Opposite HUDA City Centre",
        state: "Haryana", city: "Gurugram", pincode: "122002",
        phoneL: "0124-4962200",
        contactPerson: "Dr. Ritu Garg", contactDesignation: "Zonal Director",
        contactEmail: "info@fortis.com", contactM: "9911223344",
        altContactPerson: "Amit Kumar", altContactDesignation: "Operations Head",
        altContactEmail: "amit.k@fortis.com", altContactM: "9822334455",
        rateMale: "4800", rateFemale: "5500", validUpto: "2027-06-30",
        concessionInfo: "Free consultation follow-up",
        remarks: "High quality facilities"
    },
    {
        vendorCode: "HOSP003", name: "Paras Hospital Gurugram",
        address1: "C-1, Sushant Lok", address2: "Phase-1",
        state: "Haryana", city: "Gurugram", pincode: "122002",
        phoneL: "0124-4585555",
        contactPerson: "Dr. Nikhil Chandra", contactDesignation: "CEO",
        contactEmail: "info@parashospitals.com", contactM: "9810101010",
        altContactPerson: "Rohit Negi", altContactDesignation: "Client Relations",
        altContactEmail: "rohit.n@parashospitals.com", altContactM: "9810202020",
        rateMale: "4200", rateFemale: "4900", validUpto: "2027-09-30",
        concessionInfo: "5% concession on lab tests",
        remarks: "ISO certified facilities"
    },

    // ── HARYANA : FARIDABAD ─────────────────────────────────────────────────
    {
        vendorCode: "HOSP004", name: "Asian Institute of Medical Sciences",
        address1: "Plot No.69, Sector 21-A", address2: "Badkal Flyover Road",
        state: "Haryana", city: "Faridabad", pincode: "121001",
        phoneL: "0129-4200000",
        contactPerson: "Dr. Amar Singh", contactDesignation: "Medical Superintendent",
        contactEmail: "info@asianims.com", contactM: "9811234567",
        altContactPerson: "Neha Dhawan", altContactDesignation: "Corporate Coordinator",
        altContactEmail: "neha.d@asianims.com", altContactM: "9812345678",
        rateMale: "3800", rateFemale: "4400", validUpto: "2027-12-31",
        concessionInfo: "Free ECG for corporate clients",
        remarks: "NABH accredited"
    },
    {
        vendorCode: "HOSP005", name: "Sarvodaya Hospital",
        address1: "YMCA Road", address2: "Sector 8",
        state: "Haryana", city: "Faridabad", pincode: "121006",
        phoneL: "0129-4282222",
        contactPerson: "Dr. P.K. Aggarwal", contactDesignation: "Director",
        contactEmail: "corporate@sarvodayahospital.com", contactM: "9899887766",
        altContactPerson: "Sanjay Bhatia", altContactDesignation: "Billing Head",
        altContactEmail: "sanjay.b@sarvodayahospital.com", altContactM: "9898776655",
        rateMale: "3600", rateFemale: "4100", validUpto: "2027-06-30",
        concessionInfo: "15% off on diagnostic packages",
        remarks: "Well-equipped diagnostic centre"
    },
    {
        vendorCode: "HOSP006", name: "Metro Hospital Faridabad",
        address1: "NIT, NH-2", address2: "Sector 12",
        state: "Haryana", city: "Faridabad", pincode: "121007",
        phoneL: "0129-3080100",
        contactPerson: "Dr. Vivek Pathak", contactDesignation: "COO",
        contactEmail: "contact@metrohospitals.com", contactM: "9891234567",
        altContactPerson: "Pooja Gupta", altContactDesignation: "Admin Manager",
        altContactEmail: "pooja.g@metrohospitals.com", altContactM: "9891235678",
        rateMale: "3500", rateFemale: "4000", validUpto: "2027-03-31",
        concessionInfo: "Free blood sugar test",
        remarks: "Good emergency services"
    },

    // ── HARYANA : PANCHKULA ─────────────────────────────────────────────────
    {
        vendorCode: "HOSP007", name: "Alchemist Hospital Panchkula",
        address1: "Sector 21", address2: "Near Sec-21 Bus Stand",
        state: "Haryana", city: "Panchkula", pincode: "134112",
        phoneL: "0172-5088888",
        contactPerson: "Dr. Rohit Anand", contactDesignation: "MD",
        contactEmail: "info@alchemisthospital.com", contactM: "9876512345",
        altContactPerson: "Manpreet Kaur", altContactDesignation: "Corporate Desk",
        altContactEmail: "manpreet.k@alchemisthospital.com", altContactM: "9876623456",
        rateMale: "3900", rateFemale: "4500", validUpto: "2027-12-31",
        concessionInfo: "10% concession on health packages",
        remarks: "Multi-speciality, Tri-city area"
    },
    {
        vendorCode: "HOSP008", name: "Grecian Super Speciality Hospital",
        address1: "Sector 69", address2: "Phase 10, Mohali",
        state: "Haryana", city: "Panchkula", pincode: "160062",
        phoneL: "0172-5088000",
        contactPerson: "Dr. Gurpreet Singh", contactDesignation: "Director",
        contactEmail: "info@grecian.in", contactM: "9914001234",
        altContactPerson: "Harjinder Pal", altContactDesignation: "Operations",
        altContactEmail: "hp@grecian.in", altContactM: "9914002345",
        rateMale: "4000", rateFemale: "4700", validUpto: "2027-09-30",
        concessionInfo: "Free treadmill test included",
        remarks: "Serves Chandigarh tri-city area"
    },

    // ── DELHI : NEW DELHI ───────────────────────────────────────────────────
    {
        vendorCode: "HOSP009", name: "Max Super Speciality Hospital, Saket",
        address1: "1-2, Press Enclave Road", address2: "Saket",
        state: "Delhi", city: "New Delhi", pincode: "110017",
        phoneL: "011-26515050",
        contactPerson: "Dr. Ajay Lall", contactDesignation: "Director - Pulmonology",
        contactEmail: "contact.saket@maxhealthcare.com", contactM: "9876501234",
        altContactPerson: "Rahul Malhotra", altContactDesignation: "Manager Operations",
        altContactEmail: "rahul.m@maxhealthcare.com", altContactM: "9876543210",
        rateMale: "4200", rateFemale: "4900", validUpto: "2027-12-31",
        concessionInfo: "5% discount on pharmacy",
        remarks: "Located in South Delhi"
    },
    {
        vendorCode: "HOSP010", name: "Indraprastha Apollo Hospital",
        address1: "Sarita Vihar", address2: "Delhi-Mathura Road",
        state: "Delhi", city: "New Delhi", pincode: "110076",
        phoneL: "011-26925858",
        contactPerson: "Dr. Anupam Sibal", contactDesignation: "Group Medical Director",
        contactEmail: "apollo.delhi@apollohospitals.com", contactM: "9988770011",
        altContactPerson: "Vikram Shah", altContactDesignation: "Lead Coordinator",
        altContactEmail: "vikram.s@apollohospitals.com", altContactM: "9988223344",
        rateMale: "4600", rateFemale: "5400", validUpto: "2027-10-31",
        concessionInfo: "Free dietitian consult",
        remarks: "Leading multi-specialty"
    },
    {
        vendorCode: "HOSP011", name: "AIIMS Delhi",
        address1: "Ansari Nagar East", address2: "New Delhi",
        state: "Delhi", city: "New Delhi", pincode: "110029",
        phoneL: "011-26588500",
        contactPerson: "Dr. M. Srinivas", contactDesignation: "Director",
        contactEmail: "corporate@aiims.edu", contactM: "9810056789",
        altContactPerson: "Ravi Shankar", altContactDesignation: "Admin Officer",
        altContactEmail: "ravi.s@aiims.edu", altContactM: "9810067890",
        rateMale: "2800", rateFemale: "3200", validUpto: "2028-03-31",
        concessionInfo: "Government rates applicable",
        remarks: "Premier government hospital"
    },

    // ── DELHI : NORTH DELHI ─────────────────────────────────────────────────
    {
        vendorCode: "HOSP012", name: "GTB Hospital (Govt. Corporate Wing)",
        address1: "Dilshad Garden", address2: "Shahdara",
        state: "Delhi", city: "North Delhi", pincode: "110095",
        phoneL: "011-22582222",
        contactPerson: "Dr. Suresh Yadav", contactDesignation: "Medical Director",
        contactEmail: "corporate@gtbhospital.gov.in", contactM: "9971234567",
        altContactPerson: "Anita Sharma", altContactDesignation: "Billing Executive",
        altContactEmail: "anita.s@gtbhospital.gov.in", altContactM: "9971345678",
        rateMale: "2500", rateFemale: "2900", validUpto: "2028-03-31",
        concessionInfo: "Government panel rates",
        remarks: "North Delhi government hospital"
    },
    {
        vendorCode: "HOSP013", name: "Max Hospital Shalimar Bagh",
        address1: "FC-50, Shalimar Bagh", address2: "Near Ring Road",
        state: "Delhi", city: "North Delhi", pincode: "110088",
        phoneL: "011-45055050",
        contactPerson: "Dr. Prashant Sharma", contactDesignation: "Medical Director",
        contactEmail: "shalimarbagh@maxhealthcare.com", contactM: "9810198765",
        altContactPerson: "Vishal Arora", altContactDesignation: "Corporate Desk",
        altContactEmail: "vishal.a@maxhealthcare.com", altContactM: "9810287654",
        rateMale: "4000", rateFemale: "4700", validUpto: "2027-12-31",
        concessionInfo: "10% off on comprehensive package",
        remarks: "Well known in North Delhi"
    },
    {
        vendorCode: "HOSP014", name: "BLK-Max Super Speciality Hospital",
        address1: "5, Pusa Road", address2: "Rajinder Nagar",
        state: "Delhi", city: "North Delhi", pincode: "110005",
        phoneL: "011-30403040",
        contactPerson: "Dr. Deepak Govil", contactDesignation: "Chairman",
        contactEmail: "corporate@blkmax.com", contactM: "9958112233",
        altContactPerson: "Namita Sood", altContactDesignation: "Corporate Relations",
        altContactEmail: "namita.s@blkmax.com", altContactM: "9958223344",
        rateMale: "4400", rateFemale: "5100", validUpto: "2027-09-30",
        concessionInfo: "Free follow-up consultation",
        remarks: "Renowned multi-specialty"
    },

    // ── DELHI : SOUTH DELHI ─────────────────────────────────────────────────
    {
        vendorCode: "HOSP015", name: "Fortis Escorts Heart Institute",
        address1: "Okhla Road", address2: "Near Sukhdev Vihar Metro",
        state: "Delhi", city: "South Delhi", pincode: "110025",
        phoneL: "011-47135000",
        contactPerson: "Dr. Ashok Seth", contactDesignation: "Chairman",
        contactEmail: "fehi@fortishealthcare.com", contactM: "9810345678",
        altContactPerson: "Divya Jain", altContactDesignation: "Corporate Relations",
        altContactEmail: "divya.j@fortishealthcare.com", altContactM: "9810456789",
        rateMale: "5000", rateFemale: "5800", validUpto: "2027-12-31",
        concessionInfo: "Free Holter monitoring",
        remarks: "Best cardiac care in Delhi"
    },
    {
        vendorCode: "HOSP016", name: "Safdarjung Hospital (Corporate Block)",
        address1: "Ansari Nagar West", address2: "Ring Road",
        state: "Delhi", city: "South Delhi", pincode: "110029",
        phoneL: "011-26707444",
        contactPerson: "Dr. Balvinder Kumar", contactDesignation: "Medical Superintendent",
        contactEmail: "corporate@safdarjung.gov.in", contactM: "9868567890",
        altContactPerson: "Rekha Singh", altContactDesignation: "Accounts",
        altContactEmail: "rekha.s@safdarjung.gov.in", altContactM: "9868678901",
        rateMale: "2600", rateFemale: "3000", validUpto: "2028-03-31",
        concessionInfo: "Government empanelled",
        remarks: "Government hospital with corporate ward"
    },

    // ── MAHARASHTRA : MUMBAI ────────────────────────────────────────────────
    {
        vendorCode: "HOSP017", name: "Kokilaben Dhirubhai Ambani Hospital",
        address1: "Rao Saheb Achutrao Patwardhan Marg", address2: "Four Bungalows, Andheri West",
        state: "Maharashtra", city: "Mumbai", pincode: "400053",
        phoneL: "022-30999999",
        contactPerson: "Dr. Santosh Shetty", contactDesignation: "CEO",
        contactEmail: "corporate@kokilabenhospital.com", contactM: "9820012345",
        altContactPerson: "Anjali Desai", altContactDesignation: "Corporate Manager",
        altContactEmail: "anjali.d@kokilabenhospital.com", altContactM: "9820123456",
        rateMale: "5500", rateFemale: "6200", validUpto: "2027-12-31",
        concessionInfo: "Priority appointment for RITES employees",
        remarks: "Premium corporate packages available"
    },
    {
        vendorCode: "HOSP018", name: "Hinduja Hospital",
        address1: "Veer Savarkar Marg", address2: "Mahim",
        state: "Maharashtra", city: "Mumbai", pincode: "400016",
        phoneL: "022-24447000",
        contactPerson: "Dr. Joachim Dsouza", contactDesignation: "Medical Director",
        contactEmail: "corporate@hindujahospital.com", contactM: "9821234567",
        altContactPerson: "Grace Pinto", altContactDesignation: "Corporate Billing",
        altContactEmail: "grace.p@hindujahospital.com", altContactM: "9821345678",
        rateMale: "5200", rateFemale: "5900", validUpto: "2027-09-30",
        concessionInfo: "Cashless facility",
        remarks: "Well established hospital"
    },
    {
        vendorCode: "HOSP019", name: "Lilavati Hospital",
        address1: "A-791, Bandra Reclamation", address2: "Bandra West",
        state: "Maharashtra", city: "Mumbai", pincode: "400050",
        phoneL: "022-26751000",
        contactPerson: "Dr. Shashank Joshi", contactDesignation: "Director",
        contactEmail: "corporate@lilavatihospital.com", contactM: "9819012345",
        altContactPerson: "Sneha Nair", altContactDesignation: "Admin Head",
        altContactEmail: "sneha.n@lilavatihospital.com", altContactM: "9819123456",
        rateMale: "4900", rateFemale: "5600", validUpto: "2027-12-31",
        concessionInfo: "Free nutritionist session",
        remarks: "Trusted by Mumbai corporates"
    },

    // ── MAHARASHTRA : PUNE ──────────────────────────────────────────────────
    {
        vendorCode: "HOSP020", name: "Ruby Hall Clinic",
        address1: "40, Sassoon Road", address2: "Camp",
        state: "Maharashtra", city: "Pune", pincode: "411001",
        phoneL: "020-66455100",
        contactPerson: "Dr. Bomi Bhote", contactDesignation: "CEO",
        contactEmail: "corporate@rubyhall.com", contactM: "9822023456",
        altContactPerson: "Nilima Kale", altContactDesignation: "Corporate Desk",
        altContactEmail: "nilima.k@rubyhall.com", altContactM: "9822134567",
        rateMale: "4300", rateFemale: "5000", validUpto: "2027-12-31",
        concessionInfo: "Free X-ray with package",
        remarks: "Most preferred in Pune"
    },
    {
        vendorCode: "HOSP021", name: "Jehangir Hospital",
        address1: "32, Sassoon Road", address2: "Opposite Pune Station",
        state: "Maharashtra", city: "Pune", pincode: "411001",
        phoneL: "020-66810000",
        contactPerson: "Dr. Lancelot Pinto", contactDesignation: "Medical Director",
        contactEmail: "corporate@jehangirhospital.com", contactM: "9822245678",
        altContactPerson: "Priya Karandikar", altContactDesignation: "Operations",
        altContactEmail: "priya.k@jehangirhospital.com", altContactM: "9822356789",
        rateMale: "4100", rateFemale: "4800", validUpto: "2027-09-30",
        concessionInfo: "10% off on annual checkup",
        remarks: "Historic hospital with modern facilities"
    },
    {
        vendorCode: "HOSP022", name: "Sahyadri Hospital Pune",
        address1: "30-C, Erandwane", address2: "Karve Road",
        state: "Maharashtra", city: "Pune", pincode: "411004",
        phoneL: "020-67210000",
        contactPerson: "Dr. Charudutt Apte", contactDesignation: "Chairman",
        contactEmail: "corporate@sahyadrihospital.com", contactM: "9823456789",
        altContactPerson: "Swapnil Deshpande", altContactDesignation: "Billing",
        altContactEmail: "swapnil.d@sahyadrihospital.com", altContactM: "9823567890",
        rateMale: "4000", rateFemale: "4600", validUpto: "2027-12-31",
        concessionInfo: "Cashless & priority service",
        remarks: "Expanding chain across Maharashtra"
    },

    // ── MAHARASHTRA : NAGPUR ────────────────────────────────────────────────
    {
        vendorCode: "HOSP023", name: "KIMS Kingsway Hospital",
        address1: "44, Kingsway", address2: "Nagpur",
        state: "Maharashtra", city: "Nagpur", pincode: "440001",
        phoneL: "0712-6600500",
        contactPerson: "Dr. Ravi Wankhede", contactDesignation: "Director",
        contactEmail: "corporate@kimshospital.com", contactM: "9890112233",
        altContactPerson: "Sonal Bhatt", altContactDesignation: "Corporate Billing",
        altContactEmail: "sonal.b@kimshospital.com", altContactM: "9890223344",
        rateMale: "3700", rateFemale: "4300", validUpto: "2027-12-31",
        concessionInfo: "Free cardiac screening",
        remarks: "Central India's leading hospital"
    },
    {
        vendorCode: "HOSP024", name: "Alexis Hospital",
        address1: "Mankapur Square", address2: "Ring Road",
        state: "Maharashtra", city: "Nagpur", pincode: "440015",
        phoneL: "0712-3028888",
        contactPerson: "Dr. Swapnil Trivedi", contactDesignation: "MD",
        contactEmail: "contact@alexismultispeciality.com", contactM: "9881234567",
        altContactPerson: "Manisha Joshi", altContactDesignation: "Admin",
        altContactEmail: "manisha.j@alexismultispeciality.com", altContactM: "9881345678",
        rateMale: "3500", rateFemale: "4100", validUpto: "2027-09-30",
        concessionInfo: "Free vision test",
        remarks: "Growing in Nagpur region"
    },

    // ── WEST BENGAL : KOLKATA ───────────────────────────────────────────────
    {
        vendorCode: "HOSP025", name: "Apollo Gleneagles Hospital",
        address1: "58, Canal Circular Road", address2: "Kadapara",
        state: "West Bengal", city: "Kolkata", pincode: "700054",
        phoneL: "033-23202122",
        contactPerson: "Dr. Sabyasachi Bal", contactDesignation: "Director",
        contactEmail: "corporate.kolkata@apollohospitals.com", contactM: "9830123456",
        altContactPerson: "Subrata Das", altContactDesignation: "Corporate Manager",
        altContactEmail: "subrata.d@apollohospitals.com", altContactM: "9830234567",
        rateMale: "4400", rateFemale: "5100", validUpto: "2027-12-31",
        concessionInfo: "Priority queue for RITES employees",
        remarks: "Premier hospital in East India"
    },
    {
        vendorCode: "HOSP026", name: "AMRI Hospital Salt Lake",
        address1: "JC-16&17, Sector III", address2: "Salt Lake",
        state: "West Bengal", city: "Kolkata", pincode: "700098",
        phoneL: "033-66000000",
        contactPerson: "Dr. Rupak Barua", contactDesignation: "Group CEO",
        contactEmail: "corporate@amrihospitals.in", contactM: "9831234567",
        altContactPerson: "Debjani Mitra", altContactDesignation: "Corporate Relations",
        altContactEmail: "debjani.m@amrihospitals.in", altContactM: "9831345678",
        rateMale: "4100", rateFemale: "4700", validUpto: "2027-09-30",
        concessionInfo: "10% on comprehensive packages",
        remarks: "NABH accredited"
    },
    {
        vendorCode: "HOSP027", name: "Fortis Hospital Anandapur",
        address1: "730, Anandapur", address2: "EM Bypass",
        state: "West Bengal", city: "Kolkata", pincode: "700107",
        phoneL: "033-66284444",
        contactPerson: "Dr. Subhash Datta", contactDesignation: "Facility Director",
        contactEmail: "kolkata@fortishealthcare.com", contactM: "9832345678",
        altContactPerson: "Aruna Sinha", altContactDesignation: "Billing Manager",
        altContactEmail: "aruna.s@fortishealthcare.com", altContactM: "9832456789",
        rateMale: "4300", rateFemale: "5000", validUpto: "2027-12-31",
        concessionInfo: "Cashless facility available",
        remarks: "Modern multi-specialty"
    },

    // ── WEST BENGAL : HOWRAH ────────────────────────────────────────────────
    {
        vendorCode: "HOSP028", name: "Narayana Multispeciality Hospital Howrah",
        address1: "78, Jessore Road South", address2: "Kolkata",
        state: "West Bengal", city: "Howrah", pincode: "700074",
        phoneL: "033-40506050",
        contactPerson: "Dr. Devi Shetty", contactDesignation: "Chairman",
        contactEmail: "howrah@narayanahospitals.com", contactM: "9800111222",
        altContactPerson: "Abhijit Roy", altContactDesignation: "Operations Head",
        altContactEmail: "abhijit.r@narayanahospitals.com", altContactM: "9800222333",
        rateMale: "3900", rateFemale: "4500", validUpto: "2027-12-31",
        concessionInfo: "Free cardiac consultation",
        remarks: "Affordable super-speciality"
    },
    {
        vendorCode: "HOSP029", name: "RN Tagore International Institute",
        address1: "124, EM Bypass", address2: "Mukundapur",
        state: "West Bengal", city: "Howrah", pincode: "700099",
        phoneL: "033-66000000",
        contactPerson: "Dr. K.M. Mandana", contactDesignation: "Director",
        contactEmail: "corporate@rtiics.org", contactM: "9801122334",
        altContactPerson: "Priyanka Bose", altContactDesignation: "Admin",
        altContactEmail: "priyanka.b@rtiics.org", altContactM: "9801233445",
        rateMale: "4200", rateFemale: "4900", validUpto: "2027-09-30",
        concessionInfo: "Free consultation with referral",
        remarks: "Specialised cardiac care"
    },

    // ── WEST BENGAL : DURGAPUR ──────────────────────────────────────────────
    {
        vendorCode: "HOSP030", name: "Mission Hospital Durgapur",
        address1: "219, PO: Dishergarh", address2: "Dist. Paschim Bardhaman",
        state: "West Bengal", city: "Durgapur", pincode: "713305",
        phoneL: "0343-2500100",
        contactPerson: "Dr. Soumitra Ghosh", contactDesignation: "Medical Director",
        contactEmail: "corporate@missionhospital.org", contactM: "9832567890",
        altContactPerson: "Tuhin Majumdar", altContactDesignation: "Admin Officer",
        altContactEmail: "tuhin.m@missionhospital.org", altContactM: "9832678901",
        rateMale: "3400", rateFemale: "4000", validUpto: "2027-12-31",
        concessionInfo: "10% off on RITES employees",
        remarks: "Trusted in industrial belt"
    },
    {
        vendorCode: "HOSP031", name: "ILS Hospital Durgapur",
        address1: "Nazrul Sarani, City Centre", address2: "Durgapur",
        state: "West Bengal", city: "Durgapur", pincode: "713216",
        phoneL: "0343-6600300",
        contactPerson: "Dr. Arindam Kar", contactDesignation: "Director",
        contactEmail: "durgapur@ilshospitals.com", contactM: "9832789012",
        altContactPerson: "Sukanta Paul", altContactDesignation: "Billing",
        altContactEmail: "sukanta.p@ilshospitals.com", altContactM: "9832890123",
        rateMale: "3200", rateFemale: "3800", validUpto: "2027-09-30",
        concessionInfo: "Free blood group test",
        remarks: "Good outpatient services"
    },

    // ── UTTAR PRADESH : LUCKNOW ─────────────────────────────────────────────
    {
        vendorCode: "HOSP032", name: "Medanta Hospital Lucknow",
        address1: "Sector A, Pocket 1", address2: "Amar Shaheed Path, Sushant Golf City",
        state: "Uttar Pradesh", city: "Lucknow", pincode: "226030",
        phoneL: "0522-4506000",
        contactPerson: "Dr. Rakesh Kapoor", contactDesignation: "Director",
        contactEmail: "lucknow@medanta.org", contactM: "9415001234",
        altContactPerson: "Prerna Gupta", altContactDesignation: "Corporate Relations",
        altContactEmail: "prerna.g@medanta.org", altContactM: "9415112345",
        rateMale: "4000", rateFemale: "4700", validUpto: "2027-12-31",
        concessionInfo: "Priority booking for RITES employees",
        remarks: "State-of-the-art facilities"
    },
    {
        vendorCode: "HOSP033", name: "Sahara Hospital Lucknow",
        address1: "Viraj Khand", address2: "Gomti Nagar",
        state: "Uttar Pradesh", city: "Lucknow", pincode: "226010",
        phoneL: "0522-6780000",
        contactPerson: "Dr. Anurag Tandon", contactDesignation: "MD",
        contactEmail: "corporate@saharahospital.com", contactM: "9415223456",
        altContactPerson: "Alka Shukla", altContactDesignation: "Admin",
        altContactEmail: "alka.s@saharahospital.com", altContactM: "9415334567",
        rateMale: "3700", rateFemale: "4300", validUpto: "2027-09-30",
        concessionInfo: "15% discount on packages",
        remarks: "Well known in Gomti Nagar area"
    },

    // ── UTTAR PRADESH : NOIDA ───────────────────────────────────────────────
    {
        vendorCode: "HOSP034", name: "Fortis Hospital Noida",
        address1: "B-22, Sector 62", address2: "Noida",
        state: "Uttar Pradesh", city: "Noida", pincode: "201301",
        phoneL: "0120-4677777",
        contactPerson: "Dr. Deepak Gupta", contactDesignation: "Facility Director",
        contactEmail: "noida@fortishealthcare.com", contactM: "9810512345",
        altContactPerson: "Kavita Verma", altContactDesignation: "Corporate Desk",
        altContactEmail: "kavita.v@fortishealthcare.com", altContactM: "9810623456",
        rateMale: "4100", rateFemale: "4800", validUpto: "2027-12-31",
        concessionInfo: "Cashless claim support",
        remarks: "Premium facility in NCR"
    },
    {
        vendorCode: "HOSP035", name: "Kailash Hospital Noida",
        address1: "Sector 27", address2: "Near Noida City Centre",
        state: "Uttar Pradesh", city: "Noida", pincode: "201301",
        phoneL: "0120-4444000",
        contactPerson: "Dr. H.S. Bhatia", contactDesignation: "Director",
        contactEmail: "corporate@kailashhealthcare.com", contactM: "9811712345",
        altContactPerson: "Geeta Rawat", altContactDesignation: "Billing",
        altContactEmail: "geeta.r@kailashhealthcare.com", altContactM: "9811823456",
        rateMale: "3800", rateFemale: "4400", validUpto: "2027-09-30",
        concessionInfo: "5% off on all packages",
        remarks: "Affordable & quality care"
    },

    // ── KARNATAKA : BENGALURU ───────────────────────────────────────────────
    {
        vendorCode: "HOSP036", name: "Manipal Hospital Old Airport Road",
        address1: "98, HAL Airport Road", address2: "Bangalore",
        state: "Karnataka", city: "Bengaluru", pincode: "560017",
        phoneL: "080-25023344",
        contactPerson: "Dr. H. Sudarshan Ballal", contactDesignation: "Chairman",
        contactEmail: "corporate.blr@manipalhospitals.com", contactM: "9845012345",
        altContactPerson: "Shruti Rao", altContactDesignation: "Corporate Manager",
        altContactEmail: "shruti.r@manipalhospitals.com", altContactM: "9845123456",
        rateMale: "4700", rateFemale: "5400", validUpto: "2027-12-31",
        concessionInfo: "Priority scheduling",
        remarks: "Leading hospital in Bangalore"
    },
    {
        vendorCode: "HOSP037", name: "Narayana Health City Bengaluru",
        address1: "258/A, Bommasandra", address2: "Anekal Taluk",
        state: "Karnataka", city: "Bengaluru", pincode: "560099",
        phoneL: "080-71222222",
        contactPerson: "Dr. Devi Prasad Shetty", contactDesignation: "Founder",
        contactEmail: "corporate.hc@narayanahospitals.com", contactM: "9845234567",
        altContactPerson: "Mohan Das", altContactDesignation: "Admin",
        altContactEmail: "mohan.d@narayanahospitals.com", altContactM: "9845345678",
        rateMale: "4200", rateFemale: "4900", validUpto: "2027-09-30",
        concessionInfo: "Affordable world-class care",
        remarks: "Largest healthcare city in world"
    },
    {
        vendorCode: "HOSP038", name: "Fortis Hospital Bangalore",
        address1: "14, Cunningham Road", address2: "Vasanth Nagar",
        state: "Karnataka", city: "Bengaluru", pincode: "560052",
        phoneL: "080-66214444",
        contactPerson: "Dr. Ashutosh Raghuvanshi", contactDesignation: "MD",
        contactEmail: "bangalore@fortishealthcare.com", contactM: "9845456789",
        altContactPerson: "Deepika Nair", altContactDesignation: "Corporate Relations",
        altContactEmail: "deepika.n@fortishealthcare.com", altContactM: "9845567890",
        rateMale: "4500", rateFemale: "5200", validUpto: "2027-12-31",
        concessionInfo: "10% off comprehensive package",
        remarks: "Top-rated in Bangalore"
    },

    // ── TAMIL NADU : CHENNAI ────────────────────────────────────────────────
    {
        vendorCode: "HOSP039", name: "Apollo Hospital Chennai",
        address1: "21, Greams Lane", address2: "Off Greams Road",
        state: "Tamil Nadu", city: "Chennai", pincode: "600006",
        phoneL: "044-28290200",
        contactPerson: "Dr. Prathap Reddy", contactDesignation: "Founder Chairman",
        contactEmail: "corporate.chennai@apollohospitals.com", contactM: "9841012345",
        altContactPerson: "Surekha Krishnan", altContactDesignation: "Corporate Billing",
        altContactEmail: "surekha.k@apollohospitals.com", altContactM: "9841123456",
        rateMale: "4800", rateFemale: "5500", validUpto: "2027-12-31",
        concessionInfo: "Comprehensive package discounts",
        remarks: "Flagship hospital of Apollo"
    },
    {
        vendorCode: "HOSP040", name: "Fortis Malar Hospital",
        address1: "52, 1st Main Road", address2: "Gandhi Nagar, Adyar",
        state: "Tamil Nadu", city: "Chennai", pincode: "600020",
        phoneL: "044-42892222",
        contactPerson: "Dr. A.K. Bansal", contactDesignation: "Director",
        contactEmail: "malar@fortishealthcare.com", contactM: "9841234567",
        altContactPerson: "Meenakshi R", altContactDesignation: "Admin Manager",
        altContactEmail: "meenakshi.r@fortishealthcare.com", altContactM: "9841345678",
        rateMale: "4500", rateFemale: "5100", validUpto: "2027-09-30",
        concessionInfo: "Free follow-up visit",
        remarks: "Leading in Adyar belt"
    },

    // ── TELANGANA : HYDERABAD ────────────────────────────────────────────────
    {
        vendorCode: "HOSP041", name: "Apollo Hospital Hyderabad",
        address1: "Jubilee Hills", address2: "Road No. 72",
        state: "Telangana", city: "Hyderabad", pincode: "500033",
        phoneL: "040-23607777",
        contactPerson: "Dr. K. Hari Prasad", contactDesignation: "President - Apollo Hospitals",
        contactEmail: "corporate.hyd@apollohospitals.com", contactM: "9848012345",
        altContactPerson: "Padmaja Rao", altContactDesignation: "Corporate Manager",
        altContactEmail: "padmaja.r@apollohospitals.com", altContactM: "9848123456",
        rateMale: "4600", rateFemale: "5300", validUpto: "2027-12-31",
        concessionInfo: "Priority OPD for RITES employees",
        remarks: "Top hospital in Hyderabad"
    },
    {
        vendorCode: "HOSP042", name: "Yashoda Hospital Somajiguda",
        address1: "Rajbhavan Road", address2: "Somajiguda",
        state: "Telangana", city: "Hyderabad", pincode: "500082",
        phoneL: "040-45670000",
        contactPerson: "Dr. G. Venkateswar Rao", contactDesignation: "MD",
        contactEmail: "corporate@yashodahospitals.com", contactM: "9849234567",
        altContactPerson: "Kavitha Reddy", altContactDesignation: "Corporate Relations",
        altContactEmail: "kavitha.r@yashodahospitals.com", altContactM: "9849345678",
        rateMale: "4300", rateFemale: "5000", validUpto: "2027-09-30",
        concessionInfo: "10% off diagnostic package",
        remarks: "Well known in twin cities"
    },
    {
        vendorCode: "HOSP043", name: "KIMS Hospital Secunderabad",
        address1: "1-8-31/1, Minister Road", address2: "Secunderabad",
        state: "Telangana", city: "Secunderabad", pincode: "500003",
        phoneL: "040-44885000",
        contactPerson: "Dr. Bhaskara Rao", contactDesignation: "Director",
        contactEmail: "corporate.sec@kimshospitals.com", contactM: "9848567890",
        altContactPerson: "Srinivas Kumar", altContactDesignation: "Admin",
        altContactEmail: "srinivas.k@kimshospitals.com", altContactM: "9848678901",
        rateMale: "4000", rateFemale: "4700", validUpto: "2027-12-31",
        concessionInfo: "Free ECG & X-ray with package",
        remarks: "NABH accredited"
    }
];

class RitesEhcApi {
    constructor() {
        this.initStorage();
    }

    initStorage() {
        // Reset seed data if the data version has changed
        const isNewVersion = localStorage.getItem(DATA_VERSION) !== 'true';
        if (isNewVersion) {
            localStorage.setItem(HOSPITALS_KEY, JSON.stringify(defaultHospitals));
            localStorage.setItem(CITIES_KEY, JSON.stringify(defaultStatesCities));
            localStorage.setItem(DATA_VERSION, 'true');
            // Do NOT clear requests or config — preserve user's existing workflow data
        }

        if (!localStorage.getItem(CONFIG_KEY)) {
            localStorage.setItem(CONFIG_KEY, JSON.stringify(defaultConfig));
        }
        if (!localStorage.getItem(REQUESTS_KEY)) {
            localStorage.setItem(REQUESTS_KEY, JSON.stringify([]));
        }
    }

    getConfig() {
        return JSON.parse(localStorage.getItem(CONFIG_KEY)) || defaultConfig;
    }

    updateConfig(newConfig) {
        localStorage.setItem(CONFIG_KEY, JSON.stringify(newConfig));
    }

    // Cities & States
    async getStatesAndCities() {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/cities`, { headers: config.headers });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }
        return JSON.parse(localStorage.getItem(CITIES_KEY));
    }

    async addCity(stateName, cityName) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/cities`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify({ state: stateName, city: cityName })
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const citiesData = JSON.parse(localStorage.getItem(CITIES_KEY)) || [];
        const stateObj = citiesData.find(item => item.state === stateName);
        if (stateObj) {
            if (!stateObj.cities.includes(cityName)) {
                stateObj.cities.push(cityName);
            }
        } else {
            citiesData.push({ state: stateName, cities: [cityName] });
        }
        localStorage.setItem(CITIES_KEY, JSON.stringify(citiesData));
        return { success: true, message: "City added successfully" };
    }

    // Hospitals
    async getHospitals() {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/hospitals`, { headers: config.headers });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }
        return JSON.parse(localStorage.getItem(HOSPITALS_KEY));
    }

    async addHospital(hospital) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/hospitals`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify(hospital)
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const hospitals = JSON.parse(localStorage.getItem(HOSPITALS_KEY));
        hospitals.push(hospital);
        localStorage.setItem(HOSPITALS_KEY, JSON.stringify(hospitals));
        return { success: true, message: "Hospital added successfully", data: hospital };
    }

    // Employees
    async getEmployee(empNo) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/employees/${empNo}`, { headers: config.headers });
                if (response.ok) return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }
        return mockEmployees[empNo] || null;
    }

    // EHC Requests
    async getRequests() {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests`, { headers: config.headers });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }
        return JSON.parse(localStorage.getItem(REQUESTS_KEY));
    }

    async submitRequest(request) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify(request)
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const newRequest = {
            ...request,
            ehcId: `EHC-${Date.now().toString().slice(-6)}`,
            status: 'Pending SBU',
            remarks: '',
            submissionDate: new Date().toLocaleDateString('en-GB')
        };
        requests.push(newRequest);
        localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
        return { success: true, message: "Health checkup request submitted successfully", data: newRequest };
    }

    async updateRequestStatus(ehcId, status, remarks) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests/${ehcId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify({ status, remarks })
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const index = requests.findIndex(r => r.ehcId === ehcId);
        if (index !== -1) {
            requests[index].status = status;
            requests[index].remarks = remarks;
            localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
            return { success: true, message: `Request status updated to ${status}`, data: requests[index] };
        }
        return { success: false, message: "Request not found" };
    }

    async updateHospitalRates(vendorCode, rateMale, rateFemale) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/hospitals/${vendorCode}/rates`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify({ rateMale, rateFemale })
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const hospitals = JSON.parse(localStorage.getItem(HOSPITALS_KEY));
        const index = hospitals.findIndex(h => h.vendorCode === vendorCode);
        if (index !== -1) {
            hospitals[index].rateMale = rateMale.toString();
            hospitals[index].rateFemale = rateFemale.toString();
            localStorage.setItem(HOSPITALS_KEY, JSON.stringify(hospitals));
            return { success: true, message: "Hospital rates updated successfully" };
        }
        return { success: false, message: "Hospital not found" };
    }

    async uploadRequestBill(ehcId, billDetails) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests/${ehcId}/bill`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify(billDetails)
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const index = requests.findIndex(r => r.ehcId === ehcId);
        if (index !== -1) {
            requests[index].status = 'Bill Uploaded';
            requests[index].billDetails = billDetails;
            localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
            return { success: true, message: "Bill uploaded successfully", data: requests[index] };
        }
        return { success: false, message: "Request not found" };
    }

    async approveRequestBill(ehcId, financeRemarks) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests/${ehcId}/approve-bill`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify({ financeRemarks })
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const index = requests.findIndex(r => r.ehcId === ehcId);
        if (index !== -1) {
            requests[index].status = 'Bill Approved';
            requests[index].financeRemarks = financeRemarks;
            localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
            return { success: true, message: "Bill approved by finance", data: requests[index] };
        }
        return { success: false, message: "Request not found" };
    }

    async rejectRequestBill(ehcId, financeRemarks) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests/${ehcId}/reject-bill`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify({ financeRemarks })
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const index = requests.findIndex(r => r.ehcId === ehcId);
        if (index !== -1) {
            requests[index].status = 'Bill Rejected';
            requests[index].financeRemarks = financeRemarks;
            localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
            return { success: true, message: "Bill rejected by finance", data: requests[index] };
        }
        return { success: false, message: "Request not found" };
    }

    async disburseRequest(ehcId, disbursementDetails) {
        const config = this.getConfig();
        if (config.mode === 'live') {
            try {
                const response = await fetch(`${config.baseUrl}/requests/${ehcId}/disburse`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', ...config.headers },
                    body: JSON.stringify(disbursementDetails)
                });
                return await response.json();
            } catch (e) {
                console.error("Live API Error, falling back to Demo:", e);
            }
        }

        const requests = JSON.parse(localStorage.getItem(REQUESTS_KEY));
        const index = requests.findIndex(r => r.ehcId === ehcId);
        if (index !== -1) {
            requests[index].status = 'Disbursed';
            requests[index].disbursementDetails = disbursementDetails;
            localStorage.setItem(REQUESTS_KEY, JSON.stringify(requests));
            return { success: true, message: "Disbursement completed successfully", data: requests[index] };
        }
        return { success: false, message: "Request not found" };
    }
}

const api = new RitesEhcApi();
window.api = api; // Expose globally for app.js
