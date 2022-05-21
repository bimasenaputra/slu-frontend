function calculateDepartureTime() {
    var startingLoc = document.getElementById("startingloc").value;
    var destination = document.getElementById("destination").value;
    var startTime = document.getElementById("arrivaltime").value;
    var error = document.getElementById("errormsg");

    if(startingLoc === "" || destination === "") {
        error.value = "Please fill out Origin and Destination field";
        return;
    } if (startTime === ""){
        error.value = "Please fill out Arrival Time";
        return;
    } else
        error.value = "Click 'Save' to save the departure recommendation result";

    var travelMode;
    var selectedTravelMode = document.getElementById("travel-mode-deptrec").value;
    if (selectedTravelMode === "driving") {
        travelMode = google.maps.TravelMode.DRIVING;
    } else if (selectedTravelMode === "walking") {
        travelMode = google.maps.TravelMode.WALKING;
    } else if (selectedTravelMode === "bicycling") {
        travelMode = google.maps.TravelMode.BICYCLING;
    } else {
        return;
    }

    var service = new google.maps.DistanceMatrixService();
    service.getDistanceMatrix(
        {
            origins: [startingLoc],
            destinations: [destination],
            travelMode: travelMode,
            avoidHighways: false,
            avoidTolls: false,
        },
        callback2
    );
}
function callback2(response, status) {
    if(status==="OK") {
        var error = document.getElementById("errormsg");
        if (response.rows[0].elements[0].status === "ZERO_RESULTS") {
            error.value = "No route could be found between the origin and destination";
        } else if (response.rows[0].elements[0].status === "NOT_FOUND") {
            error.value = "The origin and/or destination of this pairing could not be geocoded";
        } else if (response.rows[0].elements[0].status === "MAX_ROUTE_LENGTH_EXCEEDED") {
            error.value = "The requested route is too long and cannot be processed";
        } else if (response.rows[0].elements[0].status === "OK") {
            var arrival = document.getElementById("arrivaltime").value;
            var day = document.getElementById("day").value * 86400;
            var hour = document.getElementById("hour").value * 3600;
            var minute = document.getElementById("minute").value * 60;

            var endtime = new Date(arrival);
            endtime.setSeconds(endtime.getSeconds() + day + hour + minute);

            var departuretime = new Date(arrival);
            departuretime.setSeconds(departuretime.getSeconds() - response.rows[0].elements[0].duration.value);

            document.getElementById("resultstart").value = departuretime.toLocaleDateString() + "T" + departuretime.toTimeString().slice(0,5);
            document.getElementById("resultend").value = endtime.toLocaleDateString() + "T" + endtime.toTimeString().slice(0,5)
        }
    }
}

function save(){
    var starttime = document.getElementById("date");
    var endtime = document.getElementById("time");
    starttime.value = document.getElementById("resultstart").value;
    endtime.value = document.getElementById("resultend").value;
}
