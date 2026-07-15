// RITES EHC Portal UI Controller Logic

document.addEventListener("DOMContentLoaded", () => {
    // Current state
    let activeEmployee = null;
    let selectedRequests = []; // Tracks selected EHC request IDs in HR view
    let hrRequests = [];       // Cache of requests in HR view
    let hrPage = 1;
    let hrPageSize = 10;
    
    // DOM Elements
    const views = document.querySelectorAll(".view-section");
    const navItems = document.querySelectorAll(".nav-item, .nav-sub-item");
    const modeBadge = document.getElementById("mode-badge");
    const scrollBtn = document.getElementById("scroll-to-top");
    
    // Initialize
    initApp();

    async function initApp() {
        updateModeBadge();
        setupNavigation();
        setupDropdowns();
        setupValidationListeners();
        await loadEmployeeData("10124"); // Default employee in screenshot
        await refreshDashboard();
        await refreshHospitalsDirectory();
        await refreshHrRequests();
        setupHrTableEvents();
        setupSettingsEvents();
        setupNewViewsEvents();
        setupScrollTop();
    }

    // Badge showing Demo vs Live Mode
    function updateModeBadge() {
        const config = window.api.getConfig();
        modeBadge.className = `config-badge ${config.mode}`;
        modeBadge.textContent = `${config.mode} Mode`;
    }

    // 1. Sidebar navigation & Page routing
    function setupNavigation() {
        // Global navigation triggers
        document.body.addEventListener("click", (e) => {
            const targetTrigger = e.target.closest("[data-target]");
            if (!targetTrigger) return;
            
            e.preventDefault();
            const targetViewId = targetTrigger.getAttribute("data-target");
            
            // Switch views
            switchView(targetViewId);

            // Highlight sidebar item if it belongs to sidebar
            const sidebarItem = targetTrigger.closest(".nav-item, .nav-sub-item");
            if (sidebarItem) {
                navItems.forEach(item => item.classList.remove("active"));
                sidebarItem.classList.add("active");
            }
        });
    }

    function switchView(viewId) {
        views.forEach(view => {
            if (view.id === viewId) {
                view.classList.add("active");
            } else {
                view.classList.remove("active");
            }
        });

        // Trigger updates on switching views
        if (viewId === "dashboard-view") {
            refreshDashboard();
        } else if (viewId === "update-hospital-view") {
            refreshHospitalsDirectory();
        } else if (viewId === "hr-approve-view") {
            refreshHrRequests();
        } else if (viewId === "city-master-view") {
            refreshCityMaster();
        } else if (viewId === "update-rates-view") {
            refreshUpdateRatesView();
        } else if (viewId === "sbu-approve-view") {
            refreshSbuRequests();
        } else if (viewId === "hr-issue-view") {
            refreshHrIssueLettersView();
        } else if (viewId === "hr-upload-view") {
            refreshHrUploadBillsView();
        } else if (viewId === "finance-bill-view") {
            refreshFinanceBillsView();
        } else if (viewId === "finance-prepare-view") {
            refreshFinanceDisbursementsView();
        }
    }

    // 2. Dropdown lists binding
    async function setupDropdowns() {
        const statesData = await window.api.getStatesAndCities();
        
        // Target dropdowns
        const hospitalStateSelect = document.getElementById("hospital-state-select");
        const hospitalCitySelect = document.getElementById("hospital-city-select");
        const reqStateSelect = document.getElementById("req-state-select");
        const reqCitySelect = document.getElementById("req-city-select");
        const reqHospitalSelect = document.getElementById("req-hospital-select");

        // Clear and populate states
        [hospitalStateSelect, reqStateSelect].forEach(select => {
            if (!select) return;
            select.innerHTML = '<option value="">--Select Value--</option>';
            statesData.forEach(item => {
                const opt = document.createElement("option");
                opt.value = item.state;
                opt.textContent = item.state;
                select.appendChild(opt);
            });
        });

        // State change listener for Add Hospital
        if (hospitalStateSelect && hospitalCitySelect) {
            hospitalStateSelect.addEventListener("change", () => {
                const stateVal = hospitalStateSelect.value;
                hospitalCitySelect.innerHTML = '<option value="">--Select Value--</option>';
                if (!stateVal) return;
                
                const found = statesData.find(item => item.state === stateVal);
                if (found) {
                    found.cities.forEach(city => {
                        const opt = document.createElement("option");
                        opt.value = city;
                        opt.textContent = city;
                        hospitalCitySelect.appendChild(opt);
                    });
                }
            });
        }

        // State and City change listener for Health Checkup Request
        if (reqStateSelect && reqCitySelect && reqHospitalSelect) {
            reqStateSelect.addEventListener("change", updateRequestCities);
            reqCitySelect.addEventListener("change", updateRequestHospitals);
        }

        async function updateRequestCities() {
            const stateVal = reqStateSelect.value;
            reqCitySelect.innerHTML = '<option value="">--Select Value--</option>';
            reqHospitalSelect.innerHTML = '<option value="">--Select Value--</option>';
            if (!stateVal) return;

            const found = statesData.find(item => item.state === stateVal);
            if (found) {
                found.cities.forEach(city => {
                    const opt = document.createElement("option");
                    opt.value = city;
                    opt.textContent = city;
                    reqCitySelect.appendChild(opt);
                });
            }
        }

        async function updateRequestHospitals() {
            const stateVal = reqStateSelect.value;
            const cityVal = reqCitySelect.value;
            reqHospitalSelect.innerHTML = '<option value="">--Select Value--</option>';
            if (!stateVal || !cityVal) return;

            const hospitals = await window.api.getHospitals();
            // Filter by selected state and city
            const filtered = hospitals.filter(h => h.state === stateVal && h.city === cityVal);
            
            filtered.forEach(h => {
                const opt = document.createElement("option");
                opt.value = h.name;
                opt.textContent = h.name;
                reqHospitalSelect.appendChild(opt);
            });
        }
        
        // Dynamic loading & typing for Employee health checkup request
        const reqEmpNoInput = document.getElementById("req-emp-no");
        if (reqEmpNoInput) {
            reqEmpNoInput.addEventListener("input", async () => {
                const empNo = reqEmpNoInput.value.trim();
                const emp = await window.api.getEmployee(empNo);
                if (emp) {
                    activeEmployee = emp;
                    document.getElementById("req-emp-name").value = emp.name;
                    document.getElementById("req-designation").value = emp.designation;
                    document.getElementById("req-division").value = emp.division;
                    document.getElementById("req-mobile").value = emp.mobile || "";
                    document.getElementById("req-landline").value = emp.landline || "";
                    await loadEmployeeData(empNo);
                } else {
                    activeEmployee = {
                        empNo: empNo,
                        name: document.getElementById("req-emp-name").value,
                        designation: document.getElementById("req-designation").value,
                        division: document.getElementById("req-division").value,
                        mobile: document.getElementById("req-mobile").value,
                        landline: document.getElementById("req-landline").value,
                        dependents: [
                            { name: document.getElementById("req-emp-name").value || "Self", relation: "Self", dob: "1985-01-01", gender: "Male" }
                        ]
                    };
                    renderCustomDependents();
                }
            });

            ["req-emp-name", "req-designation", "req-division", "req-mobile", "req-landline"].forEach(id => {
                const inputEl = document.getElementById(id);
                if (inputEl) {
                    inputEl.addEventListener("input", () => {
                        if (activeEmployee) {
                            const prop = id.replace("req-", "");
                            const key = prop === "emp-name" ? "name" : prop;
                            activeEmployee[key] = inputEl.value;
                            if (key === "name" && activeEmployee.dependents && activeEmployee.dependents[0]) {
                                activeEmployee.dependents[0].name = inputEl.value;
                            }
                            renderCustomDependents();
                        }
                    });
                }
            });
        }
    }

    function renderCustomDependents() {
        if (!activeEmployee || !activeEmployee.dependents) return;
        const tbody = document.getElementById("dependents-table-body");
        if (!tbody) return;
        tbody.innerHTML = "";
        activeEmployee.dependents.forEach((dep, index) => {
            const tr = document.createElement("tr");
            const birthYear = new Date(dep.dob).getFullYear();
            const age = 2026 - birthYear;
            tr.innerHTML = `
                <td><input type="checkbox" class="dependent-select" data-index="${index}" checked></td>
                <td>${dep.name}</td>
                <td>${formatDate(dep.dob)}</td>
                <td>${age}</td>
                <td style="font-weight: 500; color: var(--rites-green)">Yes</td>
            `;
            tbody.appendChild(tr);
        });
    }

    // 3. Form input validation states matching screenshots
    function setupValidationListeners() {
        const forms = ["add-hospital-form", "health-request-form"];
        
        forms.forEach(formId => {
            const form = document.getElementById(formId);
            if (!form) return;

            // Handle invalid submission styles
            form.addEventListener("submit", (e) => {
                e.preventDefault();
                
                let isValid = true;
                const requiredInputs = form.querySelectorAll("[required]");
                
                requiredInputs.forEach(input => {
                    const wrapper = input.closest(".input-wrapper");
                    if (!input.value || !input.checkValidity()) {
                        isValid = false;
                        if (wrapper) wrapper.classList.add("invalid");
                    } else {
                        if (wrapper) wrapper.classList.remove("invalid");
                    }
                });

                if (isValid) {
                    if (formId === "add-hospital-form") {
                        submitHospitalForm(form);
                    } else if (formId === "health-request-form") {
                        submitRequestForm(form);
                    }
                }
            });

            // Remove invalid class on user typing
            form.querySelectorAll("input, select, textarea").forEach(field => {
                field.addEventListener("input", () => {
                    const wrapper = field.closest(".input-wrapper");
                    if (wrapper && field.value && field.checkValidity()) {
                        wrapper.classList.remove("invalid");
                    }
                });
                field.addEventListener("change", () => {
                    const wrapper = field.closest(".input-wrapper");
                    if (wrapper && field.value) {
                        wrapper.classList.remove("invalid");
                    }
                });
            });
        });
    }

    // Save Hospital Data
    async function submitHospitalForm(form) {
        const formData = new FormData(form);
        const hospital = {};
        formData.forEach((value, key) => {
            hospital[key] = value;
        });

        const result = await window.api.addHospital(hospital);
        if (result.success) {
            alert("Hospital Master Data added successfully!");
            form.reset();
            switchView("dashboard-view");
        }
    }

    // Save Health Checkup Request Data
    async function submitRequestForm(form) {
        // Gather selected dependents
        const checkedBoxes = document.querySelectorAll(".dependent-select:checked");
        if (checkedBoxes.length === 0) {
            alert("Please select at least one dependent (including Self if desired) for health checkup!");
            return;
        }

        const selectedDependentsList = [];
        checkedBoxes.forEach(box => {
            const index = box.dataset.index;
            selectedDependentsList.push(activeEmployee.dependents[index]);
        });

        const request = {
            empNo: activeEmployee.empNo,
            empName: activeEmployee.name,
            designation: activeEmployee.designation,
            division: activeEmployee.division,
            mobile: document.getElementById("req-mobile").value,
            landline: document.getElementById("req-landline").value,
            puHead: document.getElementById("req-pu-head").value,
            state: document.getElementById("req-state-select").value,
            city: document.getElementById("req-city-select").value,
            hospitalName: document.getElementById("req-hospital-select").value,
            dependents: selectedDependentsList
        };

        const result = await window.api.submitRequest(request);
        if (result.success) {
            alert("Health Checkup Request submitted successfully!");
            form.reset();
            // Re-render empty table
            document.getElementById("dependents-table-body").innerHTML = "";
            switchView("dashboard-view");
        }
    }

    // 4. Employee Profile & Dependents Table
    async function loadEmployeeData(empNo) {
        activeEmployee = await window.api.getEmployee(empNo);
        if (!activeEmployee) return;

        // Render in Request Form fields
        document.getElementById("req-emp-no").value = activeEmployee.empNo;
        document.getElementById("req-emp-name").value = activeEmployee.name;
        document.getElementById("req-designation").value = activeEmployee.designation;
        document.getElementById("req-division").value = activeEmployee.division;
        document.getElementById("req-mobile").value = activeEmployee.mobile;
        document.getElementById("req-landline").value = activeEmployee.landline;

        // Populate dependents table
        const tbody = document.getElementById("dependents-table-body");
        tbody.innerHTML = "";

        activeEmployee.dependents.forEach((dep, index) => {
            const tr = document.createElement("tr");
            
            // Calculate age
            const birthYear = new Date(dep.dob).getFullYear();
            const currentYear = 2026; // Current simulation year
            const age = currentYear - birthYear;
            
            // Determine eligibility (In this mock system: Self and Spouse are eligible, else depends on age)
            const isEligible = (dep.relation === 'Self' || dep.relation === 'Spouse' || age < 18 || age > 60) ? 'Yes' : 'No';

            tr.innerHTML = `
                <td><input type="checkbox" class="dependent-select" data-index="${index}"></td>
                <td>${dep.name}</td>
                <td>${formatDate(dep.dob)}</td>
                <td>${age}</td>
                <td style="font-weight: 500; color: ${isEligible === 'Yes' ? 'var(--rites-green)' : 'var(--error-color)'}">${isEligible}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    function formatDate(dateStr) {
        // Date input is YYYY-MM-DD, output DD-MM-YYYY
        if (!dateStr) return '';
        const parts = dateStr.split('-');
        if (parts.length === 3) {
            return `${parts[2]}-${parts[1]}-${parts[0]}`;
        }
        return dateStr;
    }

    // Helper to calculate age from DOB string YYYY-MM-DD
    function calculateAge(dobStr) {
        const birthYear = new Date(dobStr).getFullYear();
        return 2026 - birthYear; // Simulator fixed current year
    }

    // 5. Dashboard Metrics and Recent Requests
    async function refreshDashboard() {
        const hospitals = await window.api.getHospitals();
        const requests = await window.api.getRequests();

        document.getElementById("stat-hospitals").textContent = hospitals.length;
        document.getElementById("stat-requests").textContent = requests.length;
        
        const pending = requests.filter(r => r.status.toLowerCase().includes('pending'));
        document.getElementById("stat-pending").textContent = pending.length;

        const approved = requests.filter(r => r.status.toLowerCase().includes('approved'));
        document.getElementById("stat-approved").textContent = approved.length;

        // Fill recent requests table
        const tbody = document.querySelector("#dashboard-requests-table tbody");
        tbody.innerHTML = "";

        if (requests.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No requests submitted yet.</td></tr>';
            return;
        }

        // Show last 5 requests
        const recent = [...requests].reverse().slice(0, 5);
        recent.forEach(r => {
            const tr = document.createElement("tr");
            let badgeClass = 'demo';
            if (r.status.toLowerCase().includes('approved')) badgeClass = 'live';
            if (r.status.toLowerCase().includes('reject')) badgeClass = 'demo'; // red styled using demo class or custom

            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--primary-blue);">${r.ehcId}</td>
                <td>${r.empName}</td>
                <td>${r.submissionDate}</td>
                <td><span class="config-badge ${badgeClass}">${r.status}</span></td>
            `;
            tbody.appendChild(tr);
        });
    }

    // 6. Empanelled Hospitals Directory List (Update Hospital View)
    async function refreshHospitalsDirectory() {
        const hospitals = await window.api.getHospitals();
        const tbody = document.querySelector("#hospitals-list-table tbody");
        tbody.innerHTML = "";

        if (hospitals.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No hospitals empanelled.</td></tr>';
            return;
        }

        hospitals.forEach(h => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td style="font-weight: 600;">${h.vendorCode}</td>
                <td style="color: var(--text-dark); font-weight: 500;">${h.name}</td>
                <td>${h.city}</td>
                <td>₹${h.rateMale}</td>
                <td>₹${h.rateFemale}</td>
                <td>${formatDate(h.validUpto)}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    // 7. HR Approve/Reject request list
    async function refreshHrRequests() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#hr-requests-table tbody");
        tbody.innerHTML = "";
        selectedRequests = [];
        document.getElementById("hr-select-all").checked = false;

        // Expand nested requests (since one request can have multiple family members)
        hrRequests = [];
        requests.forEach(req => {
            if (req.status === 'Pending SBU' || req.status === 'Rejected by SBU') {
                return;
            }
            req.dependents.forEach(dep => {
                // We display each family member request as a line item in HR view
                hrRequests.push({
                    ehcId: req.ehcId,
                    empId: req.empNo,
                    empName: req.empName,
                    name: dep.name,
                    relation: dep.relation,
                    gender: dep.gender || 'Male',
                    dob: dep.dob,
                    paidBy: req.status.toLowerCase().includes('approved') ? 'RITES' : 'Pending Approval',
                    status: req.status
                });
            });
        });

        // Apply filters & search
        const searchTerm = document.getElementById("hr-table-search").value.toLowerCase();
        let filtered = hrRequests;
        if (searchTerm) {
            filtered = hrRequests.filter(r => 
                r.ehcId.toLowerCase().includes(searchTerm) ||
                r.empId.toLowerCase().includes(searchTerm) ||
                r.empName.toLowerCase().includes(searchTerm) ||
                r.name.toLowerCase().includes(searchTerm) ||
                r.relation.toLowerCase().includes(searchTerm)
            );
        }

        const lengthVal = parseInt(document.getElementById("hr-table-length").value);
        hrPageSize = lengthVal;

        // Pagination calculations
        const total = filtered.length;
        const start = total === 0 ? 0 : (hrPage - 1) * hrPageSize;
        const end = Math.min(start + hrPageSize, total);
        const pagedData = filtered.slice(start, end);

        // Update indicators
        document.getElementById("hr-table-info").textContent = `Showing ${total === 0 ? 0 : start + 1} to ${end} of ${total} entries`;
        document.getElementById("hr-prev-btn").disabled = hrPage === 1;
        document.getElementById("hr-next-btn").disabled = end >= total;

        if (pagedData.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; padding: 20px 0;">No data available in table</td></tr>';
            return;
        }

        pagedData.forEach((row, i) => {
            const tr = document.createElement("tr");
            const checkedState = selectedRequests.includes(row.ehcId) ? 'checked' : '';
            
            // Check if already approved/rejected to toggle interaction
            const isSettled = !row.status.toLowerCase().includes('pending');
            const disabledState = isSettled ? 'disabled' : '';
            const statusDisplay = isSettled ? row.status : row.paidBy;

            tr.innerHTML = `
                <td><input type="checkbox" class="hr-row-select" data-ehcid="${row.ehcId}" ${checkedState} ${disabledState}></td>
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td>${row.empId}</td>
                <td>${row.name}</td>
                <td>${row.relation}</td>
                <td>${row.gender}</td>
                <td>${formatDate(row.dob)}</td>
                <td>${calculateAge(row.dob)}</td>
                <td style="font-weight: 500; color: ${isSettled ? 'var(--rites-green)' : 'var(--text-muted)'}">${statusDisplay}</td>
            `;
            tbody.appendChild(tr);
        });

        // Add listeners to checkboxes
        tbody.querySelectorAll(".hr-row-select").forEach(box => {
            box.addEventListener("change", (e) => {
                const ehcId = e.target.dataset.ehcid;
                if (e.target.checked) {
                    if (!selectedRequests.includes(ehcId)) selectedRequests.push(ehcId);
                } else {
                    selectedRequests = selectedRequests.filter(id => id !== ehcId);
                }
            });
        });
    }

    function setupHrTableEvents() {
        // Select All handler
        document.getElementById("hr-select-all").addEventListener("change", (e) => {
            const boxes = document.querySelectorAll(".hr-row-select:not(:disabled)");
            boxes.forEach(box => {
                box.checked = e.target.checked;
                const ehcId = box.dataset.ehcid;
                if (e.target.checked) {
                    if (!selectedRequests.includes(ehcId)) selectedRequests.push(ehcId);
                } else {
                    selectedRequests = selectedRequests.filter(id => id !== ehcId);
                }
            });
        });

        // Live Search listener
        document.getElementById("hr-table-search").addEventListener("input", () => {
            hrPage = 1;
            refreshHrRequests();
        });

        // Length Change listener
        document.getElementById("hr-table-length").addEventListener("change", () => {
            hrPage = 1;
            refreshHrRequests();
        });

        // Pagination buttons
        document.getElementById("hr-prev-btn").addEventListener("click", () => {
            if (hrPage > 1) {
                hrPage--;
                refreshHrRequests();
            }
        });

        document.getElementById("hr-next-btn").addEventListener("click", () => {
            hrPage++;
            refreshHrRequests();
        });

        // Approval Buttons
        document.getElementById("btn-hr-approve").addEventListener("click", async () => {
            if (selectedRequests.length === 0) {
                alert("Please select at least one pending checkup request to approve!");
                return;
            }

            const remarks = document.getElementById("hr-remarks").value;
            if (!remarks) {
                alert("Please enter approval remarks!");
                return;
            }

            for (const ehcId of selectedRequests) {
                await window.api.updateRequestStatus(ehcId, 'Approved by HR', remarks);
            }

            alert("Selected requests approved successfully.");
            document.getElementById("hr-remarks").value = "";
            await refreshHrRequests();
            await refreshDashboard();
        });

        document.getElementById("btn-hr-reject").addEventListener("click", async () => {
            if (selectedRequests.length === 0) {
                alert("Please select at least one pending checkup request to reject!");
                return;
            }

            const remarks = document.getElementById("hr-remarks").value;
            if (!remarks) {
                alert("Please enter rejection remarks!");
                return;
            }

            for (const ehcId of selectedRequests) {
                await window.api.updateRequestStatus(ehcId, 'Rejected by HR', remarks);
            }

            alert("Selected requests rejected.");
            document.getElementById("hr-remarks").value = "";
            await refreshHrRequests();
            await refreshDashboard();
        });
    }

    // 8. Settings & API Connection setup
    function setupSettingsEvents() {
        const userProfileBtn = document.getElementById("user-profile-btn");
        if (userProfileBtn) {
            userProfileBtn.addEventListener("click", () => {
                // Open Settings card
                switchView("settings-view");
                
                // Populate Settings Form fields
                const config = window.api.getConfig();
                document.getElementById("settings-mode").value = config.mode;
                document.getElementById("settings-base-url").value = config.baseUrl;
                document.getElementById("settings-headers").value = JSON.stringify(config.headers, null, 2);
            });
        }

        const settingsForm = document.getElementById("settings-form");
        if (settingsForm) {
            settingsForm.addEventListener("submit", (e) => {
                e.preventDefault();
                
                const modeVal = document.getElementById("settings-mode").value;
                const baseUrlVal = document.getElementById("settings-base-url").value;
                const headersVal = document.getElementById("settings-headers").value;

                let headersObj = {};
                try {
                    if (headersVal.trim()) {
                        headersObj = JSON.parse(headersVal);
                    }
                } catch (err) {
                    alert("Authorization Headers must be valid JSON!");
                    return;
                }

                window.api.updateConfig({
                    mode: modeVal,
                    baseUrl: baseUrlVal,
                    headers: headersObj
                });

                updateModeBadge();
                alert("Settings saved successfully! Portal mode updated.");
                switchView("dashboard-view");
                window.location.reload(); // Reload to refresh datasets
            });
        }

        const resetBtn = document.getElementById("reset-mock-db");
        if (resetBtn) {
            resetBtn.addEventListener("click", () => {
                if (confirm("Are you sure you want to reset all mock data to factory defaults?")) {
                    localStorage.removeItem(CONFIG_KEY);
                    localStorage.removeItem(HOSPITALS_KEY);
                    localStorage.removeItem(REQUESTS_KEY);
                    localStorage.removeItem(CITIES_KEY);
                    alert("Local mock database reset completed.");
                    window.location.reload();
                }
            });
        }
    }

    // 9. Floating action Scroll-to-top FAB
    function setupScrollTop() {
        window.addEventListener("scroll", () => {
            if (window.scrollY > 200) {
                scrollBtn.classList.add("visible");
            } else {
                scrollBtn.classList.remove("visible");
            }
        });

        scrollBtn.addEventListener("click", () => {
            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    }

    // --- NEW CONTROLLERS & EVENT BINDINGS FOR THE 6 NEW VIEWS ---
    let selectedSbuRequests = [];
    let sbuRequests = [];
    let sbuPage = 1;
    let sbuPageSize = 10;
    let selectedDisburseRequests = [];

    function setupNewViewsEvents() {
        // --- 1. Update Rates Form Submit ---
        const updateRatesForm = document.getElementById("update-rates-form");
        if (updateRatesForm) {
            updateRatesForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                const vendorCode = document.getElementById("update-rates-hospital-select").value;
                const rateMale = document.getElementById("update-rates-male").value;
                const rateFemale = document.getElementById("update-rates-female").value;
                if (!vendorCode || !rateMale || !rateFemale) {
                    alert("Please select a hospital and fill in all rates!");
                    return;
                }
                const res = await window.api.updateHospitalRates(vendorCode, rateMale, rateFemale);
                if (res.success) {
                    alert("Hospital rates updated successfully!");
                    updateRatesForm.reset();
                    switchView("dashboard-view");
                } else {
                    alert("Failed to update rates: " + res.message);
                }
            });
        }

        // --- 2. Update Rates Hospital Selection Change ---
        const updateRatesHospitalSelect = document.getElementById("update-rates-hospital-select");
        if (updateRatesHospitalSelect) {
            updateRatesHospitalSelect.addEventListener("change", async () => {
                const vendorCode = updateRatesHospitalSelect.value;
                if (!vendorCode) {
                    document.getElementById("update-rates-male").value = "";
                    document.getElementById("update-rates-female").value = "";
                    return;
                }
                const hospitals = await window.api.getHospitals();
                const hosp = hospitals.find(h => h.vendorCode === vendorCode);
                if (hosp) {
                    document.getElementById("update-rates-male").value = hosp.rateMale;
                    document.getElementById("update-rates-female").value = hosp.rateFemale;
                }
            });
        }

        // --- 3. SBU Approve/Reject Buttons & Search ---
        const sbuSelectAll = document.getElementById("sbu-select-all");
        if (sbuSelectAll) {
            sbuSelectAll.addEventListener("change", (e) => {
                const boxes = document.querySelectorAll(".sbu-row-select:not(:disabled)");
                boxes.forEach(box => {
                    box.checked = e.target.checked;
                    const ehcId = box.dataset.ehcid;
                    if (e.target.checked) {
                        if (!selectedSbuRequests.includes(ehcId)) selectedSbuRequests.push(ehcId);
                    } else {
                        selectedSbuRequests = selectedSbuRequests.filter(id => id !== ehcId);
                    }
                });
            });
        }

        const sbuTableSearch = document.getElementById("sbu-table-search");
        if (sbuTableSearch) {
            sbuTableSearch.addEventListener("input", () => {
                sbuPage = 1;
                refreshSbuRequests();
            });
        }

        const sbuTableLength = document.getElementById("sbu-table-length");
        if (sbuTableLength) {
            sbuTableLength.addEventListener("change", () => {
                sbuPage = 1;
                refreshSbuRequests();
            });
        }

        const sbuPrevBtn = document.getElementById("sbu-prev-btn");
        if (sbuPrevBtn) {
            sbuPrevBtn.addEventListener("click", () => {
                if (sbuPage > 1) {
                    sbuPage--;
                    refreshSbuRequests();
                }
            });
        }

        const sbuNextBtn = document.getElementById("sbu-next-btn");
        if (sbuNextBtn) {
            sbuNextBtn.addEventListener("click", () => {
                sbuPage++;
                refreshSbuRequests();
            });
        }

        const btnSbuApprove = document.getElementById("btn-sbu-approve");
        if (btnSbuApprove) {
            btnSbuApprove.addEventListener("click", async () => {
                if (selectedSbuRequests.length === 0) {
                    alert("Please select at least one pending checkup request to approve!");
                    return;
                }
                const remarks = document.getElementById("sbu-remarks").value || "Approved by SBU Head";
                for (const ehcId of selectedSbuRequests) {
                    await window.api.updateRequestStatus(ehcId, 'Pending HR', remarks);
                }
                alert("Selected requests approved by SBU and sent to HR approval queue!");
                document.getElementById("sbu-remarks").value = "";
                selectedSbuRequests = [];
                await refreshSbuRequests();
                await refreshDashboard();
            });
        }

        const btnSbuReject = document.getElementById("btn-sbu-reject");
        if (btnSbuReject) {
            btnSbuReject.addEventListener("click", async () => {
                if (selectedSbuRequests.length === 0) {
                    alert("Please select at least one pending checkup request to reject!");
                    return;
                }
                const remarks = document.getElementById("sbu-remarks").value;
                if (!remarks) {
                    alert("Please enter rejection remarks!");
                    return;
                }
                for (const ehcId of selectedSbuRequests) {
                    await window.api.updateRequestStatus(ehcId, 'Rejected by SBU', remarks);
                }
                alert("Selected requests rejected by SBU.");
                document.getElementById("sbu-remarks").value = "";
                selectedSbuRequests = [];
                await refreshSbuRequests();
                await refreshDashboard();
            });
        }

        // --- 4. HR Letter Issuance Confirmation ---
        const btnConfirmIssueLetter = document.getElementById("btn-confirm-issue-letter");
        if (btnConfirmIssueLetter) {
            btnConfirmIssueLetter.addEventListener("click", async () => {
                const ehcId = btnConfirmIssueLetter.dataset.ehcid;
                if (!ehcId) return;
                await window.api.updateRequestStatus(ehcId, 'Letter Issued', 'Referral letter issued to clinic');
                alert("Referral letter issued successfully!");
                document.getElementById("referral-letter-preview-card").style.display = "none";
                await refreshHrIssueLettersView();
                await refreshDashboard();
            });
        }

        // --- 5. Upload Bill Form Submit & Cancel ---
        const billUploadForm = document.getElementById("bill-upload-form");
        if (billUploadForm) {
            billUploadForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                const ehcId = document.getElementById("upload-hidden-ehcid").value;
                const billNumber = document.getElementById("bill-number").value;
                const billDate = document.getElementById("bill-date").value;
                const billAmount = document.getElementById("bill-amount").value;
                
                const billDetails = {
                    billNumber,
                    billDate,
                    billAmount,
                    fileName: "invoice_" + ehcId + ".pdf"
                };

                const res = await window.api.uploadRequestBill(ehcId, billDetails);
                if (res.success) {
                    alert("Clinic bill details uploaded successfully!");
                    billUploadForm.reset();
                    document.getElementById("bill-upload-card").style.display = "none";
                    await refreshHrUploadBillsView();
                    await refreshDashboard();
                } else {
                    alert("Failed to upload bill: " + res.message);
                }
            });
        }

        const btnCancelUpload = document.getElementById("btn-cancel-upload");
        if (btnCancelUpload) {
            btnCancelUpload.addEventListener("click", () => {
                document.getElementById("bill-upload-form").reset();
                document.getElementById("bill-upload-card").style.display = "none";
            });
        }

        // --- 6. Finance Audit Actions ---
        const btnFinanceApproveBill = document.getElementById("btn-finance-approve-bill");
        if (btnFinanceApproveBill) {
            btnFinanceApproveBill.addEventListener("click", async () => {
                const ehcId = btnFinanceApproveBill.dataset.ehcid;
                const remarks = document.getElementById("finance-audit-remarks").value || "Audited and approved";
                const res = await window.api.approveRequestBill(ehcId, remarks);
                if (res.success) {
                    alert("Bill audited and approved successfully!");
                    document.getElementById("finance-audit-card").style.display = "none";
                    document.getElementById("finance-audit-remarks").value = "";
                    await refreshFinanceBillsView();
                    await refreshDashboard();
                }
            });
        }

        const btnFinanceRejectBill = document.getElementById("btn-finance-reject-bill");
        if (btnFinanceRejectBill) {
            btnFinanceRejectBill.addEventListener("click", async () => {
                const ehcId = btnFinanceRejectBill.dataset.ehcid;
                const remarks = document.getElementById("finance-audit-remarks").value;
                if (!remarks) {
                    alert("Please enter rejection remarks!");
                    return;
                }
                const res = await window.api.rejectRequestBill(ehcId, remarks);
                if (res.success) {
                    alert("Bill audit rejected.");
                    document.getElementById("finance-audit-card").style.display = "none";
                    document.getElementById("finance-audit-remarks").value = "";
                    await refreshFinanceBillsView();
                    await refreshDashboard();
                }
            });
        }

        const btnFinanceCancelAudit = document.getElementById("btn-finance-cancel-audit");
        if (btnFinanceCancelAudit) {
            btnFinanceCancelAudit.addEventListener("click", () => {
                document.getElementById("finance-audit-remarks").value = "";
                document.getElementById("finance-audit-card").style.display = "none";
            });
        }

        // --- 7. Prepare Disbursements Bulk Actions ---
        const disburseSelectAll = document.getElementById("disburse-select-all");
        if (disburseSelectAll) {
            disburseSelectAll.addEventListener("change", (e) => {
                const boxes = document.querySelectorAll(".disburse-row-select:not(:disabled)");
                boxes.forEach(box => {
                    box.checked = e.target.checked;
                    const ehcId = box.dataset.ehcid;
                    if (e.target.checked) {
                        if (!selectedDisburseRequests.includes(ehcId)) selectedDisburseRequests.push(ehcId);
                    } else {
                        selectedDisburseRequests = selectedDisburseRequests.filter(id => id !== ehcId);
                    }
                });
            });
        }

        const disburseForm = document.getElementById("disburse-form");
        if (disburseForm) {
            disburseForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                if (selectedDisburseRequests.length === 0) {
                    alert("Please select at least one approved bill for disbursement payout!");
                    return;
                }
                const paymentMode = document.getElementById("payout-mode").value;
                const payoutRef = document.getElementById("payout-ref").value;
                
                const disbursementDetails = {
                    paymentMode,
                    payoutRef,
                    date: new Date().toLocaleDateString('en-GB')
                };

                for (const ehcId of selectedDisburseRequests) {
                    await window.api.disburseRequest(ehcId, disbursementDetails);
                }

                alert(`Payout generated successfully for ${selectedDisburseRequests.length} checkup claims.`);
                disburseForm.reset();
                selectedDisburseRequests = [];
                await refreshFinanceDisbursementsView();
                await refreshDashboard();
                switchView("dashboard-view");
            });
        }

        // --- 8. City Master Add Form Submit ---
        const addCityForm = document.getElementById("add-city-form");
        if (addCityForm) {
            addCityForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                const stateVal = document.getElementById("new-city-state").value.trim();
                const cityVal = document.getElementById("new-city-name").value.trim();
                if (!stateVal || !cityVal) return;

                const res = await window.api.addCity(stateVal, cityVal);
                if (res.success) {
                    alert("State/City added successfully to Master Database!");
                    addCityForm.reset();
                    await refreshCityMaster();
                    await setupDropdowns(); // Re-populate all application dropdowns with new city data
                }
            });
        }
    }

    async function refreshUpdateRatesView() {
        const hospitals = await window.api.getHospitals();
        const select = document.getElementById("update-rates-hospital-select");
        if (!select) return;
        select.innerHTML = '<option value="">--Select Hospital--</option>';
        hospitals.forEach(h => {
            const opt = document.createElement("option");
            opt.value = h.vendorCode;
            opt.textContent = `${h.name} (${h.vendorCode} - ${h.city})`;
            select.appendChild(opt);
        });
        document.getElementById("update-rates-male").value = "";
        document.getElementById("update-rates-female").value = "";
    }

    async function refreshSbuRequests() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#sbu-requests-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";
        selectedSbuRequests = [];
        document.getElementById("sbu-select-all").checked = false;

        const sbuPending = requests.filter(r => r.status === 'Pending SBU');

        // Apply filters & search
        const searchTerm = document.getElementById("sbu-table-search").value.toLowerCase();
        let filtered = sbuPending;
        if (searchTerm) {
            filtered = sbuPending.filter(r => 
                r.ehcId.toLowerCase().includes(searchTerm) ||
                r.empNo.toLowerCase().includes(searchTerm) ||
                r.empName.toLowerCase().includes(searchTerm) ||
                r.hospitalName.toLowerCase().includes(searchTerm)
            );
        }

        const lengthVal = parseInt(document.getElementById("sbu-table-length").value) || 10;
        sbuPageSize = lengthVal;

        const total = filtered.length;
        const start = total === 0 ? 0 : (sbuPage - 1) * sbuPageSize;
        const end = Math.min(start + sbuPageSize, total);
        const pagedData = filtered.slice(start, end);

        document.getElementById("sbu-table-info").textContent = `Showing ${total === 0 ? 0 : start + 1} to ${end} of ${total} entries`;
        document.getElementById("sbu-prev-btn").disabled = sbuPage === 1;
        document.getElementById("sbu-next-btn").disabled = end >= total;

        if (pagedData.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px 0;">No pending requests for SBU approval.</td></tr>';
            return;
        }

        pagedData.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><input type="checkbox" class="sbu-row-select" data-ehcid="${row.ehcId}"></td>
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td>${row.empNo}</td>
                <td style="font-weight: 500;">${row.empName}</td>
                <td>${row.hospitalName}</td>
                <td>${row.submissionDate}</td>
                <td><span class="config-badge demo">${row.status}</span></td>
            `;
            tbody.appendChild(tr);
        });

        tbody.querySelectorAll(".sbu-row-select").forEach(box => {
            box.addEventListener("change", (e) => {
                const ehcId = e.target.dataset.ehcid;
                if (e.target.checked) {
                    if (!selectedSbuRequests.includes(ehcId)) selectedSbuRequests.push(ehcId);
                } else {
                    selectedSbuRequests = selectedSbuRequests.filter(id => id !== ehcId);
                }
            });
        });
    }

    async function refreshHrIssueLettersView() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#hr-issue-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";
        
        const eligible = requests.filter(r => r.status === 'Approved by HR');

        if (eligible.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px 0;">No approved requests pending referral letter issuance.</td></tr>';
            return;
        }

        eligible.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td style="font-weight: 500;">${row.empName}</td>
                <td>${row.hospitalName}</td>
                <td>${row.submissionDate}</td>
                <td><span class="config-badge live">${row.status}</span></td>
                <td>
                    <button class="btn btn-primary btn-sm view-letter-trigger" data-ehcid="${row.ehcId}" style="padding: 4px 10px; font-size: 11px;">View & Issue Letter</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        tbody.querySelectorAll(".view-letter-trigger").forEach(btn => {
            btn.addEventListener("click", async () => {
                const ehcId = btn.dataset.ehcid;
                const req = eligible.find(r => r.ehcId === ehcId);
                if (!req) return;

                const hospitals = await window.api.getHospitals();
                const hosp = hospitals.find(h => h.name === req.hospitalName) || {};

                document.getElementById("let-ehcid").textContent = req.ehcId;
                document.getElementById("let-hosp-name").textContent = req.hospitalName;
                document.getElementById("let-emp-name").textContent = req.empName;
                document.getElementById("let-emp-no").textContent = req.empNo;
                document.getElementById("let-designation").textContent = req.designation;
                document.getElementById("let-rate-male").textContent = hosp.rateMale || "0";
                document.getElementById("let-rate-female").textContent = hosp.rateFemale || "0";
                
                const validDate = new Date();
                validDate.setMonth(validDate.getMonth() + 3);
                document.getElementById("let-valid-date").textContent = validDate.toLocaleDateString('en-GB');

                const depList = document.getElementById("let-dependents-list");
                depList.innerHTML = "";
                req.dependents.forEach(d => {
                    const dTr = document.createElement("tr");
                    dTr.innerHTML = `
                        <td>${d.name}</td>
                        <td>${d.relation}</td>
                        <td>${d.gender || 'Male'}</td>
                    `;
                    depList.appendChild(dTr);
                });

                document.getElementById("btn-confirm-issue-letter").dataset.ehcid = ehcId;
                document.getElementById("referral-letter-preview-card").style.display = "block";
                document.getElementById("referral-letter-preview-card").scrollIntoView({ behavior: 'smooth' });
            });
        });
    }

    async function refreshHrUploadBillsView() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#hr-upload-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";

        const eligible = requests.filter(r => r.status === 'Letter Issued' || r.status === 'Bill Uploaded');

        if (eligible.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 20px 0;">No referral letters issued yet. Please issue a letter first.</td></tr>';
            return;
        }

        eligible.forEach(row => {
            const tr = document.createElement("tr");
            const isUploaded = row.status === 'Bill Uploaded';
            
            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td style="font-weight: 500;">${row.empName}</td>
                <td>${row.hospitalName}</td>
                <td><span class="config-badge ${isUploaded ? 'live' : 'demo'}">${row.status}</span></td>
                <td>
                    ${isUploaded 
                        ? `<span style="font-size: 12px; color: var(--rites-green); font-weight: 500;"><i class="fas fa-check-circle"></i> Bill Uploaded (₹${row.billDetails.billAmount})</span>`
                        : `<button class="btn btn-primary btn-sm upload-bill-trigger" data-ehcid="${row.ehcId}" style="padding: 4px 10px; font-size: 11px;">Upload Bill</button>`
                    }
                </td>
            `;
            tbody.appendChild(tr);
        });

        tbody.querySelectorAll(".upload-bill-trigger").forEach(btn => {
            btn.addEventListener("click", () => {
                const ehcId = btn.dataset.ehcid;
                const req = eligible.find(r => r.ehcId === ehcId);
                if (!req) return;

                document.getElementById("upload-ehcid").textContent = ehcId;
                document.getElementById("upload-hidden-ehcid").value = ehcId;
                
                const today = new Date().toISOString().split('T')[0];
                document.getElementById("bill-date").value = today;

                document.getElementById("bill-upload-card").style.display = "block";
                document.getElementById("bill-upload-card").scrollIntoView({ behavior: 'smooth' });
            });
        });
    }

    async function refreshFinanceBillsView() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#finance-bill-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";

        const eligible = requests.filter(r => r.status === 'Bill Uploaded');

        if (eligible.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px 0;">No uploaded bills pending audit approval.</td></tr>';
            return;
        }

        eligible.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td style="font-weight: 500;">${row.empName}</td>
                <td>${row.hospitalName}</td>
                <td>${row.billDetails.billNumber}</td>
                <td>${formatDate(row.billDetails.billDate)}</td>
                <td>₹${row.billDetails.billAmount}</td>
                <td>
                    <button class="btn btn-primary btn-sm audit-bill-trigger" data-ehcid="${row.ehcId}" style="padding: 4px 10px; font-size: 11px;">Audit & Process</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        tbody.querySelectorAll(".audit-bill-trigger").forEach(btn => {
            btn.addEventListener("click", () => {
                const ehcId = btn.dataset.ehcid;
                const req = eligible.find(r => r.ehcId === ehcId);
                if (!req) return;

                document.getElementById("audit-ehcid").textContent = ehcId;
                document.getElementById("audit-empname").textContent = req.empName;
                document.getElementById("audit-hosp").textContent = req.hospitalName;
                document.getElementById("audit-billno").textContent = req.billDetails.billNumber;
                document.getElementById("audit-billdate").textContent = formatDate(req.billDetails.billDate);
                document.getElementById("audit-billamount").textContent = req.billDetails.billAmount;
                document.getElementById("audit-filename").textContent = req.billDetails.fileName;

                document.getElementById("btn-finance-approve-bill").dataset.ehcid = ehcId;
                document.getElementById("btn-finance-reject-bill").dataset.ehcid = ehcId;

                document.getElementById("finance-audit-card").style.display = "block";
                document.getElementById("finance-audit-card").scrollIntoView({ behavior: 'smooth' });
            });
        });
    }

    async function refreshFinanceDisbursementsView() {
        const requests = await window.api.getRequests();
        const tbody = document.querySelector("#finance-disburse-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";
        selectedDisburseRequests = [];
        document.getElementById("disburse-select-all").checked = false;

        const eligible = requests.filter(r => r.status === 'Bill Approved' || r.status === 'Disbursed');

        if (eligible.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px 0;">No approved bills pending disbursement payout.</td></tr>';
            return;
        }

        eligible.forEach(row => {
            const tr = document.createElement("tr");
            const isDisbursed = row.status === 'Disbursed';
            const checkboxHtml = isDisbursed 
                ? `<input type="checkbox" disabled>`
                : `<input type="checkbox" class="disburse-row-select" data-ehcid="${row.ehcId}">`;

            tr.innerHTML = `
                <td>${checkboxHtml}</td>
                <td style="font-weight: 600; color: var(--primary-blue);">${row.ehcId}</td>
                <td style="font-weight: 500;">${row.empName}</td>
                <td>${row.hospitalName}</td>
                <td>${row.billDetails.billNumber}</td>
                <td>₹${row.billDetails.billAmount}</td>
                <td>
                    <span class="config-badge ${isDisbursed ? 'live' : 'demo'}">
                        ${isDisbursed ? `Paid via ${row.disbursementDetails.paymentMode}` : 'Pending Payout'}
                    </span>
                </td>
            `;
            tbody.appendChild(tr);
        });

        tbody.querySelectorAll(".disburse-row-select").forEach(box => {
            box.addEventListener("change", (e) => {
                const ehcId = e.target.dataset.ehcid;
                if (e.target.checked) {
                    if (!selectedDisburseRequests.includes(ehcId)) selectedDisburseRequests.push(ehcId);
                } else {
                    selectedDisburseRequests = selectedDisburseRequests.filter(id => id !== ehcId);
                }
            });
        });
    }

    async function refreshCityMaster() {
        const citiesData = await window.api.getStatesAndCities();
        const tbody = document.querySelector("#city-master-table tbody");
        if (!tbody) return;
        tbody.innerHTML = "";

        if (citiesData.length === 0) {
            tbody.innerHTML = '<tr><td colspan="2" style="text-align: center; padding: 20px 0;">No cities configured.</td></tr>';
            return;
        }

        citiesData.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td style="font-weight: 600; color: var(--text-dark);">${row.state}</td>
                <td>${row.cities.join(", ")}</td>
            `;
            tbody.appendChild(tr);
        });
    }
});
