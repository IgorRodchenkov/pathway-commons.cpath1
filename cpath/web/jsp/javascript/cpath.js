//  Change Style of Any Element
//  This is a fairly generic Javascript function.
//  It takes two parameters:
//  1)  The first parameter is the name of an element, as defined by its id.
//  For example, this DIV tag has an id named:  "affy"
//  <DIV id='affy' class='hide'>
//  2)  The second parameter is the name of a style.  For example, this
//  is a style defined in style.css
//  .hide {
//	display:none;
//  }

//  See InteractionTable.java for a full example of how to use this.

function changeStyle (element, newStyle) {
    //  Get the Element by ID
    var section = document.getElementById(element);

    //  Sets this Element Style to the New Style
    section.className = newStyle;

    return true;
}