var showHideNewSchema = function() {
    if (window.showNewSchema) {
        document.getElementById("tdnewschema").style.display = "none";
        document.getElementById("tdschema").style.display = "";
    }
    else {
        document.getElementById("tdnewschema").style.display = "";
        document.getElementById("tdschema").style.display = "none";
    }
    window.showNewSchema = !window.showNewSchema;
};

var showHideNewRepo = function() {
    if (window.showNewRepo) {
        document.getElementById("newigform:newrepo").style.display = "none";
        document.getElementById("newigform:repo").style.display = "";
    }
    else {
        document.getElementById("newigform:newrepo").style.display = "";
        document.getElementById("newigform:repo").style.display = "none";
    }
    window.showNewRepo = !window.showNewRepo;
};


var disableRadioButtons = function(event) {
    var selectedRadio = this;
    jQuery('input[type="radio"]').each(function(idx, obj) {
        if (obj.name != selectedRadio.name) {
            obj.checked = false;
        } 
    });
}