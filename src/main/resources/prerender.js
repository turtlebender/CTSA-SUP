//configure the environment to accept inline scripts as well as remote javascript files.
//This is needed so that we can load jquery.
Envjs({
    scriptTypes: {
        '': true, //anonymous and inline
        'text/javascript': true
    }
});

function render(templateLocation, slotDefinitions) {
    //specify the document to use as the template.
    window.location = templateLocation;
    document.async = false;

    //If jquery was not loaded as part of the template document, do so here.
    if (typeof jQuery == 'undefined') {
        load('http://code.jquery.com/jquery-1.4.2.js');
    }
    //Load the slots definition from the mapping file.
    load(slotDefinitions);

    //This is necessary so that the loads happen synchronously.
    $.ajaxSetup({
        async: false
    });

    //Loop through the slot definitions and apply the slots to the template.
    for (var key in slots) {
        if (slots.hasOwnProperty(key)) {
            var selector = '#' + key;
            $(selector).load(slots[key]);
        }
    }

    //Render the final html.
    return "<html>" + $('html').html() + "</html>";
}

