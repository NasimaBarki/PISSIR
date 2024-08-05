"use strict";

//Display dei double come int
function doubleToInt() {
    var numbers = document.getElementsByClassName("double");

    for(var i = 0; i < numbers.length; i++) {
        var number = numbers[i].innerHTML;
        numbers[i].innerHTML = Math.floor(number);
    }
}

window.addEventListener("load",function() { doubleToInt() });

//Form richiesta ricarica
function showSecondForm() {
    document.getElementById('rechargeFormPartTwo').style.display = 'block';
    document.getElementById('next').style.display = 'none';
}