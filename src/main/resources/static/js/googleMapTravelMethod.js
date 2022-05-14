function calculateTime() {
    var startingLoc = document.getElementById("startingloc").value;
    var destination = document.getElementById("destination").value;

    if(startingLoc === "" || destination === "") {
        var result = document.getElementById("result");
        result.value = "Please fill out Origin and Destination field";
        return;
    }

    var travelMode;
    var selectedTravelMode = document.getElementById("travel-mode").value;
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
        callback
    );
}
function callback(response, status) {
    if(status==="OK") {
        var result = document.getElementById("result");
        if (response.rows[0].elements[0].status === "ZERO_RESULTS") {
            result.value = "No route could be found between the origin and destination";
        } else if (response.rows[0].elements[0].status === "NOT_FOUND") {
            result.value = "The origin and/or destination of this pairing could not be geocoded";
        } else if (response.rows[0].elements[0].status === "MAX_ROUTE_LENGTH_EXCEEDED") {
            result.value = "The requested route is too long and cannot be processed";
        } else if (response.rows[0].elements[0].status === "OK") {
            result.value = response.rows[0].elements[0].duration.text;
        }
    }
}