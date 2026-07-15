# API Reference

Base URL: `http://localhost:8080/api`

## Health
* `GET /health` - Check backend status

## Cities
* `GET /cities` - List states and cities
* `POST /cities` - Add new city `{"state": "...", "city": "..."}`

## Hospitals
* `GET /hospitals` - List hospitals
* `POST /hospitals` - Add hospital
* `PUT /hospitals/{vendorCode}/rates` - Update rates `{"rateMale": 100, "rateFemale": 120}`

## Employees
* `GET /employees/{empNo}` - Get employee details and dependents

## Requests
* `GET /requests` - List EHC requests
* `POST /requests` - Submit new request
* `PUT /requests/{ehcId}` - Update generic status
* `PUT /requests/{ehcId}/bill` - Upload bill details
* `PUT /requests/{ehcId}/approve-bill` - Approve bill by finance
* `PUT /requests/{ehcId}/reject-bill` - Reject bill by finance
* `PUT /requests/{ehcId}/disburse` - Disburse payment
