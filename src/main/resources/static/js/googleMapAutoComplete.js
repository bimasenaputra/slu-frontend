let autocompletestart;
let autocompletedest;
function initAutocomplete() {
    autocompletestart = new google.maps.places.Autocomplete(
        document.getElementById('startingloc'),
        {
            types: ['establishment'],
            componentRestrictions: {'country': ['IDN']},
            fields: ['places_id', 'geometry', 'name']
        });

    autocompletedest = new google.maps.places.Autocomplete(
        document.getElementById('destination'),
        {
            types: ['establishment'],
            componentRestrictions: {'country': ['IDN']},
            fields: ['places_id', 'geometry', 'name']
        });
}