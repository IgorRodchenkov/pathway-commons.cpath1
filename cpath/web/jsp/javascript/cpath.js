
//  Updates User Interface for Advanced Search Box.
function updateAdvancedSearchBox () {
    var searchOrganismBox =	document.getElementById("searchOrganismBox")
    var selectOrganism = document.getElementById("selectOrganism");
    var searchTextBox = document.getElementById("searchTextBox").style
    var curSelect = document.getElementById("searchCriteria");

    var value = curSelect.options[curSelect.selectedIndex].value
    if (value == "get_by_interactor_tax_id") {
        searchTextBox.display="none"
        searchOrganismBox.style.display="block"
        selectOrganism.name="q";
    }
	else {
		searchTextBox.display="block"
		searchOrganismBox.style.display="none"
		selectOrganism.name="organism";
	}

	var option1Text =	document.getElementById("option1Text").style
	var option2Text =	document.getElementById("option2Text").style
	var option3Text =	document.getElementById("option3Text").style
	var option4Text =	document.getElementById("option4Text").style
	var option5Text =	document.getElementById("option5Text").style
	var option6Text =	document.getElementById("option6Text").style

    if (value == "get_by_interactor_id") {
        option1Text.display="block";
        option2Text.display="none";
        option3Text.display="none";
        option4Text.display="none";
        option5Text.display="none";
        option6Text.display="none";
    }
    if (value == "get_by_interactor_name") {
        option1Text.display="none";
        option2Text.display="block";
        option3Text.display="none";
        option4Text.display="none";
        option5Text.display="none";
        option6Text.display="none";
    }
    if (value == "get_by_interactor_tax_id") {
        option1Text.display="none";
        option2Text.display="none";
        option3Text.display="block";
        option4Text.display="none";
        option5Text.display="none";
        option6Text.display="none";
    }
    if (value == "get_by_interactor_keyword") {
        option1Text.display="none";
        option2Text.display="none";
        option3Text.display="none";
        option4Text.display="block";
        option5Text.display="none";
        option6Text.display="none";
    }
    if (value == "get_by_interaction_db") {
        option1Text.display="none";
        option2Text.display="none";
        option3Text.display="none";
        option4Text.display="none";
        option5Text.display="block";
        option6Text.display="none";
    }
    if (value == "get_by_interaction_pmid") {
        option1Text.display="none";
        option2Text.display="none";
        option3Text.display="none";
        option4Text.display="none";
        option5Text.display="none";
        option6Text.display="block";
    }
	return false
}