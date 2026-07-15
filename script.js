const fs = require('fs');

const apiJsContent = fs.readFileSync('frontend/api.js', 'utf8');

// Extract the defaultHospitals array string using regex or simple parsing
const startMarker = 'const defaultHospitals = [';
const endMarker = '];\n\nclass RitesEhcApi';

const startIndex = apiJsContent.indexOf(startMarker);
const endIndex = apiJsContent.indexOf(endMarker, startIndex);

const arrayString = apiJsContent.substring(startIndex + 'const defaultHospitals = '.length, endIndex + 1);

// Parse the array (since it's plain JS, we can eval it)
let hospitals;
eval('hospitals = ' + arrayString);

let javaLines = [];
for (const h of hospitals) {
    const vc = h.vendorCode || '';
    const name = (h.name || '').replace(/"/g, '\\"');
    const a1 = (h.address1 || '').replace(/"/g, '\\"');
    const a2 = (h.address2 || '').replace(/"/g, '\\"');
    const st = h.state || '';
    const ci = h.city || '';
    const pc = h.pincode || '';
    const ph = h.phoneL || '';
    
    const cp = (h.contactPerson || '').replace(/"/g, '\\"');
    const cd = (h.contactDesignation || '').replace(/"/g, '\\"');
    const ce = h.contactEmail || '';
    const cm = h.contactM || '';
    
    const acp = (h.altContactPerson || '').replace(/"/g, '\\"');
    const acd = (h.altContactDesignation || '').replace(/"/g, '\\"');
    const ace = h.altContactEmail || '';
    const acm = h.altContactM || '';
    
    const rm = h.rateMale || '0';
    const rf = h.rateFemale || '0';
    const vu = h.validUpto || '';
    
    const con = (h.concessionInfo || '').replace(/"/g, '\\"');
    const rem = (h.remarks || '').replace(/"/g, '\\"');

    javaLines.push(                insertHospital(conn, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", , , "", "", ""););
}

const seedDataPath = 'backend/src/main/java/com/rites/ehc/SeedData.java';
let seedDataContent = fs.readFileSync(seedDataPath, 'utf8');

const regex = /(if \(!hasRows\(conn, "ehc_hospitals"\)\) \{\n)([\s\S]*?)(\n\s*\})/m;
seedDataContent = seedDataContent.replace(regex, $1);

fs.writeFileSync(seedDataPath, seedDataContent);
console.log("Updated SeedData.java with " + hospitals.length + " hospitals.");
